package com.example.startconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIrLogin: Button = findViewById(R.id.btnIrLogin)
        val btnIrCadastro: Button = findViewById(R.id.btnIrCadastro)

        btnIrLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
