package com.example.examen_api.network

import com.example.examen_api.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    // 1. Obtener lista de contactos
    @GET("contacts")
    suspend fun getUsers(): Response<List<User>>

    // 2. Crear un contacto nuevo
    @POST("contacts")
    suspend fun createUser(@Body user: User): Response<User>

    // 3. Actualizar un contacto existente
    // Laravel suele usar la ruta: contacts/{id}
    @PUT("contacts/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,  // Este id reemplaza el {id} en la URL
        @Body user: User
    ): Response<User>

    // 4. Eliminar un contacto
    @DELETE("contacts/{id}")
    suspend fun deleteUser(
        @Path("id") id: Int
    ): Response<Unit> // Unit es equivalente a void (cuando no devuelve cuerpo)
}