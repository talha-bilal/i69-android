package com.i69.data.remote.repository

import com.i69.data.models.market.FreightEstimateResponse
import com.i69.data.models.market.GetAllCategoriesResponse
import com.i69.data.models.market.GetChildrenCategoriesResponse
import com.i69.data.models.market.ProductDetails
import com.i69.data.models.market.Products
import com.i69.data.models.market.SearchProducts
import com.i69.data.remote.api.GraphqlApi
import com.i69.di.modules.AppModule
import com.i69.ui.screens.main.moment.db.MomentDao
import com.i69.utils.getResponse
import javax.inject.Inject

class MarketPlacesRepository @Inject constructor(
    private val api: GraphqlApi, private val momentsDao: MomentDao
) {

    suspend fun addCartItem(
        buyerId: String, productId: String,
        skuId: String, quantity: String
    ): String? {
        val queryName = "addCartItem"
        var queryNew = ""

        queryNew = buildString {
            append("mutation {")
            append("  $queryName(input: {")
            append("    buyerId: \"$buyerId\",")
            append("    productId: \"$productId\",")
            append("    skuId: \"$skuId\",")
            append("    quantity: \"$quantity\"")
            append("  }) {")
            append("    response {")
            append("      status")
            append("      message")
            append("    }")
            append("  }")
            append("}")
        }

        val result = AppModule.provideGraphqlApiMarket().getResponse<String>(
            queryNew, queryName, "userToken"
        ).data?.data
        return result
    }

    suspend fun getCart(buyerId: String) {
        val queryName = "getCart"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  $queryName(buyerId:\"$buyerId\") {")
            append("    cartId")
            append("    buyerId")
            append("    currency")
            append("    createdAt")
            append("    cartItems {")
            append("      productId")
            append("      quantity")
            append("      productName")
            append("      productImage")
            append("      price")
            append("      discountedPrice")
            append("      productUrl")
            append("      itemPrices")
            append("      createdAt")
            append("      skuAttr")
            append("    }")
            append("    cartTotal")
            append("  }")
            append("}")
        }
    }

    suspend fun freightEstimate(
        productId: String,
        quantity: String,
        skuId: String
    ): ArrayList<FreightEstimateResponse>? {
        val queryName = "freightEstimate"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  freightEstimate(productId:\"$productId\", quantity:\"$quantity\", skuId:\"$skuId\") {")
            append("    code")
            append("    freeShipping")
            append("    shipFromCountry")
            append("    company")
            append("    mayHavePfs")
            append("    shippingFeeCent")
            append("    tracking")
            append("    estimatedDeliveryTime")
            append("    shippingFeeFormat")
            append("    maxDeliveryDays")
            append("    deliveryDateDesc")
            append("    minDeliveryDays")
            append("    guaranteedDeliveryDays")
            append("    ddpIncludeVatTax")
            append("    shippingFeeCurrency")
            append("  }")
            append("}")
        }

        val result =
            AppModule.provideGraphqlApiMarket().getResponse<ArrayList<FreightEstimateResponse>>(
                queryNew, queryName, "userToken"
            ).data?.data
        return result
    }

    suspend fun searchImage(imageString: String): ArrayList<Products>? {
        val queryName = "imageSearch"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  $queryName(")

            append("shptTo:\"US\",")
            append("sort:\"RELEVANCE_ASC\",")
            append("imageFileBytes:\"$imageString\"")

            append(") {")
            append("    productId")
            append("    title")
            append("    currency")
            append("    currencySymbol")
            append("    price")
            append("    markupPrice")
            append("    discountedPrice")
            append("    resellPrice")
            append("    imageUrl")
            append("    detailUrl")
            append("    shopUrl")
            append("    firstLevelCategoryName")
            append("    secondLevelCategoryName")
            append("    score")
            append("    discount")
            append("  }")
            append("}")
        }

        val result = AppModule.provideGraphqlApiMarket().getResponse<ArrayList<Products>>(
            queryNew, queryName, "userToken"
        ).data?.data
        return result
    }

    suspend fun getFeedItems(
        searchText: String,
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        sortPrice: String
    ): ArrayList<Products>? {

        val queryName = "feedItems"
        var queryNew = ""

        queryNew = buildString {
            append("query {")
            append("  $queryName(feedName: \"3SproductsUS\"")

            if (categoryId!!.isNotEmpty()) {
                append(", categoryId:\"$categoryId\"")
            }
            if (sortPrice.isNotEmpty()) {
//                append(", minPrice:${minPrice.toFloat()}")
                append(", sort:\"${sortPrice}\"")
            }

            append(", pageSize:$pageSize, pageNo:$pageIndex) {")
            append("      productId")
            append("      title")
            append("      currency")
            append("      currencySymbol")
            append("      price")
            append("      markupPrice")
            append("      discountedPrice")
            append("      discount")
            append("      imageUrl")
            append("      detailUrl")
            append("      shopUrl")
            append("      firstLevelCategoryName")
            append("      score")
            append("      resellPrice")
            append("    }")
            append("}")
        }

        val result = AppModule.provideGraphqlApiMarket().getResponse<ArrayList<Products>>(
            queryNew,
            queryName, "userToken"
        ).data?.data
        return result
    }

    suspend fun searchProductsAPI(
        searchText: String,
        categoryId: String?,
        pageSize: Int,
        pageIndex: Int,
        minPrice: String,
        maxPrice: String
    ): SearchProducts? {

//        val queryName = "searchProducts"
        val queryName = "searchProductsStable"
        var queryNew = ""

        queryNew = buildString {
            append("mutation {")
            append("  searchProductsStable(searchText:\"$searchText\"")

            if (categoryId!!.isNotEmpty()) {
                append(", categoryId:\"$categoryId\"")
            }
            if (minPrice.isNotEmpty()) {
                append(", minPrice:${minPrice.toFloat()}")
            }
            if (maxPrice.isNotEmpty()) {
                append(", maxPrice:${maxPrice.toFloat()}")
            }

            append(", pageSize:$pageSize, pageIndex:$pageIndex) {")
            append("    products {")
            append("      productId")
            append("      title")
            append("      currency")
            append("      currencySymbol")
            append("      price")
            append("      markupPrice")
            append("      discountedPrice")
            append("      discount")
            append("      imageUrl")
            append("      detailUrl")
            append("      shopUrl")
            append("      firstLevelCategoryName")
            append("      score")
            append("      resellPrice")
            append("    }")
            append("  }")
            append("}")
        }

        val result = AppModule.provideGraphqlApiMarket().getResponse<SearchProducts>(
            queryNew,
            queryName, "userToken"
        ).data?.data
        return result
    }

    suspend fun productDetailsAPI(productId: String, countryToShip: String): ProductDetails? {

        val queryName = "productDetails"

        val query = "query productDetails{" +
                "  productDetails(productId:\"${productId}\",shipToCountry:\"${countryToShip}\"){" +
                "    itemInfo{" +
                "      productId" +
                "      mobileDetail" +
                "      subject" +
                "      categoryId" +
                "     gmtCreate" +
                "      salesCount" +
                "      categorySequence" +
                "      wsOfflineDate" +
                "      currencyCode" +
                "      detail" +
                "      wsDisplay" +
                "      avgEvaluationRating" +
                "    }" +
                "    skuInfo {" +
                "      skuAttr" +
                "      skuId" +
                "      availableStock" +
                "      resellPrice" +
                "      skuPrice" +
                "      markupPrice" +
                "      discountedPrice" +
                "      discountPercentage" +
                "      currency" +
                "      currencySymbol" +
                "      discountPercentage" +
                "      aeSkuPropertyDtos {" +
                "        skuPropertyValue" +
                "        skuImage" +
                "        skuPropertyName" +
                "        propertyValueDefinitionName" +
                "        propertyValueId" +
                "        skuPropertyId" +
                "      }" +
                "    }" +
                "    logisticsInfo {" +
                "      deliveryTime" +
                "      shipToCountry" +
                "    }" +
                "    storeInfo {" +
                "      storeId" +
                "      shippingSpeedRating" +
                "      communicationRating" +
                "      storeName" +
                "      storeCountryCode" +
                "      itemAsDescribedRating" +
                "    }" +
                "    packageInfo {" +
                "      packageWidth" +
                "      packageHeight" +
                "      packageLength" +
                "      grossWeight" +
                "      packageType" +
                "      productUnit" +
                "    }" +
                "    multimediaInfo{" +
                "      imageUrls" +
                "    }" +
                "    itemProperties {" +
                "      attrNameId" +
                "      attrValueId" +
                "      attrName" +
                "      attrValue" +
                "    }" +
                "  }" +
                "}"

        val result = AppModule.provideGraphqlApiMarket().getResponse<ProductDetails>(
            query,
            queryName, "userToken"
        ).data?.data
        return result
    }

    suspend fun categoryAPI(): ArrayList<GetAllCategoriesResponse>? {

        val queryName = "getAllCategories"

//        val query = "query {" +
//                "  getAllCategories(categoryId:\"201379402\") {" +
//                "    categoryId" +
//                "    categoryName" +
//                "    parentCategoryId" +
//                "  } " +
//                "}"

        val queryNew = "query{" +
                "  getAllCategories{" +
                "    categoryId" +
                "    categoryName" +
                "    parentCategoryId" +
                "  } " +
                "}"

        val result =
            AppModule.provideGraphqlApiMarket().getResponse<ArrayList<GetAllCategoriesResponse>>(
                queryNew,
                queryName, "userToken"
            ).data?.data
        return result
    }

    suspend fun getChildrenCategoriesApi(categoryId: String): ArrayList<GetChildrenCategoriesResponse>? {
        val queryName = "getChildrenCategories"

        val query = """
    query getChildrenCategories {
        getChildrenCategories(categoryId: "$categoryId") {
            categoryId
            categoryName
            parentCategoryId
            level
            isLeaf
        }
    }
""".trimIndent()

        val result = AppModule.provideGraphqlApiMarket()
            .getResponse<ArrayList<GetChildrenCategoriesResponse>>(
                query, queryName, "userToken"
            ).data?.data

        return result
    }
}