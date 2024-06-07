package org.example

import kotlinx.serialization.Serializable

@Serializable
data class AlternativeEntity(
    val id: Int,
    val name: String,
    val criteriaToValue: Map<String, String?>
) {
    fun getValueByCriteria(criteria: CriteriaEntity): String? {
        val criteriaName = criteria.name
        return criteriaToValue[criteriaName]
    }

    fun getCriteriaNum(): Int {
        return criteriaToValue.filter { it.value != null }.keys.size
    }

    fun isEqual(alt: AlternativeEntity): Boolean {
        val critNames = alt.criteriaToValue.keys
        if (critNames.equals(criteriaToValue.keys)) {
            for (name in critNames) {
                val curCritValue = criteriaToValue.get(name)
                val altCritValue = alt.criteriaToValue.get(name)
                if ((curCritValue == null && altCritValue != null)
                    || (curCritValue != null && !curCritValue.equals(altCritValue))) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun copy(): AlternativeEntity {
        val copyCriterias = HashMap(criteriaToValue)
        return AlternativeEntity(-1, name, copyCriterias)
    }

    fun toString(criteriaNames: Set<String>): String {
        val result = StringBuilder("(")
        for (name in criteriaNames) {
            val value = criteriaToValue[name]
            if (value == null) {
                result.append("$name: - , ")
            } else {
                result.append("$name: $value").append(", ")
            }
        }
        result.deleteRange(result.length - 2, result.length)
        result.append(")")
        return result.toString()
    }

    fun toStringWithName(criteriaNames: Set<String>): String {
        return "$name ${toString(criteriaNames)}"
    }

    fun comparable(alternative: AlternativeEntity): Boolean {
        val compareKeys = alternative.criteriaToValue.filter { it.value != null }.keys
        val alternativeKeys = criteriaToValue.filter { it.value != null }.keys
        return compareKeys == alternativeKeys
    }
}
