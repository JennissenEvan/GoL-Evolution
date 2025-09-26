package fridge.lifebattle.rule

import fridge.lifebattle.rule.part.beginRuleFromString
import fridge.lifebattle.rule.part.stayRuleFromString

fun ruleFromString(string: String): Rule {
    fun extractRuleComponent(char: Char): String {
        val match = Regex("$char(\\d*)([/\\\\]|\$)", RegexOption.IGNORE_CASE).find(string)

        if (match == null) return ""

        return match.groupValues[1]
    }

    return Rule(
        beginRuleFromString(extractRuleComponent('B')),
        stayRuleFromString(extractRuleComponent('S'))
    )
}
