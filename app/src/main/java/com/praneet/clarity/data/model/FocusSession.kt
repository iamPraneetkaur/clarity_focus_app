
package com.praneet.clarity.data.model
enum class EnergyLevel(val emoji: String) {
    HIGH("⚡"),
    BALANCED("🌱"),
    LOW("☁️")
}

data class FocusSession(
    val id: String = "",
    val goalId: String = "",
    val durationMinutes: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val energyLevel: EnergyLevel = EnergyLevel.BALANCED
)
