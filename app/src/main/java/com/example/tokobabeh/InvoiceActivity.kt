package com.example.tokobabeh

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.data_adapter_request_response.*
import com.example.tokobabeh.databinding.ActivityInvoiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InvoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this@InvoiceActivity)
        var token = "Bearer ${prefManager.getToken()}"
        var userId = prefManager.getIdUser()?.toInt()

        binding.btnSave.setOnClickListener {
            val rootView = window.decorView.rootView
            val bitmap = getScreenshotLayout(rootView)
            saveBitmapToStorage(bitmap)
            Toast.makeText(this@InvoiceActivity,"Berhasil disimpan!",Toast.LENGTH_LONG).show()
        }

        binding.btnShare.setOnClickListener {
            val rootView = window.decorView.rootView
            val bitmap = getScreenshotLayout(rootView)
            saveBitmapToStorage(bitmap)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            val uri = getImageUri(binding.root.context, bitmap)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Bagikan Via"))
        }

        binding.btnSelesai.setOnClickListener {
            reCreateCart(userId, token)
            Intent(this,ContainerActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.invoiceBack.setOnClickListener {
            Intent(this,ContainerActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getListMenu()
    }

    private fun getListMenu() {
        binding.rvInvoice.setHasFixedSize(true)
        binding.rvInvoice.layoutManager = LinearLayoutManager(this)

        val token = "Bearer ${prefManager.getToken()}"
        val idCart = prefManager.getIdCart()?.toInt()
        ApiClient.instances.getCart(token,idCart).enqueue(object : Callback<ResponseInvoice>{
            override fun onResponse(
                call: Call<ResponseInvoice>,
                response: Response<ResponseInvoice>
            ) {
                if(response.isSuccessful){
                    val cartData: DataCart? = response.body()?.data ?:return
                    binding.priceAkhir.text = "Rp. ${cartData?.price_total.toString().replace(".0","")}"

                    val menuDetailList: List<DataDetailCart> = response.body()?.detail ?:return
                    val menuList: List<MenuData> = response.body()?.menu ?:return
                    val adapter = MenuDibeliAdapter(menuDetailList,menuList)
                    binding.rvInvoice.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@InvoiceActivity,response.errorBody().toString(),Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseInvoice>, t: Throwable) {
               Toast.makeText(this@InvoiceActivity,"Throwable :" + t.localizedMessage,Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val path: String = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Invoice", null)
        return Uri.parse(path)
    }

    private fun saveBitmapToStorage(bitmap: Bitmap) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(directory, "screenshot.jpg")
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()

            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal disimpan", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getScreenshotLayout(rootView: View): Bitmap {
        rootView.isDrawingCacheEnabled = true
        val screenshot: Bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        return screenshot
    }

    private fun reCreateCart(userId: Int?, token: String) {
        ApiClient.instances.recreateCart(token,userId).enqueue(object : Callback<ResponseRecreateCart>{
            override fun onResponse(
                call: Call<ResponseRecreateCart>,
                response: Response<ResponseRecreateCart>
            ) {
                if (response.isSuccessful){
                    val dataCart: DataCart = response.body()?.data ?: return
                    prefManager.setIdCart(dataCart.id)
                }else{
                    Toast.makeText(this@InvoiceActivity,response.errorBody().toString(),Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseRecreateCart>, t: Throwable) {
                Toast.makeText(this@InvoiceActivity,"Throwable :" + t.localizedMessage,Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onBackPressed() {
        prefManager = PrefManager(this@InvoiceActivity)
        var token = "Bearer ${prefManager.getToken()}"
        var userId = prefManager.getIdUser()?.toInt()

        super.onBackPressed()
        reCreateCart(userId,token)
    }
}