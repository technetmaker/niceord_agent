package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class PostCategoryRequestModel(
    @SerializedName("user_id") var user_id: String?,
    @SerializedName("category_name") var category_name: String?,
    @SerializedName("category_image") var category_image: String?,
)