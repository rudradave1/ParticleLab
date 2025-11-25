package com.rudra.particlelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rudra.particlelab.ui.screens.HomeScreen
import com.rudra.particlelab.ui.theme.ParticleLabTheme
import com.rudra.particlelab.viewmodel.ParticleViewModel

class MainActivity : ComponentActivity() {
    private val vm: ParticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // keep if you have it; optional
        setContent {
            ParticleLabTheme {
                HomeScreen(vm)
            }
        }
    }
}