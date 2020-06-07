package com.example.whocooler.Authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whocooler.R
import kotlinx.android.synthetic.main.activity_auhtorization.*

class AuhtorizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auhtorization)

//        setupViews()
    }

    private fun setupViews() {
        authInfoText.text = getString(R.string.auth_info)
//        auth_google_button.setOnClickListener {
//            println("GOOGLE BUTTON CLICKED")
//        }
    }


}
