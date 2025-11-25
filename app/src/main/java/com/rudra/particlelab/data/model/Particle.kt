package com.rudra.particlelab.data.model

import androidx.compose.ui.graphics.Color

data class Particle(
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var radius: Float = 6f,
    var color: Color = Color.White,
    // reuse flag (optional). Not strictly required but helps pooling.
    var alive: Boolean = true
) {
    fun resetTo(other: Particle) {
        x = other.x; y = other.y
        vx = other.vx; vy = other.vy
        radius = other.radius; color = other.color
        alive = other.alive
    }
}
