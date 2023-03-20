package com.niceord.agent.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.niceord.agent.R
import com.niceord.agent.databinding.ActivityAuthenticationScreenBinding
import com.niceord.agent.databinding.ActivityForgotPasswordBinding
import com.niceord.agent.databinding.FragmentForgotPasswordBinding
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.models.requestModels.ResetPasswordRequestModel
import com.niceord.agent.utils.Utils

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    var sharedPreference: MySharedPreference? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = MySharedPreference(this)

        onClicks()

    }

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
        if (this.let { Utils.isOnline(it) } == true) {
            Utils.showProgress(this)
            val apiService = RestApiService()
            val userInfo = ResetPasswordRequestModel(
                email = binding.mobileNumberET.text.trim().toString())


            apiService.resetPassword(userInfo) {
                if (it?.success == true) {
                    Utils.hideProgress()

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
                    //getUserInfo()
                    startActivity(Intent(this@ForgotPasswordActivity, PinViewScreen::class.java).putExtra(Utils.comingFrom, Utils.reset))
                } else {
                    Utils.hideProgress()
                    Utils.showSnackBar(it?.message, binding.parentView)
                }
            }
        } else {
            Utils.showSnackBar(this.getString(R.string.check_internet), binding.parentView)
        }
    }
}