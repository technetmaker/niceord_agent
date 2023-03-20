package com.niceord.agent.fragments

import com.niceord.agent.R
import com.niceord.agent.activities.HomeScreen
import com.niceord.agent.activities.PinViewScreen
import com.niceord.agent.databinding.FragmentLoginBinding
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.models.requestModels.LoginRequestModel
import com.niceord.agent.utils.Utils
import com.niceord.agent.utils.Utils.Companion.showProgress
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi

import androidx.fragment.app.FragmentTransaction
import com.niceord.agent.databinding.FragmentForgotPasswordBinding
import com.niceord.agent.models.requestModels.ChangePasswordRequestModel
import com.niceord.agent.models.requestModels.ResetPasswordRequestModel
import com.niceord.agent.utils.Utils.Companion.deviceToken

class ForgotPasswordFragment : Fragment() {
    lateinit var binding: FragmentForgotPasswordBinding
    var sharedPreference: MySharedPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        onClicks()
        sharedPreference = activity?.let { MySharedPreference(it) }
        //getIntentMethod()
        return binding.root;

    }

/*    private fun getIntentMethod() {

        if (arguments != null) {
            if (requireArguments().getString(Utils.comingFrom)
                    .equals(Utils.forgot)
            ) {
                binding.loginFieldsLayout.visibility = View.GONE
                binding.submitBtnText.text = getString(R.string.createPassword)

            }else if (requireArguments().getString(Utils.comingFrom)
                    .equals(Utils.changePassword)
            ) {
                binding.loginFieldsLayout.visibility = View.GONE
                binding.newUserLayout.visibility = View.GONE
                binding.submitBtnText.text = getString(R.string.changePassword)
                binding.createPasswordFieldsLayout.visibility = View.VISIBLE
                binding.oldPasswordET.visibility = View.VISIBLE
            }
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicks() {
        binding.loginBtn.setOnClickListener() {
            if (binding.mobileNumberET.text.isEmpty()) {
                Utils.showSnackBar(
                    getString(R.string.pleaseEnterEmailId),
                    binding.parentView
                )
                return@setOnClickListener
            }else{
                loginApi()
            }


        }

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun loginApi() {
        if (activity?.let { Utils.isOnline(it) } == true) {
            showProgress(activity)
            val apiService = RestApiService()
            val userInfo = ResetPasswordRequestModel(
                email = binding.mobileNumberET.text.trim().toString())


            apiService.resetPassword(userInfo) {
                if (it?.success == true) {

println("resetPassword ${it.data?.email}")
                    Utils.otp.let { it1 ->
                        sharedPreference?.saveString(
                            it1,
                            it.data?.otp.toString()
                        )
                    }

                    Utils.emailID.let { it1 ->
                        sharedPreference?.saveString(
                            it1,
                            it.data?.email.toString()
                        )
                    }
                    Handler().postDelayed({
                        Utils.hideProgress()
                        startActivity(Intent(activity, PinViewScreen::class.java).putExtra(Utils.comingFrom, Utils.reset))
                    }, 1500)
                    //getUserInfo()

                } else {
                    Utils.hideProgress()
                    Utils.showSnackBar(it?.message, binding.parentView)
                }
            }
        } else {
            Utils.showSnackBar(activity?.getString(R.string.check_internet), binding.parentView)
        }
    }




    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                    activity?.finish()

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserInfo() {
        val apiService = RestApiService()

        apiService.getUserInfo(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey)
        ) {
            try {
                if (it?.success == true) {

                    it.data?.shopType?.let { it1 ->
                        sharedPreference!!.saveString(
                            Utils.agentShopType,
                            it1
                        )
                    }
                    it.data?.firstName?.let { it1 ->
                        sharedPreference!!.saveString(
                            Utils.agentFirstName,
                            it1
                        )
                    }
                    it.data?.lastName?.let { it1 ->
                        sharedPreference!!.saveString(
                            Utils.agentLastName,
                            it1
                        )
                    }

                    it.data?.url?.let { it1 ->
                        sharedPreference!!.saveString(
                            Utils.agentCategoryUrl,
                            it1
                        )
                    }
                    activity?.finishAffinity()
                    startActivity(
                        Intent(
                            activity,
                            HomeScreen::class.java
                        ).putExtra(Utils.comingFrom, it.data!!.isActive)
                    )
                }
            }catch (e: Exception){
                println(e)
            }
        }

    }

}