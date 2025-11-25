import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ParticleControls(
    gravity: Float,
    onGravityChange: (Float) -> Unit,
    slowMotion: Boolean,
    onSlowMotionToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Text("Gravity: ${gravity.toInt()}", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = gravity,
            onValueChange = onGravityChange,
            valueRange = 0f..20f
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Switch(checked = slowMotion, onCheckedChange = onSlowMotionToggle)
            Text("Slow Motion", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
