package days

import kotlin.math.max
import kotlin.math.min

class Day22 : Day {
    private data class Point(val x: Int, val y: Int, var z: Int) {
        companion object {
            fun fromString(string: String): Point {
                val (x, y, z) = string.split(',').map { it.toInt() }
                return Point(x, y, z)
            }
        }
    }

    private data class Brick(val p1: Point, val p2: Point) {
        companion object {
            fun fromString(string: String): Brick {
                val (a, b) = string.split('~').map { Point.fromString(it) }
                return Brick(a, b)
            }
        }

        private fun getRange(a: Int, b: Int): IntRange {
            return min(a, b)..max(a, b)
        }

        fun getXRange() = getRange(p1.x, p2.x)
        fun getYRange() = getRange(p1.y, p2.y)
        fun getZRange() = getRange(p1.z, p2.z)

        fun canDrop(filledSpace: Set<Point>): Boolean {
            val z = getZRange().first() - 1
            if (z < 1) return false
            for (y in getYRange()) {
                for (x in getXRange()) {
                    if (filledSpace.contains(Point(x, y, z))) return false
                }
            }
            return true
        }

        fun dropInMap(filledSpace: MutableSet<Point>) {
            val zRange = getZRange()
            for (y in getYRange()) {
                for (x in getXRange()) {
                    filledSpace.remove(Point(x, y, zRange.last))
                    filledSpace.add(Point(x, y, zRange.first - 1))
                }
            }
            p1.z--
            p2.z--
        }

        fun disintegrate(filledSpace: MutableSet<Point>) {
            for (z in getZRange()) {
                for (y in getYRange()) {
                    for (x in getXRange()) {
                        filledSpace.remove(Point(x, y, z))
                    }
                }
            }
        }
    }

    private val bricks = javaClass.getResource("/day22.txt")!!.readText().lines().map { Brick.fromString(it) }
        .sortedBy { min(it.p1.z, it.p2.z) }

    private fun dropBricks(filledSpace: MutableSet<Point>, bricks: List<Brick>): Int {
        var droppedCount = 0
        for (brick in bricks) {
            if (brick.canDrop(filledSpace)) {
                droppedCount++
                brick.dropInMap(filledSpace)
            }
        }
        return droppedCount
    }

    private fun getSettledSpace(): MutableSet<Point> {
        val filledSpace = mutableSetOf<Point>()
        for (brick in bricks) {
            for (z in brick.getZRange()) {
                for (y in brick.getYRange()) {
                    for (x in brick.getXRange()) {
                        filledSpace.add(Point(x, y, z))
                    }
                }
            }
        }

        var somethingDropped: Boolean
        do {
            somethingDropped = dropBricks(filledSpace, bricks) > 0
        } while(somethingDropped)

        return filledSpace
    }

    override fun part1(): String {
        val filledSpace = getSettledSpace()

        return bricks.count { brick ->
            val filledSpaceCopy = mutableSetOf<Point>()
            filledSpaceCopy.addAll(filledSpace)
            brick.disintegrate(filledSpaceCopy)
            dropBricks(filledSpaceCopy, bricks.filterNot { it == brick }.map{Brick(it.p1.copy(), it.p2.copy())}) == 0
        }.toString()
    }

    override fun part2(): String {
        val filledSpace = getSettledSpace()

        return bricks.sumOf { brick ->
            val filledSpaceCopy = mutableSetOf<Point>()
            filledSpaceCopy.addAll(filledSpace)
            brick.disintegrate(filledSpaceCopy)
            dropBricks(filledSpaceCopy, bricks.filterNot { it == brick }.map{Brick(it.p1.copy(), it.p2.copy())})
        }.toString()
    }
}