package com.example.productbrowser.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String,
    val images: List<String>,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String
)

