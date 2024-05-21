package org.example

import kotlinx.serialization.Serializable

@Serializable
data class DataContext(
    var alts: MutableList<AlternativeEntity>,
    var criterias: MutableList<CriteriaEntity>,
    var P: MutableList<RuleEntity>,
    var I: MutableList<RuleEntity>,
    var altsComparsion: MutableList<AlternativeComparsionEntity>
) {
    fun getCriteriaByNameOrNull(name: String): CriteriaEntity? {
        return criterias.firstOrNull { criteria ->
            criteria.name == name
        }
    }

    fun getCriteriaNames(): Set<String> {
        return criterias.map { it.name }.toSet()
    }

    fun getNonPriorAlts(): List<AlternativeEntity> {
        val notPrepareAlts = altsComparsion
            .filter { it.rule.set == RuleSet.PREPARE }
            .map { it.rule.pair.second }
        return alts.filter { alt -> notPrepareAlts.firstOrNull { it.isEqual(alt) } == null }
    }

    fun addP(statement: AlternativePair) {
        P.add(RuleEntity(statement, RuleSet.PREPARE))
    }

    fun addI(statement: AlternativePair) {
        I.add(RuleEntity(statement, RuleSet.EQUAL))
    }

    fun removeRule(rule: RuleEntity) {
        when (rule.set) {
            RuleSet.PREPARE -> P.remove(rule)
            RuleSet.EQUAL -> I.remove(rule)
        }
    }

    fun addCriteria(criteria: CriteriaEntity) {
        criterias.add(criteria)
    }

    fun removeCriteria(criteria: CriteriaEntity) {
        criterias.remove(criteria)
    }

    fun formRulesByCriteriaValues() {
        P.removeAll { it.pair.first.criteriaToValue.count() == 1 }
        I.removeAll { it.pair.first.criteriaToValue.count() == 1 }
        criterias.forEach { criteria ->
            val priorToValue = criteria.valueToPrior
                .map { it.value to it.key }
                .sortedBy { it.first }
            priorToValue.forEachIndexed { index, pair ->
                for (i in index + 1 until priorToValue.size) {
                    val alternative1 = AlternativeEntity(-1, String(), mapOf(criteria.name to pair.second))
                    val alternative2 = AlternativeEntity(
                        -1, String(), mapOf(criteria.name to priorToValue.get(i).second))
                    if (priorToValue.get(i).first == pair.first) {
                        addI(AlternativePair(alternative1, alternative2))
                    } else {
                        addP(AlternativePair(alternative1, alternative2))
                    }
                }
            }
        }
    }
}
