package com.example.productbrowser.di

import com.example.productbrowser.data.remote.ProductApiServiceImpl
import com.example.productbrowser.data.repository.ProductRepositoryImpl
import com.example.productbrowser.domain.repository.ProductRepository
import com.example.productbrowser.domain.usecase.GetCategoriesUseCase
import com.example.productbrowser.domain.usecase.GetProductDetailsUseCase
import com.example.productbrowser.domain.usecase.GetProductsByCategoryUseCase
import com.example.productbrowser.domain.usecase.GetProductsUseCase
import com.example.productbrowser.domain.usecase.SearchProductsUseCase
import com.example.productbrowser.presentation.productdetail.ProductDetailViewModel
import com.example.productbrowser.presentation.productlist.ProductListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppModule {

    private val httpClient: HttpClient by lazy {
        HttpClient(createHttpClientEngine()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    private val productApiService: ProductApiServiceImpl by lazy {
        ProductApiServiceImpl(httpClient)
    }

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(productApiService)
    }

    val getProductsUseCase: GetProductsUseCase by lazy {
        GetProductsUseCase(productRepository)
    }

    val searchProductsUseCase: SearchProductsUseCase by lazy {
        SearchProductsUseCase(productRepository)
    }

    val getProductDetailsUseCase: GetProductDetailsUseCase by lazy {
        GetProductDetailsUseCase(productRepository)
    }

    val getCategoriesUseCase: GetCategoriesUseCase by lazy {
        GetCategoriesUseCase(productRepository)
    }

    val getProductsByCategoryUseCase: GetProductsByCategoryUseCase by lazy {
        GetProductsByCategoryUseCase(productRepository)
    }

    fun provideProductListViewModel(): ProductListViewModel =
        ProductListViewModel(getProductsUseCase, searchProductsUseCase, getCategoriesUseCase, getProductsByCategoryUseCase)

    fun provideProductDetailViewModel(productId: Int): ProductDetailViewModel =
        ProductDetailViewModel(getProductDetailsUseCase, productId)
}

