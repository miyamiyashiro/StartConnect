package com.example.startconnect

import android.os.Bundle
import android.view.View
import android.widget.ImageView
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

class HomeInvestidorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_investidor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.investorHomeRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        fetchStartups()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.startupsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchStartups() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.102/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getStartups().enqueue(object : Callback<List<StartupResponse>> {
            override fun onResponse(
                call: Call<List<StartupResponse>>,
                response: Response<List<StartupResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val startups = response.body()!!.map { startup ->
                        Startup(
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

                    recyclerView.adapter = StartupAdapter(startups)
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

        menuItems.forEach { (container, icon) ->
            container.setOnClickListener {
                selectItem(container, icon)
            }
        }

        selectItem(findViewById(R.id.navHomeContainer), findViewById(R.id.navHomeIcon))
    }
}
