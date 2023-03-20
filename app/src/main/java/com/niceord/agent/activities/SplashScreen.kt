package com.niceord.agent.activities

import com.niceord.agent.databinding.ActivitySplashBinding
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.utils.Utils
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.niceord.agent.BuildConfig
import com.niceord.agent.R
import com.niceord.agent.interfaces.RestApiService

class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    var sharedPreference: MySharedPreference? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference =  MySharedPreference(this)

        if (sharedPreference!!.getValueString(Utils.userTokenKey) == null) {
            callMethodAfterSeconds("gettingStart")
        }else{
            callMethodAfterSeconds("homeScreen")
        }
        val mId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        println("jatt device id    "+ mId)

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                return@OnCompleteListener
            }
            val token = task.result
            sharedPreference!!.saveString(Utils.deviceToken, token.toString())
        })

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun callMethodAfterSeconds(intent: String){
        Handler(Looper.getMainLooper()).postDelayed(Runnable {getUserInfo(intent)}, 2000)
    }
    private fun intentMethod(intent: String, isActive: String){
        if(intent == "gettingStart"){
            startActivity(Intent(this@SplashScreen, GettingStartScreen::class.java))
            finish()
        }else{
            startActivity(Intent(this@SplashScreen, HomeScreen::class.java).putExtra(Utils.comingFrom, isActive))
            finish()
        }

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserInfo(intent: String) {
        if(Utils.isOnline(this@SplashScreen)){
            val apiService = RestApiService()

            apiService.getUserInfo(
                "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey)
            ) {
                try{
                if (it?.success == true) {

                    it.data!!.isActive?.let { it1 -> intentMethod(intent, it1) }


                    it.data?.shopType?.let { it1 ->
                        sharedPreference!!.saveString(Utils.agentShopType,
                            it1
                        )
                    }
                    it.data?.firstName?.let { it1 ->
                        sharedPreference!!.saveString(Utils.agentFirstName,
                            it1
                        )
                    }
                    it.data?.lastName?.let { it1 ->
                        sharedPreference!!.saveString(Utils.agentLastName,
                            it1
                        )
                    }

                    it.data?.url?.let { it1 ->
                        sharedPreference!!.saveString(Utils.agentCategoryUrl,
                            it1
                        )
                    }
                }else{
                    intentMethod(intent, "0")
                }}
                catch (e: Exception){
                    println(e)
                }
            }

    }else {

            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

}