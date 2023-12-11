package days

import kotlin.math.abs

class Day11 : Day {
    private data class Point(var x: Long, var y: Long) {
        operator fun plusAssign(rhs: Point) {
            x += rhs.x
            y += rhs.y
        }

        infix fun manhattanDistance(rhs: Point): Long {
            return abs(x - rhs.x) + abs(y - rhs.y)
        }
    }

    private val lines = javaClass.getResource("/day11.txt")!!.readText().lines()
    private fun getPoints(expansionFactor: Long = 1): MutableList<Point> {
        val points = lines.drop(1).flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, char -> if (char == '.') null else Point(x.toLong(), y.toLong()) }
        }.toMutableList()

        val xGaps = (0..points.maxOf { it.x }).toSet() - points.map { it.x }.toSet()
        val yGaps = (0..points.maxOf { it.y }).toSet() - points.map { it.y }.toSet()

        val incX = Point(expansionFactor, 0)
        xGaps.forEachIndexed { index, baseX ->
            val x = (index * expansionFactor) + baseX
            for (point in points) {
                if (point.x > x) {
                    point += incX
                }
            }
        }
        val incY = Point(0, expansionFactor)
        yGaps.forEachIndexed { index, baseY ->
            val y = (index * expansionFactor) + baseY
            for (point in points) {
                if (point.y > y) {
                    point += incY
                }
            }
        }

        return points
    }

    private fun getDistanceSum(points: List<Point>): Long {
        return points.dropLast(1).withIndex().sumOf { (index, a) ->
            points.drop(1 + index).sumOf { b -> a manhattanDistance b }
        }
    }

    override fun part1(): String {
        return getDistanceSum(getPoints()).toString()
    }

    override fun part2(): String {
        val expansionFactor = lines[0].toLong()
        return getDistanceSum(getPoints(expansionFactor - 1)).toString()
    }
}