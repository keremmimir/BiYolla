package com.example.biyolla.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.biyolla.databinding.FragmentCartBinding
import com.example.biyolla.model.Cart
import com.example.biyolla.ui.adapter.CartAdapter
import com.example.biyolla.ui.viewmodel.CartViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.function.LongFunction

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupViews()
    }

    private fun setupViews() {
        binding.textTotalPrice.text = "0"
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeData() {
        cartViewModel.getUid()

        cartViewModel.userid.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                cartViewModel.getCart(uid)
                setupAdapter(uid)
            }
        }

        cartViewModel.cartList.observe(viewLifecycleOwner) { cartList ->
            adapter.submitList(cartList)
        }

        cartViewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.textTotalPrice.text = "${totalPrice.toString()} ₺"
        }
    }

    private fun setupAdapter(userName: String) {
        adapter =
            CartAdapter(
                requireContext(),
                userName = userName,
                onDeleteToggle = { cartFoodId, username ->
                    deleteCart(cartFoodId, userName)
                })
        binding.recylerView.adapter = adapter
    }

    private fun deleteCart(cartFoodId: Int, userName: String) {
        cartViewModel.deleteCart(cartFoodId, userName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}