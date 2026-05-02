package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
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

class FavoritosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favoritosRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: ""

        findViewById<TextView>(R.id.txtFavTipo).text = "@${usuarioTipo.lowercase()}"

        recyclerView = findViewById(R.id.favoritosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchFavoritos()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchFavoritos() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getFavoritos(usuarioId).enqueue(object : Callback<List<StartupResponse>> {
            override fun onResponse(call: Call<List<StartupResponse>>, response: Response<List<StartupResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val startups = response.body()!!.map { s ->
                        Startup(
                            startupId = s.startupId,
                            nome = s.nome,
                            segmento = s.segmento,
                            subtitulo = s.subtitulo,
                            tags = listOfNotNull(
                                s.tag1?.takeIf { it.isNotBlank() },
                                s.tag2?.takeIf { it.isNotBlank() },
                                s.tag3?.takeIf { it.isNotBlank() },
                                s.tag4?.takeIf { it.isNotBlank() }
                            )
                        )
                    }

                    if (startups.isEmpty()) {
                        findViewById<TextView>(R.id.txtFavoritosVazio).visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.txtFavoritosVazio).visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.adapter = StartupAdapter(startups) { startup ->
                            showDesfavoritarDialog(startup)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<StartupResponse>>, t: Throwable) {
                Toast.makeText(this@FavoritosActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showDesfavoritarDialog(startup: Startup) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_desfavoritar)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<TextView>(R.id.txtDesfavoritarMsg).text =
            "Tem certeza que deseja desfavoritar\n${startup.nome}?"

        dialog.findViewById<MaterialButton>(R.id.btnCancelarDesfavoritar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<MaterialButton>(R.id.btnConfirmarDesfavoritar).setOnClickListener {
            val apiService = getRetrofit().create(ApiService::class.java)
            apiService.desfavoritar(usuarioId, startup.startupId).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        dialog.dismiss()
                        showDesfavoritouDialog(startup.nome)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@FavoritosActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        dialog.show()
    }

    private fun showDesfavoritouDialog(nome: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_desfavoritou)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<TextView>(R.id.txtDesfavoritouMsg).text =
            "$nome\ndesfavoritado\ncom sucesso"

        dialog.findViewById<View>(R.id.btnFecharDesfavoritou).setOnClickListener {
            dialog.dismiss()
            fetchFavoritos()
        }

        dialog.show()
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
            val intent = Intent(this, dest)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
            finish()
        }
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navFavoritosIcon))
        }
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            startActivity(Intent(this, NotificacoesActivity::class.java).putExtra("usuarioId", usuarioId).putExtra("usuarioTipo", usuarioTipo))
        }
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            startActivity(Intent(this, IntroActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        selectItem(findViewById(R.id.navFavoritosContainer), findViewById(R.id.navFavoritosIcon))
    }
}
