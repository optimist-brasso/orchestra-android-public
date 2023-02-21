package com.co.brasso.feature.shared.adapter.cartList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.co.brasso.databinding.AdapterCartListBinding
import com.co.brasso.feature.shared.adapter.viewholder.CartListViewHolder
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

class CartListAdapter(
    var cartListResponse: List<CartListResponse>?,
    var onItemClicked: (purchaseItem: CartListResponse?) -> Unit,
    var onItemAdd: (purchaseItem: CartListResponse?) -> Unit
) : RecyclerView.Adapter<CartListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        val binding =
            AdapterCartListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CartListViewHolder(binding,{
            onItemClicked(cartListResponse?.get(it ?: 0))
        },{
            onItemAdd(cartListResponse?.get(it ?: 0))
        })
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.bindData(cartListResponse?.get(position))
    }

    override fun getItemCount() = cartListResponse?.size ?: 0

}