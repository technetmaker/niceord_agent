package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestModel(
    @SerializedName("old_password") var old_password: String?,
    @SerializedName("new_password") var new_password: String?,
    @SerializedName("confirm_password") var confirm_password: String?
)