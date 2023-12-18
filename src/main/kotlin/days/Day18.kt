package days

import kotlin.math.abs

class Day18 : Day {
    private data class Point(val x: Long, val y: Long) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }

        operator fun times(rhs: Long): Point {
            return Point(x * rhs, y * rhs)
        }

        infix fun manhattanDistance(rhs: Point): Long {
            return abs(x - rhs.x) + abs(y - rhs.y)
        }
    }

    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0));

        companion object {
            fun fromString(string: String): Direction {
                return when (string) {
                    "U" -> NORTH
                    "D" -> SOUTH
                    "R" -> EAST
                    "L" -> WEST
                    else -> error("unrecognized direction: string")
                }
            }
        }
    }

    private data class Instruction(
        val direction: Direction,
        val steps: Long,
        val colorDirection: Direction,
        val colorSteps: Long
    ) {
        companion object {
            fun fromString(string: String): Instruction {
                val (direction, steps, color) = string.split(" ")
                val colorDirection = when (color.dropLast(1).takeLast(1).toInt(16)) {
                    0 -> Direction.EAST
                    1 -> Direction.SOUTH
                    2 -> Direction.WEST
                    3 -> Direction.NORTH
                    else -> error("invalid direction")
                }
                return Instruction(
                    Direction.fromString(direction),
                    steps.toLong(),
                    colorDirection,
                    color.drop(2).take(5).toLong(16)
                )
            }
        }
    }

    private val instructions =
        javaClass.getResource("/day18.txt")!!.readText().lines().map { Instruction.fromString(it) }

    private fun calculateArea(points: List<Point>): Long {
        val innerArea = abs(points.windowed(2, 1).sumOf { (a, b) ->
            (a.y + b.y) * (a.x - b.x)
        } / 2)
        val edgePoints = points.windowed(2, 1).sumOf {(a, b) ->
            a manhattanDistance b
        }
        return innerArea + (edgePoints / 2) + 1
    }

    override fun part1(): String {
        var position = Point(0, 0)
        val points = mutableListOf(position)
        for (instruction in instructions) {
            position += (instruction.direction.offset * instruction.steps)
            points.add(position)
        }

        return calculateArea(points).toString()
    }

    override fun part2(): String {
        var position = Point(0, 0)
        val points = mutableListOf(position)
        for (instruction in instructions) {
            position += (instruction.colorDirection.offset * instruction.colorSteps)
            points.add(position)
        }

        return calculateArea(points).toString()
    }
}