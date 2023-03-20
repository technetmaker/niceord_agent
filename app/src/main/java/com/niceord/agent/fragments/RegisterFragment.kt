package com.niceord.agent.fragments

import com.niceord.agent.R
import com.niceord.agent.activities.HomeScreen
import com.niceord.agent.adapters.DialogPopUpAdapter
import com.niceord.agent.databinding.FragmentRegisterBinding
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.models.*
import com.niceord.agent.models.requestModels.RegistrationRequestModel
import com.niceord.agent.utils.Utils
import com.niceord.agent.viewModels.SignUpModel
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.niceord.agent.deviceSharedPreference.MySharedPreference

class SignUpFragment : Fragment()  {
    lateinit var signUpModel: SignUpModel
    lateinit var binding: FragmentRegisterBinding
    lateinit var loginFragment: Fragment
    var shopTypeList: ArrayList<DataItem?>? = null
    var settingSystemList: ArrayList<DataItem?>? = null
    var sharedPreference: MySharedPreference? = null
    var selectedShopTypeId: String? =  null
    var selectedSittingSystemId: String? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpModel = activity?.run {
            ViewModelProviders.of(this)[SignUpModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
       // binding = FragmentSignUpBinding.inflate(layoutInflater)
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_register, container, false)
        mobileNumberTextWatcher()
        loginFragment = LoginFragment()
        onClicks()
        shopTypeList = ArrayList()
        getShopType()
        sharedPreference = MySharedPreference((requireActivity()))
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicks(){
        binding.loginBtn.setOnClickListener() {
            replaceFragment(loginFragment)
        }
        binding.shopTypeTV.setOnClickListener{
            if (shopTypeList != null) {
                dialogPopUp1(activity, shopTypeList, "shopType")
            }

        }

        binding.sittingSystemTV.setOnClickListener {
            if(settingSystemList?.isNotEmpty() == true){
                dialogPopUp1(activity, settingSystemList, "sittingSystem")
        }
        }
        binding.signupBtn.setOnClickListener{

            if(binding.firstNameET.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseEnterFirstName), binding.parentView)
                return@setOnClickListener
            }
/*
            if(binding.lastNameET.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseEnterLastName), binding.parentView)
                return@setOnClickListener
            }*/

            if(binding.mobileNumberET.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseEnterMobileNumber), binding.parentView)
                return@setOnClickListener
            }

            if(binding.mobileNumberET.text.length != 10){
                Utils.showSnackBar(getString(R.string.pleaseEnterValidMobileNumber), binding.parentView)
                return@setOnClickListener
            }

            if(binding.emailIdET.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseEnterEmailId), binding.parentView)
                return@setOnClickListener
            }

            if(!Utils.isValidEmail(binding.emailIdET.text.toString().trim())){
                Utils.showSnackBar(getString(R.string.pleaseEnterValidEmailId), binding.parentView)
                return@setOnClickListener
            }

            if(binding.passwordET.text?.isEmpty() == true){
                Utils.showSnackBar(getString(R.string.pleaseenter_password), binding.parentView)
                return@setOnClickListener
            }

            if(binding.confirmPasswordET.text?.isEmpty() == true){
                Utils.showSnackBar(getString(R.string.please_confirm_password), binding.parentView)
                return@setOnClickListener
            }

            if(binding.passwordET.text.toString().trim() != binding.confirmPasswordET.text.toString().trim()){
                Utils.showSnackBar(getString(R.string.please_match_pasword), binding.parentView)
                return@setOnClickListener
            }

            if(binding.shopTypeTV.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseSelectShopType), binding.parentView)
                return@setOnClickListener
            }

            if(binding.sittingSystemTV.text.isEmpty()){
                Utils.showSnackBar(getString(R.string.pleaseSelectSittingSystem), binding.parentView)
                return@setOnClickListener
            }

            registerApi()


        }
    }



    private fun mobileNumberTextWatcher(){

        binding.mobileNumberET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    val mobileNumber = s.toString()
                    if (mobileNumber.startsWith("0")) {
                        binding.mobileNumberET.setText(
                            Utils.removeZeroMobileNumer(
                                mobileNumber
                            )
                        )
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }
    private fun dialogPopUp1(context: Context?, mList: ArrayList<DataItem?>?, view: String){
        val dialogBuilder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        dialogBuilder.setCancelable(false)

        val dialogView: View = inflater.inflate(R.layout.shop_type_dialog_popup, null)
        dialogBuilder.setView(dialogView)
        val alertDialog: android.app.AlertDialog?

        val popUpRV = dialogView.findViewById<View>(R.id.popUpRV) as RecyclerView
        val crossBtn = dialogView.findViewById<View>(R.id.crossBtn) as ImageView

        alertDialog = dialogBuilder.create()

        alertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog?.show()
       // alertDialog?.getWindow()?.setLayout(700, 800);
        popUpRV.layoutManager = LinearLayoutManager(context)
        val adapter = DialogPopUpAdapter(mList, object : DialogPopUpAdapter.BtnClickListener{
            override fun onBtnClick(dataItem: DataItem) {
                if(view == "shopType"){
                    binding.shopTypeTV.text = dataItem.title
                    settingSystemList?.clear()
                    binding.sittingSystemTV.text = ""
                    selectedShopTypeId = dataItem.id.toString()
                    getSittingSystem(dataItem.id.toString())
                }else{
                    selectedSittingSystemId = dataItem.id.toString()
                    binding.sittingSystemTV.text = dataItem.title
                }

                alertDialog?.dismiss()
                alertDialog?.hide()
            }

        })
        popUpRV.adapter = adapter

        crossBtn.setOnClickListener(){
            alertDialog?.dismiss()
            alertDialog?.hide()
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun registerApi() {
        if (activity?.let { Utils.isOnline(it) } == true) {
            Utils.showProgress(context)
        val apiService = RestApiService()
        val userInfo = RegistrationRequestModel(binding.firstNameET.text.trim().toString(),
            binding.lastNameET.text.trim().toString(),
            binding.mobileNumberET.text.trim().toString(),
            binding.emailIdET.text.trim().toString(),
            selectedShopTypeId.toString(),
            selectedSittingSystemId.toString(),
            binding.passwordET.text?.trim().toString(),
            binding.confirmPasswordET.text?.trim().toString(),
            sharedPreference!!.getValueString(Utils.deviceToken).toString())

        apiService.registerUser(userInfo) {
            if (it?.success == true) {

                Utils.hideProgress()
                activity?.finishAffinity()
                startActivity(Intent(activity, HomeScreen::class.java).putExtra(Utils.comingFrom, it.data!!.isActive))
            } else {
                Utils.hideProgress()
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    } else {
            Utils.hideProgress()
        Utils.showSnackBar(activity?.getString(R.string.check_internet), binding.parentView)
    }
}

    private fun getShopType() {
        val apiService = RestApiService()

        apiService.getShopType() {
            if (it?.success == true) {
                shopTypeList = it.data
            }
        }
    }

    private fun getSittingSystem(dataItem: String) {
        val apiService = RestApiService()

        apiService.getSittingSystem(dataItem) {
            if (it?.success == true) {
                settingSystemList = it.data
            }
        }
    }


}