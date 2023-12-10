package days

import kotlin.math.min

// this is so gross but i am tired
class Day10 : Day {
    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0))
    }

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }
    }

    private data class Step(val point: Point, val cameFrom: Direction)

    private val map = javaClass.getResource("/day10.txt")!!.readText().lines()
    private val startPoint: Point
    private val startingSteps: Pair<Step, Step>

    init {
        val startY = map.indexOfFirst { it.contains('S') }
        val startX = map[startY].indexOf('S')
        startPoint = Point(startX, startY)
        val steps = buildList {
            val north = (startPoint + Direction.NORTH.offset)
            val south = (startPoint + Direction.SOUTH.offset)
            val east = (startPoint + Direction.EAST.offset)
            val west = (startPoint + Direction.WEST.offset)
            if ("|7F".contains(getAtPoint(north))) {
                add(Step(north, Direction.SOUTH))
            }
            if ("|LJ".contains(getAtPoint(south))) {
                add(Step(south, Direction.NORTH))
            }
            if ("-J7".contains(getAtPoint(east))) {
                add(Step(east, Direction.WEST))
            }
            if ("-LF".contains(getAtPoint(west))) {
                add(Step(west, Direction.EAST))
            }
        }
        startingSteps = Pair(steps[0], steps[1])
    }

    private fun getAtPoint(x: Int, y: Int): Char {
        val point = map.getOrNull(y)?.getOrNull(x) ?: '.'
        if (point != 'S') return point

        val directions = setOf(startingSteps.first.cameFrom, startingSteps.second.cameFrom)

        if (directions.contains(Direction.NORTH)) {
            return if (directions.contains(Direction.EAST)) {
                '7'
            } else if (directions.contains(Direction.WEST)) {
                'F'
            } else {
                '|'
            }
        } else if (directions.contains(Direction.SOUTH)) {
            return if (directions.contains(Direction.EAST)) {
                'J'
            } else if (directions.contains(Direction.WEST)) {
                'L'
            } else {
                '|'
            }
        } else {
            return '-'
        }
    }

    private fun getAtPoint(point: Point): Char {
        return getAtPoint(point.x, point.y)
    }

    private fun getNextStep(step: Step): Step {
        val pipe = getAtPoint(step.point)
        return when (pipe to step.cameFrom) {
            '|' to Direction.NORTH -> Step(step.point + Direction.SOUTH.offset, Direction.NORTH)
            '|' to Direction.SOUTH -> Step(step.point + Direction.NORTH.offset, Direction.SOUTH)

            '-' to Direction.EAST -> Step(step.point + Direction.WEST.offset, Direction.EAST)
            '-' to Direction.WEST -> Step(step.point + Direction.EAST.offset, Direction.WEST)

            'L' to Direction.NORTH -> Step(step.point + Direction.EAST.offset, Direction.WEST)
            'L' to Direction.EAST -> Step(step.point + Direction.NORTH.offset, Direction.SOUTH)

            'J' to Direction.NORTH -> Step(step.point + Direction.WEST.offset, Direction.EAST)
            'J' to Direction.WEST -> Step(step.point + Direction.NORTH.offset, Direction.SOUTH)

            'F' to Direction.SOUTH -> Step(step.point + Direction.EAST.offset, Direction.WEST)
            'F' to Direction.EAST -> Step(step.point + Direction.SOUTH.offset, Direction.NORTH)

            '7' to Direction.SOUTH -> Step(step.point + Direction.WEST.offset, Direction.EAST)
            '7' to Direction.WEST -> Step(step.point + Direction.SOUTH.offset, Direction.NORTH)

            else -> error("unexpected combination of pipe and came from")
        }
    }

    override fun part1(): String {
        var (stepA, stepB) = startingSteps
        val loopPoints = mutableSetOf(stepA.point, stepB.point)
        var distance = 1

        while (true) {
            stepA = getNextStep(stepA)
            stepB = getNextStep(stepB)
            if (stepA.point == stepB.point) {
                return (distance + 1).toString()
            } else if (!loopPoints.add(stepA.point) || !loopPoints.add(stepB.point)) {
                return distance.toString()
            }
            distance++
        }
    }

    private fun getFillPoint(point: Point): Point? {
        val pipe = getAtPoint(point)
        return when (pipe) {
            'L' -> point + Direction.NORTH.offset + Direction.EAST.offset
            'F' -> point + Direction.SOUTH.offset + Direction.EAST.offset
            'J' -> point + Direction.NORTH.offset + Direction.WEST.offset
            '7' -> point + Direction.SOUTH.offset + Direction.WEST.offset
            else -> null
        }
    }

    override fun part2(): String {
        val startingSteps = startingSteps
        var (stepA, stepB) = startingSteps
        val loopPoints = mutableSetOf(startPoint, stepA.point, stepB.point)
        val fills = mutableSetOf<Point>()

        while (true) {
            stepA = getNextStep(stepA)
            stepB = getNextStep(stepB)

            getFillPoint(stepA.point)?.also { fills.add(it) }
            getFillPoint(stepB.point)?.also { fills.add(it) }

            if (stepA.point == stepB.point) {
                loopPoints.add(stepA.point)
                break
            } else if (!loopPoints.add(stepA.point) || !loopPoints.add(stepB.point)) {
                break
            }
        }

        val minX = loopPoints.minOf { it.x }
        val maxX = loopPoints.maxOf { it.x }
        val minY = loopPoints.minOf { it.y }
        val maxY = loopPoints.maxOf { it.y }

        // https://en.wikipedia.org/wiki/Point_in_polygon
        var count = 0
        for (x in minX..maxX) {
            var lefts = 0
            var rights = 0
            for (y in minY..maxY) {
                val point = Point(x, y)
                if (loopPoints.contains(point)) {
                    when (getAtPoint(point)) {
                        '-' -> {
                            lefts++
                            rights++
                        }

                        'L' -> rights++
                        'F' -> rights++
                        'J' -> lefts++
                        '7' -> lefts++
                        '|' -> {}
                        else -> error("Unexpected point char ${getAtPoint(point)}")
                    }
                } else {
                    if (min(lefts, rights) % 2 == 1) {
                        count++
                    }
                }
            }
        }
        return count.toString()
    }
}