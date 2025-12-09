package com.app.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Product;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

	Page<Product> findByNameLike(String keyword, Pageable pageDetails);


}
