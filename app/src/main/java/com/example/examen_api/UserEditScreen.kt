package com.example.examen_api

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.examen_api.model.User
import com.example.examen_api.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditScreen(navController: NavController, userId: String?, viewModel: UserViewModel) {
    val idInt = userId?.toIntOrNull()
    val userToEdit = viewModel.users.find { it.id == idInt }

    var name by remember { mutableStateOf(userToEdit?.name ?: "") }
    var email by remember { mutableStateOf(userToEdit?.email ?: "") }
    var phone by remember { mutableStateOf(userToEdit?.phone ?: "") }
    var isError by remember { mutableStateOf(false) }

    fun saveChanges() {
        // VALIDACIÓN: 10 dígitos obligatorios
        if (userToEdit != null && name.isNotBlank() && email.isNotBlank() && phone.length == 10) {
            val updatedUser = User(
                id = userToEdit.id,
                name = name,
                email = email,
                phone = phone,
                image = userToEdit.image // Mantenemos la foto original
            )
            viewModel.updateUser(updatedUser) {
                navController.popBackStack()
            }
        } else {
            isError = true
        }
    }

    if (userToEdit == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Usuario no encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Editar Contacto", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    TextButton(onClick = { saveChanges() }) {
                        Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, CircleShape)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Edit Photo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Change Photo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            EditCustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Default.Person,
                keyboardType = KeyboardType.Text
            )

            // CAMPO TELÉFONO EDITAR VALIDADO
            EditCustomTextField(
                value = phone,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= 10) {
                        phone = input
                    }
                },
                label = "Phone Number",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Number
            )

            EditCustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            if (isError) {
                Text(
                    text = if (phone.length != 10) "El teléfono debe tener 10 dígitos" else "Por favor completa todos los campos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { saveChanges() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Guardar Cambios", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun EditCustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = keyboardType
            )
        )
    }
}