package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ViewPostFragmentBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class ViewPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val args by navArgs<ViewPostFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ViewPostFragmentBinding.inflate(
        inflater,
        container,
        false
    ).also { binding ->
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == args.postId } ?: return@observe
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                content.movementMethod = ScrollingMovementMethod()
                like.text = numberToString(post.likes)
                share.text = numberToString(post.shares)
                views.text = numberToString(post.views)
                like.isChecked = post.likedByMe
                if (post.contentVideo.isBlank()) group.visibility =
                    View.GONE else group.visibility =
                    View.VISIBLE
                like.setOnClickListener {
                    viewModel.onLike(post)
                }
                share.setOnClickListener {
                    viewModel.onShare(post)
                }
                parentFragmentManager.setFragmentResultListener(
                    NewPostFragment.REQUEST_KEY, viewLifecycleOwner
                ) { requestKey, resultBundle ->
                    if (requestKey != NewPostFragment.REQUEST_KEY) return@setFragmentResultListener

                    val newPostContent = resultBundle.getString(
                        NewPostFragment.NEW_POST_CONTENT_KEY, ""
                    )
                    viewModel.changeContent(newPostContent)
                }

                val popupMenu = PopupMenu(menu.context, binding.menu).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                viewModel.onRemove(post)
                                parentFragmentManager.popBackStack()
                                true
                            }
                            R.id.edit -> {
                                viewModel.curentPost = post
                                val directions =
                                    ViewPostFragmentDirections.actionThisPostFragmentToNewPostFragment(
                                        post.content
                                    )
                                findNavController().navigate(directions)
                                true
                            }
                            else -> false
                        }
                    }
                }
                menu.setOnClickListener {
                    popupMenu.show()
                }
            }
        }


    }.root

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