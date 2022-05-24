package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FeedFragmentBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


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
    ): View = FeedFragmentBinding.inflate(inflater, container, false).also { binding ->

        val adapter = PostsAdapter(viewModel)
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        viewModel.navigeteToThisPostScreen.observe(viewLifecycleOwner) { postId->
            val directions =
                FeedFragmentDirections.actionFeedFragmentToThisPostFragment(postId)
            findNavController().navigate(directions)
        }

        viewModel.navigeteToNewPostScreen.observe(viewLifecycleOwner) { initialContent ->
            val directions =
                FeedFragmentDirections.globalToNewPostFragment(initialContent)
            findNavController().navigate(directions)
        }
        parentFragmentManager.setFragmentResultListener(
            NewPostFragment.REQUEST_KEY, this
        ) { requestKey, resultBundle ->
            if (requestKey != NewPostFragment.REQUEST_KEY) return@setFragmentResultListener

            val newPostContent = resultBundle.getString(
                NewPostFragment.NEW_POST_CONTENT_KEY, ""
            )
            viewModel.changeContent(newPostContent)

        }


        viewModel.videoUrl.observe(viewLifecycleOwner) { id ->
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id)
            )
            try {
                startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(webIntent)
            }
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.globalToNewPostFragment(""))
        }

    }.root

}

