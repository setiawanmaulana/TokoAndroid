package com.example.tokobabeh.api
import com.example.tokobabeh.data_adapter_request_response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.sql.DataTruncation

interface Api {
    @POST("login")
    fun userLogin(
        @Query("username")username:String,
        @Query("password")password:String
    ): Call<LoginResponse>

    @POST("register")
    fun userRegister(
        @Query("name")name:String,
        @Query("username")username:String,
        @Query("address")address:String,
        @Query("password")password: String,
        @Query("password_confirmation")confirm:String
    ): Call<RegisterResponse>

    @GET("menu")
    fun getData(
        @Header("Authorization") token: String
    ): Call<ResponseDataMenu>

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<UserData>

    @POST("cart/cart-add/{cart}")
        fun postCart(
        @Header("Authorization") token: String,
        @Path("cart") cart: Int?,
        @Body requestCartAdd: RequestCartAdd
    ):Call<ResponseCartAdd>

    @POST("cart/cart-reduced/{cart}")
    fun reduceCart(
        @Header("Authorization") token: String,
        @Path("cart") cart: Int?,
        @Body reduceCart: RequestReduceCart
    ):Call<ResponseReduceCart>

    @POST("cart/submit/{cart}")
    fun submitCart(
        @Header("Authorization") token: String,
        @Path("cart") cart: Int?,
        @Query("price_total") priceTotal: Double
    ):Call<ResponseSubmitCart>

    @GET("cart/invoice/{cart}")
    fun getCart(
        @Header("Authorization") token: String,
        @Path("cart") cart: Int?
    ):Call<ResponseInvoice>

    @POST("recreate/{user}")
    fun recreateCart(
        @Header("Authorization") token: String,
        @Path("user") user: Int?
    ):Call<ResponseRecreateCart>
}