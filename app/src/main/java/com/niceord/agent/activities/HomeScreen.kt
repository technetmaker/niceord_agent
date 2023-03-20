package com.niceord.agent.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.niceord.agent.BuildConfig
import com.niceord.agent.R
import com.niceord.agent.adapters.CategoryAdapter
import com.niceord.agent.databinding.ActivityHomeScreenBinding
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.globalyDialogs.ConfirmationDialog
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.models.*
import com.niceord.agent.models.requestModels.PostCategoryRequestModel
import com.niceord.agent.utils.Utils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.vansuita.library.CheckNewAppVersion
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*


class HomeScreen : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ConfirmationDialog.Listener {
    lateinit var binding: ActivityHomeScreenBinding
    var sharedPreference: MySharedPreference? = null
    var getCategoryList: ArrayList<DataItem?>? = null
    var uploadedImgCV: CardView? = null
    var setUploadedImg: ImageView? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private var mUri: Uri? = null
    var imagePath: String? = null
    var adapter: CategoryAdapter? = null
    var categoryId: Int? = null
    var alertDialog: android.app.AlertDialog? = null
    lateinit var noInternetLayout: LinearLayout
    var handleClickable: String? = null
    private val mToastDuration = 10000
    @SuppressLint("SetTextI18n", "HardwareIds")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //val msg = token
            Log.d("noInternetLayout", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
        CheckNewAppVersion(this).setOnTaskCompleteListener(object : CheckNewAppVersion.ITaskComplete {
            override fun onTaskComplete(result: CheckNewAppVersion.Result) {

                //Checks if there is a new version available on Google Play Store.
                if(result.hasNewVersion()){

                    val alertDialogBuilder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@HomeScreen,
                            R.style.Base_Theme_AppCompat
                        )
                    )

                    alertDialogBuilder.setTitle(this@HomeScreen.getString(R.string.youAreNotUpdatedTitle))
                    alertDialogBuilder.setMessage(
                        this@HomeScreen.getString(R.string.youAreNotUpdatedMessage)
                            .toString() + " " + result.getNewVersionCode() + this@HomeScreen.getString(R.string.youAreNotUpdatedMessage1)
                    )
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton(
                        R.string.update
                    ) { dialog, id ->
                        result.openUpdateLink()
                        dialog.cancel()
                    }
                    alertDialogBuilder.setNegativeButton(
                        R.string.dismiss
                    ) { dialog, id ->
                        dialog.cancel()
                    }
                    alertDialogBuilder.show()
                }

            }
        }).execute()
        noInternetLayout = binding.noInternetLayout.root
        sharedPreference = MySharedPreference((this))

        onClicksListner()

        getCategoryList = ArrayList()
        if(intent!=null){
            if(intent.getStringExtra(Utils.comingFrom).equals("0")){
                showWaitingView()
                handleClickable = "stop"
                //binding.parentView.setBackgroundResource(R.color.black)
               // binding.addCategoryImg.setImageResource(R.drawable.ic_add)
               // binding.drawerBtn.setImageResource(R.drawable.ic_menu_white)
               // binding.searchView.setBackgroundResource(R.drawable.outer_line_fields_white_shape)

            }else{
                handleClickable = ""
                binding.waitingViewRL.visibility = View.GONE
                binding.categoryRV.visibility = View.VISIBLE
                binding.addCategoryImg.isClickable = true
                getCategory()
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicksListner() {

        noInternetLayout.setOnClickListener{
            getCategory()
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }
        })


        binding.addCategoryImg.setOnClickListener() {
            addCategoryPopUp(this, DataItem(), "addCategory")

        }

        binding.drawerBtn.setOnClickListener() {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.openDrawer(
                GravityCompat.START
            )
            else binding.drawerLayout.closeDrawer(GravityCompat.END);
        }

    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    private fun initialize() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun addCategoryPopUp(context: Context?, dataItem: DataItem, comingFrom: String) {
        val dialogBuilder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        dialogBuilder.setCancelable(false)

        val dialogView: View = inflater.inflate(R.layout.add_category_popup, null)
        dialogBuilder.setView(dialogView)
        val crossBtn = dialogView.findViewById<View>(R.id.crossBtn) as ImageView
        val headerTV = dialogView.findViewById<View>(R.id.headerTV) as TextView
        val saveBtn = dialogView.findViewById<View>(R.id.saveBtn) as LinearLayout
        val takePhotoBtn = dialogView.findViewById<View>(R.id.takePhotoBtn) as LinearLayout
        val gallaryBtn = dialogView.findViewById<View>(R.id.gallaryBtn) as LinearLayout
        val categoryName = dialogView.findViewById<View>(R.id.categoryName) as EditText
        uploadedImgCV = dialogView.findViewById<View>(R.id.uploadedImgCV) as CardView
        setUploadedImg = dialogView.findViewById<View>(R.id.setUploadedImg) as ImageView
        if (comingFrom == "updateCategory") {
            headerTV.text = getString(R.string.add_category)
            categoryName.setText(dataItem.category_name)
            uploadedImgCV!!.visibility = View.VISIBLE
            Glide.with(this@HomeScreen).load(dataItem.category_image)
                .placeholder(R.drawable.ic_launcher_background).into(
                setUploadedImg!!
            )

        } else {
            headerTV.text = getString(R.string.add_category)
        }


        alertDialog = dialogBuilder.create()

        alertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);

        alertDialog?.show()

        //alertDialog?.getWindow()?.setLayout(700, 800);

        saveBtn.setOnClickListener() {
            if (categoryName.text.isEmpty()) {
                Utils.showSnackBar(getString(R.string.please_categoryName), binding.parentView)
                return@setOnClickListener
            }
            if (uploadedImgCV!!.visibility == View.GONE) {
                Utils.showSnackBar(getString(R.string.please_uploadImage), binding.parentView)
                return@setOnClickListener
            }
            if(comingFrom == "updateCategory"){
              updateCategory(categoryName.text.trim().toString(), dataItem)
            }else{
                postCategory(categoryName.text.trim().toString())
            }


        }

        takePhotoBtn.setOnClickListener() {
            capturePhoto()
        }
        gallaryBtn.setOnClickListener() {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                openGallery()
            }
        }
        crossBtn.setOnClickListener() {
            alertDialog?.dismiss()
            alertDialog?.hide()
            imagePath = null
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.orders) {
            if(handleClickable == "") {
                startActivity(Intent(this, OrdersScreen::class.java))
            }else{
                Utils.errorDialogPopup(this@HomeScreen, getString(R.string.wait_for_active))
            }
        }
        if (id == R.id.privacy_policy) {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://technetmaker.com/niceord")
            )
            startActivity(browserIntent)
        }
        else if (id == R.id.qr_code) {
            if(handleClickable == "") {
                startActivity(Intent(this, QrCodeScreen::class.java))
            }else{
                Utils.errorDialogPopup(this@HomeScreen, getString(R.string.wait_for_active))
            }
        }
        else if (id == R.id.change_password) {
            if(handleClickable == "") {
                startActivity(Intent(this, AuthenticationScreen::class.java).putExtra(Utils.comingFrom, Utils.changePassword))
            }else{
                Utils.errorDialogPopup(this@HomeScreen, getString(R.string.wait_for_active))
            }
        }
        else if (id == R.id.log_out) {
            showCustomDialog(getString(R.string.logout_confirmation_msg))
        } else if (id == R.id.delete_acount) {
            showCustomDialog(getString(R.string.delete_confirmation_msg))
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showCustomDialog(msg: String?) {
        ConfirmationDialog().apply {
            this.listener = this@HomeScreen
            this.messageText = msg!!
        }.show(supportFragmentManager, "custom_dialog_tag")
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCategory() {
        if(Utils.isOnline(this@HomeScreen)){
       binding.categoryRV.visibility = View.VISIBLE
       noInternetLayout.visibility = View.GONE
        Utils.showProgress(this@HomeScreen)
        val apiService = RestApiService()
        val userData = PostCategoryRequestModel(
            user_id = sharedPreference?.getValueString(Utils.userId),
            category_name = null,
            category_image = null
        )
        apiService.getCategory(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),
            userData
        ) {
            try{
            if (it?.success == true) {
                Utils.hideKeyboard(this@HomeScreen)
                binding.categoryRV.visibility = View.VISIBLE
                binding.pleaseTapOnBtnTV.visibility = View.GONE
                Utils.hideProgress()
                getCategoryList = it.data
                binding.categoryRV.layoutManager =
                    GridLayoutManager(this@HomeScreen, 2)
                  adapter = CategoryAdapter(
                    this@HomeScreen,
                    getCategoryList,getCategoryList,
                    object : CategoryAdapter.BtnClickListener {
                        override fun onBtnClick(dataItem: DataItem) {

                            startActivity(
                                Intent(
                                    this@HomeScreen,
                                    ProductsScreen::class.java
                                ).putExtra(
                                    Utils.id,
                                    dataItem.id
                                ).putExtra(
                                    Utils.categoryName,
                                    dataItem.category_name
                                )
                            )

                        }

                        override fun onUpdate(dataItem: DataItem) {
                            addCategoryPopUp(this@HomeScreen, dataItem, "updateCategory")
                        }

                        override fun onDelete(dataItem: DataItem) {
                            categoryId = dataItem.id!!.toInt()
                            showCustomDialog(getString(R.string.delete_category_confirmation_msg))

                        }

                        override fun onEmptyFilter(data: String?) {
                            if(data!!.isEmpty()){
                                binding.categoryRV.visibility = View.GONE
                                binding.pleaseTapOnBtnTV.text = getString(R.string.no_found)
                                binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                            }else{
                                binding.categoryRV.visibility = View.VISIBLE
                                binding.pleaseTapOnBtnTV.visibility = View.GONE
                            }
                        }

                    })
                binding.categoryRV.adapter = adapter
                Collections.reverse(getCategoryList);
            } else {
                Utils.hideProgress()
                binding.categoryRV.visibility = View.GONE
                binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                // Utils.showSnackBar(it?.message, binding.mainLayout)

            }}catch (e: Exception){
                println(e)
            }
        }
    }else {
            noInternetLayout.visibility = View.VISIBLE
            binding.categoryRV.visibility = View.GONE
        Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
    }
}

    @RequiresApi(Build.VERSION_CODES.M)
    private fun postCategory(categoryName: String?) {
        if(Utils.isOnline(this@HomeScreen)){
        noInternetLayout.visibility = View.GONE
        Utils.showProgress(this@HomeScreen)
        val file = File(imagePath)
        val apiService = RestApiService()
        val mimeType = getMimeType(imagePath)
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user_id", sharedPreference?.getValueString(Utils.userId).toString())
            .addFormDataPart("category_name", categoryName.toString())
            .addFormDataPart(
                "category_image", file.name,
                file.asRequestBody(mimeType!!.toMediaTypeOrNull())
            )
            .build()
        apiService.postCategory(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), requestBody
        ) {
            if (it?.success == true) {
                alertDialog?.hide()
                Utils.hideProgress()
                getCategory()
                Utils.showSnackBar(it.message, binding.parentView)
            } else {
                Utils.hideProgress()
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
            noInternetLayout.visibility = View.VISIBLE
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateCategory(categoryName: String?, dataItem: DataItem?) {
        if(Utils.isOnline(this@HomeScreen)){
        noInternetLayout.visibility = View.GONE
        val requestBody: RequestBody
        if(imagePath!=null){
            val file = File(imagePath)
            val mimeType = getMimeType(imagePath)
            requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", sharedPreference?.getValueString(Utils.userId).toString())
                .addFormDataPart("category_name", categoryName.toString())
                .addFormDataPart(
                    "category_image", file.name,
                    file.asRequestBody(mimeType!!.toMediaTypeOrNull())
                )
                .build()
        }else{
              requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", sharedPreference?.getValueString(Utils.userId).toString())
                .addFormDataPart("category_name", categoryName.toString())
                .build()
        }
        val apiService = RestApiService()

         apiService.updateCategory(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), dataItem?.id!!.toInt() , requestBody
        ) {
            if (it?.success == true) {
                alertDialog?.hide()
                imagePath = null
                getCategory()
                Utils.showSnackBar(it.message, binding.parentView)
            } else {
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
            noInternetLayout.visibility = View.VISIBLE
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun deleteCategory(categoryId: Int?) {
        if(Utils.isOnline(this@HomeScreen)){
        noInternetLayout.visibility = View.GONE
        val apiService = RestApiService()

        apiService.deleteCategory(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), categoryId
        ) {
            if (it?.success == true) {
                alertDialog?.hide()
                getCategory()
                Utils.showSnackBar(it.message, binding.parentView)
            } else {
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
            noInternetLayout.visibility = View.VISIBLE
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->
                if (resultCode == RESULT_OK) {
                    val result = CropImage.getActivityResult(data)

                    imagePath = result.uri.path
                    uploadedImgCV?.visibility = View.VISIBLE
                    setUploadedImg?.setImageURI(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                }

            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    CropImage.activity(mUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this@HomeScreen)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        CropImage.activity(data?.data)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this@HomeScreen)
                       // handleImageOnKitkat(data)
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantedResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when (requestCode) {
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    Utils.showSnackBar(getString(R.string.permission_denied), binding.parentView)
                }
        }
    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    private fun capturePhoto() {
        val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                  Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    override fun onLogoutTap() {
        sharedPreference!!.clearSharedPreference()
        startActivity(Intent(this, SplashScreen::class.java))
        finishAffinity()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCategoryDeteteTap() {
        deleteCategory(categoryId)
    }

    override fun onProductDeleteTap() {

    }

    private fun showWaitingView(){
        binding.waitingViewRL.visibility = View.VISIBLE
        binding.categoryRV.visibility = View.GONE
        binding.addCategoryImg.isClickable = false
    }


}
