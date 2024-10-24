package com.example.biyolla.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.biyolla.R
import com.example.biyolla.databinding.FragmentHomeBinding
import com.example.biyolla.model.Food
import com.example.biyolla.ui.adapter.ProductAdapter
import com.example.biyolla.ui.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupViews()
    }

    private fun setupViews() {
        binding.textWelcome.visibility = View.INVISIBLE

        binding.imageExit.setOnClickListener {
            showSignOutConfirmationDialog()
        }
        searchBox()
    }

    private fun observeData() {
        homeViewModel.loadUserName()
        homeViewModel.getUid()
        homeViewModel.authResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    navigateToSignInPage()
                }.onFailure { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.message ?: "Unknown error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

        homeViewModel.userid.observe(viewLifecycleOwner) { userid ->
            if (userid != null) {
                homeViewModel.getCart(userid)
                setupAdapter(userid)
            }
        }

        homeViewModel.username.observe(viewLifecycleOwner) { userName ->
            binding.textWelcome.text = "Hoşgeldin ${userName ?: ""}"
            binding.textWelcome.visibility = View.VISIBLE
        }

        homeViewModel.foodList.observe(viewLifecycleOwner) { foodList ->
            adapter.submitList(foodList)
        }

        homeViewModel.filteredItems.observe(viewLifecycleOwner) { filteredItems ->
            adapter.submitList(filteredItems)
        }
    }

    private fun setupAdapter(userName: String){
        adapter = ProductAdapter(
            requireContext(),
            onDetailToggle = { detail ->
                detailPageNavigation(detail)
            },
            onCartToggle = { foodName, foodImageName, foodPrice, foodOrderQuantity, userName ->
                addCart(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)
            }, userName = userName
        )
        binding.recylerView.adapter = adapter
    }

    private fun detailPageNavigation(detail: Food) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(detail)
        findNavController().navigate(action)
    }

    private fun addCart(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) {
        homeViewModel.addCart(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)
        Snackbar.make(binding.root, "Sepete Eklendi.", Snackbar.LENGTH_SHORT).show()
    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Çıkış")
            .setMessage("Çıkış yapmak istediğine emin misin ?")
            .setPositiveButton("Evet") { dialog, _ ->
                signOut()
                dialog.dismiss()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun signOut() {
        homeViewModel.signOut()
    }

    private fun navigateToSignInPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToSignInFragment()
        findNavController().navigate(action)
    }

    private fun searchBox() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    homeViewModel.searchItems(it)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}