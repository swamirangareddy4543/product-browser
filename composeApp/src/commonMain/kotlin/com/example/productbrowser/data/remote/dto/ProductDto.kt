package com.example.productbrowser.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String,
    val images: List<String> = emptyList(),
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String? = null,
    val category: String = ""
)

