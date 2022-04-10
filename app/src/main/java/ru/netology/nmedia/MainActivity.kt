package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
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
        val group: Group = findViewById(R.id.groupIsNotVisible)
        group.visibility = View.GONE
        viewModel.editPost.observe(this) { post: Post? ->
            val content = post?.content ?: ""
            group.visibility = View.VISIBLE
            binding.contentEditText.setText(content)
            group.setVisibility(View.VISIBLE)
        }
        binding.cancelButton.setOnClickListener {
            with(binding.contentEditText) {
                setText("")// TODO так делать не надо
                clearFocus()
                hideKeyboard()
                group.visibility =View.GONE
            }
        }
        binding.saveButton.setOnClickListener {
            with(binding.contentEditText) {
                val content = text.toString()
                viewModel.onSaveButtonClicked(content)
                setText("")// TODO так делать не надо
                clearFocus()
                hideKeyboard()
                group.visibility =View.GONE
            }
        }
    }
}