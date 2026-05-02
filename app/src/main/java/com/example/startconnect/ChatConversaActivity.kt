package com.example.startconnect

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatConversaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMensagem: EditText
    private var usuarioId: Int = -1
    private var outroUsuarioId: Int = -1
    private var startupId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_conversa)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversaRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        outroUsuarioId = intent.getIntExtra("outroUsuarioId", -1)
        startupId = intent.getIntExtra("startupId", -1)
        val startupNome = intent.getStringExtra("startupNome") ?: "Chat"

        findViewById<TextView>(R.id.txtConversaNome).text = startupNome
        findViewById<View>(R.id.btnVoltarChat).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.mensagensRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        etMensagem = findViewById(R.id.etMensagemChat)

        findViewById<MaterialButton>(R.id.btnEnviar).setOnClickListener {
            val texto = etMensagem.text.toString().trim()
            if (texto.isEmpty()) return@setOnClickListener
            enviarMensagem(texto)
        }

        fetchMensagens()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchMensagens() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getMensagens(usuarioId, startupId).enqueue(object : Callback<List<MensagemResponse>> {
            override fun onResponse(call: Call<List<MensagemResponse>>, response: Response<List<MensagemResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val mensagens = response.body()!!
                    recyclerView.adapter = MensagemAdapter(mensagens, usuarioId)
                    if (mensagens.isNotEmpty()) {
                        recyclerView.scrollToPosition(mensagens.size - 1)
                    }
                }
            }

            override fun onFailure(call: Call<List<MensagemResponse>>, t: Throwable) {
                Toast.makeText(this@ChatConversaActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun enviarMensagem(texto: String) {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.enviarMensagem(usuarioId, outroUsuarioId, startupId, texto).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    etMensagem.text.clear()
                    fetchMensagens()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@ChatConversaActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
