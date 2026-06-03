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
import androidx.activity.addCallback
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
    private lateinit var etPerfilNome: EditText
    private lateinit var etPerfilEmail: EditText
    private var initialNome: String = ""
    private var initialEmail: String = ""

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
        etPerfilNome = findViewById(R.id.txtPerfilNome)
        etPerfilEmail = findViewById(R.id.txtPerfilEmail)

        findViewById<View>(R.id.btnEditarFotoPerfil).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        loadProfilePhoto()

        fetchPerfil()

        findViewById<View>(R.id.btnMudarSenha).setOnClickListener {
            showAlterarSenhaDialog()
        }

        findViewById<View>(R.id.btnApagarConta).setOnClickListener {
            showApagarContaDialog()
        }

        findViewById<MaterialButton>(R.id.btnSalvarPerfil).setOnClickListener {
            salvarPerfil()
        }

        setupBottomNavigation()

        onBackPressedDispatcher.addCallback(this) {
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog { finish() }
            } else {
                finish()
            }
        }
    }

    private fun loadProfilePhoto() {
        val bitmap = ProfilePhotoHelper.getPhotoBitmap(this, usuarioId)
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap)
        } else {
            profileImage.setImageResource(R.drawable.foto_vazia)
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchPerfil() {
        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.getPerfil(usuarioId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    etPerfilNome.setText(user.usuarioNome)
                    etPerfilEmail.setText(user.usuarioEmail)
                    findViewById<TextView>(R.id.txtPerfilTipo).text = "@${user.usuarioNome}"
                    initialNome = user.usuarioNome
                    initialEmail = user.usuarioEmail
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@PerfilActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
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
            apiService.alterarSenha(usuarioId, senhaAtual, novaSenha)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val resp = response.body()!!
                            Toast.makeText(
                                this@PerfilActivity,
                                resp.message,
                                Toast.LENGTH_LONG
                            ).show()
                            if (resp.success) {
                                dialog.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(
                            this@PerfilActivity,
                            "Falha na conexão: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        dialog.show()
    }

    private fun salvarPerfil() {
        val nome = etPerfilNome.text.toString().trim()
        val email = etPerfilEmail.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha nome e email", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = getRetrofit().create(ApiService::class.java)
        apiService.updatePerfil(usuarioId, nome, email).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    Toast.makeText(this@PerfilActivity, resp.message, Toast.LENGTH_LONG).show()
                    if (resp.success) {
                        initialNome = nome
                        initialEmail = email
                        findViewById<TextView>(R.id.txtPerfilTipo).text = "@$nome"
                    }
                } else {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Erro ao salvar perfil (${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun hasUnsavedChanges(): Boolean {
        return etPerfilNome.text.toString().trim() != initialNome ||
            etPerfilEmail.text.toString().trim() != initialEmail
    }

    private fun showUnsavedChangesDialog(onConfirmExit: () -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirmar_saida)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<MaterialButton>(R.id.btnConfirmarSaida).setOnClickListener {
            dialog.dismiss()
            onConfirmExit()
        }

        dialog.findViewById<MaterialButton>(R.id.btnCancelarSaida).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navigateWithUnsavedCheck(destination: Class<*>) {
        val action = {
            val intent = Intent(this, destination)
            intent.putExtra("usuarioId", usuarioId)
            intent.putExtra("usuarioTipo", usuarioTipo)
            startActivity(intent)
            finish()
        }

        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog(action)
        } else {
            action()
        }
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
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val resp = response.body()!!
                        if (resp.success) {
                            dialog.dismiss()
                            showContaApagadaDialog()
                        } else {
                            Toast.makeText(
                                this@PerfilActivity,
                                resp.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
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
        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            val destination = if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
                HomeInvestidorActivity::class.java
            } else {
                AddStartupActivity::class.java
            }
            navigateWithUnsavedCheck(destination)
        }

        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navContaIcon))
        }

        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            navigateWithUnsavedCheck(ChatListActivity::class.java)
        }

        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
                navigateWithUnsavedCheck(FavoritosActivity::class.java)
            } else {
                navigateWithUnsavedCheck(ConexoesActivity::class.java)
            }
        }

        findViewById<View>(R.id.navNotificacoesContainer).setOnClickListener {
            navigateWithUnsavedCheck(NotificacoesActivity::class.java)
        }

        findViewById<View>(R.id.navSairContainer).setOnClickListener {
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog { showLogoutDialog() }
            } else {
                showLogoutDialog()
            }
        }

        selectItem(findViewById(R.id.navContaContainer), findViewById(R.id.navContaIcon))
    }

}
