package com.example.productbrowser

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.productbrowser.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}
