package ru.netology.nmedia.adapter

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val interactionLisitner: PostInteractionListiner
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var post: Post

    private val popupMenu by lazy {
        PopupMenu(itemView.context, binding.menu).apply {
            inflate(R.menu.options_post)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.remove -> {
                        interactionLisitner.onRemove(post)
                        true
                    }
                    R.id.edit->{
                        interactionLisitner.onEdit(post)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    init {
        with(binding){
            share.setOnClickListener {
                interactionLisitner.onShare(post)
            }
            like.setOnClickListener {
                interactionLisitner.onLike(post)
            }
            menu.setOnClickListener {
                popupMenu.show()
            }
        }
    }

    fun bind(post: Post) {
        this.post = post
        with(binding) {
            author.text = post.author

            published.text = post.published
            content.text = post.content
            like.text = numberToString(post.likes)
            share.text = numberToString(post.shares)
            views.text = numberToString(post.views)
            like.isChecked = post.likedByMe
//            like.setImageResource(
//                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
//            )
        }
    }

    fun numberToString(number: Int): String {
        val thousandths: Int = number / 1_000
        val hundredths: Int = number / 100 - thousandths * 10
        val milions: Int = number / 1_000_000
        val hudretdthsThousandths: Int = number / 100_000 - milions * 10
        val numberToString: String = when {
            number < 1_000 -> number.toString()
            number < 10_000 && hundredths == 0 -> "$thousandths K"
            number < 10_000 && hundredths > 0 -> "$thousandths,$hundredths K"
            number in 10_000..999_999 -> "$thousandths K"
            number > 999_999 && hudretdthsThousandths == 0 -> "$milions M"
            else -> "$milions,$hudretdthsThousandths M"
        }
        return numberToString
    }
}
