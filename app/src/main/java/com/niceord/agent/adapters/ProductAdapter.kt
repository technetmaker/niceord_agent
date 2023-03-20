package com.niceord.agent.adapters

import com.niceord.agent.R
import com.niceord.agent.models.DataItem
import com.niceord.agent.utils.RandomBgPicker
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter (private val mContext: Context, private val mList: ArrayList<DataItem?>?, private var filteredUserList: java.util.ArrayList<DataItem?>?, val btnlistener: BtnClickListener) : RecyclerView.Adapter<ProductAdapter.ViewHolder>(),
    Filterable {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categories_details_items, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        val ItemsViewModel = filteredUserList?.get(position)

        if (ItemsViewModel != null) {
            holder.productTitleTV.text = ItemsViewModel.product_title

            holder.productQuantityTV.text =  ItemsViewModel.product_quantity
            holder.productPriceTV.text = ItemsViewModel.product_price
            holder.mainCV.setBackgroundResource(RandomBgPicker.getBgDrawable())
            holder.itemView.setOnClickListener() {
                if (mClickListener != null)
                    ItemsViewModel.let { it1 -> mClickListener?.onBtnClick(it1) }
            }
            holder.deleteLL.setOnClickListener() {
                if (mClickListener != null)
                    ItemsViewModel.let { it1 -> mClickListener?.onDelete(it1) }
            }
            Glide.with(mContext).load(ItemsViewModel.product_image).placeholder(R.drawable.animate_placeholder_image_layout).into(holder.productImg)

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return filteredUserList!!.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productTitleTV: TextView = itemView.findViewById(R.id.productTitleTV)
        val productQuantityTV: TextView = itemView.findViewById(R.id.productQuantityTV)
        val productPriceTV: TextView = itemView.findViewById(R.id.productPriceTV)
        val productImg: ImageView = itemView.findViewById(R.id.productImg)
        val mainCV: CardView = itemView.findViewById(R.id.mainCV)
        val deleteLL: LinearLayout = itemView.findViewById(R.id.deleteLL)
    }

    open interface BtnClickListener {
        fun onBtnClick(dataItem: DataItem)
        fun onDelete(dataItem: DataItem)
        fun onEmptyFilter(data: String?)
    }
    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val searchString = charSequence.toString()
                if (searchString.isEmpty()) {
                    filteredUserList = mList
                } else {
                    val tempFilteredList: java.util.ArrayList<DataItem?> = java.util.ArrayList()
                    if (mList != null) {
                        for (list in mList) {
                            if (list?.product_title?.toLowerCase()?.contains(searchString) == true) {
                                tempFilteredList.add(list)
                            }
                        }
                    }
                    filteredUserList = tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredUserList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredUserList = filterResults.values as java.util.ArrayList<DataItem?>
                notifyDataSetChanged()
                if(filteredUserList!!.size == 0){
                    if(mClickListener != null){
                        mClickListener?.onEmptyFilter("")
                    }
                }else{
                    if(mClickListener != null){
                       mClickListener?.onEmptyFilter("noEmpty")
                    }
                }
            }
        }
    }
}