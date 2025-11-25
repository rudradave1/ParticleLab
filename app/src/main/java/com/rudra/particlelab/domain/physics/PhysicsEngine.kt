package com.rudra.particlelab.domain.physics

import com.rudra.particlelab.data.Mode
import com.rudra.particlelab.data.model.Particle
import kotlin.math.hypot
import kotlin.math.sqrt


import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

object PhysicsEngine {

    // base tunables (can be overridden per-frame by VM)
    var pixelsPerMeter = 60f
    var bounce = -0.5f

    /**
     * Updates particles in-place.
     *
     * gravity, mode-specific params are passed from ViewModel to avoid global state coupling.
     */
    fun updateParticles(
        particles: MutableList<Particle>,
        width: Float,
        height: Float,
        dt: Float,
        mode: Mode,
        gravity: Float,
        gravityWellStrength: Float,
        galaxyTangential: Float
    ) {
        if (dt <= 0f) return

        // apply mode-specific forces first (affects velocities)
        when (mode) {
            Mode.FIRE -> {
                // upward thrust + jitter
                particles.forEach { p ->
                    p.vy -= gravity * pixelsPerMeter * dt * 1.4f
                    p.vx += (Random.nextFloat() - 0.5f) * 80f * dt
                    // tiny drag
                    p.vx *= 0.999f
                    p.vy *= 0.999f
                }
            }

            Mode.SNOW -> {
                particles.forEach { p ->
                    p.vy += gravity * pixelsPerMeter * dt * 0.25f
                    p.vx += (Random.nextFloat() - 0.5f) * 20f * dt
                    p.vx *= 0.995f
                    p.vy *= 0.999f
                }
            }

            Mode.EXPLOSION -> {
                // damping/explosion decay
                particles.forEach { p ->
                    p.vx *= 0.985f
                    p.vy *= 0.985f
                }
            }

            Mode.GRAVITY_WELL -> {
                val cx = width * 0.5f
                val cy = height * 0.5f
                particles.forEach { p ->
                    val dx = cx - p.x
                    val dy = cy - p.y
                    val dist = hypot(dx, dy).coerceAtLeast(1f)
                    val force = gravityWellStrength / (dist + 40f)
                    p.vx += (force * dx / dist) * dt
                    p.vy += (force * dy / dist) * dt
                }
            }

            Mode.GALAXY -> {
                val cx = width * 0.5f
                val cy = height * 0.5f
                particles.forEach { p ->
                    val dx = p.x - cx
                    val dy = p.y - cy
                    val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                    val tangential = galaxyTangential / (dist + 40f)
                    p.vx += -dy * tangential * dt
                    p.vy += dx * tangential * dt
                    val pull = 40f / (dist + 10f)
                    p.vx += -dx * pull * dt
                    p.vy += -dy * pull * dt
                }
            }

            Mode.BOUNCY, Mode.NORMAL -> {
                // no extra per-mode preforces; gravity applied below for NORMAL/BOUNCY
            }
        }

        // Integrate gravity for applicable modes
        val applyGravity = mode != Mode.FIRE // fire simulates anti-gravity
        particles.forEach { p ->
            if (applyGravity) p.vy += gravity * pixelsPerMeter * dt
            p.x += p.vx * dt
            p.y += p.vy * dt
        }

        // Boundary collisions and clamping
        particles.forEach { p ->
            // left
            if (p.x - p.radius < 0f) {
                p.x = p.radius
                p.vx *= bounce
            }
            // right
            if (p.x + p.radius > width) {
                p.x = width - p.radius
                p.vx *= bounce
            }
            // top
            if (p.y - p.radius < 0f) {
                p.y = p.radius
                p.vy *= bounce
            }
            // bottom
            if (p.y + p.radius > height) {
                p.y = height - p.radius
                p.vy *= bounce
            }
        }
    }
}
