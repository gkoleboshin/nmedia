package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
        viewModel.cancelEditPost.observe(this){
            binding.groupIsNotVisible.visibility = if (!it) View.INVISIBLE else View.VISIBLE
        }
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.editPost.observe(this) { post: Post? ->
            val content = post?.content ?: ""
            binding.contentEditText.setText(content)
        }
        binding.cancelButton.setOnClickListener {
            viewModel.onCancel()
            with(binding.contentEditText) {
                setText("")// TODO так делать не надо
                clearFocus()
                hideKeyboard()
            }

        }
        binding.saveButton.setOnClickListener {
            viewModel.onCancel()
            with(binding.contentEditText) {
                val content = text.toString()
                viewModel.onSaveButtonClicked(content)
                setText("")// TODO так делать не надо
                clearFocus()
                hideKeyboard()
            }

        }
    }
}