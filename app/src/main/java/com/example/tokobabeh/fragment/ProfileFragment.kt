package com.example.tokobabeh.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tokobabeh.LoginActivity
import com.example.tokobabeh.PrefManager
import com.example.tokobabeh.api.ApiClient
import com.example.tokobabeh.data_adapter_request_response.UserData
import com.example.tokobabeh.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var prefManager: PrefManager
    private var _binding: FragmentProfileBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.btnLogout.setOnClickListener{
            Logout()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())
    }

    override fun onStart() {
        super.onStart()
        dataProfile()
    }

    private fun dataProfile() {
        val token = "Bearer ${prefManager.getToken()}"
        ApiClient.instances.getUser(token).enqueue(object : Callback<UserData>{
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                val userModel = response.body()
                userModel?.let {
                    binding.nameUser.text = it.name
                    binding.addressUser.text = it.address
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(context,"Trowble :" + t.localizedMessage,Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun Logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Yakin Ingin Logout?")
        builder.setPositiveButton("Logout") { dialog, _ ->
            prefManager.removeData()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }
}