package fridge.lifebattle

import kotlin.random.Random

fun coinFlipHeads() = setOf(true, false).random()
fun chance(percentage: Double): Boolean {
    if (percentage < 0 || percentage > 100) throw IllegalArgumentException()

    return Random.nextDouble() <= percentage / 100
}
fun chance(percentage: Int) = chance(percentage.toDouble())
