package com.niceord.agent.activities

import com.niceord.agent.BuildConfig
import com.niceord.agent.R
import com.niceord.agent.adapters.ProductAdapter
import com.niceord.agent.databinding.ActivityProductsBinding
import com.niceord.agent.interfaces.RestApiService
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.models.DataItem
import com.niceord.agent.models.requestModels.PostProductRequestModel
import com.niceord.agent.utils.Utils
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.niceord.agent.globalyDialogs.ConfirmationDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ProductsScreen : AppCompatActivity(), ConfirmationDialog.Listener  {
    lateinit var binding: ActivityProductsBinding
    lateinit var toolbarLabel: TextView
    lateinit var toolbarBackArrowImg: ImageView
    var sharedPreference: MySharedPreference? = null
    var getProductList: ArrayList<DataItem?>? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private var mUri: Uri? = null
    var imagePath: String? = null
    var alertDialog: android.app.AlertDialog? = null
    var uploadedImgCV: CardView? = null
    var setUploadedImg: ImageView? = null
    var categoryId: Int? = null
    var productId: Int? = null
    var adapter: ProductAdapter? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarLabel = binding.toolbarLayout.toolbarLabelTV
        toolbarBackArrowImg = binding.toolbarLayout.toolbarBackArrowBtn
        sharedPreference = MySharedPreference((this))
        getProductList = ArrayList()
        onClicksListner()
    }

    fun showCustomDialog(msg: String?) {
        ConfirmationDialog().apply {
            this.listener = this@ProductsScreen
            this.messageText = msg!!
        }.show(supportFragmentManager, "custom_dialog_tag")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        initialize()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClicksListner(){
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }
        })
        toolbarBackArrowImg.setOnClickListener(){
            finish()
        }
        binding.toolbarLayout.addBtn.setOnClickListener(){

            addCategoryDetialsPopUp(this@ProductsScreen, DataItem(), "addProduct")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initialize(){
        binding.toolbarLayout.addBtn.visibility = View.VISIBLE

        categoryId =  intent.getIntExtra(Utils.id, 0)
        binding.toolbarLayout.toolbarLabelTV.text = intent.getStringExtra(Utils.categoryName)
        if(categoryId != null){
            getProducts(categoryId!!)

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun addCategoryDetialsPopUp(context: Context?, dataItem: DataItem, comingFrom: String) {
        val dialogBuilder: android.app.AlertDialog.Builder =
        android.app.AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        dialogBuilder.setCancelable(false)

        val dialogView: View = inflater.inflate(R.layout.add_product_popup, null)
        dialogBuilder.setView(dialogView)

        val crossBtn = dialogView.findViewById<View>(R.id.crossBtn) as ImageView
        val headerTV = dialogView.findViewById<View>(R.id.headerTV) as TextView
        val saveBtn = dialogView.findViewById<View>(R.id.saveBtn) as LinearLayout
        val takePhotoBtn = dialogView.findViewById<View>(R.id.takePhotoBtn) as LinearLayout
        val gallaryBtn = dialogView.findViewById<View>(R.id.gallaryBtn) as LinearLayout
        val productTitleET = dialogView.findViewById<View>(R.id.productTitleET) as EditText
        val productQuantityET = dialogView.findViewById<View>(R.id.productQuantityET) as EditText
        val productPriceET = dialogView.findViewById<View>(R.id.productPriceET) as EditText
        uploadedImgCV = dialogView.findViewById<View>(R.id.uploadedImgCV) as CardView
        setUploadedImg = dialogView.findViewById<View>(R.id.setUploadedImg) as ImageView

        if(comingFrom == "updateProduct"){
            headerTV.text = getString(R.string.update_product)
            productTitleET.setText(dataItem.product_title)
            productQuantityET.setText(dataItem.product_quantity)
            productPriceET.setText(dataItem.product_price)
            uploadedImgCV!!.visibility = View.VISIBLE
            Glide.with(this@ProductsScreen).load(dataItem.product_image).placeholder(R.drawable.ic_launcher_background).into(
                setUploadedImg!!
            )

        }else{
            headerTV.text = getString(R.string.add_product)
        }


        alertDialog = dialogBuilder.create()

        alertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);

        alertDialog?.show()

        //alertDialog?.getWindow()?.setLayout(700, 800);

        saveBtn.setOnClickListener() {
            if (productTitleET.text.isEmpty()) {
                Utils.showSnackBar(getString(R.string.please_productName), binding.parentView)
                return@setOnClickListener
            }
            if (productPriceET.text.isEmpty()) {
                Utils.showSnackBar(getString(R.string.please_productPrice), binding.parentView)
                return@setOnClickListener
            }
           /* if (productQuantityET.text.isEmpty()) {
                Utils.showSnackBar(getString(R.string.please_productQuantity), binding.parentView)
                return@setOnClickListener
            }*/
            if (uploadedImgCV!!.visibility == View.GONE) {
                Utils.showSnackBar(getString(R.string.please_uploadImage), binding.parentView)
                return@setOnClickListener
            }
            if(comingFrom == "updateProduct"){
               updateProduct(productTitleET.text.toString(),productPriceET.text.toString(),productQuantityET.text.toString(), dataItem)
            }else{
                postProduct(productTitleET.text.toString(),productPriceET.text.toString(),productQuantityET.text.toString())
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getProducts(categoryId: Int) {
        if(Utils.isOnline(this@ProductsScreen)){
            Utils.showProgress(this@ProductsScreen)
        val apiService = RestApiService()
        val userData = PostProductRequestModel(
            category_id =  categoryId,
            product_title = null,
            product_quantity = null,
            product_price = null,
            product_image = null
        )
        apiService.getProducts("Bearer " + sharedPreference?.getValueString(Utils.userTokenKey),userData) {
            if (it?.success == true) {
                Utils.hideKeyboard(this@ProductsScreen)
                Utils.hideProgress()
                getProductList = it.data
                binding.searchView.visibility = View.VISIBLE
                binding.productsRV.layoutManager = LinearLayoutManager(this)
                adapter = ProductAdapter(this@ProductsScreen, getProductList,getProductList, object : ProductAdapter.BtnClickListener {
                    override fun onBtnClick(dataItem: DataItem) {
                        addCategoryDetialsPopUp(this@ProductsScreen, dataItem, "updateProduct")
                    }

                    override fun onDelete(dataItem: DataItem) {
                        productId = dataItem.id!!.toInt()
                        showCustomDialog(getString(R.string.delete_product_confirmation_msg))
                    }

                    override fun onEmptyFilter(data: String?) {
                        if(data!!.isEmpty()){
                            binding.productsRV.visibility = View.GONE
                            binding.pleaseTapOnBtnTV.text = getString(R.string.no_found)
                            binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                        }else{
                            binding.productsRV.visibility = View.VISIBLE
                            binding.pleaseTapOnBtnTV.visibility = View.GONE
                        }
                    }

                })
                binding.productsRV.adapter = adapter
                Collections.reverse(getProductList);
            } else {
                Utils.hideProgress()
                binding.searchView.visibility = View.GONE
                binding.productsRV.visibility = View.GONE
                binding.pleaseTapOnBtnTV.visibility = View.VISIBLE
                // Utils.showSnackBar(it?.message, binding.mainLayout)

            }
        }
    }else {
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun postProduct(productTitle: String?, productPrice: String?,productQuantity: String?) {
        if(Utils.isOnline(this@ProductsScreen)){
        val file = File(imagePath)
        val apiService = RestApiService()
        val mimeType = getMimeType(imagePath)
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("category_id", categoryId.toString())
            .addFormDataPart("product_title", productTitle.toString())
            .addFormDataPart("product_price", productPrice.toString())
           // .addFormDataPart("product_quantity", productQuantity.toString())
            .addFormDataPart(
                "product_image", file.name,
                file.asRequestBody(mimeType!!.toMediaTypeOrNull())
            )
            .build()
        apiService.postProduct(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), requestBody
        ) {
            if (it?.success == true) {
                //getProducts(it.data?.category_id!!)
                alertDialog?.hide()
                Utils.showSnackBar(it.message, binding.parentView)
                val intent = intent
                finish()
                startActivity(intent)
            } else {
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateProduct(productTitle: String?, productPrice: String?,productQuantity: String?, dataItem: DataItem?) {
        if(Utils.isOnline(this@ProductsScreen)){
        val requestBody: RequestBody
        if(imagePath!=null) {
            val file = File(imagePath)
            val mimeType = getMimeType(imagePath)

             requestBody  = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("category_id", categoryId.toString())
                .addFormDataPart("product_title", productTitle.toString())
                .addFormDataPart("product_quantity", productQuantity.toString())
                .addFormDataPart("product_price", productPrice.toString())
                .addFormDataPart(
                    "product_image", file.name,
                    file.asRequestBody(mimeType!!.toMediaTypeOrNull())
                )
                .build()
        }else{
            requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("category_id", categoryId.toString())
                .addFormDataPart("product_title", productTitle.toString())
                .addFormDataPart("product_quantity", productQuantity.toString())
                .addFormDataPart("product_price", productPrice.toString())
                .build()
        }
            val apiService = RestApiService()
         apiService.updateProduct(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), dataItem?.id!!.toInt() , requestBody
        ) {
            if (it?.success == true) {
                alertDialog?.hide()
                imagePath = null
                getProducts(categoryId!!)
                Utils.showSnackBar(it.message, binding.parentView)
            } else {
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
            Utils.showSnackBar(getString(R.string.check_internet), binding.parentView)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun deleteProduct(productId: Int?) {
        if(Utils.isOnline(this@ProductsScreen)){
        val apiService = RestApiService()

        apiService.deleteProduct(
            "Bearer " + sharedPreference?.getValueString(Utils.userTokenKey), productId
        ) {
            if (it?.success == true) {
                alertDialog?.hide()
                getProducts(categoryId!!)
                Utils.showSnackBar(it.message, binding.parentView)
            } else {
                Utils.showSnackBar(it?.message, binding.parentView)
            }
        }
    }else {
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
                        .start(this@ProductsScreen)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        CropImage.activity(data?.data)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this@ProductsScreen)
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

    }

    override fun onCategoryDeteteTap() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onProductDeleteTap() {
         deleteProduct(productId)
    }

}