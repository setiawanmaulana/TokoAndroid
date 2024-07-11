package com.example.tokobabeh.data_adapter_request_response

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobabeh.databinding.ListCartBinding

class MenuDibeliAdapter(
    private val cartDetailList: List<DataDetailCart>,
    private val menuList: List<MenuData>
) : RecyclerView.Adapter<MenuDibeliAdapter.MenuDibeliViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDibeliViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListCartBinding.inflate(inflater, parent, false)
        return MenuDibeliViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartDetailList.size
    }

    override fun onBindViewHolder(holder: MenuDibeliViewHolder, position: Int) {
        val menuData = menuList[position]
        val cartDetail = cartDetailList[position]

        holder.binding.titleMenuDibeli.text = menuData.name
        holder.binding.priceMenuDibeli.text = menuData.price.toString()
        holder.binding.qtyMenuDibeli.text = cartDetail.qty.toString()
        holder.binding.subtMenuDibeli.text = cartDetail.subtotal.toString()
    }

    inner class MenuDibeliViewHolder(val binding: ListCartBinding) : RecyclerView.ViewHolder(binding.root)
}