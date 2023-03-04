package com.example.mobilvizeproje

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("background_image") val gameImage: String,
    @SerializedName("genres") val type: List<Genre>?,
    @SerializedName("id") val id: Int?,
    @SerializedName("metacritic") val score: Int?,
    @SerializedName("name") val name: String?,
    var favourite:Boolean? = false,
    var gameDetail: String?
)