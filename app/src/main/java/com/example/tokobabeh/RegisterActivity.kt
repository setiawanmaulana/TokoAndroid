package com.example.tokobabeh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.databinding.ActivityRegisterBinding
import com.example.tokobabeh.data_adapter_request_response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity(){
    private  lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.btnRegister.setOnClickListener {
           Register()
       }

        binding.btnBack.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.confirmPassword.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.confirmPassword.clearFocus()
                Register()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun Register() {
        val name = binding.txtName.text.toString().trim()
        val username = binding.txtUsername.text.toString().trim()
        val address = binding.txtAlamat.text.toString().trim()
        val password = binding.txtPassword.text.toString().trim()
        val confrim = binding.confirmPassword.text.toString().trim()

        if(name.isEmpty() || username.isEmpty() || address.isEmpty() || password.isEmpty() || confrim.isEmpty()){
            failedMsg("Semua isian wajib di isi!")
        }else if (password != confrim) {
            failedMsg("Password dan konfirmasi password tidak cocok")
        }else {
            ApiClient.instances.userRegister(name,username,address,password,confrim)
                .enqueue(object : Callback<RegisterResponse>{
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if(response.isSuccessful){
                           succesMsg("Username ${binding.txtUsername.text} berhasil terdaftar,Lanjut login?")
                        }else{
                            Toast.makeText(this@RegisterActivity,response.body()?.message,Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        failedMsg("Trowble : " + t.localizedMessage)
                    }

                })
        }
    }
    private fun succesMsg(s: String) {
        val alertBuild = AlertDialog.Builder(this@RegisterActivity)
        alertBuild.setTitle("Register Berhasil")
            .setMessage(s)
            .setPositiveButton("Oke") { dialog, which ->
                dialog.dismiss()
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }
        val alert = alertBuild.create()
        alert.show()
    }
    private fun failedMsg(s: String) {
        val alertBuild = AlertDialog.Builder(this@RegisterActivity)
        alertBuild.setTitle("Register Gagal")
            .setMessage(s)
            .setPositiveButton("Oke") { dialog, which ->
                dialog.dismiss()
            }
        val alert = alertBuild.create()
        alert.show()
    }
}
