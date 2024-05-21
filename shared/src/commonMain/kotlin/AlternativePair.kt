package org.example

import kotlinx.serialization.Serializable

@Serializable
data class AlternativePair(
    var first: AlternativeEntity,
    var second: AlternativeEntity
) {
    fun isEqual(other: Any?): Boolean {
        if (other == null || other !is AlternativePair) return false
        if (first.isEqual(other.first) && second.isEqual(other.second)) return true
        return first.isEqual(other.second) && second.isEqual(other.first)
    }
}
