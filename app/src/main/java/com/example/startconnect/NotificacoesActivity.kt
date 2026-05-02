package com.example.startconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificacoesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificacoes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notificacoesRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: ""

        findViewById<TextView>(R.id.txtNotifTipo).text = "@${usuarioTipo}"

        recyclerView = findViewById(R.id.notificacoesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchNotificacoes()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchNotificacoes() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getNotificacoes(usuarioId).enqueue(object : Callback<List<NotificacaoResponse>> {
            override fun onResponse(call: Call<List<NotificacaoResponse>>, response: Response<List<NotificacaoResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val notificacoes = response.body()!!

                    if (notificacoes.isEmpty()) {
                        findViewById<TextView>(R.id.txtNotifVazio).visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        findViewById<TextView>(R.id.txtNotifContador).text = "Você têm 0 novas\nnotificações"
                    } else {
                        findViewById<TextView>(R.id.txtNotifVazio).visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        findViewById<TextView>(R.id.txtNotifContador).text =
                            "Você têm ${notificacoes.size} nova${if (notificacoes.size > 1) "s" else ""}\nnotificaç${if (notificacoes.size > 1) "ões" else "ão"}"

                        recyclerView.adapter = NotificacaoAdapter(notificacoes) { notif ->
                            // Ao clicar na notificacao, abre o chat da startup
                            val intent = Intent(this@NotificacoesActivity, ChatConversaActivity::class.java)
                            intent.putExtra("usuarioId", usuarioId)
                            intent.putExtra("usuarioTipo", usuarioTipo)
                            intent.putExtra("startupId", notif.startupId)
                            intent.putExtra("startupNome", notif.startupNome)
                            intent.putExtra("outroUsuarioId", notif.remetenteId)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<NotificacaoResponse>>, t: Throwable) {
                Toast.makeText(this@NotificacoesActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
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
                container.animate().scaleX(if (isSelected) 1.08f else 1f).scaleY(if (isSelected) 1.08f else 1f).setDuration(180).start()
                icon.animate().alpha(if (isSelected) 1f else 0.88f).setDuration(180).start()
            }
        }

        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            val dest = if (usuarioTipo.equals("Investidor", ignoreCase = true)) HomeInvestidorActivity::class.java else AddStartupActivity::class.java
            startActivity(Intent(this, dest).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
            finish()
        }
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navNotificacoesIcon))
        }
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            startActivity(Intent(this, IntroActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        selectItem(findViewById(R.id.navNotificacoesContainer), findViewById(R.id.navNotificacoesIcon))
    }
}
