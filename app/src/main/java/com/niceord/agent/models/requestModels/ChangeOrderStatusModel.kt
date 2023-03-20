package com.niceord.agent.models.requestModels

import com.google.gson.annotations.SerializedName

data class ChangeOrderStatusModel(
    @SerializedName("order_status") var order_status: String?,
    @SerializedName("order_id") var order_id: String?,
)
