package com.niceord.agent.interfaces

import com.niceord.agent.models.*
import com.niceord.agent.models.requestModels.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {

    fun loginUser(userData: LoginRequestModel, onResult: (CommonResponse?) -> Unit){
        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        retrofit.login(userData).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    onResult(null)

                }
                override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }

    fun resetPassword(userData: ResetPasswordRequestModel, onResult: (CommonResponse?) -> Unit){
        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        retrofit.forgotpassword(userData).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    onResult(null)

                }
                override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }

    fun registerUser(userData: RegistrationRequestModel, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        retrofit.register(userData).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    onResult(null)

                }
                override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }

    fun getShopType(onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        retrofit.getShopType().enqueue(
            object : Callback<CommonResponseArray> {
                override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                    onResult(null)

                }
                override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }
    fun postCategory(userToken: String?,  categoryImage: RequestBody?, onResult: (CommonResponse?) -> Unit) {

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.postCategory(userToken, categoryImage).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse(
                        call: Call<CommonResponse>,
                        response: Response<CommonResponse>
                    ) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }


    fun updateCategory(userToken: String?, categoryId: Int?, categoryImage: RequestBody?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.updateCategory(userToken,categoryId!!.toInt(), categoryImage).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }
    fun updateProduct(userToken: String?, productId: Int?, categoryImage: RequestBody?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.updateProduct(userToken,productId!!.toInt(), categoryImage).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }

    fun deleteCategory(userToken: String?, category_id: Int?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.deleteCategory(userToken,category_id!!.toInt()).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }
    fun getUserInfo(userToken: String?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.getUserInfo(userToken).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }
    fun deleteProduct(userToken: String?, product_id: Int?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.deleteProduct(userToken,product_id!!.toInt()).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }
    fun postProduct(userToken: String?,  categoryImage: RequestBody?, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)

        if (userToken != null) {
            retrofit.postProduct(userToken, categoryImage).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }

                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }

    }
    fun getCategory(userToken: String?, userData: PostCategoryRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.getCategory(userToken, userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }
    fun getOrderDetails(userToken: String?, userData: OrderDetailsRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.getOrderDetails(userToken, userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        println("onResponse** ${response.body()}")
                        onResult(addedUser)

                    }
                }
            )
        }
    }

    fun changePassword(userToken: String?, userData: ChangePasswordRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.changePassword(userToken, userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }


    fun resetPassword(userToken: String?, userData: NewPasswordRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.resetpassword( userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }
    fun getProducts(userToken: String?, userData: PostProductRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.getProducts(userToken, userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }
 fun getOrders(userToken: String?, userData: PostCategoryRequestModel, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.getOrders(userToken, userData).enqueue(
                object : Callback<CommonResponseArray> {
                    override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }
 fun changeOrderStatus(userToken: String?, userData: ChangeOrderStatusModel, onResult: (CommonResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        if (userToken != null) {
            retrofit.changeOrderStatus(userToken, userData).enqueue(
                object : Callback<CommonResponse> {
                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onResult(null)

                    }
                    override fun onResponse( call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        val addedUser = response.body()
                        onResult(addedUser)

                    }
                }
            )
        }
    }

    fun getSittingSystem(shopType: String, onResult: (CommonResponseArray?) -> Unit){

        val retrofit = ServiceBuilder.buildService(APIService::class.java)
        retrofit.getSittingSystem(shopType).enqueue(
            object : Callback<CommonResponseArray> {
                override fun onFailure(call: Call<CommonResponseArray>, t: Throwable) {
                    onResult(null)

                }
                override fun onResponse( call: Call<CommonResponseArray>, response: Response<CommonResponseArray>) {
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }
}