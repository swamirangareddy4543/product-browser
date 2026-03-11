package com.example.productbrowser.data.repository

import com.example.productbrowser.data.remote.ProductApiService
import com.example.productbrowser.data.remote.dto.ProductDto
import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val apiService: ProductApiService
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): Result<List<Product>> {
        return runCatching {
            apiService.getProducts(limit, skip).products.map { it.toDomain() }
        }
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return runCatching {
            apiService.searchProducts(query).products.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        return runCatching {
            apiService.getProductById(id).toDomain()
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return runCatching {
            apiService.getProductsByCategory(category).products.map { it.toDomain() }
        }
    }

    override suspend fun getCategories(): Result<List<String>> {
        return runCatching {
            apiService.getCategories()
        }
    }

    private fun ProductDto.toDomain(): Product = Product(
        id = id,
        title = title,
        description = description,
        price = price,
        thumbnail = thumbnail,
        images = images,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category
    )
}

