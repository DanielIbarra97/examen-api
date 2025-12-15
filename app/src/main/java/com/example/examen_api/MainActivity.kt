package com.example.examen_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.examen_api.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para que la barra de estado se vea bien
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        setContent {
            // Tema personalizado con los colores del examen
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF006CFF), // Azul
                    background = Color(0xFFF5F5F5), // Gris claro
                    surface = Color.White
                )
            ) {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Instancia del ViewModel (el cerebro de la app)
    val viewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "index") {

        // 1. PANTALLA LISTA (INDEX)
        composable("index") {
            PantallaLista(navController, viewModel)
        }

        // 2. PANTALLA DETALLE (SHOW)
        composable(
            route = "show/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType }) // IMPORTANTE: Definir el tipo
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserDetailScreen(navController, userId, viewModel)
        }

        // 3. PANTALLA CREAR (CREATE)
        composable("create") {
            UserFormScreen(navController, viewModel)
        }

        // 4. PANTALLA EDITAR (EDIT)
        composable(
            route = "edit/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType }) // IMPORTANTE: Definir el tipo
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserEditScreen(navController, userId, viewModel)
        }
    }
}