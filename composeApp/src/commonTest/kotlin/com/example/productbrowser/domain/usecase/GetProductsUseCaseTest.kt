package com.example.productbrowser.domain.usecase

import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.repository.ProductRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetProductsUseCaseTest {

    private val fakeProducts = listOf(
        Product(
            id = 1,
            title = "iPhone 15",
            description = "Latest iPhone",
            price = 999.99,
            thumbnail = "https://example.com/iphone.jpg",
            images = listOf("https://example.com/iphone.jpg"),
            rating = 4.8,
            stock = 100,
            brand = "Apple",
            category = "smartphones"
        ),
        Product(
            id = 2,
            title = "Samsung Galaxy S24",
            description = "Latest Samsung phone",
            price = 899.99,
            thumbnail = "https://example.com/samsung.jpg",
            images = listOf("https://example.com/samsung.jpg"),
            rating = 4.6,
            stock = 80,
            brand = "Samsung",
            category = "smartphones"
        )
    )

    @Test
    fun `getProductsUseCase returns success with products`() = runTest {
        val repository = FakeProductRepository(fakeProducts)
        val useCase = GetProductsUseCase(repository)

        val result = useCase(limit = 20, skip = 0)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("iPhone 15", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getProductsUseCase returns failure on repository error`() = runTest {
        val repository = FakeProductRepository(
            products = emptyList(),
            shouldThrow = true
        )
        val useCase = GetProductsUseCase(repository)

        val result = useCase(limit = 20, skip = 0)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchProductsUseCase returns filtered products`() = runTest {
        val repository = FakeProductRepository(fakeProducts)
        val useCase = SearchProductsUseCase(repository)

        val result = useCase("iPhone")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("iPhone 15", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getProductDetailsUseCase returns single product`() = runTest {
        val repository = FakeProductRepository(fakeProducts)
        val useCase = GetProductDetailsUseCase(repository)

        val result = useCase(1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.id)
        assertEquals("iPhone 15", result.getOrNull()?.title)
    }

    @Test
    fun `getProductDetailsUseCase returns failure for unknown id`() = runTest {
        val repository = FakeProductRepository(
            products = fakeProducts,
            shouldThrow = true
        )
        val useCase = GetProductDetailsUseCase(repository)

        val result = useCase(999)

        assertTrue(result.isFailure)
    }
}

private class FakeProductRepository(
    private val products: List<Product>,
    private val shouldThrow: Boolean = false
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): Result<List<Product>> {
        return if (shouldThrow) Result.failure(Exception("Network error"))
        else Result.success(products.drop(skip).take(limit))
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return if (shouldThrow) Result.failure(Exception("Network error"))
        else Result.success(products.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        })
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        if (shouldThrow) return Result.failure(Exception("Network error"))
        return products.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Product not found"))
    }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return if (shouldThrow) Result.failure(Exception("Network error"))
        else Result.success(products.filter { it.category == category })
    }

    override suspend fun getCategories(): Result<List<String>> {
        return if (shouldThrow) Result.failure(Exception("Network error"))
        else Result.success(products.map { it.category }.distinct())
    }
}

