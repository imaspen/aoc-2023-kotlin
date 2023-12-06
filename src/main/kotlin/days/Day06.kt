package days

import kotlin.math.*

class Day06 : Day {
    private val lines = javaClass.getResource("/day06.txt")!!.readText().lines()

    private fun getWinCount(time: Double, distance: Double): Long {
        val start = floor(1.0 + 0.5 * (time - sqrt(time.pow(2) - 4 * distance)))
        val end = ceil(0.5 * (time + sqrt(time.pow(2) - 4 * distance)) - 1.0)
        return (1 + end - start).roundToLong()
    }

    override fun part1(): String {
        val numberMatcher = Regex("""\d+""")
        val definitions = lines.map { numberMatcher.findAll(it).map { match -> match.value.toDouble() } }
            .zipWithNext { a, b -> a.zip(b) }[0].toList()

        // saw the brute force punishment coming for once and went algebraic from the start
        return definitions.fold(1L) { acc, (time, distance) -> acc * getWinCount(time, distance) }.toString()
    }

    override fun part2(): String {
        val definitions = lines.map { it.replace(" ", "").split(":")[1].toDouble() }
        return getWinCount(definitions[0], definitions[1]).toString()
    }
}