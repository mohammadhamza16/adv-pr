package com.example.ecommerce.service;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.dao.CategoryRepository;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() throws SQLException {
        return repository.findAll();
    }

    public Category create(Category category) throws SQLException {
        return repository.create(category);
    }

    public boolean update(Category category) throws SQLException {
        return repository.update(category);
    }

    public boolean delete(long id) throws SQLException {
        return repository.delete(id);
    }
}

