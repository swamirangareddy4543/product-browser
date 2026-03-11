package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository

class GetProductDetailsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Int): Result<Product> {
        return repository.getProductById(id)
    }
}

