package com.i69.ui.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.i69.data.models.market.FreightEstimateResponse
import com.i69.data.models.market.GetAllCategoriesResponse
import com.i69.data.models.market.GetChildrenCategoriesResponse
import com.i69.data.models.market.ProductDetails
import com.i69.data.models.market.Products
import com.i69.data.models.market.SearchProducts
import com.i69.data.remote.repository.MarketPlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketPlacesViewModel @Inject constructor(private val marketPlacesRepository: MarketPlacesRepository) :
    ViewModel() {

    val marketPlaces: LiveData<SearchProducts> get() = _marketPlaces
    private var _marketPlaces = MutableLiveData<SearchProducts>()

    val marketPlacesProduct: LiveData<ProductDetails> get() = _marketPlacesProduct
    private var _marketPlacesProduct = MutableLiveData<ProductDetails>()

    val getAllCategoriesResponse: LiveData<ArrayList<GetAllCategoriesResponse>> get() = _getAllCategoriesResponse
    private var _getAllCategoriesResponse = MutableLiveData<ArrayList<GetAllCategoriesResponse>>()

    val getChildrenCategoriesResponse: LiveData<ArrayList<GetChildrenCategoriesResponse>> get() = _getChildrenCategoriesResponse
    private var _getChildrenCategoriesResponse =
        MutableLiveData<ArrayList<GetChildrenCategoriesResponse>>()

    val feedItems: LiveData<ArrayList<Products>> get() = _feedItems
    private var _feedItems = MutableLiveData<ArrayList<Products>>()

    val imageSearchProducts: LiveData<ArrayList<Products>> get() = _imageSearchProducts
    private var _imageSearchProducts = MutableLiveData<ArrayList<Products>>()

    val freightEstimateResponse: LiveData<ArrayList<FreightEstimateResponse>> get() = _freightEstimateResponse
    private var _freightEstimateResponse =
        MutableLiveData<ArrayList<FreightEstimateResponse>>()

    val addCartItem: LiveData<String> get() = _addCartItem
    private var _addCartItem = MutableLiveData<String>()

//    private var _searchProducts: SearchProducts? = null
//    private var searchProductsMutableLiveData: MutableLiveData<SearchProducts> = MutableLiveData()

    fun addCartItem(
        context: Context, buyerId: String, productId: String,
        skuId: String, quantity: String
    ) {
        if (_addCartItem == null || true) viewModelScope.launch {
            marketPlacesRepository.addCartItem(buyerId, productId, skuId, quantity)
                ?.let { _addCartItem.postValue(it) }
        }
    }

    fun searchImage(context: Context, imageString: String) {
        if (_imageSearchProducts == null || true) viewModelScope.launch {
            marketPlacesRepository.searchImage(imageString)
                ?.let { _imageSearchProducts.postValue(it) }
        }
    }

    fun freightEstimate(
        context: Context, productId: String,
        quantity: String,
        skuId: String
    ) {
        if (_freightEstimateResponse == null || true) viewModelScope.launch {
            marketPlacesRepository.freightEstimate(productId, quantity, skuId)
                ?.let { _freightEstimateResponse.postValue(it) }
        }
    }

    fun feedItems(
        context: Context,
        searchText: String,
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        sortPrice: String
    ) {
        if (_feedItems == null || true) viewModelScope.launch {
            marketPlacesRepository.getFeedItems(
                searchText,
                categoryId,
                pageSize,
                pageIndex,
                sortPrice
            )?.let { _feedItems.postValue(it) }
        }
    }

    fun searchProducts(
        context: Context,
        searchText: String,
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        minPrice: String,
        maxPrice: String
    ) {
//        userMomentsRepo.getMultiUserStories(context, token) { success, stories, message ->
//            if (success == 0) {
//
//                _errorMessage.postValue(message)
//            } else {
//                _stories.postValue(stories)
//            }
//        }

        if (_marketPlaces == null || true) viewModelScope.launch {
            marketPlacesRepository.searchProductsAPI(
                searchText, categoryId, pageSize, pageIndex, minPrice,
                maxPrice
            )?.let {
                _marketPlaces.postValue(it)
            }
        }
    }

    fun getCategories() {
        if (_getAllCategoriesResponse == null || true) viewModelScope.launch {
            marketPlacesRepository.categoryAPI()?.let {
                _getAllCategoriesResponse.postValue(it)
            }
        }
    }

    fun getChildrenCategories(categoryId: String) {
        _getChildrenCategoriesResponse = MutableLiveData<ArrayList<GetChildrenCategoriesResponse>>()
        if (_getChildrenCategoriesResponse == null || true) viewModelScope.launch {
            marketPlacesRepository.getChildrenCategoriesApi(categoryId)?.let {
                _getChildrenCategoriesResponse.postValue(it)
            }
        }
    }

    fun productDetails(context: Context, productId: String, countryToShip: String) {
        if (_marketPlacesProduct == null || true) viewModelScope.launch {
            marketPlacesRepository.productDetailsAPI(productId, countryToShip)?.let {
                _marketPlacesProduct.postValue(it)
            }
        }
    }
}