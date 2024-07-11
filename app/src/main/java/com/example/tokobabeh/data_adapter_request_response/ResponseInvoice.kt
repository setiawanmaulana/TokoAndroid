package com.example.tokobabeh.data_adapter_request_response

data class ResponseInvoice(
    var success: Boolean,
    var message: String,
    var data: DataCart?,
    var detail: List<DataDetailCart>,
    var menu: List<MenuData>
)
