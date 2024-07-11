package com.example.tokobabeh.fragment

import MenuAdapter
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokobabeh.InvoiceActivity
import com.example.tokobabeh.PrefManager
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.databinding.FragmentTransactionBinding
import com.example.tokobabeh.data_adapter_request_response.MenuData
import com.example.tokobabeh.data_adapter_request_response.ResponseDataMenu
import com.example.tokobabeh.data_adapter_request_response.ResponseSubmitCart
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val listMenu = ArrayList<MenuData>()
    private var selectedMenus = ArrayList<MenuData>()
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTransactionBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.txtCari.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                getDataCart()
                return@setOnKeyListener true
            }
            false
        }

        binding.btnBayar.setOnClickListener{
            submitCart()
        }

        return view
    }

    private fun submitCart() {
        if(binding.txtPriceTotal.text == "Rp. 0"){
            Toast.makeText(context,"Mohon pilih item terlebih dahulu !",Toast.LENGTH_LONG).show()
        }else{
            val token = "Bearer ${prefManager.getToken()}"
            val cartId = prefManager.getIdCart()?.toInt()
            val priceTotal = binding.txtPriceTotal.text.toString().replace("Rp. ","").toDouble()

            ApiClient.instances.submitCart(token,cartId,priceTotal).enqueue(object: Callback<ResponseSubmitCart>{
                override fun onResponse(
                    call: Call<ResponseSubmitCart>,
                    response: Response<ResponseSubmitCart>
                ) {
                    if (response.isSuccessful){
                        Intent(context,InvoiceActivity::class.java).also {
                            startActivity(it)
                        }
                        binding.txtPriceTotal.setText("Rp. 0")
                    }else{
                        Toast.makeText(context,response.errorBody().toString(),Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseSubmitCart>, t: Throwable) {
                    Toast.makeText(context,"Throwable :" + t.localizedMessage,Toast.LENGTH_LONG).show()
                }

            })

        }
    }

    override fun onStart() {
        super.onStart()
        getDataCart()
        selectedMenus?.removeAll(selectedMenus)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())
    }

    private fun getDataCart() {
        binding.rvData.setHasFixedSize(true)
        binding.rvData.layoutManager = LinearLayoutManager(context)

        val token = "Bearer ${prefManager.getToken()}"
        binding.progressBar.visibility
        ApiClient.instances.getData(token).enqueue(object : Callback<ResponseDataMenu> {
            override fun onResponse(
                call: Call<ResponseDataMenu>,
                response: Response<ResponseDataMenu>
            ) {
                if (response.isSuccessful) {
                    listMenu.clear()
                    response.body()?.let { responseData ->
                        val searchData = if (binding.txtCari.text.isNullOrEmpty()) {
                            responseData.data
                        } else {
                            responseData.data.filter { it.name.contains(binding.txtCari.text, ignoreCase = true) }
                        }
                        listMenu.addAll(searchData)
                    }
                    binding.progressBar.isVisible = false
                    val adapter = MenuAdapter(listMenu, selectedMenus,binding.txtPriceTotal)
                    binding.rvData.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else{
                    val JsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Toast.makeText(requireContext(),"${JsonObj.getString("message")}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseDataMenu>, t: Throwable) {
                Toast.makeText(context, "Throwable : " + t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }

}
