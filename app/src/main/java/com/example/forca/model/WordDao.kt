package com.example.forca.model

import com.google.gson.annotations.SerializedName

data class WordDao(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Palavra")
    val word: String,
    @SerializedName("Letras")
    val letters: Int,
    @SerializedName("Nivel")
    val difficulty: Int
)