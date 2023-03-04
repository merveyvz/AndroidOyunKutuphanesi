package com.example.mobilvizeproje


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilvizeproje.databinding.FragmentGamesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private lateinit var adapter: ItemAdapter
private lateinit var recyclerView: RecyclerView
@SuppressLint("StaticFieldLeak")
private lateinit var searchView: SearchView

@SuppressLint("StaticFieldLeak")
class GamesFragment : Fragment() {
    private lateinit var binding : FragmentGamesBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: MainViewModel
    private lateinit var retrofit: Retrofit
    //private lateinit var api: GamesAPI
    private val myJob = Job()
    private val myScope = CoroutineScope(Dispatchers.IO + myJob)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view model provider ile initilaze edildi
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamesBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(context)
        recyclerView = binding.myRecyclerView  //recycler view tanımlandı
        recyclerView.layoutManager= layoutManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun sendDetail(it: Result) {
            val typeList = ArrayList<String>()
            for (genre in it.type!!){
                typeList.add(genre.name)
            }

            //tıklanan view verileri geçilen fragmant a gönderildi
            val bundle = bundleOf("game_image" to it.gameImage, "game_name" to it.name,"game_detail" to it.gameDetail,
                "game_score" to it.score, "game_type" to typeList, "game_favourite" to it.favourite, "game_id" to it.id)

            //detail fragment sayfasın ageçiş yapıyor
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }


        retrofit = Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(GamesAPI::class.java)

        searchView = binding.searchView  //searc view atama yapıldı

        myScope.launch {
            api.getGames().enqueue(object : Callback<JsonGame>{
                override fun onResponse(call: Call<JsonGame>, response: Response<JsonGame>) {

                    adapter = ItemAdapter(response.body()!!.results){}  //adapter tanımlandı
                    recyclerView.adapter=adapter  //recycler view un adatorüne tanımlanan adaptör atandı

                    recyclerView.adapter = ItemAdapter(response.body()!!.results){
                        val typeList = ArrayList<String>()
                        for (genre in it.type!!){
                            typeList.add(genre.name)
                        }

                        //tıklanan view verileri geçilen fragmant a gönderildi
                        val bundle = bundleOf("game_image" to it.gameImage, "game_name" to it.name,"game_detail" to it.gameDetail,
                            "game_score" to it.score, "game_type" to typeList, "game_favourite" to it.favourite, "game_id" to it.id)

                        //detail fragment sayfasın ageçiş yapıyor
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_detailFragment, bundle)
                    }

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                        //enter a basınca çalışır
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        //search teki değişiklikte arama sağlar
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onQueryTextChange(newText: String?): Boolean {

                            if (newText != null) {
                                if (newText.length >= 3){
                                    adapter.filter.filter(newText)  // ıtem adaptor un filter fonksiyonuna arancak text gönderilir
                                    recyclerView.adapter= ItemAdapter(adapter.gamesFilterList){
                                        sendDetail(it)
                                    }
                                }else{
                                    recyclerView.adapter = ItemAdapter(response.body()!!.results){
                                        sendDetail(it)
                                    }
                                }
                            }
                            else{
                                recyclerView.adapter = ItemAdapter(response.body()!!.results){
                                    sendDetail(it)
                                }
                            }
                            return false
                        }
                    })

                }
                override fun onFailure(call: Call<JsonGame>, t: Throwable) {
                    Log.d("NotWorking",t.message!!)

                }
            })
        }


        }
}