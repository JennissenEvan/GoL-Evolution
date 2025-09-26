package fridge.lifebattle.rule.part

internal const val MAX_NEIGHBOR_COUNT = 8
internal const val BEGIN_MIN_NEIGHBOR_COUNT = 1
internal const val STAY_MIN_NEIGHBOR_COUNT = 0

val beginNeighborCountRange = getNeighborCountRange(BEGIN_MIN_NEIGHBOR_COUNT)
val stayNeighborCountRange = getNeighborCountRange(STAY_MIN_NEIGHBOR_COUNT)
