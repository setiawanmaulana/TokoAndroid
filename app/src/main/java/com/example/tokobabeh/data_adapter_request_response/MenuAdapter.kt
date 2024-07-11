import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokobabeh.PrefManager
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.data_adapter_request_response.*
import com.example.tokobabeh.databinding.ListMenuBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MenuAdapter(
    private val listMenu: ArrayList<MenuData>,
    private var selectedMenus: ArrayList<MenuData>,
    private val txtTotalPrice: TextView
) : RecyclerView.Adapter<MenuAdapter.CartViewHolder>() {

    private var totalPrice: Double = 0.0

    private fun updateTotalPrice(price: Double) {
        txtTotalPrice.text = "Rp. ${price.toString().replace(".0","")}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(listMenu[position])
    }

    override fun getItemCount(): Int = listMenu.size

    inner class CartViewHolder(private val binding: ListMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                plusCart.setOnClickListener {
                    val menuData = listMenu[adapterPosition]
                    addToCart(menuData)
                }

                minCart.setOnClickListener {
                    val menuData = listMenu[adapterPosition]
                    removeFromCart(menuData)
                }
            }
        }

        fun bind(menuData: MenuData) {
            with(binding) {
                val localeID = Locale("id", "ID")
                val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                val format = formatRupiah.format(menuData.price)
                val price = format.replace(",00", "")

                titleCart.text = menuData.name
                priceCart.text = price
                val url = "http://192.168.0.122:8000/${menuData.image}"
                Glide
                    .with(root.context)
                    .load(url)
                    .centerCrop()
                    .into(imageCart)
            }
        }

        private fun addToCart(menuData: MenuData) {
            val prefManager = PrefManager(binding.root.context)
            val token = "Bearer ${prefManager.getToken()}"
            val idCart = prefManager.getIdCart()?.toInt()
            val menuId = menuData.id
            val qty = 1

            val requestCartAdd = RequestCartAdd(menuId, qty)
            ApiClient.instances.postCart(token, idCart, requestCartAdd).enqueue(object : Callback<ResponseCartAdd> {
                override fun onResponse(call: Call<ResponseCartAdd>, response: Response<ResponseCartAdd>) {
                    if (response.isSuccessful) {
                        val dataCartDetail = response.body()?.detail
                        val dataMenu = response.body()?.menu

                        dataCartDetail?.let {
                            binding.qtyCart.text = it.qty.toString()
                        }
                        dataMenu?.let { selectedMenus.add(it) }

                        totalPrice += menuData.price
                        updateTotalPrice(totalPrice)
                    } else {
                        Toast.makeText(binding.root.context, response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseCartAdd>, t: Throwable) {
                    Toast.makeText(binding.root.context, "Throwable : " + t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            })
        }

        private fun removeFromCart(menuData: MenuData) {
            if (binding.qtyCart.text != "0") {
                val prefManager = PrefManager(binding.root.context)
                val token = "Bearer ${prefManager.getToken()}"
                val idCart = prefManager.getIdCart()?.toInt()
                val menuId = menuData.id
                val qty = 1

                val requestReduceCart = RequestReduceCart(menuId, qty)
                ApiClient.instances.reduceCart(token, idCart, requestReduceCart).enqueue(object : Callback<ResponseReduceCart> {
                    override fun onResponse(call: Call<ResponseReduceCart>, response: Response<ResponseReduceCart>) {
                        if (response.isSuccessful) {
                            val dataCartDetail = response.body()?.detail

                            dataCartDetail?.let {
                                binding.qtyCart.text = it.qty.toString()
                            }

                            val menuToRemove = selectedMenus.find { it.id == menuId }
                            menuToRemove?.let {
                                selectedMenus.remove(it)
                            }

                            totalPrice -= menuData.price
                            updateTotalPrice(totalPrice)
                        } else {
                            Toast.makeText(binding.root.context, response.errorBody().toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseReduceCart>, t: Throwable) {
                        Toast.makeText(binding.root.context, "Throwable : " + t.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })
            }
        }
    }
}