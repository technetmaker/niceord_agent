package com.niceord.agent.globalyDialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.niceord.agent.R

class ConfirmationDialog: DialogFragment() {
    var listener: Listener? = null
    var messageText = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = this.activity?.let { AlertDialog.Builder(it) }
        val rootView = activity?.layoutInflater?.inflate(R.layout.confirmation_dailog_layout, null)
        isCancelable = false
        val title = rootView?.findViewById<View>(R.id.title) as TextView
        val okBtn = rootView.findViewById<View>(R.id.okBtn) as TextView
        val cancelBtn = rootView.findViewById<View>(R.id.cancelBtn) as TextView

        if (messageText.isNotBlank()) {
            title.text = messageText
        }

        cancelBtn.setOnClickListener {

            dismiss()
        }

 okBtn.setOnClickListener {
            if(messageText == getString(R.string.logout_confirmation_msg)){
                listener?.onLogoutTap()
                dismiss()
            }else if(messageText == getString(R.string.delete_category_confirmation_msg)){
                listener?.onCategoryDeteteTap()
                dismiss()
            }else if(messageText == getString(R.string.delete_product_confirmation_msg)){
                listener?.onProductDeleteTap()
                dismiss()
            }else if(messageText == getString(R.string.delete_confirmation_msg)){
                listener?.onLogoutTap()
                dismiss()
            }

        }

        builder!!.setView(rootView)
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    interface Listener {
        fun onLogoutTap()
        fun onCategoryDeteteTap()
        fun onProductDeleteTap()
    }
}