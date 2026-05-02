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

class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatListRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: ""

        findViewById<TextView>(R.id.txtChatTipo).text = "@${usuarioTipo.lowercase()}"

        recyclerView = findViewById(R.id.chatsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchChats()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchChats() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getChats(usuarioId).enqueue(object : Callback<List<ChatResponse>> {
            override fun onResponse(call: Call<List<ChatResponse>>, response: Response<List<ChatResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val chats = response.body()!!
                    if (chats.isEmpty()) {
                        findViewById<TextView>(R.id.txtChatsVazio).visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.txtChatsVazio).visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.adapter = ChatAdapter(chats) { chat ->
                            val intent = Intent(this@ChatListActivity, ChatConversaActivity::class.java)
                            intent.putExtra("usuarioId", usuarioId)
                            intent.putExtra("usuarioTipo", usuarioTipo)
                            intent.putExtra("startupId", chat.startupId)
                            intent.putExtra("startupNome", chat.startupNome)
                            intent.putExtra("outroUsuarioId", chat.outroUsuarioId)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ChatResponse>>, t: Throwable) {
                Toast.makeText(this@ChatListActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
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
            selectItem(it, findViewById(R.id.navChatIcon))
        }
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            startActivity(Intent(this, NotificacoesActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            startActivity(Intent(this, IntroActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        selectItem(findViewById(R.id.navChatContainer), findViewById(R.id.navChatIcon))
    }
}
