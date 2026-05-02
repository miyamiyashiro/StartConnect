package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
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

class HomeInvestidorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var usuarioId: Int = -1
    private var usuarioTipo: String = "Investidor"
    private var allStartups: List<Startup> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_investidor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.investorHomeRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: "Investidor"

        setupRecyclerView()
        setupSearch()
        fetchStartups()
        setupBottomNavigation()
        loadProfilePhoto()
    }

    private fun loadProfilePhoto() {
        val bitmap = ProfilePhotoHelper.getPhotoBitmap(this, usuarioId)
        if (bitmap != null) {
            findViewById<ImageView>(R.id.imgHeaderProfile).setImageBitmap(bitmap)
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.startupsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                if (query.isEmpty()) {
                    showStartups(allStartups)
                } else {
                    val filtered = allStartups.filter { startup ->
                        startup.nome.lowercase().contains(query) ||
                        startup.segmento.lowercase().contains(query) ||
                        startup.subtitulo.lowercase().contains(query) ||
                        startup.tags.any { it.lowercase().contains(query) }
                    }
                    showStartups(filtered)
                }
            }
        })
    }

    private fun showStartups(startups: List<Startup>) {
        recyclerView.adapter = StartupAdapter(startups) { startup ->
            val intent = Intent(this@HomeInvestidorActivity, StartupDetalhesActivity::class.java)
            intent.putExtra("startupId", startup.startupId)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }
    }

    private fun fetchStartups() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getStartups().enqueue(object : Callback<List<StartupResponse>> {
            override fun onResponse(
                call: Call<List<StartupResponse>>,
                response: Response<List<StartupResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    allStartups = response.body()!!.map { startup ->
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

                    showStartups(allStartups)
                } else {
                    Toast.makeText(this@HomeInvestidorActivity, "Erro ao carregar startups", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<StartupResponse>>, t: Throwable) {
                Toast.makeText(this@HomeInvestidorActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_LONG).show()
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

        // Home -> ja esta aqui
        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navHomeIcon))
        }

        // Conta -> vai pra PerfilActivity
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        // Chat -> vai pra ChatListActivity
        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        // Favoritos -> vai pra FavoritosActivity
        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            val intent = Intent(this, FavoritosActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        // Notificacoes -> vai pra NotificacoesActivity
        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            val intent = Intent(this, NotificacoesActivity::class.java)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
        }

        // Sair -> dialog de logout
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            showLogoutDialog()
        }

        selectItem(findViewById(R.id.navHomeContainer), findViewById(R.id.navHomeIcon))
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
