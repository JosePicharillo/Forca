package com.example.forca.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GameForcaAPI {
    @GET("identificadores/{id}")
    fun getIdentifier(@Path("id") id: Int): Call<Array<Int>>

    @GET("palavra/{id}")
    fun getWord(@Path("id") id: Int): Call<Array<WordDao>>
}