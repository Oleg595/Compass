package org.example

import kotlinx.serialization.Serializable

@Serializable
data class CriteriaEntity(
    val name: String,
    val values: MutableSet<String> = mutableSetOf(),
    val valueToPrior: MutableMap<String,Int> = mutableMapOf()
) {
    fun addValue(value: String) {
        values.add(value)
    }
    fun removeValue(value: String) {
        values.remove(value)
    }

    fun setPrior(value: String, prior: Int) {
        valueToPrior[value] = prior
    }
    fun removePrior(value: String) {
        valueToPrior.remove(value)
    }

    fun allValuesHavePrior(): Boolean {
        return valueToPrior.keys.containsAll(values)
    }
}
