package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.entity.Book;
import com.letocart.java_apirest_2026.entity.Category;
import com.letocart.java_apirest_2026.repository.BookRepository;
import com.letocart.java_apirest_2026.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public Book createBook(String title, Set<Integer> categoryIds) {
        Book book = new Book(title);
        
        // Ajouter les catégories au livre
        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                book.getCategories().add(category);
            }
        }
        
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
}

