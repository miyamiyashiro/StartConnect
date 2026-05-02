package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PerfilActivity : AppCompatActivity() {

    private var usuarioId: Int = -1
    private var usuarioTipo: String = ""
    private lateinit var profileImage: ShapeableImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ProfilePhotoHelper.savePhoto(this, usuarioId, it)
            loadProfilePhoto()
            Toast.makeText(this, "Foto atualizada!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfilRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: ""

        profileImage = findViewById(R.id.imgPerfil)
        profileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        loadProfilePhoto()

        val txtPerfilTipo = findViewById<TextView>(R.id.txtPerfilTipo)
        txtPerfilTipo.text = "@${usuarioTipo.lowercase()}"

        fetchPerfil()

        findViewById<View>(R.id.btnMudarSenha).setOnClickListener {
            showAlterarSenhaDialog()
        }

        findViewById<View>(R.id.btnApagarConta).setOnClickListener {
            showApagarContaDialog()
        }

        setupBottomNavigation()
    }

    private fun loadProfilePhoto() {
        val bitmap = ProfilePhotoHelper.getPhotoBitmap(this, usuarioId)
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap)
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchPerfil() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getPerfil(usuarioId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    findViewById<TextView>(R.id.txtPerfilNome).text = user.usuarioNome
                    findViewById<TextView>(R.id.txtPerfilEmail).text = user.usuarioEmail
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Falha na conexao: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAlterarSenhaDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alterar_senha)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etSenhaAtual = dialog.findViewById<EditText>(R.id.etSenhaAtual)
        val etNovaSenha = dialog.findViewById<EditText>(R.id.etNovaSenha)
        val etConfirma = dialog.findViewById<EditText>(R.id.etConfirmaNovaSenha)

        dialog.findViewById<View>(R.id.btnFecharAlterarSenha).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<MaterialButton>(R.id.btnSalvarSenha).setOnClickListener {
            val senhaAtual = etSenhaAtual.text.toString().trim()
            val novaSenha = etNovaSenha.text.toString().trim()
            val confirma = etConfirma.text.toString().trim()

            if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaSenha != confirma) {
                Toast.makeText(this, "As senhas nao coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiService = getRetrofit().create(ApiService::class.java)
            apiService.alterarSenha(usuarioId, senhaAtual, novaSenha).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val resp = response.body()!!
                        Toast.makeText(this@PerfilActivity, resp.message, Toast.LENGTH_LONG).show()
                        if (resp.success) {
                            dialog.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Falha na conexao: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        dialog.show()
    }

    private fun showApagarContaDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_apagar_conta)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<View>(R.id.btnFecharApagarConta).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<MaterialButton>(R.id.btnCancelarApagar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<MaterialButton>(R.id.btnConfirmarApagar).setOnClickListener {
            val apiService = getRetrofit().create(ApiService::class.java)
            apiService.apagarConta(usuarioId).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val resp = response.body()!!
                        if (resp.success) {
                            dialog.dismiss()
                            showContaApagadaDialog()
                        } else {
                            Toast.makeText(this@PerfilActivity, resp.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Falha na conexao: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        dialog.show()
    }

    private fun showContaApagadaDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_conta_apagada)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        dialog.findViewById<View>(R.id.btnFecharContaApagada).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@PerfilActivity, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
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
            val intent = Intent(this@PerfilActivity, IntroActivity::class.java)
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

        // Home -> volta pra tela principal
        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            val destination = if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
                HomeInvestidorActivity::class.java
            } else {
                AddStartupActivity::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
            finish()
        }

        // Conta -> ja esta aqui
        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navContaIcon))
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

        // Sair -> logout
        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            showLogoutDialog()
        }

        // Seleciona Conta como ativo
        selectItem(findViewById(R.id.navContaContainer), findViewById(R.id.navContaIcon))
    }
}
