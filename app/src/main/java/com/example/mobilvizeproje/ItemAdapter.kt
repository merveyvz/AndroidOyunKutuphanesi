package com.example.mobilvizeproje


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class ItemAdapter(private val gameList: ArrayList<Result>, private val clickListener: (Result) -> Unit) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(), Filterable{

    var gamesFilterList = ArrayList<Result>()  //search view araması sonucu filtrelenmiş liste


    init {
        gamesFilterList = gameList  // gelen game list filter liste atandı
    }

    class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView){

        // Declaring executor to parse the URL
        val executor = Executors.newSingleThreadExecutor()
        // Once the executor parses the URL
        // and receives the image, handler will load it
        // in the ImageView
        val handler = Handler(Looper.getMainLooper())
        // Initializing the image
        var image: Bitmap? = null
        var typeListString: String = ""

        //game nesnesinden gelen veriler card view un ilgili atalanlarına atandı
        fun bind(game: Result, clickListener: (Result) -> Unit){
            val name = itemView.findViewById<TextView>(R.id.gameName)
            val score = itemView.findViewById<TextView>(R.id.gameScore)
            val type = itemView.findViewById<TextView>(R.id.gameType)
            val imageView = itemView.findViewById<ImageView>(R.id.gameImage)
            name.text = game.name
            score.text = game.score.toString()

            for (genre in game.type!!){
                if (typeListString == "")
                    typeListString = genre.name
                else
                    typeListString = typeListString +", "+ genre.name
            }
            type.text = typeListString
            executor.execute {

                val imageURL = game.gameImage

                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        imageView.setImageBitmap(image)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //view için click listener oluşturldu
            itemView.setOnClickListener{
                clickListener(game)
            }

        }
    }

    //list item template olusturuldu
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolder(view)
    }

    //gelen game nesnelerin her biri card view lara bağlandı
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = gameList[position]
        holder.bind(game, clickListener)
    }

    //game listenin boyutu alındı
    override fun getItemCount(): Int {
        return gameList.size
    }

    //search den texte göre listede filtreleme yapıyor
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()  //aranan yazı
                //Arama alanında bir yazı aranmadıysa tüm oyunları gamesFilterListe aktarıyoruz
                if (charSearch.isEmpty()) {
                    gamesFilterList = gameList
                } else {
                    val resultList = ArrayList<Result>()
                    for (game in gameList) {
                        //Aranan oyun varsa add metodu ile listeye eklenir
                        if (game.name?.lowercase(Locale.getDefault())!!.contains(
                                charSearch.lowercase(Locale.getDefault()))) {
                            resultList.add(game)
                        }
                    }
                    gamesFilterList = resultList //filtre sonucu atandı
                }

                val filterResults =  FilterResults()
                filterResults.values = gamesFilterList
                //sonuçlar FilterResult tipinde döndürüldü
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")

            //filtreleme sonucu adaptöre gönderilir
            override fun publishResults(constraint: CharSequence?, result: FilterResults?) {
                gamesFilterList = result?.values as ArrayList<Result>
                notifyDataSetChanged()
            }

        }
    }


}







