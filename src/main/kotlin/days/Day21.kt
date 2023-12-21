package days

class Day21 : Day {
    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0))
    }

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }
    }

    private val steps: List<Int>
    private val map: List<String>

    init {
        val (stepsString, mapString) = javaClass.getResource("/day21.txt")!!.readText().split("\n\n")
        steps = stepsString.split(',').map { it.toInt() }
        map = mapString.lines()
    }

    override fun part1(): String {
        val startY = map.indexOfFirst { it.contains('S') }
        val startX = map[startY].indexOf('S')
        var currentSteps = mutableSetOf<Point>(Point(startX, startY))
        var nextSteps = mutableSetOf<Point>()
        val validX = 0..<(map[0].count())
        val validY = 0..<(map.count())

        for (i in 0..<steps[0]) {
            for (step in currentSteps) {
                for (direction in Direction.entries) {
                    val point = step + direction.offset
                    val (x, y) = point
                    if (!validX.contains(x)) continue
                    if (!validY.contains(y)) continue
                    if (map[y][x] == '#') continue

                    nextSteps.add(point)
                }
            }

            val t = currentSteps
            currentSteps = nextSteps
            nextSteps = t
            nextSteps.clear()
        }
        return currentSteps.size.toString()
    }

    override fun part2(): String {
        val startY = map.indexOfFirst { it.contains('S') }
        val startX = map[startY].indexOf('S')
        var currentSteps = mutableSetOf(Point(startX, startY))
        var nextSteps = mutableSetOf<Point>()
        val maxX = map[0].count()
        val maxY = map.count()
        val quadratic = mutableListOf<Int>()

        for (i in 1..steps[1]) {
            for (step in currentSteps) {
                for (direction in Direction.entries) {
                    val point = step + direction.offset
                    val (x, y) = point
                    if (map[Math.floorMod(y, maxY)][Math.floorMod(x, maxX)] == '#') continue

                    nextSteps.add(point)
                }
            }

            val t = currentSteps
            currentSteps = nextSteps
            nextSteps = t
            nextSteps.clear()

            if (i % maxY == steps[1] % maxY) {
                quadratic.add(currentSteps.count())
                if (quadratic.size == 3) {
                    val n = (steps[1] - (steps[1] % maxY)) / maxY
                    val (v1, v2, v3) = quadratic
                    val a = (v1 - (2L * v2) + v3) / 2L
                    val b = (-3L * v1 + 4L * v2 - v3) / 2L
                    return (a * n * n + b * n + v1).toString()
                }
            }
        }
        return currentSteps.size.toString()
    }
}