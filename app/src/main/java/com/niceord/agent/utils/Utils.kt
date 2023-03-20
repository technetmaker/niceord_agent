package com.niceord.agent.utils

import com.niceord.agent.R
import com.niceord.agent.adapters.DialogPopUpAdapter
import com.niceord.agent.application.MainApplication
import com.niceord.agent.models.DataItem
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.util.regex.Pattern

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.app.Dialog
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Base64
import android.view.inputmethod.InputMethodManager

import android.widget.TextView
import androidx.annotation.RequiresApi

class Utils {

    companion object {
        const val COMMON_API_RESPONSE_DATA = "common_api_response_data"
        var comingFrom = "comingFrom"
        const val consumerNumber = "consumerNumber"
        var consumerOrderId = "consumerOrderId"
        var tapOnLogin = "tapOnLogin"
        var tapOnSingUp = "tapOnSingUp"
        var forgot = "forgot"
        var changePassword = "changePassword"
        var reset = "reset"
        var id = "id"
        var categoryName = "categoryName"
        const val getIntentValue = "getIntentValue"
        val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
        val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
        val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
        const val userTokenKey = "userTokenKey"
        const val userId = "userId"
        const val agentShopType = "agentShopType"
        const val agentFirstName = "agentFirstName"
        const val agentLastName = "agentLastName"
        const val agentMobileNumber = "agentMobileNumber"
        const val otp = "otp"
        const val emailID = "emailId"
        const val agentCategoryUrl = "agentCategoryUrl"
        const val deviceToken = "deviceToken"
        const val isActive = "isActive"
        const val isOrderNotification = "isOrderNotification"


        var dialog: Dialog? = null

        fun showProgress(context: Context?){
            dialog = Dialog(context!!)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.loading_progressbar_layout)
            dialog!!.show()
        }

        fun hideProgress(){
            dialog!!.hide()
            dialog!!.dismiss()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                        return true
                    }
                }
            }
            return false
        }

        fun removeZeroMobileNumer(mobileNumber: String): String {
            var mobileNumber = mobileNumber
            if (!TextUtils.isEmpty(mobileNumber)) {
                if (mobileNumber.startsWith("0")) {
                    mobileNumber = removeZeroMobileNumer(mobileNumber.substring(1))
                }

            }
            return mobileNumber
        }

        fun isValidEmail(email: String?): Boolean {
            val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$"
            val pat = Pattern.compile(emailRegex)
            return if (email == null) false else pat.matcher(email).matches()
        }

        fun showSnackBar(message: String?, view: View?) {
            try {
                if (view != null && view !is ScrollView && !TextUtils.isEmpty(message)) {
                    val snackbar = Snackbar.make(view, message!!, Snackbar.LENGTH_SHORT)
                    snackbar.show()
                } else if (!TextUtils.isEmpty(message)) {
                    showToastWithoutContext(message)
                }
            } catch (e: Exception) {
                showToastWithoutContext(message)
            }
        }

        private fun showToastWithoutContext(message: String?) {
            Toast.makeText(MainApplication.getApplicationInstance(), message, Toast.LENGTH_SHORT)
                .show()
        }

        fun showToast(context: Context?, text: String?) {
            Toast.makeText(
                context, text,
                Toast.LENGTH_LONG
            ).show()
        }

        fun errorDialogPopup(context: Context?, s: String){
            val dialogBuilder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(context)
            val inflater: LayoutInflater = LayoutInflater.from(context)
            dialogBuilder.setCancelable(false)

            val dialogView: View = inflater.inflate(R.layout.error_dialog_popup, null)
            dialogBuilder.setView(dialogView)
            val alertDialog: android.app.AlertDialog?

            val title = dialogView.findViewById<View>(R.id.title) as TextView
            val okBtn = dialogView.findViewById<View>(R.id.okBtn) as LinearLayout

            title.text = s

            alertDialog = dialogBuilder.create()

            alertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog?.show()
            alertDialog?.getWindow()?.setLayout(700, 800);


            okBtn.setOnClickListener() {
                alertDialog?.dismiss()
                alertDialog?.hide()
            }

        }

        fun dialogPopUp(context: Context?, mList: ArrayList<DataItem?>?) : String {
            val dialogBuilder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(context)
            val inflater: LayoutInflater = LayoutInflater.from(context)
            dialogBuilder.setCancelable(false)

            val dialogView: View = inflater.inflate(R.layout.error_dialog_popup, null)
            dialogBuilder.setView(dialogView)
            val alertDialog: android.app.AlertDialog?

            val popUpRV = dialogView.findViewById<View>(R.id.popUpRV) as RecyclerView
            val crossBtn = dialogView.findViewById<View>(R.id.crossBtn) as ImageView

            alertDialog = dialogBuilder.create()

            alertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog?.show()
            alertDialog?.getWindow()?.setLayout(700, 800);
            popUpRV.layoutManager = LinearLayoutManager(context)
            lateinit var selectedItem: String
            val adapter = DialogPopUpAdapter(mList, object : DialogPopUpAdapter.BtnClickListener {
                override fun onBtnClick(dataItem: DataItem) {
                    selectedItem = "Dfdf"
                    //SignUpFragment().shopType.setText("dfdfd")
                    alertDialog?.dismiss()
                    alertDialog?.hide()
                }

            })
            popUpRV.adapter = adapter

            crossBtn.setOnClickListener() {
                alertDialog?.dismiss()
                alertDialog?.hide()
            }
            return selectedItem
        }

        fun popUpMenuItem(context: Context?, view: View?) {
            var popup = PopupMenu(context, view)
            // popup.menuInflater.inflate(R.menu.shope_type_menu_items, popup.menu);

            popup.setOnMenuItemClickListener() {

                true
            }
            popup.show()

        }


        private fun getStringWithoutSpecialCharacter(s: String): String {
            return if (TextUtils.isEmpty(s)) "0" else s.replace("\\D".toRegex(), "")
        }

        fun getTenDigitMobileNo(contact_no: String?): String? {
            var contact: String = getStringWithoutSpecialCharacter(contact_no!!)
            return if (contact.length < 10) {
                println("updated contact$contact")
                contact
            } else {
                contact = contact.substring(contact.length - 10)
                println("updated contact$contact")
                contact
            }
        }

        fun showKeyboard(activity: Activity, editText: EditText){
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun checkForInternet(context: Context): Boolean {

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val network = connectivityManager.activeNetwork ?: return false

                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                    else -> false
                }
            } else {
                @Suppress("DEPRECATION") val networkInfo =
                    connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }
    }

}