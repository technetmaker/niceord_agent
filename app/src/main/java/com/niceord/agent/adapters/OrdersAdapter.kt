package com.niceord.agent.adapters

import com.niceord.agent.R
import com.niceord.agent.models.DataItem
import com.niceord.agent.utils.RandomBgPicker
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrdersAdapter (private val mContext: Context, private val mList: ArrayList<DataItem?>?, private val btnlistener: BtnClickListener) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_layout, parent, false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        val ItemsViewModel = mList?.get(position)

        holder.categoryTitleTV.text = ItemsViewModel!!.id.toString()
        holder.productQuantityTV.text = ItemsViewModel?.quantity
        //holder.productQuantityTV.visibility = View.GONE
        Glide.with(mContext).load(ItemsViewModel!!.orderdetails!![0]!!.categoryImage.toString()).placeholder(R.drawable.app_logo).into(holder.categoryImg)

        //holder.mainCV.setBackgroundResource(RandomBgPicker.getBgDrawable())
        if(ItemsViewModel.order_status == mContext.getString(R.string.pending)){
         holder.orderStatus.setTextColor(mContext.getColor(R.color.orange))
        }else if(ItemsViewModel.order_status == mContext.getString(R.string.reject)){
            holder.orderStatus.setTextColor(mContext.getColor(R.color.gradient_top_color_red))
        }else if(ItemsViewModel.order_status == mContext.getString(R.string.deliver)){
            holder.orderStatus.setTextColor(mContext.getColor(R.color.grey))
        }else{
            holder.orderStatus.setTextColor(mContext.getColor(R.color.green))
        }
        holder.orderStatus.text = ItemsViewModel.order_status

        holder.itemView.setOnClickListener(){
            if (mClickListener != null){
                if (ItemsViewModel != null) {
                    mClickListener!!.onBtnClick(ItemsViewModel)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val categoryTitleTV: TextView = itemView.findViewById(R.id.categoryTitleTV)
        val productQuantityTV: TextView = itemView.findViewById(R.id.productQuantityTV)
        val mainCV: CardView = itemView.findViewById(R.id.mainCV)
        val categoryImg: ImageView = itemView.findViewById(R.id.categoryImg)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
    }

    interface BtnClickListener {
        fun onBtnClick(dataItem: DataItem)
    }
}
