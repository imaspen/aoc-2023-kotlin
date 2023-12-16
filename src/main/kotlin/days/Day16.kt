package days

import kotlin.math.max

class Day16 : Day {
    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0))
    }

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }
    }

    private val map = javaClass.getResource("/day16.txt")!!.readText().lines()

    // it ain't pretty, but it works
    private fun getEnergised(points: List<Pair<Point, Direction>>): Set<Point> {
        var nextPoints = points
        val visited = mutableSetOf<Point>()
        val explored = mutableSetOf<Pair<Point, Direction>>()

        while (nextPoints.isNotEmpty()) {
            nextPoints = nextPoints.flatMap { pair ->
                if (!explored.add(pair)) {
                    return@flatMap emptyList()
                }
                val (point, direction) = pair
                visited.add(point)
                when (map.getOrNull(point.y)?.getOrNull(point.x)) {
                    null -> {
                        visited.remove(point); emptyList()
                    }

                    '.' -> listOf(Pair(point + direction.offset, direction))
                    '\\' -> when (direction) {
                        Direction.NORTH -> listOf(Pair(point + Direction.WEST.offset, Direction.WEST))
                        Direction.EAST -> listOf(Pair(point + Direction.SOUTH.offset, Direction.SOUTH))
                        Direction.SOUTH -> listOf(Pair(point + Direction.EAST.offset, Direction.EAST))
                        Direction.WEST -> listOf(Pair(point + Direction.NORTH.offset, Direction.NORTH))
                    }

                    '/' -> when (direction) {
                        Direction.NORTH -> listOf(Pair(point + Direction.EAST.offset, Direction.EAST))
                        Direction.EAST -> listOf(Pair(point + Direction.NORTH.offset, Direction.NORTH))
                        Direction.SOUTH -> listOf(Pair(point + Direction.WEST.offset, Direction.WEST))
                        Direction.WEST -> listOf(Pair(point + Direction.SOUTH.offset, Direction.SOUTH))
                    }

                    '-' -> when {
                        direction == Direction.NORTH || direction == Direction.SOUTH -> {
                            val a = Pair(point + Direction.EAST.offset, Direction.EAST)
                            val b = Pair(point + Direction.WEST.offset, Direction.WEST)
                            listOf(a, b)
                        }

                        else -> listOf(Pair(point + direction.offset, direction))
                    }

                    '|' -> when {
                        direction == Direction.EAST || direction == Direction.WEST -> {
                            val a = Pair(point + Direction.NORTH.offset, Direction.NORTH)
                            val b = Pair(point + Direction.SOUTH.offset, Direction.SOUTH)
                            listOf(a, b)
                        }

                        else -> listOf(Pair(point + direction.offset, direction))
                    }

                    else -> error("Unexpected character in map")
                }
            }
        }

        return visited
    }

    override fun part1(): String {
        return getEnergised(listOf(Pair(Point(0, 0), Direction.EAST))).count().toString()
    }

    override fun part2(): String {
        val maxX = map[0].count() - 1
        val maxY = map.count() - 1
        return max((0..maxX).maxOf { x ->
            max(
                getEnergised(listOf(Pair(Point(x, 0), Direction.SOUTH))).count(),
                getEnergised(listOf(Pair(Point(x, maxY), Direction.NORTH))).count()
            )
        }, (0..maxY).maxOf { y ->
            max(
                getEnergised(listOf(Pair(Point(0, y), Direction.EAST))).count(),
                getEnergised(listOf(Pair(Point(maxX, y), Direction.WEST))).count()
            )
        }).toString()
    }
}