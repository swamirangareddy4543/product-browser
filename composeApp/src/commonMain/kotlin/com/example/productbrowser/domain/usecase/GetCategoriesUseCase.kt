package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.repository.ProductRepository

class GetCategoriesUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getCategories()
    }
}

