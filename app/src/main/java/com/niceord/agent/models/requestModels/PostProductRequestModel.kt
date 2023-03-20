package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class PostProductRequestModel(
    @SerializedName("category_id") var category_id: Int?,
    @SerializedName("product_title") var product_title: String?,
    @SerializedName("product_quantity") var product_quantity: String?,
    @SerializedName("product_price") var product_price: String?,
    @SerializedName("product_image") var product_image: String?,
)
