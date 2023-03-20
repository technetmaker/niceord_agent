package com.niceord.agent.activities

import com.niceord.agent.R
import com.niceord.agent.utils.Utils.Companion.comingFrom
import com.niceord.agent.utils.Utils.Companion.tapOnLogin
import com.niceord.agent.databinding.ActivityAuthenticationScreenBinding
import com.niceord.agent.fragments.LoginFragment
import com.niceord.agent.fragments.SignUpFragment
import com.niceord.agent.utils.Utils
import com.niceord.agent.utils.Utils.Companion.forgot
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.niceord.agent.utils.Utils.Companion.changePassword
import com.niceord.agent.utils.Utils.Companion.reset


class AuthenticationScreen : AppCompatActivity() {
    lateinit var binding: ActivityAuthenticationScreenBinding
    lateinit var loginFragment: Fragment
    lateinit var signupFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginFragment = LoginFragment()
        signupFragment = SignUpFragment()
        if(intent?.getStringExtra(comingFrom) == tapOnLogin){
            replaceFragment(loginFragment, "")
        }
        else if(intent?.getStringExtra(comingFrom) == forgot){
            replaceFragment(loginFragment, forgot)
        }
        else if(intent?.getStringExtra(comingFrom) == changePassword){
            replaceFragment(loginFragment, changePassword)
        }
        else{
            replaceFragment(signupFragment, "")
        }

    }
    private fun replaceFragment(fragment: Fragment, intent: String) {
        val mArgs = Bundle()
        mArgs.putString(comingFrom, intent)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        if(intent.isNotEmpty()) {
            fragment.arguments = mArgs
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent?.getStringExtra(comingFrom) == forgot) {
            replaceFragment(loginFragment, "")
        }else if(signupFragment.isVisible){
            replaceFragment(loginFragment, "")
        }else{
            finish()
        }

    }
}