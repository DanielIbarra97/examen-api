package com.example.examen_api

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.examen_api.model.User
import com.example.examen_api.model.UserImage
import com.example.examen_api.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(navController: NavController, viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // --- ESTADO PARA CONTROLAR EL GUARDADO MÚLTIPLE ---
    var isSaving by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    fun saveContact() {
        // 1. SI YA ESTÁ GUARDANDO, IGNORAMOS CUALQUIER OTRO CLIC
        if (isSaving) return

        if (name.isNotBlank() && email.isNotBlank() && phone.length == 10) {

            // 2. BLOQUEAMOS LA INTERFAZ
            isSaving = true

            val imageObject = if (selectedImageUri != null) {
                UserImage(url = selectedImageUri.toString())
            } else {
                null
            }

            val newUser = User(
                id = 0,
                name = name,
                email = email,
                phone = phone,
                image = imageObject
            )

            viewModel.createUser(newUser) {
                // Al terminar, nos vamos (no es necesario poner isSaving=false porque la pantalla se destruye)
                navController.popBackStack()
            }
        } else {
            isError = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("New Contact", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    // Si está guardando, deshabilitamos el botón Cancelar también
                    TextButton(onClick = { if (!isSaving) navController.popBackStack() }) {
                        Text("Cancel", color = if (isSaving) Color.Gray else MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    // BOTÓN SAVE SUPERIOR
                    if (isSaving) {
                        // Si está guardando, mostramos un circulito cargando
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(onClick = { saveContact() }) {
                            Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
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

            // FOTO
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable(enabled = !isSaving) { // Deshabilitar clic si guarda
                        galleryLauncher.launch("image/*")
                    }
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add Photo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add Photo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPOS

            CustomTextField(
                value = name,
                onValueChange = { if (!isSaving) name = it }, // Bloquear escritura si guarda
                label = "Full Name",
                placeholder = "John Doe",
                icon = Icons.Default.Person,
                keyboardType = KeyboardType.Text
            )

            CustomTextField(
                value = phone,
                onValueChange = { input ->
                    if (!isSaving && input.all { it.isDigit() } && input.length <= 10) {
                        phone = input
                    }
                },
                label = "Phone Number",
                placeholder = "1234567890",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Number
            )

            CustomTextField(
                value = email,
                onValueChange = { if (!isSaving) email = it },
                label = "Email Address",
                placeholder = "john.doe@example.com",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            if (isError) {
                Text(
                    text = if (phone.length != 10) "El teléfono debe tener 10 dígitos" else "Completa todos los campos",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN INFERIOR
            Button(
                onClick = { saveContact() },
                enabled = !isSaving, // SE DESHABILITA VISUALMENTE
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Save Contact", fontSize = 16.sp)
                }
            }
        }
    }
}

// (El composable CustomTextField sigue igual abajo...)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
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
            placeholder = { Text(placeholder, color = Color.LightGray) },
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