package days

class Day15 : Day {
    private val parts = javaClass.getResource("/day15.txt")!!.readText().split(",")

    private fun hashString(input: String): Int {
        return input.fold(0) { acc, char ->
            ((acc + char.code) * 17) % 256
        }
    }

    override fun part1(): String {
        return parts.sumOf { hashString(it) }.toString()
    }

    override fun part2(): String {
        val boxes = (0..<256).map { LinkedHashMap<String, Int>() }
        for (instruction in parts) {
            val (a, b) = instruction.split('=', '-')
            val box = boxes[hashString(a)]
            if (b.isEmpty()) {
                box.remove(a)
            } else {
                box[a] = b.toInt()
            }
        }
        return boxes.withIndex().sumOf {(boxIndex, box) ->
            box.values.withIndex().sumOf { (slotIndex, power) ->
                (1 + boxIndex) * (1 + slotIndex) * power
            }
        }.toString()
    }
}