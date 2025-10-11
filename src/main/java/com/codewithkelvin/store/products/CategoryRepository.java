package com.codewithkelvin.store.repositories;

import com.codewithkelvin.store.products.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}