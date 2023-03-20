package com.niceord.agent.adapters

import com.niceord.agent.R
import com.niceord.agent.models.DataItem
import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class OrderDetailsAdapter (private val mList: ArrayList<DataItem?>?) : RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_details_layout, parent, false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList?.get(position)

        holder.productTV.text = ItemsViewModel?.product_title
        holder.quantityTV.text = ItemsViewModel?.quantity
        if(ItemsViewModel?.price == null ){
            holder.priceTV.text = "-"
        }else{
            holder.priceTV.text = ItemsViewModel.price
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productTV: TextView = itemView.findViewById(R.id.productTV)
        val priceTV: TextView = itemView.findViewById(R.id.priceTV)
        val quantityTV: TextView = itemView.findViewById(R.id.quantityTV)
    }

}
