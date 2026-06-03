package com.example.startconnect

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateStartupActivity : AppCompatActivity() {

    private var usuarioId: Int = -1
    private var startupId: Int = -1
    private var isEditMode: Boolean = false
    private var usuarioTipo: String = "Empreendedor"

    private lateinit var etNomeStartup: EditText
    private lateinit var etDescricaoStartup: EditText
    private lateinit var rgTipoStartup: RadioGroup
    private lateinit var etOutraTagStartup: EditText
    private lateinit var cbTagSaude: CheckBox
    private lateinit var cbTagAlimentacao: CheckBox
    private lateinit var cbTagEntretenimento: CheckBox
    private lateinit var cbTagTech: CheckBox
    private lateinit var cbTagMeioAmbiente: CheckBox
    private lateinit var cbTagOutro: CheckBox

    private var initialNome: String = ""
    private var initialDescricao: String = ""
    private var initialTipo: String = ""
    private var initialTags: String = ""

    private val tagOptionsPadrao = listOf(
        "Saúde",
        "Alimentação",
        "Entretenimento",
        "Tech",
        "Meio-Ambiente"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_startup)

        val mainView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        startupId = intent.getIntExtra("startupId", -1)
        isEditMode = startupId != -1
        usuarioTipo = intent.getStringExtra("usuarioTipo") ?: "Empreendedor"

        etNomeStartup = findViewById(R.id.etNomeStartup)
        etDescricaoStartup = findViewById(R.id.etDescricaoStartup)
        rgTipoStartup = findViewById(R.id.rgTipoStartup)
        etOutraTagStartup = findViewById(R.id.etOutraTagStartup)
        cbTagSaude = findViewById(R.id.cbTagSaude)
        cbTagAlimentacao = findViewById(R.id.cbTagAlimentacao)
        cbTagEntretenimento = findViewById(R.id.cbTagEntretenimento)
        cbTagTech = findViewById(R.id.cbTagTech)
        cbTagMeioAmbiente = findViewById(R.id.cbTagMeioAmbiente)
        cbTagOutro = findViewById(R.id.cbTagOutro)

        val btnSalvarStartup = findViewById<MaterialButton>(R.id.btnSalvarStartup)
        val btnApagarStartup = findViewById<MaterialButton>(R.id.btnApagarStartup)
        val txtCreateStartupTitle = findViewById<TextView>(R.id.txtCreateStartupTitle)
        val txtCreateStartupHandle = findViewById<TextView>(R.id.txtCreateStartupHandle)

        setupTipoOptions()
        setupOutroTag()
        loadProfileInfo(txtCreateStartupHandle)

        if (isEditMode) {
            txtCreateStartupTitle.text = "Editar Startup"
            txtCreateStartupTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            btnSalvarStartup.text = "Salvar Alterações"
            btnApagarStartup.visibility = View.VISIBLE

            etNomeStartup.setText(intent.getStringExtra("startupNome").orEmpty())
            etDescricaoStartup.setText(intent.getStringExtra("startupSubtitulo").orEmpty())

            val tipoExistente = intent.getStringExtra("startupSegmento").orEmpty()
            setSelectedTipo(tipoExistente)

            val tags = intent.getStringArrayListExtra("startupTags").orEmpty()
            preencherTagsExistentes(tags)
        }

        saveInitialValues()

        btnApagarStartup.setOnClickListener {
            showDeleteStartupDialog()
        }

        btnSalvarStartup.setOnClickListener {
            salvarStartup()
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

    private fun setupTipoOptions() {
        rgTipoStartup.clearCheck()
    }

    private fun setSelectedTipo(tipo: String) {
        val radioId = when (tipo.lowercase()) {
            "escalavel", "escalável" -> R.id.rbTipoEscalavel
            "pequeno negocio", "pequeno negócio" -> R.id.rbTipoPequenoNegocio
            "compravel", "comprável" -> R.id.rbTipoCompravel
            "social" -> R.id.rbTipoSocial
            else -> -1
        }

        if (radioId == -1) {
            rgTipoStartup.clearCheck()
        } else {
            rgTipoStartup.check(radioId)
        }
    }

    private fun getSelectedTipo(): String {
        return when (rgTipoStartup.checkedRadioButtonId) {
            R.id.rbTipoEscalavel -> "Escalável"
            R.id.rbTipoPequenoNegocio -> "Pequeno negócio"
            R.id.rbTipoCompravel -> "Comprável"
            R.id.rbTipoSocial -> "Social"
            else -> ""
        }
    }

    private fun setupOutroTag() {
        cbTagOutro.setOnCheckedChangeListener { _, isChecked ->
            etOutraTagStartup.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                etOutraTagStartup.text.clear()
            }
        }
    }

    private fun preencherTagsExistentes(tags: List<String>) {
        cbTagSaude.isChecked = tags.any { it.equals("Saude", ignoreCase = true) || it.equals("Saúde", ignoreCase = true) }
        cbTagAlimentacao.isChecked = tags.any { it.equals("Alimentacao", ignoreCase = true) || it.equals("Alimentação", ignoreCase = true) }
        cbTagEntretenimento.isChecked = tags.any { it.equals("Entretenimento", ignoreCase = true) }
        cbTagTech.isChecked = tags.any { it.equals("Tech", ignoreCase = true) }
        cbTagMeioAmbiente.isChecked = tags.any { it.equals("Meio-Ambiente", ignoreCase = true) }

        val outraTag = tags.firstOrNull { tag ->
            tagOptionsPadrao.none { it.equals(tag, ignoreCase = true) || stripAccents(it).equals(stripAccents(tag), ignoreCase = true) }
        }

        if (!outraTag.isNullOrBlank()) {
            cbTagOutro.isChecked = true
            etOutraTagStartup.visibility = View.VISIBLE
            etOutraTagStartup.setText(outraTag)
        }
    }

    private fun salvarStartup() {
        val nome = etNomeStartup.text.toString().trim()
        val descricao = etDescricaoStartup.text.toString().trim()
        val tipoStartup = getSelectedTipo()
        val tagsSelecionadas = getSelectedTags()

        if (usuarioId == -1) {
            Toast.makeText(this, "Usuário inválido", Toast.LENGTH_LONG).show()
            return
        }

        if (nome.isEmpty() || tipoStartup.isEmpty()) {
            Toast.makeText(this, "Preencha nome e tipo de startup", Toast.LENGTH_LONG).show()
            return
        }

        if (cbTagOutro.isChecked && etOutraTagStartup.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Preencha a tag personalizada ou desmarque Outro", Toast.LENGTH_LONG).show()
            return
        }

        if (tagsSelecionadas.size > 4) {
            Toast.makeText(this, "Escolha no máximo 4 tags", Toast.LENGTH_LONG).show()
            return
        }

        val tag1 = tagsSelecionadas.getOrElse(0) { "" }
        val tag2 = tagsSelecionadas.getOrElse(1) { "" }
        val tag3 = tagsSelecionadas.getOrElse(2) { "" }
        val tag4 = tagsSelecionadas.getOrElse(3) { "" }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val subtitulo = if (descricao.isBlank()) "Clique para ver mais" else descricao

        val call = if (isEditMode) {
            apiService.updateStartup(
                startupId = startupId,
                usuarioId = usuarioId,
                nome = nome,
                segmento = tipoStartup,
                subtitulo = subtitulo,
                tag1 = tag1,
                tag2 = tag2,
                tag3 = tag3,
                tag4 = tag4
            )
        } else {
            apiService.registerStartup(
                usuarioId = usuarioId,
                nome = nome,
                segmento = tipoStartup,
                subtitulo = subtitulo,
                tag1 = tag1,
                tag2 = tag2,
                tag3 = tag3,
                tag4 = tag4
            )
        }

        call.enqueue(object : Callback<StartupRegisterResponse> {
            override fun onResponse(
                call: Call<StartupRegisterResponse>,
                response: Response<StartupRegisterResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val startupResponse = response.body()!!
                    Toast.makeText(
                        this@CreateStartupActivity,
                        startupResponse.message,
                        Toast.LENGTH_LONG
                    ).show()

                    if (startupResponse.success) {
                        saveInitialValues()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@CreateStartupActivity,
                        "Erro no servidor",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<StartupRegisterResponse>, t: Throwable) {
                Toast.makeText(
                    this@CreateStartupActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getSelectedTags(): List<String> {
        val tags = mutableListOf<String>()

        if (cbTagSaude.isChecked) tags.add("Saúde")
        if (cbTagAlimentacao.isChecked) tags.add("Alimentação")
        if (cbTagEntretenimento.isChecked) tags.add("Entretenimento")
        if (cbTagTech.isChecked) tags.add("Tech")
        if (cbTagMeioAmbiente.isChecked) tags.add("Meio-Ambiente")

        if (cbTagOutro.isChecked) {
            val outra = etOutraTagStartup.text.toString().trim()
            if (outra.isNotEmpty()) {
                tags.add(outra)
            }
        }

        return tags
    }

    private fun stripAccents(value: String): String = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD).replace("\\p{Mn}+".toRegex(), "")

    private fun showDeleteStartupDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_apagar_startup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<MaterialButton>(R.id.btnConfirmarApagarStartup).setOnClickListener {
            deleteStartup(dialog)
        }

        dialog.findViewById<MaterialButton>(R.id.btnCancelarApagarStartup).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteStartup(dialog: Dialog) {
        if (usuarioId == -1 || startupId == -1) {
            Toast.makeText(this, "Startup invalida para apagar", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.deleteStartup(startupId, usuarioId).enqueue(object : Callback<StartupRegisterResponse> {
            override fun onResponse(
                call: Call<StartupRegisterResponse>,
                response: Response<StartupRegisterResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val deleteResponse = response.body()!!
                    Toast.makeText(
                        this@CreateStartupActivity,
                        deleteResponse.message,
                        Toast.LENGTH_LONG
                    ).show()

                    if (deleteResponse.success) {
                        dialog.dismiss()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@CreateStartupActivity,
                        "Erro ao apagar startup",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<StartupRegisterResponse>, t: Throwable) {
                Toast.makeText(
                    this@CreateStartupActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun loadProfileInfo(handleTextView: TextView) {
        if (usuarioId == -1) return

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getPerfil(usuarioId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    handleTextView.text = "@${response.body()!!.usuarioNome}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                handleTextView.text = "@usuario"
            }
        })
    }

    private fun saveInitialValues() {
        initialNome = etNomeStartup.text.toString().trim()
        initialDescricao = etDescricaoStartup.text.toString().trim()
        initialTipo = getSelectedTipo()
        initialTags = getSelectedTags().joinToString(", ")
    }

    private fun hasUnsavedChanges(): Boolean {
        return etNomeStartup.text.toString().trim() != initialNome ||
            etDescricaoStartup.text.toString().trim() != initialDescricao ||
            getSelectedTipo() != initialTipo ||
            getSelectedTags().joinToString(", ") != initialTags
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

    private fun openHeartDestination() {
        val destination = if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
            FavoritosActivity::class.java
        } else {
            ConexoesActivity::class.java
        }
        navigateWithUnsavedCheck(destination)
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
                    .alpha(if (isSelected) 1f else 0.85f)
                    .setDuration(180)
                    .start()
            }
        }

        findViewById<View>(R.id.navHomeContainer).setOnClickListener {
            selectItem(it, findViewById(R.id.navHomeIcon))
            val destination = if (usuarioTipo.equals("Investidor", ignoreCase = true)) {
                HomeInvestidorActivity::class.java
            } else {
                AddStartupActivity::class.java
            }
            navigateWithUnsavedCheck(destination)
        }

        findViewById<View>(R.id.navContaContainer).setOnClickListener {
            navigateWithUnsavedCheck(PerfilActivity::class.java)
        }

        findViewById<View>(R.id.navChatContainer).setOnClickListener {
            navigateWithUnsavedCheck(ChatListActivity::class.java)
        }

        findViewById<View>(R.id.navFavoritosContainer).setOnClickListener {
            openHeartDestination()
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
