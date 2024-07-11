package com.example.tokobabeh

import android.content.Context
import android.content.SharedPreferences

class PrefManager(var context: Context) {

    private val PREF_NAME = "SharedPreferences"

    var pref:SharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
    var editor:SharedPreferences.Editor = pref.edit()

    fun setToken(token:String){
        editor.putString("token",token)
        editor.commit()
    }

    fun getToken(): String? {
        return pref.getString("token", null)
    }

    fun setIdCart(idCart: Int?){
        editor.putString("idCart", idCart.toString())
        editor.commit()
    }

    fun getIdCart(): String? {
        return pref.getString("idCart", null)
    }

    fun setIdUser(idUser: Int?){
        editor.putString("idUser", idUser.toString())
        editor.commit()
    }

    fun getIdUser(): String? {
        return pref.getString("idUser", null)
    }

    fun removeData(){
        editor.clear()
        editor.commit()
    }
}
