package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class RegistrationRequestModel(
    @SerializedName("first_name") var first_name: String?,
    @SerializedName("last_name") var last_name: String?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("shop_type") var shop_type: String?,
    @SerializedName("sitting_system") var sitting_system: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("password_confirmation") var password_confirmation: String?,
    @SerializedName("device_token") var device_token: String?
)
