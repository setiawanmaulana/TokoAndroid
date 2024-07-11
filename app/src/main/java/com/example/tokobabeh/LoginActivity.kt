package com.example.tokobabeh

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.databinding.ActivityLoginBinding
import com.example.tokobabeh.data_adapter_request_response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            Login()
        }

        binding.btnDaftar.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.etPassword.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etPassword.clearFocus()
                Login()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun Login() {
        prefManager = PrefManager(this@LoginActivity)

        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if(username.isEmpty() || username == ""){
            binding.etUsername.setError("Username wajib di isi!")
            binding.etUsername.requestFocus()
        }else if(password.isEmpty() || password == ""){
            binding.etPassword.setError("Password tidak boleh kosong!")
            binding.etPassword.requestFocus()
        }else{
            ApiClient.instances.userLogin(username,password)
                .enqueue(object :Callback<LoginResponse>{
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if(response.isSuccessful){
                            response.body()?.let { prefManager.setToken(it.token) }
                            response.body()?.let { it.cart?.let { it1 -> prefManager.setIdCart(it1.id) } }
                            response.body()?.let { it.cart?.let { it1 -> prefManager.setIdUser(it1.user_id) } }

                            Toast.makeText(applicationContext,response.body()?.message,Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LoginActivity, ContainerActivity::class.java))
                            finish()
                        }else{
                            failedMsg("Username atau Password salah!")
                            binding.etPassword.setText("")
                            binding.etUsername.requestFocus()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Throwable : " + t.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    private fun failedMsg(s: String) {
        val alertBuild = AlertDialog.Builder(this@LoginActivity)
        alertBuild.setTitle("Login Gagal")
            .setMessage(s)
            .setPositiveButton("Oke") { dialog, which ->
                dialog.dismiss()
            }
        val alert = alertBuild.create()
        alert.show()
    }
}
