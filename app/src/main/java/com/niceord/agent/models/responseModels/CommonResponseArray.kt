package com.niceord.agent.models

import com.google.gson.annotations.SerializedName

data class CommonResponseArray(

    @field:SerializedName("data")
    val data: ArrayList<DataItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class DataItem(

    @field:SerializedName("orderdetails")
    val orderdetails: List<OrderdetailsItem?>? = null,

    @field:SerializedName("updated_at")
    val updatedAt: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("order_type")
    val order_type: String? = null,
    @field:SerializedName("address")
    val address: String? = null,
    @field:SerializedName("category_name")
    val category_name: String? = null,
    @field:SerializedName("category_image")
    val category_image: String? = null,

    @field:SerializedName("product_title")
    val product_title: String? = null,

    @field:SerializedName("category_id")
    val category_id: String? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("product_quantity")
    val product_quantity: String? = null,

    @field:SerializedName("product_price")
    val product_price: String? = null,

    @field:SerializedName("product_image")
    val product_image: String? = null,

    @field:SerializedName("order_status")
    val order_status: String? = null,

    @field:SerializedName("sub_total")
    val sub_total: String? = null,
    @field:SerializedName("price")
    val price: String? = null,
    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("first_name")
    val first_name: String? = null,

    @field:SerializedName("last_name")
    val last_name: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("sitting_type_1")
    val sitting_type_1: String? = null,

    @field:SerializedName("sitting_type_2")
    val sitting_type_2: String? = null,

    @field:SerializedName("order_id")
    val order_id: Int? = null,


    )

data class OrderdetailsItem(

    @field:SerializedName("category_image")
    val categoryImage: String? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("category_id")
    val categoryId: String? = null,

    @field:SerializedName("sub_total")
    val subTotal: String? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("product_title")
    val productTitle: String? = null
)
