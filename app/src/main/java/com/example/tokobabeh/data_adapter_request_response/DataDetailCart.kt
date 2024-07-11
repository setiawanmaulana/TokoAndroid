package com.example.tokobabeh.data_adapter_request_response

data class DataDetailCart(
    var id: Int = 0,
    var cartId: Int = 0,
    var menuId: Int = 0,
    var qty: Int = 0,
    var subtotal: Double? = null,
    var priceMenu: Double? = null
)