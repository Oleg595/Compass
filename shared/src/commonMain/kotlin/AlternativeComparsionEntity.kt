package org.example

import kotlinx.serialization.Serializable

@Serializable
data class AlternativeComparsionEntity (
    val rule: RuleEntity,
    val outputRules: List<RuleEntity>
)
