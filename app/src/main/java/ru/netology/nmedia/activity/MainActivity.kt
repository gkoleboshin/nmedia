package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.hideKeyboard
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

        viewModel.editPost.observe(this) { post: Post? ->
            val content = post?.content ?: ""
            val intent:Intent  = Intent(this,NewPostActivity::class.java)
            intent.putExtra(EDIT_POST_CONTENT_KEY,content)
            startActivity(intent)
            viewModel.onSaveButtonClicked(addContentText())
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
            startActivity(intent)
        }



    }

    private fun addContentText():String {
        val intentGet:Intent = getIntent()
        val newPostContent = intent.getStringExtra(EDIT_POST_CONTENT_KEY)
        if (newPostContent!=null) return newPostContent else return ""
    }


    private companion object {
        const val NEW_POST_CONTENT_KEY = "NewPostContent"
        const val EDIT_POST_CONTENT_KEY ="EditPostContent"
    }
}