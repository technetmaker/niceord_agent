package com.niceord.agent.fragments

import com.niceord.agent.R
import com.niceord.agent.databinding.FragmentLoginBinding
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.models.requestModels.LoginRequestModel
import com.niceord.agent.utils.Utils
import com.niceord.agent.utils.Utils.Companion.showProgress
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi

import androidx.fragment.app.FragmentTransaction
import com.niceord.agent.activities.*
import com.niceord.agent.models.requestModels.ChangePasswordRequestModel
import com.niceord.agent.models.requestModels.NewPasswordRequestModel
import com.niceord.agent.utils.Utils.Companion.deviceToken
import java.util.*

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var registerFragment: Fragment
    lateinit var forgotFragment: Fragment
    var sharedPreference: MySharedPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mobileNumberTextWatcher()
        onClicks()
        registerFragment = SignUpFragment()
        forgotFragment = ForgotPasswordFragment()
        sharedPreference = activity?.let { MySharedPreference(it) }
        getIntentMethod()
        return binding.root;

    }

    private fun getIntentMethod() {

        if (arguments != null) {
            if (requireArguments().getString(Utils.comingFrom)
                    .equals(Utils.forgot)
            ) {
                binding.loginFieldsLayout.visibility = View.GONE
                binding.newUserLayout.visibility = View.GONE
                binding.submitBtnText.text = getString(R.string.createPassword)
                binding.createPasswordFieldsLayout.visibility = View.VISIBLE
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
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicks() {
        binding.loginBtn.setOnClickListener() {
            if (binding.submitBtnText.text == getString(R.string.createPassword)) {
                if (binding.createPasswordET.text.isEmpty()) {
                    Utils.showSnackBar(getString(R.string.pleaseenter_password), binding.parentView)
                    return@setOnClickListener
                }
                if (binding.confirmPasswordET.text.isEmpty()) {
                    Utils.showSnackBar(
                        getString(R.string.please_confirm_password),
                        binding.parentView
                    )
                    return@setOnClickListener
                }
                if (binding.createPasswordET.text.toString().trim() != binding.confirmPasswordET.text.toString().trim()) {
                    Utils.showSnackBar(
                        getString(R.string.please_match_pasword),
                        binding.parentView
                    )
                    return@setOnClickListener
                }
                resetPassword()
            } else if (binding.submitBtnText.text == getString(R.string.changePassword)) {
                if (binding.oldPasswordET.text.isEmpty()) {
                    Utils.showSnackBar(getString(R.string.pleaseenter_old_password), binding.parentView)
                    return@setOnClickListener
                }
                if (binding.createPasswordET.text.isEmpty()) {
                    Utils.showSnackBar(getString(R.string.pleaseenter_password), binding.parentView)
                    return@setOnClickListener
                }
                if (binding.confirmPasswordET.text.isEmpty()) {
                    Utils.showSnackBar(
                        getString(R.string.please_confirm_password),
                        binding.parentView
                    )
                    return@setOnClickListener
                }
                if (binding.createPasswordET.text.toString().trim() != binding.confirmPasswordET.text.toString().trim()) {
                    Utils.showSnackBar(
                        getString(R.string.please_match_pasword),
                        binding.parentView
                    )
                    return@setOnClickListener
                }
                changePassword()
            } else {
                if (binding.mobileNumberET.text.isEmpty()) {
                    Utils.showSnackBar(
                        getString(R.string.pleaseEnterMobileNumber),
                        binding.parentView
                    )
                    return@setOnClickListener
                }

                if (binding.mobileNumberET.text.length != 10) {
                    Utils.showSnackBar(
                        getString(R.string.pleaseEnterValidMobileNumber),
                        binding.parentView
                    )
                    return@setOnClickListener
                }

                if (binding.passwordET.text?.isEmpty() == true) {
                    Utils.showSnackBar(getString(R.string.pleaseenter_password), binding.parentView)
                    return@setOnClickListener
                }

                loginApi()
            }


        }


        binding.registerBtn.setOnClickListener() {
            replaceFragment(registerFragment)
        }

        binding.forgotPasswordBtn.setOnClickListener() {

            startActivity(Intent(activity, ForgotPasswordActivity::class.java))
        }
    }

    private fun mobileNumberTextWatcher() {
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

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loginApi() {
        if (activity?.let { Utils.isOnline(it) } == true) {
            showProgress(activity)
            val apiService = RestApiService()
            val userInfo = LoginRequestModel(
                mobile = binding.mobileNumberET.text.trim().toString(),
                password = binding.passwordET.text?.trim().toString(),
                device_token =  sharedPreference!!.getValueString(Utils.deviceToken).toString())

            apiService.loginUser(userInfo) {
                if (it?.success == true) {
                    Utils.hideProgress()
                    Utils.userTokenKey.let { it1 ->
                        sharedPreference?.saveString(
                            it1,
                            it.accessToken.toString()
                        )
                    }
                    Utils.userId.let { it1 ->
                        sharedPreference?.saveString(
                            it1,
                            it.data?.id.toString()
                        )
                    }
                    Utils.agentMobileNumber.let { it1 ->
                        sharedPreference?.saveString(
                            it1,
                            binding.mobileNumberET.text.toString().trim()
                        )
                    }
                    getUserInfo()

                } else {
                    Utils.hideProgress()
                    Utils.showSnackBar(it?.message, binding.parentView)
                }
            }
        } else {
            Utils.showSnackBar(activity?.getString(R.string.check_internet), binding.parentView)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun changePassword() {
        if (activity?.let { Utils.isOnline(it) } == true) {
            showProgress(activity)
            val apiService = RestApiService()
            val userData = ChangePasswordRequestModel(
                old_password = binding.oldPasswordET.text.toString().trim(),
                new_password = binding.createPasswordET.text.toString().trim(),
                confirm_password = binding.confirmPasswordET.text.toString().trim()

            )
            apiService.changePassword(
                "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),
                userData
            ) {
                if (it?.success == true) {
                    Utils.showSnackBar(it.message, binding.parentView)
                    Utils.hideProgress()
                    activity?.finish()
                } else {
                    Utils.hideProgress()
                    Utils.showSnackBar(it!!.message, binding.parentView)
                }
            }
        } else {
            Utils.showSnackBar(activity?.getString(R.string.check_internet), binding.parentView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetPassword() {
        if (activity?.let { Utils.isOnline(it) } == true) {
            showProgress(activity)
            val apiService = RestApiService()
            val userData = NewPasswordRequestModel(
                email = sharedPreference?.getValueString("emailId")?.lowercase(Locale.ROOT),
                otp = sharedPreference?.getValueString("otp"),
                password = binding.confirmPasswordET.text.toString().trim(),
                password_confirmation = binding.createPasswordET.text.toString().trim()

            )

            println("NewPasswordRequestModel ${userData.email}")
            println("NewPasswordRequestModel ${userData.otp}")
            println("NewPasswordRequestModel ${userData.password}")
            println("NewPasswordRequestModel ${userData.password_confirmation}")
            apiService.resetPassword(
                "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),
                userData
            ) {
                if (it?.success == true) {
                    Utils.showSnackBar(it.message, binding.parentView)
                    Utils.hideProgress()
                    startActivity(Intent(activity, GettingStartScreen::class.java))
                    activity?.finish()
                } else {
                    Utils.hideProgress()
                    //Utils.showSnackBar(it!!.message, binding.parentView)
                    startActivity(Intent(activity, GettingStartScreen::class.java))
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
                if (binding.submitBtnText.text == getString(R.string.createPassword)) {
                    binding.mobileNumberET.visibility = View.VISIBLE
                    binding.passwordET.visibility = View.VISIBLE
                   // binding.forgotPasswordBtn.visibility = View.VISIBLE
                    binding.newUserLayout.visibility = View.VISIBLE
                    binding.submitBtnText.text = getString(R.string.login)
                    binding.createPasswordFieldsLayout.visibility = View.GONE
                    binding.loginFieldsLayout.visibility = View.VISIBLE
                } else {
                    activity?.finish()
                }
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