package com.example.examen_api.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Si es localhost y usas emulador: "http://10.0.2.2:8080/"
    // Si es Render: "https://tu-proyecto.onrender.com/"
    private const val BASE_URL = "http://192.168.1.70:8000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}