package com.example.mobilvizeproje

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mobilvizeproje.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view model provider ı ile initialize edildi
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //frame layout a games fragment ı set eder
        replaceFragment(GamesFragment())

        //nav butonları dinlenir
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                //games botununa tıklanınca
                R.id.games -> {
                    replaceFragment(GamesFragment())  //frame layout a games fragment ı set eder
                    binding.fragmentName.text = "Games" //appbarın başlığı game e dönüştürüldü
                    binding.foundText.isVisible = false  //there is no favourite yazısını görünmez yapıyor
                }

                //favourites butonuna tıklanınca
                R.id.favourites -> {
                    replaceFragment(FavouritesFragment()) //frame layout a favourite fragment ı set eder
                    if (viewModel.favouriteGameslist.value?.size == 0){  //liste boş mu diye kontrol eder
                        binding.fragmentName.text = "Favourites"
                        binding.foundText.isVisible = true //there is no favourite yazısını görünür yapıyor liste boş olduğu için
                    }else{

                        //appbarın başlığı favourite e dönüştürüldü ve liste size ı yanına eklendi
                        binding.fragmentName.text = "Favourites (${viewModel.favouriteGameslist.value?.size})"
                        binding.foundText.isVisible  = false  //there is no favourite yazısını görünmez yapıyor
                    }

                }
                else ->{
                    replaceFragment(GamesFragment())  //frame layout a games fragment ı set eder
                }
            }
            true

        }

    }

    //frame layout üzerinde fragment geçişini sağlar
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


}