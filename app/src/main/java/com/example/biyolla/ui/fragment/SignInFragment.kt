package com.example.biyolla.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.biyolla.databinding.FragmentSignInBinding
import com.example.biyolla.ui.viewmodel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupViews()
    }

    private fun setupViews() {
        val currentUser = signInViewModel.getCurrentUser()
        if (currentUser != null){
            navigateToHomePage()
        }

        with(binding) {
            buttonSignIn.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Lütfen tüm alanları doldurun!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    signInViewModel.signIn(email, password)
                }
            }

            buttonSignUp.setOnClickListener {
                navigateToSignUpPage()
            }
        }
    }

    private fun observeData() {
        signInViewModel.authResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    navigateToHomePage()
                }.onFailure { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.message ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        signInViewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHomePage() {
        val action = SignInFragmentDirections.actionSignInFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignUpPage() {
        val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}