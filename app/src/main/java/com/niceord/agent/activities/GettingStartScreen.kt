package com.niceord.agent.activities

import com.niceord.agent.utils.Utils.Companion.comingFrom
import com.niceord.agent.utils.Utils.Companion.tapOnLogin
import com.niceord.agent.utils.Utils.Companion.tapOnSingUp
import com.niceord.agent.databinding.ActivityGettingStartBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GettingStartScreen : AppCompatActivity() {
    lateinit var binding: ActivityGettingStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGettingStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListner()
    }
    private fun clickListner(){
        binding.loginBtn.setOnClickListener(){
            startActivity(Intent(this@GettingStartScreen, AuthenticationScreen::class.java).putExtra(comingFrom, tapOnLogin))
        }
        binding.signupBtn.setOnClickListener(){
            startActivity(Intent(this@GettingStartScreen, AuthenticationScreen::class.java).putExtra(comingFrom, tapOnSingUp))

        }
    }
}