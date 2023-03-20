package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class LoginRequestModel (
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("device_token") var device_token: String?
)

data class ResetPasswordRequestModel (
    @SerializedName("email") var email: String?,

)

data class NewPasswordRequestModel (
    @SerializedName("email") var email: String?,
    @SerializedName("otp") var otp: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("password_confirmation") var password_confirmation: String?,

    )