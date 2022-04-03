package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel:PostViewModel by viewModels()
        viewModel.data.observe(this){post->
        with(binding){
            author.text= post.author
            published.text=post.published
            content.text=post.content
            textShare.text= numberToString(post.share)
            textLike.text= numberToString(post.likes)
            textViews.text= numberToString(post.views)
            likeButton.setImageResource(if (!post.likedByMe) R.drawable.ic_like_24 else R.drawable.ic_liked_24)
            }
            binding.likeButton.setOnClickListener {
                viewModel.like()

            }
            binding.shareButton.setOnClickListener {
                viewModel.share()
            }
        }
      }
    }
fun numberToString(number:Int):String{
        val thousandths:Int=number/1_000
        val hundredths:Int=number/100-thousandths*10
        val milions:Int=number/1_000_000
        val hudretdthsThousandths:Int=number/100_000-milions*10
        val numberToString:String = when {
        number<1_000                                 -> number.toString()
        number<10_000 && hundredths==0               -> "$thousandths K"
        number<10_000 && hundredths>0               -> "$thousandths,$hundredths K"
        number in 10_000..999_999                  -> "$thousandths K"
        number >999_999 && hudretdthsThousandths==0->"$milions M"
        else                                       -> "$milions,$hudretdthsThousandths M"
        }
        return numberToString
}


//        println(R.string.hello) // число
//        println(getString(R.string.hello)) // "Привет, Мир!" или "Hello World!"
