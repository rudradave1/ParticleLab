package com.rudra.particlelab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rudra.particlelab.data.Mode
import com.rudra.particlelab.domain.physics.PhysicsEngine
import com.rudra.particlelab.viewmodel.ParticleViewModel


@Composable
fun SettingsPanel(vm: ParticleViewModel, onClose: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mode", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Mode.entries.forEach { mode ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { vm.setMode(mode) }
                .padding(vertical = 8.dp)) {
                RadioButton(selected = vm.currentMode == mode, onClick = { vm.setMode(mode) })
                Spacer(Modifier.width(8.dp))
                Text(mode.label)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Gravity: ${vm.gravity}")
        Slider(value = vm.gravity, onValueChange = { vm.gravity = it }, valueRange = 0f..30f)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Spawn per sec: ${vm.spawnPerSecond.toInt()}")
        Slider(value = vm.spawnPerSecond, onValueChange = { vm.spawnPerSecond = it }, valueRange = 0f..300f)

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Auto reset on mode change")
            Spacer(Modifier.weight(1f))
            Switch(checked = vm.autoResetOnModeChange, onCheckedChange = { vm.autoResetOnModeChange = it })
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = onClose) { Text("Done") }
    }
}
