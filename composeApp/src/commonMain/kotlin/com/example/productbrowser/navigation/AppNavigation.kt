package com.example.productbrowser.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.productbrowser.di.AppModule
import com.example.productbrowser.presentation.productdetail.ProductDetailScreen
import com.example.productbrowser.presentation.productdetail.ProductDetailViewModel
import com.example.productbrowser.presentation.productlist.ProductListScreen
import com.example.productbrowser.presentation.productlist.ProductListViewModel
import kotlinx.serialization.Serializable

@Serializable
object ProductListRoute

@Serializable
data class ProductDetailRoute(val productId: Int)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val productListViewModel = remember { AppModule.provideProductListViewModel() }

    NavHost(
        navController = navController,
        startDestination = ProductListRoute
    ) {
        composable<ProductListRoute> {
            ProductListScreen(
                viewModel = productListViewModel,
                onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                }
            )
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route: ProductDetailRoute = backStackEntry.toRoute()
            val detailViewModel = remember(route.productId) {
                AppModule.provideProductDetailViewModel(route.productId)
            }
            ProductDetailScreen(
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

