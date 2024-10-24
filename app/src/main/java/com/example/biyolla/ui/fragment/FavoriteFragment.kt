package com.example.biyolla.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.biyolla.databinding.FragmentFavoriteBinding
import com.example.biyolla.model.Food
import com.example.biyolla.ui.adapter.ProductAdapter
import com.example.biyolla.ui.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        binding.textView.visibility = View.INVISIBLE
        searchBox()
    }

    private fun observeData() {
        favoriteViewModel.loadFavorites()
        favoriteViewModel.getUid()
        favoriteViewModel.userid.observe(viewLifecycleOwner) { userid ->
            if (userid != null) {
                favoriteViewModel.getCart(userid)
                setupAdapter(userid)
            }
        }

        favoriteViewModel.favoriteFoods.observe(viewLifecycleOwner) { foodList ->
            if (foodList.isEmpty()){
                binding.textView.visibility = View.VISIBLE
                binding.cardView.visibility = View.INVISIBLE
                binding.searchView.visibility = View.INVISIBLE
            }else{
                binding.textView.visibility = View.INVISIBLE
                binding.cardView.visibility = View.VISIBLE
                binding.searchView.visibility = View.VISIBLE
                adapter.submitList(foodList)
            }
        }

        favoriteViewModel.filteredItems.observe(viewLifecycleOwner) { filteredItems ->
            adapter.submitList(filteredItems)
        }
    }

    private fun setupAdapter(userName: String) {
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

    private fun addCart(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) {
        favoriteViewModel.addCart(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)
        Snackbar.make(binding.root, "Sepete Eklendi.", Snackbar.LENGTH_SHORT).show()
    }

    private fun detailPageNavigation(detail: Food) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(detail)
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
                    favoriteViewModel.searchItems(it)
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