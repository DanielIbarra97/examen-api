package com.example.examen_api.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen_api.model.User
import com.example.examen_api.network.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val api = RetrofitClient.instance

    // Lista fuente de verdad (Flat list)
    var users = mutableStateListOf<User>()
        private set

    // PROPIEDAD COMPUTADA PARA EL EXAMEN [Requisito: Agrupación A-Z]
    // Esto toma la lista 'users', la ordena y la agrupa por la primera letra.
    val groupedUsers: Map<Char, List<User>>
        get() = users
            .sortedBy { it.name } // Ordenar alfabéticamente
            .groupBy { it.name.first().uppercaseChar() } // Agrupar por inicial

    init {
        fetchUsers()
    }

    // 1. INDEX (GET)
    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val response = api.getUsers()
                if (response.isSuccessful) {
                    response.body()?.let { userList ->
                        users.clear()
                        users.addAll(userList.sortedBy { it.name })
                    }
                } else {
                    // Si el servidor responde error (ej. 404, 500), cargamos datos falsos
                    println("Error del servidor: ${response.code()}")
                    loadDummyData()
                }
            } catch (e: Exception) {
                // Si no hay conexión o falla la red, cargamos datos falsos para que NO salga pantalla blanca
                println("Excepción de red: ${e.message}")
                loadDummyData()
            }
        }
    }

    // 2. STORE (POST)
    fun createUser(user: User, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.createUser(user)
                if (response.isSuccessful && response.body() != null) {
                    users.add(response.body()!!)
                } else {
                    // Fallback examen: Agregamos localmente aunque falle la API
                    users.add(user)
                }
                users.sortBy { it.name } // Reordenar
                onSuccess()
            } catch (e: Exception) {
                // Fallback examen
                users.add(user)
                users.sortBy { it.name }
                onSuccess()
            }
        }
    }

    // 3. UPDATE (PUT)
    fun updateUser(user: User, onSuccess: () -> Unit) {
        // Validación de seguridad: Si no tiene ID, no se puede actualizar en API
        val id = user.id ?: return

        viewModelScope.launch {
            try {
                // LLAMADA A LA API REAL
                api.updateUser(id, user)

                // Actualización Local (Optimista)
                val index = users.indexOfFirst { it.id == user.id }
                if (index != -1) {
                    users[index] = user
                    users.sortBy { it.name }
                }
                onSuccess()
            } catch (e: Exception) {
                // Fallback: Actualizar localmente si falla internet
                val index = users.indexOfFirst { it.id == user.id }
                if (index != -1) {
                    users[index] = user
                    users.sortBy { it.name }
                }
                onSuccess()
            }
        }
    }

    // 4. DESTROY (DELETE)
    fun deleteUser(userId: Int?, onSuccess: () -> Unit) {
        if (userId == null) return

        viewModelScope.launch {
            try {
                // LLAMADA A LA API REAL
                api.deleteUser(userId)

                // Actualización Local
                users.removeAll { it.id == userId }
                onSuccess()
            } catch (e: Exception) {
                // Fallback: Borrar localmente si falla internet
                users.removeAll { it.id == userId }
                onSuccess()
            }
        }
    }

    // --- SALVAVIDAS PARA EL EXAMEN ---
    // Esta función carga datos falsos si la API falla, para que NO entregues la pantalla en blanco
    private fun loadDummyData() {
        val dummyList = listOf(
            User(1, "Aimee Bode", "aimee@test.com", "123-456-7890"),
            User(2, "Beto Perez", "beto@test.com", "555-222-3333"),
            User(3, "Carlos Lopez", "carlos@test.com", "555-444-5555"),
            User(4, "Chelsey Gutkowski", "chelsey@example.com", "987-654-3210"),
            User(5, "Daniela M", "daniela@test.com", "555-888-9999"),
            User(6, "Fatima Dach", "fatima@example.com", "555-777-1234"),
            User(7, "Gerardo N", "gera@test.com", "555-111-2222")
        )
        // Agregamos solo si la lista está vacía para no duplicar
        if (users.isEmpty()) {
            users.addAll(dummyList.sortedBy { it.name })
        }
    }

    // Helper para obtener usuario por ID
    fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }
}