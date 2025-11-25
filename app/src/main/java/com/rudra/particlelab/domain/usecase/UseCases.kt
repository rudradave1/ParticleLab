package com.rudra.particlelab.domain.usecase

import ParticleRepository
import com.rudra.particlelab.data.model.Particle

class AddParticleUseCase(private val repo: ParticleRepository) {
    operator fun invoke(p: Particle) = repo.addParticle(p)
}

class ClearParticlesUseCase(private val repo: ParticleRepository) {
    operator fun invoke() = repo.clearParticles()
}

class UpdateParticlesUseCase(private val repo: ParticleRepository) {
    operator fun invoke(list: List<Particle>) = repo.setParticles(list)
}
