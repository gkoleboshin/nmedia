package ru.netology.nmedia.activity


import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.NewPostFragmentBinding
import ru.netology.nmedia.util.AndroidUtilits
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    private val args by navArgs<NewPostFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NewPostFragmentBinding.inflate(
        inflater,
        container,
        false
    ).also { binding ->
        binding.edit.setText(args.initialText)
        binding.edit.requestFocus()
        binding.ok.setOnClickListener { onOkButtonClicked(binding.edit.text) }
    }.root

    private fun onOkButtonClicked(text: Editable) {
        if (text.isNotBlank()) {
            val result = Bundle(1).apply {
                putString(NEW_POST_CONTENT_KEY, text.toString())
            }
            parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
        }
        parentFragmentManager.popBackStack()
    }

    companion object {
        const val REQUEST_KEY = "newPost"
        const val NEW_POST_CONTENT_KEY = "newPostContent"
    }


}




