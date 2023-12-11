package days

class Day03 : Day {
    private data class Point(val x: Int, val y: Int)

    private class PartNumber(private val startPosition: Point, val number: Int) {
        override fun toString(): String {
            return "PartNumber(startPosition: $startPosition, number: $number)"
        }
    }

    private val lines = javaClass.getResource("/day03.txt")!!.readText().lines()
    private val numberMatcher = Regex("""\d+""")

    override fun part1(): String {
        val positions = mutableMapOf<Point, PartNumber>()
        val symbols = mutableListOf<Point>()
        val symbolMatcher = Regex("""[^\d.]""")
        lines.withIndex().forEach { lineWithIndex ->
            val y = lineWithIndex.index
            val line = lineWithIndex.value
            numberMatcher.findAll(line).forEach { match ->
                val partNumber = PartNumber(Point(match.range.first, y), match.value.toInt())
                match.range.forEach { x -> positions[Point(x, y)] = partNumber }
            }
            symbolMatcher.findAll(line).forEach { match -> symbols.add(Point(match.range.first, y)) }
        }
        val validParts = hashSetOf<PartNumber>()
        symbols.forEach { position ->
            for (y in -1..1) {
                for (x in -1..1) {
                    val part = positions[Point(position.x + x, position.y + y)]
                    if (part != null) {
                        validParts.add(part)
                    }
                }
            }
        }
        return validParts.sumOf { partNumber -> partNumber.number }.toString()
    }

    override fun part2(): String {
        val positions = mutableMapOf<Point, PartNumber>()
        val possibleGears = mutableListOf<Point>()
        val gearMatcher = Regex("""\*""")
        lines.withIndex().forEach { lineWithIndex ->
            val y = lineWithIndex.index
            val line = lineWithIndex.value
            numberMatcher.findAll(line).forEach { match ->
                val partNumber = PartNumber(Point(match.range.first, y), match.value.toInt())
                match.range.forEach { x -> positions[Point(x, y)] = partNumber }
            }
            gearMatcher.findAll(line).forEach { match -> possibleGears.add(Point(match.range.first, y)) }
        }
        return possibleGears.sumOf { position ->
            val validParts = hashSetOf<PartNumber>()
            for (y in -1..1) {
                for (x in -1..1) {
                    val part = positions[Point(position.x + x, position.y + y)]
                    if (part != null) {
                        validParts.add(part)
                    }
                }
            }
            if (validParts.count() == 2) {
                validParts.fold(1L) { acc, part -> acc * part.number }
            } else {
                0
            }
        }.toString()
    }
}