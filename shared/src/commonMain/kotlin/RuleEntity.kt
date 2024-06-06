package org.example

import kotlinx.serialization.Serializable

@Serializable
data class RuleEntity(
    var pair: AlternativePair,
    val set: RuleSet
) {
    fun toString(criteriaNames: Set<String>): String {
        val result = StringBuilder()
        if (set == RuleSet.PREPARE) {
            result.append("P(")
        } else {
            result.append("I(")
        }

        val first = pair.first
        val second = pair.second
        result
            .append(first.toString(criteriaNames))
            .append(", ")
            .append(second.toString(criteriaNames))
            .append(")")
        return result.toString()
    }

    fun prettyOut(criteriaNames: Set<String>): String {
        val result = StringBuilder("(")
        val first = pair.first
        val second = pair.second
        val ruleName = if (this.set == RuleSet.PREPARE) " prior\n" else " equal\n"
        result
            .append(first.toString(criteriaNames))
            .append(ruleName)
            .append(second.toString(criteriaNames))
            .append(")")
        return result.toString()
    }

    fun copy(): RuleEntity {
        return RuleEntity(pair, set)
    }
}
