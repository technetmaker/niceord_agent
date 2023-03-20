package com.niceord.agent.activities

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import com.niceord.agent.R
import com.niceord.agent.databinding.ActivityQrCodeBinding
import com.niceord.agent.deviceSharedPreference.MySharedPreference
import com.niceord.agent.utils.Utils
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.widget.Toast

import androidx.annotation.NonNull

import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.graphics.*

import android.os.Environment

import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import java.io.IOException
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import java.io.OutputStream
import java.lang.Exception


class QrCodeScreen : AppCompatActivity() {
    lateinit var binding: ActivityQrCodeBinding
    lateinit var toolbarLabel: TextView
    lateinit var toolbarBackArrowImg: ImageView
    lateinit var generatedQrCode: Bitmap
    var sharedPreference: MySharedPreference? = null
    var PERMISSION_CODE = 101
    var generated: String? = "0"
    var pageHeight = 1120
    var pagewidth = 792
    var bmp: Bitmap? = null
    var scaledbmp: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarLabel = binding.toolbarLayout.toolbarLabelTV
        toolbarBackArrowImg = binding.toolbarLayout.toolbarBackArrowBtn
        sharedPreference = MySharedPreference((this))
        Handler(Looper.getMainLooper()).postDelayed(Runnable {generateQRcodeMethod()}, 1000)
        initialize()
        onClicksLisnter()
        //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gfgimage);
        scaledbmp = bmp?.let { Bitmap.createScaledBitmap(it, 140, 140, false) };
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }

    private fun generateQRcodeMethod(){
//        var encrypted = sharedPreference!!.getValueString(Utils.agentCategoryUrl)?.let {
//            Utils.encrypt(
//                it
//            )
//        }
//        if (encrypted != null) {
//            getQrCodeBitmap(encrypted)
//        }

        if(sharedPreference!!.getValueString(Utils.userId)!!.isNotEmpty()){
            getQrCodeBitmap("NiceOrd"+sharedPreference!!.getValueString(Utils.userId))
        }

    }

    private fun initialize(){
        toolbarLabel.text = getString(R.string.app_name)

        if(sharedPreference!!.getValueString(Utils.agentShopType) != null){
         // binding.agentShopeTV.text = sharedPreference!!.getValueString(Utils.agentShopType)
        }
        if(sharedPreference!!.getValueString(Utils.agentFirstName) != null && sharedPreference!!.getValueString(Utils.agentLastName) != null){
            binding.agentNameTV.text = sharedPreference!!.getValueString(Utils.agentFirstName)+" "+sharedPreference!!.getValueString(Utils.agentLastName)
            binding.agentMobileNumberTV.text = "+91"+sharedPreference!!.getValueString(Utils.agentMobileNumber)
            binding.agentIdTV.text = "Agent ID: "+sharedPreference!!.getValueString(Utils.userId)

        }

    }

    private fun onClicksLisnter(){
        toolbarBackArrowImg.setOnClickListener(){
            finish()
        }


        binding.printQrCodeBtn.setOnClickListener{
            if (checkPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(generated.equals("1")) {
                         //takeScreenshot()
                        val bitmap = getScreenShotFromView(binding.printView)

                        // if bitmap is not null then
                        // save it to gallery
                        if (bitmap != null) {
                            saveMediaToStorage(bitmap)
                        }

                    }
                }
            } else {
                requestPermission()
            }
        }
    }
    fun store(bm: Bitmap, fileName: String?) {
        val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/Screenshots"
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getScreenShot(view: View): Bitmap? {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }
    private fun getQrCodeBitmap(text: String){
        val size = 512
        val qrCodeContent = "$text;"
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        generatedQrCode = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
        binding.qrCodeImg.setImageBitmap(generatedQrCode)
        generated = "1"

    }
    fun checkPermissions(): Boolean {
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        var readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.size > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    Utils.showSnackBar("Permission Granted..", binding.parentView)

                } else {

                    Utils.showSnackBar("Permission Denied..", binding.parentView)

                }
            }
        }
    }
    companion object {

        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.draw(c)

            return b
        }
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }


    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }
}