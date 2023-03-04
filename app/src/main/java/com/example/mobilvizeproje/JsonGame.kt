package com.example.mobilvizeproje

import com.google.gson.annotations.SerializedName

data class JsonGame(
    @SerializedName("next") val next: String,
    //@SerializedName("previous") val previous: Any,
    @SerializedName("results") val results: ArrayList<Result>
)