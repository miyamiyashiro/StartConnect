package com.example.startconnect

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddStartupActivity : AppCompatActivity() {

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

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHomeContainer = findViewById<View>(R.id.navHomeContainer)
        val navContaContainer = findViewById<View>(R.id.navContaContainer)
        val navChatContainer = findViewById<View>(R.id.navChatContainer)
        val navFavoritosContainer = findViewById<View>(R.id.navFavoritosContainer)
        val navNotificacoesContainer = findViewById<View>(R.id.navNotificacoesContainer)
        val navSairContainer = findViewById<View>(R.id.navSairContainer)

        val navHomeIcon = findViewById<ImageView>(R.id.navHomeIcon)
        val navContaIcon = findViewById<ImageView>(R.id.navContaIcon)
        val navChatIcon = findViewById<ImageView>(R.id.navChatIcon)
        val navFavoritosIcon = findViewById<ImageView>(R.id.navFavoritosIcon)
        val navNotificacoesIcon = findViewById<ImageView>(R.id.navNotificacoesIcon)
        val navSairIcon = findViewById<ImageView>(R.id.navSairIcon)

        val menuItems = listOf(
            navHomeContainer to navHomeIcon,
            navContaContainer to navContaIcon,
            navChatContainer to navChatIcon,
            navFavoritosContainer to navFavoritosIcon,
            navNotificacoesContainer to navNotificacoesIcon,
            navSairContainer to navSairIcon
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

        menuItems.forEach { (container, icon) ->
            container.setOnClickListener {
                selectItem(container, icon)
            }
        }

        selectItem(navHomeContainer, navHomeIcon)
    }
}
