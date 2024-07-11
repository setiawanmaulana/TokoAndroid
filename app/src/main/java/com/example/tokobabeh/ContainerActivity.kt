package com.example.tokobabeh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tokobabeh.fragment.ProfileFragment
import com.example.tokobabeh.fragment.TransactionFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ContainerActivity : AppCompatActivity() {
    private lateinit var navBar: BottomNavigationView

    private val transactionFragment = TransactionFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, transactionFragment, "Transaksi")
            .commit()

        navBar = findViewById(R.id.bottomBar)
        navBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.transactions -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, transactionFragment, "Transaksi")
                        .commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, profileFragment, "Profil")
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (navBar.selectedItemId != R.id.transactions) {
            navBar.selectedItemId = R.id.transactions
        } else {
            super.onBackPressed()
        }
    }
}