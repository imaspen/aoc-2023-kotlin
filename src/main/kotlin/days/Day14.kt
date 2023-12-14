package days

class Day14 : Day {
    private val lines = javaClass.getResource("/day14.txt")!!.readText().lines()

    override fun part1(): String {
        val groupFinder = Regex("""[^#]+""")

        val columns = buildList {
            val builders = lines[0].map { StringBuilder() }
            for (line in lines) {
                for (j in 0..<line.count()) {
                    builders[j].append(line[j])
                }
            }
            for (builder in builders) {
                add(builder.reverse().toString())
            }
        }

        return columns.sumOf { column ->
            groupFinder.findAll(column).sumOf { group ->
                val roundCount = group.value.count { it == 'O' }
                (roundCount * (3 + ((2 * group.range.last) - roundCount))) / 2
            }
        }.toString()
    }

    override fun part2(): String {
        val map = lines.map { it.toMutableList() }.toMutableList()

        fun tiltNorth() {
            for (x in 0..<map[0].count()) {
                var lastOpenSpace = 0
                for (y in 0..<map.count()) {
                    when (map[y][x]) {
                        '#' -> lastOpenSpace = y + 1
                        'O' -> {
                            map[y][x] = '.'
                            map[lastOpenSpace][x] = 'O'
                            lastOpenSpace++
                        }
                    }
                }
            }
        }

        fun tiltWest() {
            for (y in 0..<map.count()) {
                var lastOpenSpace = 0
                for (x in 0..<map[0].count()) {
                    when (map[y][x]) {
                        '#' -> lastOpenSpace = x + 1
                        'O' -> {
                            map[y][x] = '.'
                            map[y][lastOpenSpace] = 'O'
                            lastOpenSpace++
                        }
                    }
                }
            }
        }

        fun tiltSouth() {
            for (x in 0..<map[0].count()) {
                var lastOpenSpace = map.count() - 1
                for (y in lastOpenSpace downTo 0) {
                    when (map[y][x]) {
                        '#' -> lastOpenSpace = y - 1
                        'O' -> {
                            map[y][x] = '.'
                            map[lastOpenSpace][x] = 'O'
                            lastOpenSpace--
                        }
                    }
                }
            }
        }

        fun tiltEast() {
            for (y in 0..<map.count()) {
                var lastOpenSpace = map[0].count() - 1
                for (x in lastOpenSpace downTo 0) {
                    when (map[y][x]) {
                        '#' -> lastOpenSpace = x - 1
                        'O' -> {
                            map[y][x] = '.'
                            map[y][lastOpenSpace] = 'O'
                            lastOpenSpace--
                        }
                    }
                }
            }
        }

        fun cycle() {
            tiltNorth()
            tiltWest()
            tiltSouth()
            tiltEast()
        }

        val cache = mutableMapOf<String, Int>()
        for (i in 1..1_000_000_000) {
            cycle()
            val mapString = map.joinToString("\n") { it.joinToString("") }
            val lastSeen = cache[mapString]
            if (lastSeen != null) {
                val period = i - lastSeen
                val offset = (1_000_000_000 - i) % period
                for (j in 0..<offset) {
                    cycle()
                }
                break
            }
            cache[mapString] = i
        }

        return map.reversed().withIndex().sumOf { (index, line) ->
            line.sumOf { if (it == 'O') index + 1 else 0 }
        }.toString()
    }
}