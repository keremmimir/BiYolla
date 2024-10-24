package com.example.biyolla.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.biyolla.databinding.FragmentSignUpBinding
import com.example.biyolla.ui.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            buttonSignUp.setOnClickListener {
                val name = editTextName.text.toString()
                val surname = editTextSurname.text.toString()
                val username = editTextUsername.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Lütfen tüm alanları doldurun!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    signUpViewModel.signUp(name, surname, username, email, password)
                }
            }
            iconBack.setOnClickListener {
                navigateToSignInPage()
            }
        }
    }

    private fun observeData() {
        signUpViewModel.authResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    navigateToSignInPage()
                }.onFailure { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.message ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        signUpViewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToSignInPage() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}