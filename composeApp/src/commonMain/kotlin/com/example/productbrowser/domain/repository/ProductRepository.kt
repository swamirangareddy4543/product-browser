package com.example.productbrowser.domain.repository

import com.example.productbrowser.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(limit: Int = 20, skip: Int = 0): Result<List<Product>>
    suspend fun searchProducts(query: String): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun getProductsByCategory(category: String): Result<List<Product>>
    suspend fun getCategories(): Result<List<String>>
}

