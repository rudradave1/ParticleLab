package com.rudra.particlelab.viewmodel

import ParticleRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.particlelab.data.Mode
import com.rudra.particlelab.data.model.Particle
import com.rudra.particlelab.domain.physics.PhysicsEngine
import com.rudra.particlelab.domain.usecase.AddParticleUseCase
import com.rudra.particlelab.domain.usecase.ClearParticlesUseCase
import com.rudra.particlelab.domain.usecase.UpdateParticlesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ParticleViewModel(
    private val repo: ParticleRepository = ParticleRepository(),
    private val addUseCase: AddParticleUseCase = AddParticleUseCase(repo),
    private val clearUseCase: ClearParticlesUseCase = ClearParticlesUseCase(repo),
    private val updateUseCase: UpdateParticlesUseCase = UpdateParticlesUseCase(repo)
) : ViewModel() {

    // UI-observed particles
    val particles: StateFlow<List<Particle>> = repo.getParticles()

    // canvas size
    val canvasSize = MutableStateFlow(IntSize(0, 0))

    // app settings (exposed)
    var currentMode by mutableStateOf(Mode.NORMAL)
        private set

    var gravity by mutableStateOf(9.8f)
    var gravityWellStrength by mutableStateOf(4000f)
    var galaxyTangential by mutableStateOf(120f)

    var spawnPerSecond by mutableStateOf(60f)
    var autoResetOnModeChange by mutableStateOf(true)
    var slowMotion by mutableStateOf(false)

    // performance cap
    private var _maxParticles by mutableStateOf(2000)
    var maxParticles: Int
        get() = _maxParticles
        set(value) {
            _maxParticles = value
            repo.maxParticles = value
        }

    // in ParticleViewModel
    fun spawnAtRandom() {
        val size = canvasSize.value
        if (size.width <= 0 || size.height <= 0) return
        spawnAt(Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height))
    }

    // FPS counter
    var lastFps by mutableStateOf(0)
        private set

    // handy colors
    private val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFFFF4081),
        androidx.compose.ui.graphics.Color(0xFF40C4FF),
        androidx.compose.ui.graphics.Color(0xFF69F0AE),
        androidx.compose.ui.graphics.Color(0xFFFFD740),
        androidx.compose.ui.graphics.Color(0xFFFF5252),
        androidx.compose.ui.graphics.Color(0xFF7C4DFF)
    )

    init {
        repo.maxParticles = maxParticles
        startLoop()
    }

    fun updateCanvasSize(width: Int, height: Int) {
        canvasSize.value = IntSize(width, height)
    }

    fun setMode(mode: Mode) {
        if (mode == currentMode) return
        currentMode = mode
        if (autoResetOnModeChange) reset()
    }

    fun reset() {
        clearUseCase()
    }

    fun spawnAt(pos: Offset) {
        val p = createParticleForMode(pos.x, pos.y, currentMode)
        addUseCase(p)
    }

    fun burstRandom(count: Int) {
        val size = canvasSize.value
        if (size.width <= 0 || size.height <= 0) return
        repeat(count) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            spawnAt(Offset(x, y))
        }
    }

    private fun createParticleForMode(x: Float, y: Float, mode: Mode): Particle {
        return when (mode) {
            Mode.NORMAL -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 200f - 100f, vy = Random.nextFloat() * -200f,
                radius = Random.nextFloat() * 6f + 4f, color = colors.random()
            )
            Mode.EXPLOSION -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 600f - 300f, vy = Random.nextFloat() * 600f - 300f,
                radius = Random.nextFloat() * 6f + 6f, color = colors.random()
            )
            Mode.SNOW -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 40f - 20f, vy = Random.nextFloat() * 10f + 20f,
                radius = Random.nextFloat() * 3f + 2f, color = androidx.compose.ui.graphics.Color.White
            )
            Mode.BOUNCY -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 300f - 150f, vy = Random.nextFloat() * -300f,
                radius = Random.nextFloat() * 7f + 5f, color = androidx.compose.ui.graphics.Color(0xFF40C4FF)
            )
            Mode.FIRE -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 160f - 80f, vy = Random.nextFloat() * -260f,
                radius = Random.nextFloat() * 5f + 3f, color = androidx.compose.ui.graphics.Color(0xFFFF7043)
            )
            Mode.GRAVITY_WELL -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 160f - 80f, vy = Random.nextFloat() * -160f,
                radius = Random.nextFloat() * 6f + 4f, color = colors.random()
            )
            Mode.GALAXY -> Particle(
                x = x, y = y, vx = Random.nextFloat() * 100f - 50f, vy = Random.nextFloat() * 100f - 50f,
                radius = Random.nextFloat() * 5f + 3f, color = colors.random()
            )
        }
    }

    private fun startLoop() {
        viewModelScope.launch {
            var lastTime = System.nanoTime()

            while (isActive) {
                val now = System.nanoTime()
                var dt = (now - lastTime) / 1_000_000_000f
                lastTime = now

                dt = dt.coerceIn(1f / 240f, 1f / 10f)

                val speedFactor = if (slowMotion) 0.35f else 1f
                val effectiveDt = dt * speedFactor

                val size = canvasSize.value
                val mutable = repo.getMutableListSnapshot()

                // Only update physics IF user spawned something
                if (mutable.isNotEmpty() && size.width > 0 && size.height > 0) {
                    PhysicsEngine.updateParticles(
                        mutable,
                        size.width.toFloat(),
                        size.height.toFloat(),
                        effectiveDt,
                        currentMode,
                        gravity,
                        gravityWellStrength,
                        galaxyTangential
                    )
                    repo.publishFromMutable(mutable)
                }

                // NO AUTO SPAWN
                // User must explicitly tap Spawn / Burst

                delay(16L) // ~60fps
            }
        }
    }

}