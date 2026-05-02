package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StartupDetalhesActivity : AppCompatActivity() {

    private var startupId: Int = -1
    private var usuarioId: Int = -1
    private var usuarioTipo: String = ""
    private var donoId: Int = -1
    private var startupNome: String = ""
    private var isFavoritado: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_startup_detalhes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detalhesRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startupId = intent.getIntExtra("startupId", -1)
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: ""

        findViewById<View>(R.id.btnVoltar).setOnClickListener { finish() }
        findViewById<View>(R.id.btnVoltar2).setOnClickListener { finish() }

        fetchDetalhes()
        setupBottomNavigation()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchDetalhes() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getDetalhesStartup(startupId, usuarioId).enqueue(object : Callback<StartupDetalhesResponse> {
            override fun onResponse(call: Call<StartupDetalhesResponse>, response: Response<StartupDetalhesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val startup = response.body()!!
                    donoId = startup.donoId
                    startupNome = startup.nome
                    isFavoritado = startup.favoritado == 1

                    findViewById<TextView>(R.id.txtStartupNome).text = startup.nome
                    findViewById<TextView>(R.id.txtStartupSegmento).text = startup.segmento
                    findViewById<TextView>(R.id.txtStartupDescricao).text = startup.subtitulo

                    val tags = listOfNotNull(
                        startup.tag1?.takeIf { it.isNotBlank() },
                        startup.tag2?.takeIf { it.isNotBlank() },
                        startup.tag3?.takeIf { it.isNotBlank() },
                        startup.tag4?.takeIf { it.isNotBlank() }
                    )
                    val tagViews = listOf(
                        findViewById<TextView>(R.id.tagOne),
                        findViewById<TextView>(R.id.tagTwo),
                        findViewById<TextView>(R.id.tagThree),
                        findViewById<TextView>(R.id.tagFour)
                    )
                    tags.forEachIndexed { i, tag ->
                        if (i < tagViews.size) {
                            tagViews[i].text = tag
                            tagViews[i].visibility = View.VISIBLE
                        }
                    }

                    updateFavoritoUI()
                }
            }

            override fun onFailure(call: Call<StartupDetalhesResponse>, t: Throwable) {
                Toast.makeText(this@StartupDetalhesActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateFavoritoUI() {
        val btnFavoritar = findViewById<MaterialButton>(R.id.btnFavoritar)
        val favoritoLayout = findViewById<LinearLayout>(R.id.favoritoMessageLayout)
        val mensagemLayout = findViewById<View>(R.id.mensagemLayout)
        val btnEnviarMsg = findViewById<MaterialButton>(R.id.btnEnviarMsg)

        if (isFavoritado) {
            btnFavoritar.text = "Desfavoritar"
            btnFavoritar.setBackgroundColor(Color.parseColor("#4B2C7F"))
            favoritoLayout.visibility = View.VISIBLE
            mensagemLayout.visibility = View.VISIBLE
            btnEnviarMsg.visibility = View.VISIBLE

            findViewById<TextView>(R.id.txtFavoritoMsg).text =
                "Você favoritou\n$startupNome!\nMande uma\nmensagem para\nfazer negócio!"

            btnFavoritar.setOnClickListener {
                desfavoritar()
            }

            btnEnviarMsg.setOnClickListener {
                val texto = findViewById<EditText>(R.id.etMensagem).text.toString().trim()
                if (texto.isEmpty()) {
                    Toast.makeText(this, "Escreva uma mensagem", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                enviarMensagem(texto)
            }
        } else {
            btnFavoritar.text = "Favoritar"
            btnFavoritar.setBackgroundColor(Color.parseColor("#45A09E"))
            favoritoLayout.visibility = View.GONE
            mensagemLayout.visibility = View.GONE
            btnEnviarMsg.visibility = View.GONE

            btnFavoritar.setOnClickListener {
                favoritar()
            }
        }
    }

    private fun favoritar() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.favoritar(usuarioId, startupId).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isFavoritado = true
                    updateFavoritoUI()
                    Toast.makeText(this@StartupDetalhesActivity, "Startup favoritada!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@StartupDetalhesActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun desfavoritar() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.desfavoritar(usuarioId, startupId).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isFavoritado = false
                    updateFavoritoUI()
                    Toast.makeText(this@StartupDetalhesActivity, "Startup desfavoritada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@StartupDetalhesActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun enviarMensagem(texto: String) {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.enviarMensagem(usuarioId, donoId, startupId, texto).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    findViewById<EditText>(R.id.etMensagem).text.clear()
                    Toast.makeText(this@StartupDetalhesActivity, "Mensagem enviada!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@StartupDetalhesActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHomeContainer).setOnClickListener { finish() }
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }
        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            val intent = Intent(this, FavoritosActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            val intent = Intent(this, NotificacoesActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }
    }
}
