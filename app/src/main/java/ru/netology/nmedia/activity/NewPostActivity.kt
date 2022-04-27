package ru.netology.nmedia.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import ru.netology.nmedia.databinding.NewPostActivityBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = NewPostActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding.edit) {

            val contentPost = intent.getStringExtra(EDIT_POST_CONTENT_KEY)
            setText(contentPost)
        }
        binding.ok.setOnClickListener {
            onOkButtonClick(binding.edit.text)
        }
    }

    private fun onOkButtonClick(text: Editable) {
        val intent = Intent()
        if (text.isBlank()) {
            setResult(RESULT_CANCELED, intent)
        } else {
            intent.putExtra(EDIT_POST_CONTENT_KEY, text.toString())
            setResult(RESULT_OK, intent)
        }
        finish()
    }


    private companion object {
        const val EDIT_POST_CONTENT_KEY = "editPostContent"
    }
}