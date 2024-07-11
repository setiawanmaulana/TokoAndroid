package com.example.tokobabeh.data_adapter_request_response

data class ResponseReduceCart(
    val success: Boolean,
    val message: String,
    val data: DataCart?,
    val detail: DataDetailCart?,
    val menu: MenuData?
)
