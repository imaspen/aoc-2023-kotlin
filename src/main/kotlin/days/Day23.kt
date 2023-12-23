package days

class Day23 : Day {
    private enum class Direction(val offset: Point) {
        NORTH(Point(0, -1)), SOUTH(Point(0, 1)), EAST(Point(1, 0)), WEST(Point(-1, 0))
    }

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y)
        }

        fun getNeighbours(map: List<String>, visited: Set<Point>, ignoreHills: Boolean) = buildList {
            val validDirections = if (ignoreHills) Direction.entries else when (map[y][x]) {
                '>' -> listOf(Direction.EAST)
                '<' -> listOf(Direction.WEST)
                '^' -> listOf(Direction.NORTH)
                'v' -> listOf(Direction.SOUTH)
                else -> Direction.entries
            }
            for (direction in validDirections) {
                val nextPoint = this@Point + direction.offset
                if (nextPoint.x < 0 || nextPoint.x >= map[0].count() || nextPoint.y < 0 || nextPoint.y >= map.size) {
                    continue
                }

                val nextChar = map.getOrNull(nextPoint.y)?.getOrNull(nextPoint.x)
                val alreadySeen = visited.contains(nextPoint)
                if (nextChar != null && nextChar != '#' && !alreadySeen) {
                    add(nextPoint)
                }
            }
        }

        fun findDistanceToEnd(
            edges: Map<Point, List<Edge>>,
            endPoint: Point,
            visited: Set<Point> = emptySet(),
            currentDistance: Int = 0
        ): Int {
            if (this == endPoint) return currentDistance
            val nextVisited = visited + this
            return edges[this]!!.maxOf { (_, b, weight) ->
                if (visited.contains(b)) 0 else b.findDistanceToEnd(
                    edges,
                    endPoint,
                    nextVisited,
                    currentDistance + weight
                )
            }
        }
    }

    private data class Edge(val a: Point, val b: Point, val weight: Int) {
        companion object {
            fun fromPoint(
                map: List<String>, point: Point, pointsOfInterest: Set<Point>, ignoreHills: Boolean = false
            ): List<Edge> {
                return point.getNeighbours(map, emptySet(), ignoreHills).mapNotNull { start ->
                    val visited = mutableSetOf(point)
                    var count = 1
                    var currentPoint = start
                    while (true) {
                        if (pointsOfInterest.contains(currentPoint)) break

                        val nextPoint = currentPoint.getNeighbours(map, visited, ignoreHills).firstOrNull()
                            ?: return@mapNotNull null

                        count++
                        visited.add(currentPoint)
                        currentPoint = nextPoint
                    }
                    Edge(point, currentPoint, count)
                }
            }
        }
    }

    private val map = javaClass.getResource("/day23.txt")!!.readText().lines()

    private val startPoint = Point(map.first().indexOf('.'), 0)
    private val endPoint = Point(map.last().indexOf('.'), map.size - 1)

    private val pointsOfInterest = (1..<(map.size - 1)).flatMap { y ->
        (1..<(map[0].count() - 1)).mapNotNull { x ->
            val point = Point(x, y)
            if (map[y][x] == '#') return@mapNotNull null
            val neighbours = Direction.entries.count { direction ->
                val (x2, y2) = point + direction.offset
                map[y2][x2] != '#'
            }
            if (neighbours < 3) null else point
        }
    }.toSet().plus(listOf(startPoint, endPoint))

    override fun part1(): String {
        val edges = pointsOfInterest.associateWith { Edge.fromPoint(map, it, pointsOfInterest) }
        return startPoint.findDistanceToEnd(edges, endPoint).toString()
    }

    override fun part2(): String {
        val edges = pointsOfInterest.associateWith { Edge.fromPoint(map, it, pointsOfInterest, true) }
        return startPoint.findDistanceToEnd(edges, endPoint).toString()
    }
}