package com.example.startconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameEditText = findViewById<EditText>(R.id.registerNameEditText)
        val emailEditText = findViewById<EditText>(R.id.registerEmailEditText)
        val passwordEditText = findViewById<EditText>(R.id.registerPasswordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.registerConfirmPasswordEditText)
        val accountTypeDropdown = findViewById<AutoCompleteTextView>(R.id.registerAccountTypeDropdown)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val goToLoginText = findViewById<TextView>(R.id.goToLoginText)

        val accountTypes = resources.getStringArray(R.array.account_type_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, accountTypes)
        accountTypeDropdown.setAdapter(adapter)

        accountTypeDropdown.setOnClickListener {
            accountTypeDropdown.showDropDown()
        }

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val accountType = accountTypeDropdown.text.toString().trim()

            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || accountType.isEmpty() -> {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "As senhas precisam ser iguais", Toast.LENGTH_SHORT).show()
                }
                accountType != "Empreendedor" && accountType != "Investidor" -> {
                    Toast.makeText(this, "Selecione um tipo de conta valido", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://192.168.1.102/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val apiService = retrofit.create(ApiService::class.java)
                    val call = apiService.register(name, email, password, accountType)

                    call.enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val registerResponse = response.body()!!

                                Toast.makeText(
                                    this@RegisterActivity,
                                    registerResponse.message,
                                    Toast.LENGTH_LONG
                                ).show()

                                if (registerResponse.success) {
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                    finish()
                                }
                            } else {
                                Toast.makeText(this@RegisterActivity, "Erro no servidor", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(this@RegisterActivity, "Falha na conexao: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }

        goToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

