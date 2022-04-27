package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(viewModel)
        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        val getMessage = registerForActivityResult(EditMessageResualtContract()) {
            if (it != null) {
                viewModel.onSaveButtonClicked(it)
            }
        }
        viewModel.editPost.observe(this) { post ->
            if (post != null) {
                val contentPost = post.content
                getMessage.launch(contentPost)
            }
        }

        viewModel.videoUrl.observe(this) { id ->
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id)
            )
            try {
               startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(webIntent)
            }
        }

        viewModel.shareEvent.observe(this) { postContent ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postContent)
                type = "text/plain"
            }
            val shareIntent =
                Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }

        binding.fab.setOnClickListener {
            getMessage.launch("")
        }

    }
}