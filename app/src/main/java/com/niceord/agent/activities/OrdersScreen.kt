package com.niceord.agent.activities

import com.niceord.agent.R
import com.niceord.agent.adapters.OrdersAdapter
import com.niceord.agent.databinding.ActivityOrdersBinding
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.models.DataItem
import com.niceord.agent.models.requestModels.PostCategoryRequestModel
import com.niceord.agent.utils.Utils
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*
import kotlin.collections.ArrayList

class OrdersScreen : AppCompatActivity() {
    lateinit var binding: ActivityOrdersBinding
    lateinit var toolbarLabel: TextView
    lateinit var toolbarBackArrowImg: ImageView
    var sharedPreference: MySharedPreference? = null
    var getOrderList: ArrayList<DataItem?>? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarLabel = binding.toolbarLayout.toolbarLabelTV
        toolbarBackArrowImg = binding.toolbarLayout.toolbarBackArrowBtn
        sharedPreference = MySharedPreference((this))
        onClicksLisnter()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        initialize()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initialize(){
        toolbarLabel.text = getString(R.string.orders)
        getOrders()
    }

    private fun onClicksLisnter(){
        toolbarBackArrowImg.setOnClickListener(){
            onBackPressed()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrders() {
        if(Utils.isOnline(this@OrdersScreen)){
        Utils.showProgress(this@OrdersScreen)
        val apiService = RestApiService()
        val userData = PostCategoryRequestModel(
            user_id = sharedPreference!!.getValueString(Utils.userId),
            category_name = null,
            category_image = null

        )
        apiService.getOrders("Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),userData) {
            if (it?.success == true) {
                if(it.data != null) {
                    Utils.hideProgress()
                    getOrderList = it.data
                    binding.ordersRV.layoutManager = LinearLayoutManager(this)
                    val adapter = OrdersAdapter(
                        this@OrdersScreen,
                        getOrderList,
                        object : OrdersAdapter.BtnClickListener {
                            override fun onBtnClick(dataItem: DataItem) {
                                startActivity(
                                    Intent(
                                        this@OrdersScreen,
                                        OrderDetailsScreen::class.java
                                    ).putExtra(Utils.comingFrom, dataItem.category_id).putExtra(Utils.consumerOrderId,dataItem.id.toString())
                                )
                            }

                        })
                    binding.ordersRV.adapter = adapter
                    Collections.reverse(getOrderList);
                }else{
                    Utils.hideProgress()
                    binding.ordersRV.visibility = View.GONE
                    binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                }
            } else {
                Utils.hideProgress()
                binding.ordersRV.visibility = View.GONE
                binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                // Utils.showSnackBar(it?.message, binding.mainLayout)

            }
        }
    }else {
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent!=null){
            if(intent.getStringExtra(Utils.comingFrom).equals("notification")){
                startActivity(Intent(this@OrdersScreen, HomeScreen::class.java))
                finish()
            }
        }else{
            finish()
        }
    }
}