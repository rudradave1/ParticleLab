import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.rudra.particlelab.viewmodel.ParticleViewModel
import kotlin.math.hypot

@Composable
fun ParticleCanvas(vm: ParticleViewModel, modifier: Modifier = Modifier) {
    val particles = vm.particles.collectAsState().value

    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> vm.spawnAt(offset) },
                        onDrag = { change, _ -> vm.spawnAt(change.position) }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset -> vm.spawnAt(offset) }
                }
                .onGloballyPositioned {
                    vm.updateCanvasSize(it.size.width, it.size.height)
                }
        ) {
            // draw particles
            particles.forEach { p ->
                // simple visual: aura + main + outline
                val speed = hypot(p.vx, p.vy)
                val auraAlpha = (0.14f + speed / 2000f).coerceIn(0.05f, 0.35f)
                drawCircle(
                    color = p.color.copy(alpha = auraAlpha),
                    radius = p.radius * 2.2f,
                    center = Offset(p.x, p.y)
                )
                drawCircle(color = p.color, radius = p.radius, center = Offset(p.x, p.y))
                drawCircle(
                    color = Color.Black.copy(alpha = 0.18f),
                    radius = p.radius + 1.2f,
                    center = Offset(p.x, p.y),
                    style = Stroke(width = 1f)
                )
            }
        }
    }
}