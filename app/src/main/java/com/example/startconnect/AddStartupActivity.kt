package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
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

class AddStartupActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnAbrirAddStartup: MaterialButton
    private lateinit var btnAbrirAddStartupFooter: MaterialButton
    private lateinit var txtHeaderHandle: TextView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = "Empreendedor"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_startup)

        val mainView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: "Empreendedor"

        recyclerView = findViewById(R.id.entrepreneurStartupsRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        btnAbrirAddStartup = findViewById(R.id.btnAbrirAddStartup)
        btnAbrirAddStartupFooter = findViewById(R.id.btnAbrirAddStartupFooter)
        txtHeaderHandle = findViewById(R.id.txtHeaderHandle)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val openCreateStartup: (Startup?) -> Unit = { startup ->
            val intent = Intent(this, CreateStartupActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)

            if (startup != null) {
                intent.putExtra("startupId", startup.startupId)
                intent.putExtra("startupNome", startup.nome)
                intent.putExtra("startupSegmento", startup.segmento)
                intent.putExtra("startupSubtitulo", startup.subtitulo)
                intent.putStringArrayListExtra("startupTags", ArrayList(startup.tags))
            }

            startActivity(intent)
        }

        btnAbrirAddStartup.setOnClickListener { openCreateStartup(null) }
        btnAbrirAddStartupFooter.setOnClickListener { openCreateStartup(null) }

        findViewById<View>(R.id.btnHeaderProfileEdit).setOnClickListener {
            openPerfil()
        }

        loadProfilePhoto()
        loadProfileInfo()
        setupBottomNavigation()
    }

    private fun loadProfilePhoto() {
        val bitmap = ProfilePhotoHelper.getPhotoBitmap(this, usuarioId)
        if (bitmap != null) {
            findViewById<ImageView>(R.id.imgHeaderProfile).setImageBitmap(bitmap)
        }
    }

    private fun loadProfileInfo() {
        if (usuarioId == -1) return

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.100/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getPerfil(usuarioId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
                    txtHeaderHandle.text = "@${usuario.usuarioNome}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                txtHeaderHandle.text = "@Empreendedor"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchUserStartups()
    }

    private fun fetchUserStartups() {
        if (usuarioId == -1) {
            Toast.makeText(this, "Usuário inválido", Toast.LENGTH_LONG).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.100/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getStartupsByUser(usuarioId).enqueue(object : Callback<List<StartupResponse>> {
            override fun onResponse(
                call: Call<List<StartupResponse>>,
                response: Response<List<StartupResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val startups = response.body()!!.map { startup ->
                        Startup(
                            startupId = startup.startupId,
                            nome = startup.nome,
                            segmento = startup.segmento,
                            subtitulo = startup.subtitulo,
                            tags = listOfNotNull(
                                startup.tag1?.takeIf { it.isNotBlank() },
                                startup.tag2?.takeIf { it.isNotBlank() },
                                startup.tag3?.takeIf { it.isNotBlank() },
                                startup.tag4?.takeIf { it.isNotBlank() }
                            )
                        )
                    }

                    if (startups.isEmpty()) {
                        emptyStateLayout.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        btnAbrirAddStartupFooter.visibility = View.GONE
                    } else {
                        emptyStateLayout.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        btnAbrirAddStartupFooter.visibility = View.VISIBLE
                        recyclerView.adapter = StartupAdapter(
                            startups = startups,
                            showEditAction = true,
                            onEditClick = { startup ->
                                val intent = Intent(this@AddStartupActivity, CreateStartupActivity::class.java)
                                intent.putExtra("usuarioId", usuarioId)
                                intent.putExtra("startupId", startup.startupId)
                                intent.putExtra("startupNome", startup.nome)
                                intent.putExtra("startupSegmento", startup.segmento)
                                intent.putExtra("startupSubtitulo", startup.subtitulo)
                                intent.putStringArrayListExtra("startupTags", ArrayList(startup.tags))
                                startActivity(intent)
                            }
                        )
                    }
                } else {
                    Toast.makeText(this@AddStartupActivity, "Erro ao carregar startups", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<StartupResponse>>, t: Throwable) {
                Toast.makeText(this@AddStartupActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_LONG).show()
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

                container.animate()
                    .scaleX(if (isSelected) 1.08f else 1f)
                    .scaleY(if (isSelected) 1.08f else 1f)
                    .setDuration(180)
                    .start()

                icon.animate()
                    .alpha(if (isSelected) 1f else 0.88f)
                    .setDuration(180)
                    .start()
            }
        }

        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navHomeIcon))
        }

        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            openPerfil()
        }

        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            openHeartDestination()
        }

        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            val intent = Intent(this, NotificacoesActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            showLogoutDialog()
        }

        selectItem(findViewById(R.id.navHomeContainer), findViewById(R.id.navHomeIcon))
    }

    private fun openPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("usuarioTipo", usuarioTipo)
        startActivity(intent)
    }

    private fun openHeartDestination() {
        val destination = if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
            FavoritosActivity::class.java
        } else {
            ConexoesActivity::class.java
        }

        val intent = Intent(this, destination)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("usuarioTipo", usuarioTipo)
        startActivity(intent)
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
}