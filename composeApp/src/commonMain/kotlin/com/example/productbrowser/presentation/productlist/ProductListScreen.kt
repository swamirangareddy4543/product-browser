package com.example.productbrowser.presentation.productlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.productbrowser.presentation.components.AppIcons
import com.example.productbrowser.presentation.components.CategoryFilterRow
import com.example.productbrowser.presentation.components.ErrorView
import com.example.productbrowser.presentation.components.LoadingIndicator
import com.example.productbrowser.presentation.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onProductClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Infinite scroll trigger
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 4
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMoreProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Product Browser",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = AppIcons.ShoppingBag,
                        contentDescription = "Product Browser",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onSearch = { isSearchActive = false },
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },
                        placeholder = { Text("Search products…") },
                        leadingIcon = {
                            Icon(AppIcons.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(AppIcons.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                expanded = isSearchActive,
                onExpandedChange = { isSearchActive = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}

            // Category Filter
            if (categories.isNotEmpty() && searchQuery.isBlank()) {
                CategoryFilterRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = viewModel::filterByCategory
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Content
            when (val state = uiState) {
                is ProductListUiState.Loading -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                is ProductListUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = viewModel::retry
                    )
                }
                is ProductListUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = state.products,
                            key = { _, product -> product.id }
                        ) { _, product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

