package com.example.examen_api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // LIBRERÍA DE IMÁGENES
import com.example.examen_api.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(navController: NavController, userId: String?, viewModel: UserViewModel) {
    // Convertir ID a Int para buscar en la lista
    val idInt = userId?.toIntOrNull()
    val user = viewModel.users.find { it.id == idInt }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Usuario no encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactos", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // REQUISITO: Botón Editar en la parte superior derecha
                    TextButton(onClick = { navController.navigate("edit/${user.id}") }) {
                        Text("Editar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5) // Fondo gris claro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // REQUISITO: Imagen circular centrada (Grande) [cite: 58-63]
            Surface(
                modifier = Modifier.size(120.dp), // Tamaño más grande para detalle
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                // --- LÓGICA DE IMAGEN (Coil vs Iniciales) ---
                if (user.image?.url != null) {
                    AsyncImage(
                        model = user.image.url,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REQUISITO: Nombre grande y negritas [cite: 66]
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Product Designer", // Texto de relleno visual
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // REQUISITO: Acciones rápidas (Mensaje, Llamar, Video, Correo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(Icons.Default.Message, "Mensaje")
                ActionButton(Icons.Default.Call, "Llamar")
                ActionButton(Icons.Default.Videocam, "Video")
                ActionButton(Icons.Default.Email, "Correo")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REQUISITO: Información de contacto en tarjetas [cite: 75-76]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Teléfono [cite: 79-80]
                    InfoRow(label = "móvil", value = user.phone)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    // Correo [cite: 81-84]
                    InfoRow(label = "correo electrónico", value = user.email)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // REQUISITO: Botón Eliminar al final
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Eliminar Contacto", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        // Diálogo de confirmación
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar") },
                text = { Text("¿Estás seguro de eliminar a ${user.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteUser(user.id) {
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    }) {
                        Text("Sí, eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                },
                containerColor = Color.White
            )
        }
    }
}

// Componente auxiliar para los botones de acción (Llamar, Video, etc.)
@Composable
fun ActionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = { /* Acción dummy */ },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF2196F3))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = Color(0xFF2196F3))
    }
}

// Componente auxiliar para las filas de información
@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, color = Color(0xFF2196F3))
    }
}