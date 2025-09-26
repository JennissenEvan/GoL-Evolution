package fridge.lifebattle.rule

import fridge.lifebattle.chance
import fridge.lifebattle.rule.part.RuleComponent

class Rule(val begin: RuleComponent, val stay: RuleComponent) : Iterable<RuleComponent>, Comparable<Rule> {
    init {
        if (begin.minNeighborCount != 1 || stay.minNeighborCount != 0) throw IllegalArgumentException()
    }

    override fun toString(): String {
        return listOf(
            Pair(begin, "B"),
            Pair(stay, "S")
        )
            .mapNotNull { (component, prefix) -> if (component.isNotEmpty()) "$prefix$component" else null }
            .joinToString("/")
    }

    override operator fun equals(other: Any?) = other is Rule && other.toList() == this.toList()
    override fun hashCode(): Int {
        var result = begin.hashCode()
        result = 31 * result + stay.hashCode()
        return result
    }
    override operator fun compareTo(other: Rule): Int {
        if (other.begin > begin) return -1
        if (other.begin < begin) return 1

        if (other.stay > stay) return -1
        if (other.stay < stay) return 1

        return 0
    }

    override fun iterator() = listOf(begin, stay).iterator()

    fun canEat(other: Rule) = this > other

    fun copy(): Rule {
        return getMutantCopy(0.0)
    }
    fun getMutantCopy(mutationRate: Double = 0.1): Rule {
        if (mutationRate == 100.0) throw IllegalArgumentException()

        val newRuleComponents = mutableMapOf("begin" to begin, "stay" to stay)

        while (chance(mutationRate)) {
            val (componentKey, component) = newRuleComponents.entries.random()
            newRuleComponents[componentKey] = component.getMutantCopy()
        }

        return Rule(newRuleComponents.getValue("begin"), newRuleComponents.getValue("stay"))
    }
    fun getMutantCopy(mutationRate: Int) = getMutantCopy(mutationRate.toDouble())
}