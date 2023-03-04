package com.example.mobilvizeproje

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//listeleri tutmak için view model olulturuldu
class MainViewModel : ViewModel(){

     //var favouriteText = ""
     //var gameList = MutableLiveData<ArrayList<com.example.mobilvizeproje.Result>>()  //oyunların listesi oluşturlu
     var favouriteGameslist = MutableLiveData<ArrayList<Result>>()  //favorilenmiş oyunları tutmak içi liste

     init {
         favouriteGameslist.value = arrayListOf()
     }

    fun addFav(game : Result){
        game.favourite = true
        if (!favouriteGameslist.value!!.contains(game))
            favouriteGameslist.value?.add(game)  //favorilenen oyun listeye eklendi
    }

    fun removeFav(game: Result){
        game.favourite = false
        favouriteGameslist.value?.remove(game)
    }


}