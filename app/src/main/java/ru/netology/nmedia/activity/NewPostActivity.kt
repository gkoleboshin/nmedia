package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.NewPostActivityBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = NewPostActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding.edit) {
            val intent:Intent = getIntent()
            setText(intent.getStringExtra(NEW_POST_CONTENT_KEY))
        }
        binding.edit.requestFocus()
        binding.ok.setOnClickListener{
            onOkButtonClick(binding.edit.text)
        }
    }

    private fun onOkButtonClick(text:Editable){
        val intent = Intent(this,MainActivity::class.java)
        if (!text.isBlank()){
            val newPostContent = text.toString()
            intent.putExtra(EDIT_POST_CONTENT_KEY,newPostContent)
        }
        finish()
}


    private companion object {
        const val NEW_POST_CONTENT_KEY = "NewPostContent"
        const val EDIT_POST_CONTENT_KEY ="EditPostContent"
    }
}