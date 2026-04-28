package com.example.startconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val goToRegisterText: TextView = findViewById(R.id.goToRegisterText)

        goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.102/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.login(email, password)

            call.enqueue(object : Callback<List<LoginResponse>> {
                override fun onResponse(
                    call: Call<List<LoginResponse>>,
                    response: Response<List<LoginResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponses = response.body()!!

                        if (loginResponses.isNotEmpty()) {
                            val usuario = loginResponses.first()

                            val destination = if (usuario.usuarioTipo.equals("Investidor", ignoreCase = true)) {
                                HomeInvestidorActivity::class.java
                            } else {
                                AddStartupActivity::class.java
                            }

                            val intent = Intent(this@LoginActivity, destination)
                            intent.putExtra("usuarioId", usuario.usuarioId)
                            intent.putExtra("usuarioTipo", usuario.usuarioTipo)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Usuario ou senha invalidos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Erro no servidor",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Falha na conexao: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}

interface ApiService {
    @GET("/meu_projeto_pi/login.php")
    fun login(
        @Query("usuario") usuario: String,
        @Query("senha") senha: String
    ): Call<List<LoginResponse>>

    @GET("/meu_projeto_pi/listar_startups.php")
    fun getStartups(): Call<List<StartupResponse>>

    @GET("/meu_projeto_pi/listar_startups.php")
    fun getStartupsByUser(
        @Query("usuario_id") usuarioId: Int
    ): Call<List<StartupResponse>>

    @FormUrlEncoded
    @POST("/meu_projeto_pi/cadastro.php")
    fun register(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
        @Field("tipo") tipo: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/meu_projeto_pi/cadastro_startup.php")
    fun registerStartup(
        @Field("usuario_id") usuarioId: Int,
        @Field("nome") nome: String,
        @Field("segmento") segmento: String,
        @Field("subtitulo") subtitulo: String,
        @Field("tag1") tag1: String,
        @Field("tag2") tag2: String,
        @Field("tag3") tag3: String,
        @Field("tag4") tag4: String
    ): Call<StartupRegisterResponse>
}
