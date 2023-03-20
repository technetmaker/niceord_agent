package com.niceord.agent.adapters

import com.niceord.agent.R
import com.niceord.agent.models.DataItem
import com.niceord.agent.utils.RandomBgPicker
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.*
import androidx.cardview.widget.CardView
import java.util.*

class CategoryAdapter (private val mContext: Context, private val mList: ArrayList<DataItem?>?, private var filteredUserList: ArrayList<DataItem?>?, private val btnlistener: BtnClickListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() ,
    Filterable {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categories_items, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        val ItemsViewModel = filteredUserList?.get(position)
        holder.categoryNameTV.text = ItemsViewModel!!.category_name
        Glide.with(mContext).load(ItemsViewModel.category_image.toString()).placeholder(R.drawable.animate_placeholder_image_layout).into(holder.categoryImg)

        holder.mainCV.setBackgroundResource(RandomBgPicker.getBgDrawable())

        holder.itemView.setOnClickListener(){
            if (mClickListener != null)
                ItemsViewModel.let { it1 -> mClickListener?.onBtnClick(it1) }
        }

        holder.updateLL.setOnClickListener(){
            if (mClickListener != null)
                ItemsViewModel.let { it1 -> mClickListener?.onUpdate(it1) }
        }

        holder.deleteLL.setOnClickListener(){
            if (mClickListener != null)
                ItemsViewModel.let { it1 -> mClickListener?.onDelete(it1) }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return filteredUserList!!.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val categoryNameTV: TextView = itemView.findViewById(R.id.categoryNameTV)
        val categoryImg: ImageView = itemView.findViewById(R.id.categoryImg)
        val mainCV: CardView = itemView.findViewById(R.id.mainCV)
        val updateLL: LinearLayout = itemView.findViewById(R.id.updateLL)
        val deleteLL: LinearLayout = itemView.findViewById(R.id.deleteLL)
    }

    interface BtnClickListener {
        fun onBtnClick(dataItem: DataItem)
        fun onUpdate(dataItem: DataItem)
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
                    val tempFilteredList: ArrayList<DataItem?> = ArrayList()
                    if (mList != null) {
                        for (list in mList) {
                            if (list?.category_name?.toLowerCase()?.contains(searchString) == true) {
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
                filteredUserList = filterResults.values as ArrayList<DataItem?>
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