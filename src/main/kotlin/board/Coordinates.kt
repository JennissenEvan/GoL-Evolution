package fridge.lifebattle.board

data class Coordinates(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"

    private fun doOperationWith(other: Coordinates, operatorFunction: (Int, Int) -> Int): Coordinates {
        val newCoordinatePair = toList()
            .zip(other.toList())
            .map { (thisCoordinate, otherCoordinate) -> operatorFunction(thisCoordinate, otherCoordinate) }
            .zipWithNext().single()
        return coordinatesFrom(newCoordinatePair)
    }
    operator fun plus(other: Coordinates) =
        doOperationWith(other) { thisCoordinate, otherCoordinate -> thisCoordinate + otherCoordinate }
    operator fun minus(other: Coordinates) =
        doOperationWith(other) { thisCoordinate, otherCoordinate -> thisCoordinate - otherCoordinate }
    operator fun rem(other: Coordinates) =
        doOperationWith(other) { thisCoordinate, otherCoordinate -> Math.floorMod(thisCoordinate, otherCoordinate) }
    operator fun rem(other: Pair<Int, Int>) = rem(coordinatesFrom(other))

    fun toPair() = Pair(x, y)
    fun toList() = listOf(x, y)
}