package days

class Day24 : Day {
    private data class Point(val x: Long, val y: Long, val z: Long) {
        companion object {
            private val numberRegex = Regex("""-?\d+""")

            fun fromString(string: String): Point {
                val (x, y, z) = numberRegex.findAll(string).map { it.value.toLong() }.toList()
                return Point(x, y, z)
            }
        }

        operator fun plus(rhs: Point): Point {
            return Point(x + rhs.x, y + rhs.y, z + rhs.z)
        }
    }

    private data class Hailstone(val position: Point, val velocity: Point) {
        val m: Double = velocity.y / velocity.x.toDouble()
        val c: Double = position.y - (m * position.x)

        companion object {
            fun fromString(string: String): Hailstone {
                val (position, velocity) = string.split(" @ ").map { Point.fromString(it) }
                return Hailstone(position, velocity)
            }
        }

        fun findIntersection(min: Long, max: Long, other: Hailstone): Pair<Double, Double>? {
            val x = (other.c - c) / (m - other.m)
            val y = m * x + c
            val intersection = Pair(x, y)
            val isValid = isIntersectionValid(min, max, intersection)
            val isOtherValid = other.isIntersectionValid(min, max, intersection)

            return if (isValid && isOtherValid) {
                intersection
            } else {
                null
            }
        }

        fun isIntersectionValid(min: Long, max: Long, intersection: Pair<Double, Double>): Boolean {
            val (x, y) = intersection

            if (x < min || y < min || x > max || y > max) return false
            if (velocity.x < 0 && x > position.x) return false
            if (velocity.x > 0 && x < position.x) return false
            if (velocity.y < 0 && y > position.y) return false
            if (velocity.y > 0 && y < position.y) return false

            return true
        }
    }

    private val min: Long
    private val max: Long
    private val hailstones: List<Hailstone>

    init {
        val lines = javaClass.getResource("/day24.txt")!!.readText().lines()

        val (min, max) = lines[0].split(", ").map { it.toLong() }
        this.min = min
        this.max = max
        hailstones = lines.drop(1).map { Hailstone.fromString(it) }
    }

    override fun part1(): String {
        return hailstones.dropLast(1).withIndex().sumOf { (index, hailstone) ->
            hailstones.drop(1 + index).count { other ->
                hailstone.findIntersection(min, max, other) != null
            }
        }.toString()
    }

    override fun part2(): String {
        // just creates a sagemath script that prints the solution...

        val lines = mutableListOf("var('t0', 't1', 't2', 'x', 'y', 'z', 'vx', 'vy', 'vz')", "solution = solve([")

        for ((i, hailstone) in hailstones.take(3).withIndex()) {
            val (x, y, z) = hailstone.position
            val (vx, vy, vz) = hailstone.velocity
            lines.add("  $x + ($vx) * t$i == x + vx * t$i,")
            lines.add("  $y + ($vy) * t$i == y + vy * t$i,")
            lines.add("  $z + ($vz) * t$i == z + vz * t$i,")
        }

        lines.add("], x, vx, y, vy, z, vz, t0, t1, t2, solution_dict=true)[0]")
        lines.add("print(solution[x] + solution[y] + solution[z])")
        return lines.joinToString("\n")
    }
}