package fridge.lifebattle.rule.part

internal fun neighborCountsFromString(string: String) = string.map { char -> char.digitToInt() }.toIntArray()
fun beginRuleFromString(string: String) = RuleComponent(BEGIN_MIN_NEIGHBOR_COUNT, *neighborCountsFromString(string))
fun stayRuleFromString(string: String) = RuleComponent(STAY_MIN_NEIGHBOR_COUNT, *neighborCountsFromString(string))

internal fun getNeighborCountRange(minNeighborCount: Int) = minNeighborCount..MAX_NEIGHBOR_COUNT
