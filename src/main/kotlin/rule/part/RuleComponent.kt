package fridge.lifebattle.rule.part

import fridge.lifebattle.coinFlipHeads

class RuleComponent internal constructor(val minNeighborCount: Int, vararg neighborCounts: Int) :
    Set<Int>,
    Comparable<RuleComponent>
{
    val neighborCountRange = getNeighborCountRange(minNeighborCount)

    init {
        if (minNeighborCount !in getNeighborCountRange(0)
            || neighborCounts.any { x -> !canInclude(x) })
            throw IllegalArgumentException()
    } private val entries = neighborCounts.toSet()

    override fun toString() = entries.toList().sorted().joinToString("")

    override fun hashCode(): Int = entries.hashCode()
    override fun equals(other: Any?) = other is Set<*> && other == entries
    override operator fun compareTo(other: RuleComponent): Int {
        if (other.size != size) return if (other.size < size) -1 else 1

        for (neighborCount in union(other).toList().sorted())  {
            val thisHasCount = neighborCount in this
            val otherHasCount = neighborCount in other

            if (thisHasCount && otherHasCount) continue

            if (thisHasCount) return -1
            if (otherHasCount) return 1
        }

        return 0
    }

    override val size get() = entries.size
    override fun isEmpty() = entries.isEmpty()
    override fun iterator() = entries.iterator()
    override fun containsAll(elements: Collection<Int>) = entries.containsAll(elements)
    override fun contains(element: Int) = entries.contains(element)

    fun canInclude(count: Int) = count in neighborCountRange
    fun canFit(count: Int) = canInclude(count) && count !in entries

    fun getMutantCopy(): RuleComponent {
        val possibleAdditions = neighborCountRange.toSet() - this
        fun randomAddition() = possibleAdditions.random()
        val mutantNeighborCounts = listOf(
            fun(): Set<Int> {  // Add Mutation
                if (possibleAdditions.isEmpty()) return this

                return this union setOf(randomAddition())
            },
            fun(): Set<Int> {  // Remove Mutation
                if (this.isEmpty()) return this

                return this - setOf(this.random())
            },
            fun(): Set<Int> {  // Replace Mutation
                if (this.isEmpty()) return this

                val countToReplace = this.random()
                val withoutReplaced = this - setOf(countToReplace)

                var replacementCount: Int? = null
                if (coinFlipHeads()) {
                    val incrementallyChangedCount = countToReplace + setOf(1, -1).random()

                    if (canFit(incrementallyChangedCount)) replacementCount = incrementallyChangedCount
                }
                if (replacementCount == null && possibleAdditions.isNotEmpty()) {
                    replacementCount = randomAddition()
                }

                return withoutReplaced union if (replacementCount != null) setOf(replacementCount) else emptySet()
            }
        ).random()()

        return RuleComponent(minNeighborCount, *mutantNeighborCounts.toIntArray())
    }
}