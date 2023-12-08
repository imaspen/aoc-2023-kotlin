package days

class Day08 : Day {
    data class Point(val left: Int, val right: Int) {
        fun getNext(isLeft: Boolean): Int {
            return if (isLeft) left else right
        }
    }

    private fun codeToInt(string: String): Int {
        return string.fold(0) { acc, char -> acc.shl(5) + char.code - 'A'.code }
    }

    private fun parseFile(isPart2: Boolean = false): Pair<List<Boolean>, Map<Int, Point>> {
        val parts = javaClass.getResource(if (isPart2) "/day08.2.txt" else "/day08.1.txt")!!.readText().split("\n\n")
        val instructions = parts[0].map { it == 'L' }
        val definitions = parts[1].lines().associate { line ->
            val (from, to) = line.split(" = ")
            val sides = to.drop(1).dropLast(1).split(", ").map { codeToInt(it) }
            (codeToInt(from) to Point(sides[0], sides[1]))
        }
        return Pair(instructions, definitions)
    }


    override fun part1(): String {
        val (instructions, definitions) = parseFile()
        val destination = codeToInt("ZZZ")
        var position = 0
        var steps = 0
        var i = 0

        while (position != destination) {
            position = definitions[position]!!.getNext(instructions[i])
            steps++
            i++
            if (i == instructions.count()) {
                i = 0
            }
        }

        return steps.toString()
    }

    private fun gcd(aIn: Long, bIn: Long): Long {
        var t: Long
        var a = aIn
        var b = bIn
        while (b != 0L) {
            t = b
            b = a % b
            a = t
        }
        return a
    }

    private fun lcm(a: Long, b: Long): Long {
        return a * b / gcd(a, b)
    }

    override fun part2(): String {
        val (instructions, definitions) = parseFile(true)

        return definitions.keys.filter { (it and 0b11111) == 0 }.map { startPosition ->
            var position = startPosition
            var steps = 0
            var i = 0

            while (position and 0b11111 != 25) {
                position = definitions[position]!!.getNext(instructions[i])
                steps++
                i++
                if (i == instructions.count()) {
                    i = 0
                }
            }

            assert(definitions[position]!!.getNext(instructions[i]) == startPosition) {
                "assumed that destination always takes us back to the beginning, is true on my input"
            }

            return@map steps.toLong()
        }.reduce { a, b -> lcm(a, b) }.toString()
    }
}