package com.example.productbrowser.data.remote

import com.example.productbrowser.data.remote.dto.ProductDto
import com.example.productbrowser.data.remote.dto.ProductListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface ProductApiService {
    suspend fun getProducts(limit: Int = 20, skip: Int = 0): ProductListResponseDto
    suspend fun searchProducts(query: String): ProductListResponseDto
    suspend fun getProductById(id: Int): ProductDto
    suspend fun getProductsByCategory(category: String): ProductListResponseDto
    suspend fun getCategories(): List<String>
}

class ProductApiServiceImpl(private val httpClient: HttpClient) : ProductApiService {

    companion object {
        private const val BASE_URL = "https://dummyjson.com"
    }

    override suspend fun getProducts(limit: Int, skip: Int): ProductListResponseDto {
        return httpClient.get("$BASE_URL/products") {
            parameter("limit", limit)
            parameter("skip", skip)
        }.body()
    }

    override suspend fun searchProducts(query: String): ProductListResponseDto {
        return httpClient.get("$BASE_URL/products/search") {
            parameter("q", query)
        }.body()
    }

    override suspend fun getProductById(id: Int): ProductDto {
        return httpClient.get("$BASE_URL/products/$id").body()
    }

    override suspend fun getProductsByCategory(category: String): ProductListResponseDto {
        return httpClient.get("$BASE_URL/products/category/$category").body()
    }

    override suspend fun getCategories(): List<String> {
        return httpClient.get("$BASE_URL/products/category-list").body()
    }
}

