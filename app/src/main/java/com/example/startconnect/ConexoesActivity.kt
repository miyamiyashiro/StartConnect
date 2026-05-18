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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConexoesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = "Empreendedor"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conexoes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conexoesRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: "Empreendedor"

        if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
            startActivity(
                Intent(this, FavoritosActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
            finish()
            return
        }

        recyclerView = findViewById(R.id.conexoesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        findViewById<TextView>(R.id.txtConexoesTipo).text = "@empreendedor"
        findViewById<View>(R.id.btnHeaderProfileEdit).setOnClickListener {
            openPerfil()
        }

        loadProfilePhoto()
        loadProfileInfo()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchConexoes()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loadProfilePhoto() {
        val bitmap = ProfilePhotoHelper.getPhotoBitmap(this, usuarioId)
        if (bitmap != null) {
            findViewById<ImageView>(R.id.imgHeaderProfile).setImageBitmap(bitmap)
        }
    }

    private fun loadProfileInfo() {
        if (usuarioId == -1) return

        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getPerfil(usuarioId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    findViewById<TextView>(R.id.txtConexoesTipo).text =
                        "@${response.body()!!.usuarioNome}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                findViewById<TextView>(R.id.txtConexoesTipo).text = "@empreendedor"
            }
        })
    }

    private fun fetchConexoes() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getConexoes(usuarioId).enqueue(object : Callback<List<ConexaoResponse>> {
            override fun onResponse(
                call: Call<List<ConexaoResponse>>,
                response: Response<List<ConexaoResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val conexoes = response.body()!!
                    val emptyText = findViewById<TextView>(R.id.txtConexoesVazio)

                    if (conexoes.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyText.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.adapter = ConexaoAdapter(conexoes)
                    }
                } else {
                    Toast.makeText(
                        this@ConexoesActivity,
                        "Erro ao carregar conexoes",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ConexaoResponse>>, t: Throwable) {
                Toast.makeText(
                    this@ConexoesActivity,
                    "Falha na conexao: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun openPerfil() {
        startActivity(
            Intent(this, PerfilActivity::class.java)
                .putExtra("usuarioId", usuarioId)
                .putExtra("usuarioTipo", usuarioTipo)
        )
    }

    private fun openHeartDestination() {
        if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
            startActivity(
                Intent(this, FavoritosActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
        } else {
            startActivity(
                Intent(this, ConexoesActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
        }
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<MaterialButton>(R.id.btnConfirmarLogout).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dialog.findViewById<MaterialButton>(R.id.btnCancelarLogout).setOnClickListener {
            dialog.dismiss()
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
                container.animate().scaleX(if (isSelected) 1.08f else 1f)
                    .scaleY(if (isSelected) 1.08f else 1f)
                    .setDuration(180)
                    .start()
                icon.animate().alpha(if (isSelected) 1f else 0.88f).setDuration(180).start()
            }
        }

        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            startActivity(
                Intent(this, AddStartupActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
            finish()
        }
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            openPerfil()
        }
        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            startActivity(
                Intent(this, ChatListActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
        }
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navFavoritosIcon))
        }
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            startActivity(
                Intent(this, NotificacoesActivity::class.java)
                    .putExtra("usuarioId", usuarioId)
                    .putExtra("usuarioTipo", usuarioTipo)
            )
        }
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            showLogoutDialog()
        }

        selectItem(findViewById(R.id.navFavoritosContainer), findViewById(R.id.navFavoritosIcon))
    }
}