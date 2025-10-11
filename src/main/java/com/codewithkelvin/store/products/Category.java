package com.codewithkelvin.store.entities;

import com.codewithkelvin.store.products.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "categories", schema = "store")
public class Category {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "name")
    private String name;

    @OneToMany
    @JoinColumn(name = "category_id")
    private Set<Product> products = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }
}