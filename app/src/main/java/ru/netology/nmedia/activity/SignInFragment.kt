package ru.netology.nmedia.activity


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel


class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false,
        )
        with(binding) {
            editLogin.requestFocus()
        }
        binding.signInButton.setOnClickListener {
            viewModel.Login(binding.editLogin.text.toString(), binding.editPassword.text.toString())
        }
        viewModel._data.observe(viewLifecycleOwner){
            if (it != null){
                it.token?.let { it1 -> AppAuth.getInstance().setAuth(it.id, it1) }
                viewModel._data.value = null
            }

            findNavController().popBackStack()
        }
        return binding.root

    }




}
