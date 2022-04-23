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


        with(binding.edit){
            registerForActivityResult(
                MainActivity.ResultConrect
            ){editPostContent->
                editPostContent?:return@registerForActivityResult
                text = setText(editPostContent)
            }
        }

        binding.ok.setOnClickListener{
            onOkButtonClick(binding.edit.text)
        }

    }

    private fun onOkButtonClick(text:Editable){
        val intent = Intent()
        if (text.isBlank()){
            setResult(Activity.RESULT_CANCELED,intent)
        }else{
            val newPostContent = text.toString()
            intent.putExtra(NEW_POST_CONTENT_KEY,newPostContent)
            setResult(Activity.RESULT_OK,intent)
        }
        finish()
    }

    object ResultConrect : ActivityResultContract<Unit, String?>() {

        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(context, NewPostActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): String? =
            intent?.takeIf {
                resultCode == Activity.RESULT_OK
            }?.getStringExtra(NEW_POST_CONTENT_KEY)

    }

    private companion object {
        const val NEW_POST_CONTENT_KEY = "newPostContent"
    }
}