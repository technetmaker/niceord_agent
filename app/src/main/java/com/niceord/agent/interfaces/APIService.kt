package com.niceord.agent.interfaces

import com.niceord.agent.models.*
import com.niceord.agent.models.requestModels.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST

interface APIService {

    @POST("auth/login")
    fun login(@Body userData: LoginRequestModel): Call<CommonResponse>

    @POST("auth/forgotpassword")
    fun forgotpassword(@Body userData: ResetPasswordRequestModel): Call<CommonResponse>

    @POST("auth/register")
    fun register(@Body userData: RegistrationRequestModel): Call<CommonResponse>

    @GET("shop-type")
    fun getShopType(): Call<CommonResponseArray>

    @POST("sitting-system")
    fun getSittingSystem(@Query("shop_type_id") shop_type_id: String?): Call<CommonResponseArray>

    @POST("auth/post-categeory")
    fun postCategory(
        @Header("Authorization") Authorization: String,
        @Body image: RequestBody?
    ): Call<CommonResponse>

    @POST("auth/category/{category_id}/update")
    fun updateCategory(
        @Header("Authorization") Authorization: String,
        @Path("category_id") categoryId: Int,
        @Body image: RequestBody?
    ): Call<CommonResponse>

    @GET("auth/category/{category_id}/delete")
    fun deleteCategory(
        @Header("Authorization") Authorization: String,
        @Path("category_id") productId: Int
    ): Call<CommonResponse>

    @GET("auth/user-profile")
    fun getUserInfo(
        @Header("Authorization") Authorization: String
    ): Call<CommonResponse>

    @GET("auth/product/{product_id}/delete")
    fun deleteProduct(
        @Header("Authorization") Authorization: String,
        @Path("product_id") productId: Int
    ): Call<CommonResponse>

    @POST("auth/change-password")
    fun changePassword(
        @Header("Authorization") Authorization: String,
        @Body userData: ChangePasswordRequestModel
    ): Call<CommonResponseArray>

    @POST("auth/get-orderdetails")
    fun getOrderDetails(
        @Header("Authorization") Authorization: String,
        @Body userData: OrderDetailsRequestModel
    ): Call<CommonResponseArray>

    @POST("auth/product/{product_id}/update")
    fun updateProduct(
        @Header("Authorization") Authorization: String,
        @Path("product_id") productId: Int,
        @Body image: RequestBody?
    ): Call<CommonResponse>

    @POST("auth/get-categeory")
    fun getCategory(
        @Header("Authorization") Authorization: String,
        @Body userData: PostCategoryRequestModel
    ): Call<CommonResponseArray>

    @POST("auth/get-product")
    fun getProducts(
        @Header("Authorization") Authorization: String,
        @Body userData: PostProductRequestModel
    ): Call<CommonResponseArray>

    @POST("auth/get-order")
    fun getOrders(
        @Header("Authorization") Authorization: String,
        @Body userData: PostCategoryRequestModel
    ): Call<CommonResponseArray>

    @POST("auth/change-order-status")
    fun changeOrderStatus(
        @Header("Authorization") Authorization: String,
        @Body userData: ChangeOrderStatusModel
    ): Call<CommonResponse>

    @POST("auth/post-product")
    fun postProduct(
        @Header("Authorization") Authorization: String,
        @Body image: RequestBody?
    ): Call<CommonResponse>


    @POST("auth/resetpassword")
    fun resetpassword(
       // @Header("Authorization") Authorization: String,
        @Body userData: NewPasswordRequestModel
    ): Call<CommonResponseArray>
}