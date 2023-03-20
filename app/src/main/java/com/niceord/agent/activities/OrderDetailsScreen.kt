package com.niceord.agent.activities

import com.niceord.agent.R
import com.niceord.agent.adapters.OrderDetailsAdapter
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.models.DataItem
import com.niceord.agent.models.requestModels.ChangeOrderStatusModel
import com.niceord.agent.utils.Utils
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Bitmap
import android.text.format.DateFormat
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import com.niceord.agent.databinding.ActivityOrderDetailsScreenBinding
import com.niceord.agent.models.requestModels.OrderDetailsRequestModel


class OrderDetailsScreen : AppCompatActivity() {
    lateinit var toolbarLabel: TextView
    lateinit var downloadOrderBtn: ImageView
    lateinit var toolbarBackArrowImg: ImageView
    var sharedPreference: MySharedPreference? = null
    var getOrderList: ArrayList<DataItem?>? = null
    lateinit var binding: ActivityOrderDetailsScreenBinding

    //below variables for download Order list
    var pageHeight = 1120
    var pageWidth = 792
    var orderId: String? = null
    lateinit var bmp: Bitmap
    lateinit var scaledbmp: Bitmap
    var PERMISSION_CODE = 101
    var totalCheckoutValue: MutableList<Int> = mutableListOf<Int>()
    var sum = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarLabel = binding.toolbarLayout.toolbarLabelTV
        downloadOrderBtn = binding.toolbarLayout.addBtn
        toolbarBackArrowImg = binding.toolbarLayout.toolbarBackArrowBtn
        sharedPreference = MySharedPreference((this))
        onClicksLisnter()
        initialize()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initialize() {

        getOrderDetails()

        bmp = loadBitmapFromView(binding.printView, 300, 700)

        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicksLisnter() {

        toolbarBackArrowImg.setOnClickListener {
            finish()
        }
        downloadOrderBtn.setOnClickListener {
            if (getOrderList != null) {
                if (checkPermissions()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        takeScreenshot()
                    }
                } else {
                    requestPermission()
                }
            }
        }
        binding.changeStatusBtn.setOnClickListener {
            var orderStatusPosition: String? = null
            for (i in 0 until getOrderList!!.size) {
                orderStatusPosition = getOrderList!![i]!!.order_status
            }
            if (orderStatusPosition == getString(R.string.pending)) {
                changeOrderStatus(getString(R.string.accept))
            } else if (orderStatusPosition == getString(R.string.accept)) {
                changeOrderStatus(
                    getString(R.string.preparing)
                )
            } else if (orderStatusPosition == getString(R.string.preparing)) {
                changeOrderStatus(getString(R.string.ready_to_deliver)
                )
            } else if (orderStatusPosition == getString(R.string.ready_to_deliver)) {
                changeOrderStatus(
                    getString(R.string.deliver)
                )
            } else {
                println("item delivered")
            }

        }
        binding.rejectOrderBtn.setOnClickListener {
            var orderIdPosition: Int? = null
            for (i in 0 until getOrderList!!.size) {
                orderIdPosition = getOrderList!![i]!!.id
            }

            changeOrderStatus(getString(R.string.reject))

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrderDetails() {
        if (Utils.isOnline(this@OrderDetailsScreen)) {
            Utils.showProgress(this@OrderDetailsScreen)
            val apiService = RestApiService()
            val userData = OrderDetailsRequestModel(
                order_id = intent.getStringExtra(Utils.consumerOrderId)
            )
            apiService.getOrderDetails(
                "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),
                userData
            ) {
                if (it?.success == true) {
                    Utils.hideProgress()
                    getOrderList = it.data
                    calculateTotalValue();
                    var sub_totalPosition: String? = null
                    var first_namePosition: String? = null
                    var last_namePosition: String? = null
                    var order_statusPosition: String? = null
                    var consumerMobNumber: String? = null
                    var address: String? = null
                    var orderType: String? = null
                    var consumerSittingType1: String? = null
                    var consumerSittingType2: String? = null
                    for (i in 0 until getOrderList!!.size) {
                        sub_totalPosition = getOrderList!![i]!!.sub_total
                        first_namePosition = getOrderList!![i]!!.first_name
                        last_namePosition = getOrderList!![i]!!.last_name
                        order_statusPosition = getOrderList!![i]!!.order_status
                        consumerMobNumber = getOrderList!![i]!!.mobile
                        address = getOrderList!![i]!!.address
                        orderType = getOrderList!![i]!!.order_type
                        consumerSittingType1 = getOrderList!![i]!!.sitting_type_1
                        consumerSittingType2 = getOrderList!![i]!!.sitting_type_2
                        orderId = getOrderList!![i]!!.order_id.toString()
                        toolbarLabel.text = "Order Id: "+orderId.toString()

                    }
                    binding.subTotalTV.text = totalCheckoutValue.sum().toString() + " Rs."
                    binding.orderTypeTV.text = orderType.toString()
                    binding.addressTV.text = address.toString()
                    if(!orderType.equals("Delivery")){
                        binding.addressLayout.visibility =  View.GONE
                    }
                    binding.customerNameTV.text = first_namePosition.toString()+" "+last_namePosition.toString()
                    binding.customerNumberTV.text = "+91"+consumerMobNumber.toString()
                    if(consumerSittingType1.toString() != "null"){
                    binding.customerSittingType1TV.text = consumerSittingType1.toString()
                    }else{
                        binding.customerSittingType1TV.visibility = View.GONE
                    }

                   // binding.customerSittingType2TV.text = consumerSittingType2.toString()
                    changeButtonText(order_statusPosition)

                    binding.ordersDetialsRV.layoutManager = LinearLayoutManager(this)
                    val adapter = OrderDetailsAdapter(getOrderList)
                    binding.ordersDetialsRV.adapter = adapter
                    binding.printView.visibility = View.VISIBLE
                    binding.subTotalLL.visibility = View.VISIBLE
                } else {
                    Utils.hideProgress()

                    // Utils.showSnackBar(it?.message, binding.mainLayout)

                }
            }
        } else {
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeOrderStatus(status: String?) {

        val apiService = RestApiService()
        val userData = ChangeOrderStatusModel(
            order_id = orderId,
            order_status = status,

            )
        apiService.changeOrderStatus(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),
            userData
        ) {
            if (it?.success == true) {
                getOrderDetails()

            }
        }
    }

    private fun changeButtonText(text: String?) {
        binding.changeStatusBtn.visibility = View.VISIBLE
        if (text.toString() == getString(R.string.pending)) {
            binding.rejectOrderBtn.visibility = View.VISIBLE
        } else {
            binding.rejectOrderBtn.visibility = View.GONE
        }
        if (text == getString(R.string.pending)) {
            binding.changeStatusBtn.setBackgroundResource(R.drawable.accept_order_btn_shape)
            binding.changeStatusBtn.text = getString(R.string.accept)
        } else if (text == getString(R.string.accept)) {
            binding.toolbarLayout.addBtn.visibility = View.VISIBLE
            downloadOrderBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
            binding.changeStatusBtn.setBackgroundResource(R.drawable.round_btn_shape)
            binding.changeStatusBtn.text = getString(R.string.preparing)
        } else if (text == getString(R.string.preparing)) {
            binding.toolbarLayout.addBtn.visibility = View.VISIBLE
            downloadOrderBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
            binding.changeStatusBtn.setBackgroundResource(R.drawable.round_btn_shape)
            binding.changeStatusBtn.text = getString(R.string.ready_to_deliver)
        } else if (text == getString(R.string.ready_to_deliver)) {
            binding.toolbarLayout.addBtn.visibility = View.VISIBLE
            downloadOrderBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
            binding.changeStatusBtn.setBackgroundResource(R.drawable.round_btn_shape)
            binding.changeStatusBtn.text = getString(R.string.deliverr)
        } else if (text == getString(R.string.deliver)) {
            binding.toolbarLayout.addBtn.visibility = View.VISIBLE
            downloadOrderBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
            binding.changeStatusBtn.text = getString(R.string.delivered)
            binding.changeStatusBtn.background = getDrawable(R.drawable.delivered_order_btn_shape)
            binding.changeStatusBtn.setTextColor(resources.getColor(R.color.black))
        } else if (text == getString(R.string.reject)) {
            binding.toolbarLayout.addBtn.visibility = View.GONE
            downloadOrderBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
            binding.changeStatusBtn.text = getString(R.string.rejected)
            binding.changeStatusBtn.background = getDrawable(R.drawable.delivered_order_btn_shape)
            binding.changeStatusBtn.setTextColor(resources.getColor(R.color.black))
        }
    }

    fun checkPermissions(): Boolean {
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            WRITE_EXTERNAL_STORAGE
        )

        var readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            READ_EXTERNAL_STORAGE
        )

        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.size > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    Utils.showSnackBar("Permission Granted..", binding.parentView)

                } else {

                    Utils.showSnackBar("Permission Denied..", binding.parentView)

                }
            }
        }
    }

    companion object {

        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.draw(c)

            return b
        }
    }

    private fun takeScreenshot() {
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg"

            // create bitmap screen capture
            val v1 = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap = loadBitmapFromView(
                binding.printView,
                binding.printView.width,
                binding.printView.height
            )
            //val bitmap = Bitmap.createBitmap(v1.drawingCache)

            // v1.isDrawingCacheEnabled = false
            val imageFile = File(mPath)
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
           // openScreenshot(imageFile)
            Utils.showSnackBar("Order List downloaded successfully", binding.parentView)
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
    }

    private fun openScreenshot(imageFile: File) {
        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        val uri = Uri.parse("file://" + imageFile.absolutePath)
        intent.setDataAndType(uri, "image/*")
        startActivity(intent)
    }
    private fun calculateTotalValue() {
        totalCheckoutValue.clear()
        for (i in 0 until getOrderList!!.size) {
            var totalAmount =  getOrderList!![i]!!.sub_total
            totalCheckoutValue.add(totalAmount!!.toInt())
        }

    }
}