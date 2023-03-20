package com.niceord.agent.adapters

import com.niceord.agent.R
import com.niceord.agent.models.DataItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DialogPopUpAdapter (private val mList: ArrayList<DataItem?>?, val btnlistener: BtnClickListener) : RecyclerView.Adapter<DialogPopUpAdapter.ViewHolder>() {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shope_type_items, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        val ItemsViewModel = mList?.get(position)

        if (ItemsViewModel != null) {
            holder.textView.text = ItemsViewModel.title
        }

        if(position==(itemCount -1)){
            holder.viewLine.visibility = View.GONE
        }

        holder.clickableLayout.setOnClickListener(){
                if (mClickListener != null)
                    ItemsViewModel?.let { it1 -> mClickListener?.onBtnClick(it1) }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.labelTV)
        val viewLine: View = itemView.findViewById(R.id.viewLineVW)
        val clickableLayout: View = itemView.findViewById(R.id.mainLL)
    }
    open interface BtnClickListener {
        fun onBtnClick(dataItem: DataItem)
    }
}