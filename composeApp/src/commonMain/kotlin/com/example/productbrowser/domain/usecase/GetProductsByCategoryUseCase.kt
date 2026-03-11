package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository

class GetProductsByCategoryUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(category: String): Result<List<Product>> {
        return repository.getProductsByCategory(category)
    }
}

