package com.niceord.agent.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonResponse(

	@field:SerializedName("access_token")
	@Expose
	val accessToken: String? = null,

	@field:SerializedName("data")
	@Expose
	val data: Data? = null,

	@field:SerializedName("success")
	@Expose
	val success: Boolean? = null,

	@field:SerializedName("message")
	@Expose
	val message: String? = null,

	@field:SerializedName("token_type")
	@Expose
	val tokenType: String? = null,

	@field:SerializedName("expires_in")
	@Expose
	val expiresIn: Int? = null
)

data class Data(

	@field:SerializedName("category_id")
	@Expose
	val category_id: Int? = null,

	@field:SerializedName("is_active")
	@Expose
	val isActive: String? = null,

	@field:SerializedName("user_type")
	@Expose
	val userType: String? = null,

	@field:SerializedName("updated_at")
	@Expose
	val updatedAt: String? = null,

	@field:SerializedName("mobile")
	@Expose
	val mobile: String? = null,

	@field:SerializedName("email")
	@Expose
	val email: String? = null,

	@field:SerializedName("otp")
	@Expose
	val otp: String? = null,

	@field:SerializedName("shop_type")
	@Expose
	val shopType: String? = null,

	@field:SerializedName("last_name")
	@Expose
	val lastName: String? = null,

	@field:SerializedName("sitting_system")
	@Expose
	val sittingSystem: String? = null,

	@field:SerializedName("created_at")
	@Expose
	val createdAt: String? = null,

	@field:SerializedName("id")
	@Expose
	val id: Int? = null,

	@field:SerializedName("first_name")
	@Expose
	val firstName: String? = null,


	@field:SerializedName("url")
	@Expose
	val url: String? = null
)
