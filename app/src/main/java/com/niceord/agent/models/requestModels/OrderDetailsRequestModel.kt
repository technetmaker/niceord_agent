package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class OrderDetailsRequestModel(
    @SerializedName("order_id") var order_id: String?
)