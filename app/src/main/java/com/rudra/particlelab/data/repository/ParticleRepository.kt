import com.rudra.particlelab.data.model.Particle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
/**
 * Simple in-memory repository with a MutableList backing to reduce allocations.
 * Exposes immutable snapshots via StateFlow each frame.
 */
class ParticleRepository {

    // internal mutable list (pool-friendly)
    private val internal = ArrayList<Particle>(256)

    // snapshot flow for UI
    private val _particles = MutableStateFlow<List<Particle>>(emptyList())
    fun getParticles(): StateFlow<List<Particle>> = _particles

    // configurable max to prevent runaway allocations / OOM
    var maxParticles: Int = 2000

    fun addParticle(p: Particle) {
        if (internal.size >= maxParticles) {
            // simple policy: overwrite oldest (circular would be better)
            internal.removeAt(0)
        }
        internal.add(p)
        publish()
    }

    fun clearParticles() {
        internal.clear()
        publish()
    }

    fun setParticles(newList: List<Particle>) {
        internal.clear()
        internal.addAll(newList)
        publish()
    }

    fun getMutableListSnapshot(): MutableList<Particle> {
        // return a shallow copy so physics can mutate independently before publishing
        return internal.map { it.copy() }.toMutableList()
    }

    fun publishFromMutable(mutable: MutableList<Particle>) {
        internal.clear()
        internal.addAll(mutable)
        publish()
    }

    private fun publish() {
        // publish an immutable snapshot for Compose
        _particles.value = internal.toList()
    }
}
