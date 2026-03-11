package com.example.productbrowser.presentation.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productbrowser.domain.model.Product
import com.example.productbrowser.domain.usecase.GetCategoriesUseCase
import com.example.productbrowser.domain.usecase.GetProductsByCategoryUseCase
import com.example.productbrowser.domain.usecase.GetProductsUseCase
import com.example.productbrowser.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Success(
        val products: List<Product>,
        val isLoadingMore: Boolean = false,
        val hasReachedEnd: Boolean = false
    ) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}

@OptIn(FlowPreview::class)
class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val pageSize = 20
    private var currentSkip = 0
    private var loadJob: Job? = null

    init {
        loadProducts()
        loadCategories()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        _searchQuery
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    _selectedCategory.value = null
                    resetAndLoad()
                } else {
                    searchProducts(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadProducts(loadMore: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (!loadMore) {
                _uiState.value = ProductListUiState.Loading
                currentSkip = 0
            } else {
                val current = _uiState.value
                if (current is ProductListUiState.Success) {
                    if (current.hasReachedEnd || current.isLoadingMore) return@launch
                    _uiState.value = current.copy(isLoadingMore = true)
                }
            }

            getProductsUseCase(limit = pageSize, skip = currentSkip)
                .onSuccess { newProducts ->
                    val existingProducts = if (loadMore) {
                        (_uiState.value as? ProductListUiState.Success)?.products ?: emptyList()
                    } else emptyList()
                    val allProducts = existingProducts + newProducts
                    currentSkip += newProducts.size
                    _uiState.value = ProductListUiState.Success(
                        products = allProducts,
                        hasReachedEnd = newProducts.size < pageSize
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProductListUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun loadMoreProducts() {
        if (_searchQuery.value.isBlank() && _selectedCategory.value == null) {
            loadProducts(loadMore = true)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun searchProducts(query: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ProductListUiState.Loading
            searchProductsUseCase(query)
                .onSuccess { products ->
                    _uiState.value = ProductListUiState.Success(
                        products = products,
                        hasReachedEnd = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProductListUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
        if (category == null) {
            resetAndLoad()
        } else {
            loadJob?.cancel()
            loadJob = viewModelScope.launch {
                _uiState.value = ProductListUiState.Loading
                getProductsByCategoryUseCase(category)
                    .onSuccess { products ->
                        _uiState.value = ProductListUiState.Success(
                            products = products,
                            hasReachedEnd = true
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = ProductListUiState.Error(error.message ?: "Unknown error")
                    }
            }
        }
    }

    private fun resetAndLoad() {
        currentSkip = 0
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onSuccess { cats -> _categories.value = cats }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun retry() {
        if (_searchQuery.value.isNotBlank()) {
            searchProducts(_searchQuery.value)
        } else {
            resetAndLoad()
        }
    }
}

