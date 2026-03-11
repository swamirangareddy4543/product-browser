package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(limit: Int = 20, skip: Int = 0): Result<List<Product>> {
        return repository.getProducts(limit, skip)
    }
}

