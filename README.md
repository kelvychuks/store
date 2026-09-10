# Store API

[![CI](https://github.com/kelvychuks/store/actions/workflows/ci.yml/badge.svg)](https://github.com/kelvychuks/store/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

E-commerce backend built with Java and Spring Boot: catalogue, anonymous carts,
JWT authentication, order placement and Stripe checkout with webhook
confirmation.

**Live API:** <https://store-api-lldi.onrender.com> · **Swagger UI:** <https://store-api-lldi.onrender.com/swagger-ui/index.html>

> The API is hosted on a free tier that sleeps after inactivity, so the first
> request can take 30-50 seconds while the instance wakes up. Everything after
> that is fast.

---

## Demo credentials

| Role     | Email             | Password     | Can do                                    |
|----------|-------------------|--------------|-------------------------------------------|
| Customer | `demo@store.dev`  | `Demo1234!`  | Browse, cart, order, checkout             |
| Admin    | `admin@store.dev` | `Admin1234!` | Everything above, plus catalogue writes    |

Stripe runs in **test mode**: card `4242 4242 4242 4242`, any future expiry, any
CVC. No real money moves.

---

## What it does

- **Catalogue.** Products and categories, public to read, admin-only to change.
- **Anonymous carts.** A cart is created without an account and identified by a
  UUID, so a visitor can shop before signing up. Adding a product already in the
  cart increments the line rather than duplicating it.
- **Authentication.** Stateless JWT access tokens plus a refresh token in an
  HttpOnly cookie, with `USER` and `ADMIN` roles enforced at the filter chain.
- **Orders.** An order is built from a cart, copying unit prices at the moment
  of purchase so later price changes can't rewrite history.
- **Payments.** A Stripe Checkout session per order; the order only becomes
  `PAID` when Stripe's signed webhook says so, never on the browser redirect.

## Architecture

```
                 ┌──────────────────┐
   Browser ─────▶│  React storefront│─────┐
                 └──────────────────┘     │  JSON over HTTPS
                                          ▼
┌───────────────────────────────────────────────────────────────┐
│ Spring Boot                                                   │
│                                                               │
│  Controllers ── Services ── Repositories (Spring Data JPA)     │
│       │            │                                          │
│       │            └── PaymentGateway (interface)             │
│       │                     └── StripePaymentGateway          │
│  JwtAuthenticationFilter → SecurityFilterChain                │
└───────────────────────────────────────────────────────────────┘
             │                                  ▲
             ▼                                  │ signed webhook
     ┌───────────────┐                   ┌─────────────┐
     │  PostgreSQL   │                   │   Stripe    │
     │  (Flyway)     │                   └─────────────┘
     └───────────────┘
```

Packages are organised by feature (`products`, `carts`, `orders`, `payments`,
`users`, `auth`) rather than by layer, so everything that changes together lives
together.

`PaymentGateway` is an interface with a Stripe implementation behind it. That
boundary is the point: the ordering code talks about checkout sessions and
payment results, not about Stripe.

## Design decisions

**Money is `BigDecimal`, never `double`.** Prices and totals are `numeric(10,2)`
in the database and `BigDecimal` in Java. Binary floating point cannot represent
`0.10` exactly, and an e-commerce total that is off by a hundredth of a cent is a
reconciliation problem later.

**Prices are copied onto the order.** `order_items` stores `unit_price` and
`total_price` rather than joining back to `products`. When a price changes, past
orders must not change with it.

**The webhook is the source of truth for payment.** The success redirect only
tells you the customer's browser reached a URL. `PaymentStatus` moves to `PAID`
when Stripe's signature-verified webhook arrives.

**Flyway owns the schema; Hibernate validates it.** `ddl-auto: validate` means
the app refuses to start if the entities and the migrations have drifted apart,
which is a much better failure than silently corrupting data with `update`.

**Tests use a real PostgreSQL.** Testcontainers starts one per run. The most
valuable thing to test here is that the migrations apply and the entities match
the schema, and an in-memory database would happily accept SQL PostgreSQL rejects.

## Running it locally

**With Docker (nothing else to install):**

```bash
cp .env.example .env    # optional: only needed for real Stripe keys
docker compose up --build
```

API on <http://localhost:8080>, Swagger UI on
<http://localhost:8080/swagger-ui/index.html>, database migrated and seeded.

**Without Docker.** A JDK 17 and any PostgreSQL the machine can reach, including
a free hosted one:

```bash
export DATABASE_URL=jdbc:postgresql://<host>/<db>?sslmode=require
export DATABASE_USERNAME=<user>
export DATABASE_PASSWORD=<password>
./mvnw spring-boot:run
```

Flyway creates and seeds the schema on first start, so an empty database is all
that's needed.

Configuration is read from environment variables with development defaults in
`src/main/resources/application.yaml`; see `.env.example` for the full list.

**Tests:**

```bash
./mvnw verify
```

Unit tests run anywhere. The Testcontainers integration test starts its own
PostgreSQL and skips itself automatically when no Docker daemon is present, so
this passes on a JDK-only machine and runs in full in CI.

## API tour

| Method | Path                        | Auth      | Purpose                            |
|--------|-----------------------------|-----------|------------------------------------|
| POST   | `/users`                    | public    | Register                           |
| POST   | `/auth/login`               | public    | Exchange credentials for a JWT     |
| POST   | `/auth/refresh`             | cookie    | New access token                   |
| GET    | `/auth/me`                  | bearer    | Current user                       |
| GET    | `/products`                 | public    | Catalogue, optionally by category  |
| POST   | `/products`                 | admin     | Add a product                      |
| POST   | `/carts`                    | public    | Create an anonymous cart           |
| POST   | `/carts/{cartId}/items`     | public    | Add an item                        |
| PUT    | `/carts/{cartId}/items/{productId}`| public    | Change quantity                    |
| POST   | `/checkout`                 | bearer    | Create a Stripe checkout session   |
| POST   | `/checkout/webhook`         | Stripe    | Payment confirmation               |
| GET    | `/orders`                   | bearer    | The caller's orders                |
| GET    | `/actuator/health`          | public    | Liveness/readiness                 |

Full, interactive documentation is at `/swagger-ui/index.html`.

## Deploying

The service ships as a Docker image and needs only a PostgreSQL URL and a few
secrets. `render.yaml` is a working Render blueprint; any container host works.

One thing to watch: managed Postgres providers hand you a URL like
`postgresql://user:pass@host/db`, but Spring wants
`jdbc:postgresql://host/db?sslmode=require` with the username and password
supplied separately.

Required environment variables in production:

| Variable                    | Notes                                            |
|-----------------------------|--------------------------------------------------|
| `DATABASE_URL`              | JDBC form, as above                              |
| `DATABASE_USERNAME`         |                                                  |
| `DATABASE_PASSWORD`         |                                                  |
| `JWT_SECRET`                | ≥32 chars; `openssl rand -base64 48`             |
| `STRIPE_SECRET_KEY`         | Test-mode key for a demo deployment              |
| `STRIPE_WEBHOOK_SECRET_KEY` | From the Stripe webhook endpoint you register    |
| `WEBSITE_URL`               | Storefront origin, used for Stripe redirects     |
| `CORS_ALLOWED_ORIGINS`      | Comma-separated; the storefront origin           |

## Tech

Java 17 · Spring Boot 3.5 · Spring Security · Spring Data JPA · PostgreSQL 16 ·
Flyway · MapStruct · Lombok · Stripe Java SDK · springdoc-openapi · JUnit 5 ·
Mockito · Testcontainers · Docker · GitHub Actions
