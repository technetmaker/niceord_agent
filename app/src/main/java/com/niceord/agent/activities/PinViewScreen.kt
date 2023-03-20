package com.niceord.agent.activities

import com.niceord.agent.R
import com.niceord.agent.databinding.ActivityPinViewScreenBinding
import com.niceord.agent.utils.Utils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.niceord.agent.deviceSharedPreference.MySharedPreference

class PinViewScreen : AppCompatActivity() {

    lateinit var binding: ActivityPinViewScreenBinding
    var enteredOTP: String? = null
    var sharedPreference: MySharedPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinViewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicks()
        mobileNumberTextWatcher()
        sharedPreference = MySharedPreference((this))

    }

    private fun onClicks() {

        binding.submitBtn.setOnClickListener(){
            val s1 = binding.firstET.text.toString().trim()
            val s2 = binding.secondET.text.toString().trim()
            val s3 = binding.thirdET.text.toString().trim()
            val s4 = binding.fourthET.text.toString().trim()
            enteredOTP = concat(s1, s2, s3, s4)

            if(binding.firstET.text.trim().toString().isNotEmpty() && binding.secondET.text.trim().toString().isNotEmpty()&& binding.thirdET.text.trim().toString().isNotEmpty()&& binding.fourthET.text.trim().toString().isNotEmpty()){
                //finishAffinity()


                    println("validOtp ${sharedPreference?.getValueString("otp")}  $enteredOTP")
                    if(enteredOTP.equals(sharedPreference?.getValueString("otp"))){
                        startActivity(Intent(this@PinViewScreen, AuthenticationScreen::class.java).putExtra(Utils.comingFrom, Utils.forgot))
                    }else{
                        println("wrongOtp ${sharedPreference?.getValueString("otp")}  $enteredOTP")
                        Utils.showSnackBar(getString(R.string.please_enter_valid_otp), binding.parentView)
                    }


            }else{
                Utils.showSnackBar(getString(R.string.please_enter_otp), binding.parentView)
               }
        }
    }
    fun concat(s1: String, s2: String, s3: String, s4: String): String {
        return s1 + s2 + s3 + s4
    }
    private fun mobileNumberTextWatcher() {

        binding.firstET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.secondET.requestFocus()
                    binding.firstET.setBackgroundResource(R.drawable.outer_line_fields_shape_colored)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.secondET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.thirdET.requestFocus()
                    binding.secondET.setBackgroundResource(R.drawable.outer_line_fields_shape_colored)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.thirdET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.fourthET.requestFocus()
                    binding.fourthET.isCursorVisible = true
                    binding.thirdET.setBackgroundResource(R.drawable.outer_line_fields_shape_colored)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.fourthET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.fourthET.isCursorVisible = false
                    binding.fourthET.setBackgroundResource(R.drawable.outer_line_fields_shape_colored)
                }else{
                    binding.fourthET.isCursorVisible = true
                    binding.fourthET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.firstET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                binding.firstET.text.clear()
                binding.firstET.setBackgroundResource(R.drawable.outer_line_fields_shape)
            }
            false
        })
        binding.secondET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.secondET.text.isEmpty()) {
                    binding.firstET.requestFocus()
                    binding.firstET.text.clear()
                    binding.firstET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                } else {
                    binding.secondET.text.clear()
                    binding.secondET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                }
            }
            false
        })
        binding.thirdET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.thirdET.text.isEmpty()) {
                    binding.secondET.requestFocus()
                    binding.secondET.text.clear()
                    binding.secondET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                } else {
                    binding.thirdET.text.clear()
                    binding.thirdET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                }
            }
            false
        })
        binding.fourthET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.fourthET.text.isEmpty()) {
                    binding.thirdET.requestFocus()
                    binding.thirdET.text.clear()
                    binding.thirdET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                } else {
                    binding.fourthET.setBackgroundResource(R.drawable.outer_line_fields_shape)
                    binding.fourthET.text.clear()
                }
            }
            false
        })


    }
}