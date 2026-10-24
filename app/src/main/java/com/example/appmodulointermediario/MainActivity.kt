package com.example.appmodulointermediario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmodulointermediario.ui.navigation.CatalogoNavHost
import com.example.appmodulointermediario.ui.theme.AppModuloIntermediarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CatalogoApplication
        val repository = app.repository

        setContent {
            AppModuloIntermediarioTheme {
                CatalogoNavHost(repository = repository)
            }
        }
    }
}
