package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
}

