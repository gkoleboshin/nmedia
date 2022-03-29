package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post= Post(
            id=1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            //likes = 2_000,
            //share = 1_099,
            views = 5


        )
        with(binding){
            author.text= post.author
            published.text=post.published
            content.text=post.content
            textShare.text= numberToString(post.share)
            textLike.text= numberToString(post.likes)
            textViews.text= numberToString(post.views)
            likeButton.setImageResource(if (!post.likedByMe) R.drawable.ic_like_24 else R.drawable.ic_liked_24)
            likeButton.setOnClickListener {
                post.likedByMe =!post.likedByMe
                if (post.likedByMe) post.likes++ else post.likes--
                textLike.text=numberToString(post.likes)
                likeButton.setImageResource(if (post.likedByMe)R.drawable.ic_liked_24 else R.drawable.ic_like_24 )
            }
            shareButton.setOnClickListener {
                post.share++
                textShare.text= numberToString(post.share)
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
