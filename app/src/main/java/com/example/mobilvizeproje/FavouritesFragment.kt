package com.example.mobilvizeproje

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilvizeproje.databinding.FragmentFavouritesBinding


private lateinit var recyclerView: RecyclerView

class FavouritesFragment : Fragment() {

    //değişkenler oluşturuldu
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MYTAG", "FavouritesFragment: OnCreate")
        super.onCreate(savedInstanceState)

        //view model initilaze edildi
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //view modelden favori listesi alındı
        viewModel.favouriteGameslist.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){  //view modelden gelen favori listesi boş değilse
                val layoutManager = LinearLayoutManager(context)
                recyclerView = binding.myRecyclerView  // recycler view oluşturuldu
                recyclerView.layoutManager = layoutManager
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = ItemAdapter(it){}  //recycler view un adapter ü initilaze edildi


                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        // this method is called
                        // when the item is moved.
                        return false
                    }
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        // this method is called when we swipe our item to right direction.
                        // on below line we are getting the item at a particular position.
                        val deletedGame:Result = it.get(viewHolder.adapterPosition)

                        // below line is to get the position
                        // of the item at that position.
                        val position = viewHolder.adapterPosition

                        val builder = AlertDialog.Builder(context)

                        builder.setTitle("Delete Confirmation")
                        builder.setMessage("Are you sure you want to delete " + deletedGame.name + "?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            it.removeAt(viewHolder.adapterPosition)
                            recyclerView.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                            viewModel.removeFav(deletedGame)
                        }
                        builder.setNegativeButton("No") { _, _ ->
                            recyclerView.adapter!!.notifyItemChanged(position)
                        }
                        builder.create().show()
                    }
                }).attachToRecyclerView(recyclerView)
            }
        })
    }
}