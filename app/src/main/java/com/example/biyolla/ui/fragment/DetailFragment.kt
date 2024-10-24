package com.example.biyolla.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.biyolla.R
import com.example.biyolla.databinding.FragmentDetailBinding
import com.example.biyolla.ui.viewmodel.DetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel: DetailViewModel by viewModels()
    private val args by navArgs<DetailFragmentArgs>()
    private var piece = 1
    var username: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadingIndicator()
        observeData()
        setupViews()
    }

    private fun setupLoadingIndicator() {
        binding.progressBar.visibility = View.VISIBLE
        binding.materialCardView.visibility = View.INVISIBLE
    }

    private fun setupViews() {
        binding.textProductPrice.text = piece.toString()
        with(binding) {
            val imageURL = "http://kasimadalan.pe.hu/yemekler/resimler/${args.food.foodImage}"
            Glide.with(requireContext())
                .load(imageURL)
                .override(500, 500)
                .into(imageProduct)

            textProductName.text = args.food.foodName
            textProductPrice.text = "${args.food.foodPrice} ₺"
            textPrice.text = "${args.food.foodPrice} ₺"

            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonPiecePlus.setOnClickListener {
                piece++
                textPiece.text = piece.toString()
                val prince = piece * (args.food.foodPrice?.toInt() ?: 1)
                textPrice.text = "$prince ₺"
            }

            buttonPieceMinus.setOnClickListener {
                if (piece > 1) {
                    piece--
                }
                textPiece.text = piece.toString()
                val prince = piece * (args.food.foodPrice?.toInt() ?: 1)
                textPrice.text = "$prince ₺"
            }

            buttonAddCart.setOnClickListener {
                addCart(
                    args.food.foodName.toString(),
                    args.food.foodImage.toString(),
                    args.food.foodPrice.toString().toInt(),
                    textPiece.text.toString().toInt(),
                    username
                )
                Snackbar.make(binding.root, "Sepete Eklendi.", Snackbar.LENGTH_SHORT).show()
            }

            iconBack.setOnClickListener {
                findNavController().navigateUp()
            }

            iconFavorite.setOnClickListener {
                toogleFavorite(username, args.food.foodId!!)
            }
        }
    }

    private fun observeData() {
        detailViewModel.getUid()

        detailViewModel.userid.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                detailViewModel.getCart(uid)
                detailViewModel.getFavoriteIds(uid)
                username = uid
            }
        }

        detailViewModel.favResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }.onFailure { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.message ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        detailViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            val isFavorite = favorites.contains(args.food.foodId)
            updateFavoriteButton(isFavorite)
            binding.progressBar.visibility = View.GONE
            binding.materialCardView.visibility = View.VISIBLE
        }
    }

    private fun addCart(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) {
        detailViewModel.addCart(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)
    }

    private fun toogleFavorite(userId: String, foodId: String) {
        detailViewModel.toogleFavorite(userId, foodId)
        val isCurrentlyFavorite = detailViewModel.favorites.value?.contains(foodId)
        updateFavoriteButton(!isCurrentlyFavorite!!)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.iconFavorite.setImageResource(R.drawable.add_favroite_icon)
        } else {
            binding.iconFavorite.setImageResource(R.drawable.favorite_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}