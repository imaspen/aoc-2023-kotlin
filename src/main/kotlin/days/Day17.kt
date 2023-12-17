package days

import kotlin.math.abs

// This is slow but correct, there's probably a better way to represent the connections, but I don't know it
class Day17 : Day {
    private data class Point(val x: Int, val y: Int) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }

        infix fun manhattanDistance(rhs: Point): Int {
            return abs(x - rhs.x) + abs(y - rhs.y)
        }
    }

    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0));

        fun getTurns(): List<Direction> {
            return when (this) {
                NORTH -> listOf(EAST, WEST)
                SOUTH -> listOf(EAST, WEST)
                EAST -> listOf(NORTH, SOUTH)
                WEST -> listOf(NORTH, SOUTH)
            }
        }
    }

    private data class Step(val point: Point, val direction: Direction, val stepsSinceLastTurn: Int)

    private data class Node(
        val heatLoss: Int, val step: Step, var tentativeDistance: Int = Int.MAX_VALUE, var previousStep: Step? = null
    )

    private fun generateMap(usingUltraCrucibles: Boolean = false): List<List<Map<Direction, List<Node>>>> {
        return javaClass.getResource("/day17.txt")!!.readText().lines().mapIndexed { y, line ->
            line.mapIndexed { x, char ->
                Direction.entries.associateWith { direction ->
                    (0..(if (usingUltraCrucibles) 9 else 2)).map { stepsSinceLastTurn ->
                        Node(
                            char.digitToInt(), Step(Point(x, y), direction, stepsSinceLastTurn)
                        )
                    }
                }
            }
        }
    }

    private fun getNeighbours(step: Step, maxPoint: Point): List<Step> {
        return buildList {
            fun pointInMap(point: Point): Boolean {
                return point.x >= 0 && point.y >= 0 && point.x <= maxPoint.x && point.y <= maxPoint.y
            }

            if (step.stepsSinceLastTurn < 2) {
                val nextPoint = step.point + step.direction.offset
                if (pointInMap(nextPoint)) {
                    add(Step(nextPoint, step.direction, step.stepsSinceLastTurn + 1))
                }
            }
            for (direction in step.direction.getTurns()) {
                val nextPoint = step.point + direction.offset
                if (pointInMap(nextPoint)) {
                    add(Step(nextPoint, direction, 0))
                }
            }
        }
    }

    private fun getUltraCrucibleNeighbours(step: Step, maxPoint: Point): List<Step> {
        return buildList {
            fun pointInMap(point: Point): Boolean {
                return point.x >= 0 && point.y >= 0 && point.x <= maxPoint.x && point.y <= maxPoint.y
            }

            if (step.stepsSinceLastTurn < 9) {
                val nextPoint = step.point + step.direction.offset
                if (pointInMap(nextPoint)) {
                    add(Step(nextPoint, step.direction, step.stepsSinceLastTurn + 1))
                }
            }

            if (step.stepsSinceLastTurn >= 3) {
                for (direction in step.direction.getTurns()) {
                    val nextPoint = step.point + direction.offset
                    if (pointInMap(nextPoint)) {
                        add(Step(nextPoint, direction, 0))
                    }
                }
            }
        }
    }

    override fun part1(): String {
        // y, x, direction of entry, steps since turn
        val map = generateMap()

        val maxPoint = Point(map[0].count() - 1, map[1].count() - 1)

        val gScore = mutableMapOf(
            map[0][1][Direction.EAST]!![0].step to map[0][1][Direction.EAST]!![0].heatLoss,
            map[1][0][Direction.SOUTH]!![0].step to map[1][0][Direction.SOUTH]!![0].heatLoss
        )

        val fScore = mutableMapOf(
            map[0][1][Direction.EAST]!![0].step to (map[0][1][Direction.EAST]!![0].step.point manhattanDistance maxPoint),
            map[1][0][Direction.SOUTH]!![0].step to (map[1][0][Direction.SOUTH]!![0].step.point manhattanDistance maxPoint),
        )

        val openSet = mutableSetOf(map[0][1][Direction.EAST]!![0].step, map[1][0][Direction.SOUTH]!![0].step)

        while (openSet.isNotEmpty()) {
            val step = openSet.minBy { step -> fScore.getOrDefault(step, Int.MAX_VALUE) }
            if (step.point == maxPoint) {
                return gScore[step]!!.toString()
            }
            openSet.remove(step)

            for (neighbour in getNeighbours(step, maxPoint)) {
                val tentativeGScore = gScore[step]!! + map[neighbour.point.y][neighbour.point.x][neighbour.direction]!![neighbour.stepsSinceLastTurn].heatLoss
                if (tentativeGScore < gScore.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    gScore[neighbour] = tentativeGScore
                    fScore[neighbour] = tentativeGScore + (neighbour.point manhattanDistance maxPoint)
                    openSet.add(neighbour)
                }
            }
        }

        error("path not found")
    }

    override fun part2(): String {
        // y, x, direction of entry, steps since turn
        val map = generateMap(true)

        val maxPoint = Point(map[0].count() - 1, map[1].count() - 1)

        val gScore = mutableMapOf(
            map[0][1][Direction.EAST]!![0].step to map[0][1][Direction.EAST]!![0].heatLoss,
            map[1][0][Direction.SOUTH]!![0].step to map[1][0][Direction.SOUTH]!![0].heatLoss
        )

        val fScore = mutableMapOf(
            map[0][1][Direction.EAST]!![0].step to (map[0][1][Direction.EAST]!![0].step.point manhattanDistance maxPoint),
            map[1][0][Direction.SOUTH]!![0].step to (map[1][0][Direction.SOUTH]!![0].step.point manhattanDistance maxPoint),
        )

        val openSet = mutableSetOf(map[0][1][Direction.EAST]!![0].step, map[1][0][Direction.SOUTH]!![0].step)

        while (openSet.isNotEmpty()) {
            val step = openSet.minBy { step -> fScore.getOrDefault(step, Int.MAX_VALUE) }
            if (step.point == maxPoint) {
                return gScore[step]!!.toString()
            }
            openSet.remove(step)

            for (neighbour in getUltraCrucibleNeighbours(step, maxPoint)) {
                val tentativeGScore = gScore[step]!! + map[neighbour.point.y][neighbour.point.x][neighbour.direction]!![neighbour.stepsSinceLastTurn].heatLoss
                if (tentativeGScore < gScore.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    gScore[neighbour] = tentativeGScore
                    fScore[neighbour] = tentativeGScore + (neighbour.point manhattanDistance maxPoint)
                    openSet.add(neighbour)
                }
            }
        }

        error("path not found")
    }
}