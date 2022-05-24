package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import ru.netology.nmedia.NavGraphDirections
import ru.netology.nmedia.R


class AppActivity : AppCompatActivity(R.layout.app_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent ?: return
        if (intent.action != Intent.ACTION_SEND) return

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (text.isNullOrBlank()) return

        intent.removeExtra(Intent.EXTRA_TEXT)
        val direction = NavGraphDirections.globalToNewPostFragment(text)
        findNavController(R.id.nav_host_fragment).navigate(direction)
    }
}

