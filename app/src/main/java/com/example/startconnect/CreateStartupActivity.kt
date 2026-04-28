package com.example.startconnect

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateStartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_startup)

        val mainView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuarioId = intent.getIntExtra("usuarioId", -1)

        val etNomeStartup = findViewById<EditText>(R.id.etNomeStartup)
        val etDescricaoStartup = findViewById<EditText>(R.id.etDescricaoStartup)
        val etAreaStartup = findViewById<EditText>(R.id.etAreaStartup)
        val etTagsStartup = findViewById<EditText>(R.id.etTagsStartup)
        val btnSalvarStartup = findViewById<MaterialButton>(R.id.btnSalvarStartup)

        btnSalvarStartup.setOnClickListener {
            val nome = etNomeStartup.text.toString().trim()
            val descricao = etDescricaoStartup.text.toString().trim()
            val segmento = etAreaStartup.text.toString().trim()
            val tags = etTagsStartup.text.toString().trim()

            if (usuarioId == -1) {
                Toast.makeText(this, "Usuário inválido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (nome.isEmpty() || segmento.isEmpty()) {
                Toast.makeText(this, "Preencha pelo menos nome e área", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val tagsSeparadas = tags.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val tag1 = tagsSeparadas.getOrElse(0) { "" }
            val tag2 = tagsSeparadas.getOrElse(1) { "" }
            val tag3 = tagsSeparadas.getOrElse(2) { "" }
            val tag4 = tagsSeparadas.getOrElse(3) { "" }

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.102/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val call = apiService.registerStartup(
                usuarioId = usuarioId,
                nome = nome,
                segmento = segmento,
                subtitulo = if (descricao.isBlank()) "Clique para ver mais" else descricao,
                tag1 = tag1,
                tag2 = tag2,
                tag3 = tag3,
                tag4 = tag4
            )

            call.enqueue(object : Callback<StartupRegisterResponse> {
                override fun onResponse(
                    call: Call<StartupRegisterResponse>,
                    response: Response<StartupRegisterResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val startupResponse = response.body()!!

                        Toast.makeText(
                            this@CreateStartupActivity,
                            startupResponse.message,
                            Toast.LENGTH_LONG
                        ).show()

                        if (startupResponse.success) {
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@CreateStartupActivity,
                            "Erro no servidor",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StartupRegisterResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CreateStartupActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val menuItems = listOf(
            findViewById<View>(R.id.navHomeContainer) to findViewById<ImageView>(R.id.navHomeIcon),
            findViewById<View>(R.id.navContaContainer) to findViewById<ImageView>(R.id.navContaIcon),
            findViewById<View>(R.id.navChatContainer) to findViewById<ImageView>(R.id.navChatIcon),
            findViewById<View>(R.id.navFavoritosContainer) to findViewById<ImageView>(R.id.navFavoritosIcon),
            findViewById<View>(R.id.navNotificacoesContainer) to findViewById<ImageView>(R.id.navNotificacoesIcon),
            findViewById<View>(R.id.navSairContainer) to findViewById<ImageView>(R.id.navSairIcon)
        )

        fun selectItem(selectedContainer: View, selectedIcon: ImageView) {
            menuItems.forEach { (container, icon) ->
                val isSelected = container == selectedContainer
                container.isSelected = isSelected
                icon.isSelected = isSelected

                container.animate()
                    .scaleX(if (isSelected) 1.08f else 1f)
                    .scaleY(if (isSelected) 1.08f else 1f)
                    .setDuration(180)
                    .start()

                icon.animate()
                    .alpha(if (isSelected) 1f else 0.85f)
                    .setDuration(180)
                    .start()
            }
        }

        menuItems.forEach { (container, icon) ->
            container.setOnClickListener {
                selectItem(container, icon)
            }
        }

        selectItem(findViewById(R.id.navHomeContainer), findViewById(R.id.navHomeIcon))
    }
}
