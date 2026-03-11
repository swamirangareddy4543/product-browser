package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository

class SearchProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(query: String): Result<List<Product>> {
        return repository.searchProducts(query)
    }
}

