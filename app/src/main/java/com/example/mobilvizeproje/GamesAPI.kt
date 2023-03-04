package com.example.mobilvizeproje

import retrofit2.Call
import retrofit2.http.GET

interface GamesAPI {
    @GET("games?key=3be8af6ebf124ffe81d90f514e59856c&%20page_size=10&page=1")
    fun getGames(): Call<JsonGame>
}