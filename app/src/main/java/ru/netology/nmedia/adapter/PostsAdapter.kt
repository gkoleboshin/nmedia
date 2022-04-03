package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

typealias OnLikeListener = (post: Post) -> Unit
typealias  OnShareListener = (post:Post) -> Unit

class PostsAdapter(
    private val onShareListener: OnShareListener,
    private val onLikeListener: OnLikeListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener,onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likes.text = numberToString(post.likes)
            shares.text = numberToString(post.shares)
            views.text = numberToString(post.views)
            like.setImageResource(
                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
            )
            share.setOnClickListener {
                onShareListener(post)
            }

            like.setOnClickListener{
                onLikeListener(post)
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
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}