package fridge.lifebattle.board

import fridge.lifebattle.rule.Rule

class LifeBoard(val width: Int, val height: Int) : MutableMap<Coordinates, Rule>, Iterable<Pair<Coordinates, Rule?>> {
    constructor(size: Int) : this(size, size)

    val dimensions = Pair(width, height)

    init {
        if (dimensions.toList().any { dimension -> dimension <= 0 }) throw IllegalArgumentException()
    }

    private var boardMap = mutableMapOf<Coordinates, Rule>()

    override val entries: MutableSet<MutableMap.MutableEntry<Coordinates, Rule>> get() = boardMap.entries
    override val keys: MutableSet<Coordinates> get() = boardMap.keys
    override val values: MutableCollection<Rule> get() = boardMap.values
    override fun clear() = boardMap.clear()
    override fun put(
        coordinates: Coordinates,
        rule: Rule
    ): Rule? {
        if (
            coordinates.toList().any { coordinate -> coordinate < 0 }
            || mapCoordinatesToDimensions(coordinates).toList()
                .any { (coordinate, dimension) -> coordinate >= dimension }
            )
            throw IllegalArgumentException()

        return boardMap.put(coordinates, rule)
    }
    override fun putAll(from: Map<out Coordinates, Rule>) = boardMap.putAll(from)
    override fun remove(key: Coordinates) = boardMap.remove(key)
    override val size get() = boardMap.size
    override fun containsKey(key: Coordinates) = boardMap.contains(key)
    override fun containsValue(value: Rule) = boardMap.containsValue(value)
    override fun get(key: Coordinates) = boardMap[key]
    override fun isEmpty() = boardMap.isEmpty()

    override fun iterator(): Iterator<Pair<Coordinates, Rule?>> {
        return iterator {
            for (y in 0..<height) for (x in 0..<width) {
                val coordinates = Coordinates(x, y)
                yield(coordinates to get(coordinates))
            }
        }
    }

    private fun mapCoordinatesToDimensions(coordinates: Coordinates): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return coordinates.toList().zip(dimensions.toList()).zipWithNext().single()
    }
    private fun getNeighborsOf(coordinates: Coordinates): List<Rule> {
        val offsetRange = -1..1
        val offsets = mutableListOf<Coordinates>()
        for (x in offsetRange) for (y in offsetRange) {
            if (x == 0 && y == 0) continue

            offsets.add(Coordinates(x, y))
        }

        return offsets.mapNotNull { offset -> get((coordinates + offset) % dimensions) }.sortedDescending()
    }

    fun step() {
        val nextBoardMap = mutableMapOf<Coordinates, Rule>()

        for ((coordinates, thisTileRule) in this) {
            val neighbors = getNeighborsOf(coordinates)
            val neighborCount = neighbors.size
            val tileIsOccupied = thisTileRule != null
            val beginCandidates = neighbors.filter { neighbor -> neighborCount in neighbor.begin }
                .toMutableList()
                .sortedDescending()

            if (beginCandidates.any()) {
                if (!tileIsOccupied) {
                    nextBoardMap[coordinates] = beginCandidates.first().getMutantCopy()
                    continue
                } else if (
                    neighborCount in thisTileRule.stay
                    && beginCandidates.any { beginCandidate -> beginCandidate.stay > thisTileRule.stay }
                    ) {
                    nextBoardMap[coordinates] = beginCandidates.maxBy { it.stay }.getMutantCopy()
                    continue
                }
            }

            if (!tileIsOccupied) continue

            if (neighborCount in thisTileRule.stay) nextBoardMap[coordinates] = thisTileRule
        }

        boardMap = nextBoardMap
    }

    fun draw() {
        for ((tile, rule) in this) {
            print(if (rule != null) '▣' else '□')
            if (tile.x == width - 1) print('\n')
        }
    }
}
