package com.example.tokobabeh.data_adapter_request_response

data class LoginResponse(
    val success: String,
    val message: String,
    val token: String,
    val cart: DataCart?
)
