package days

class Day01 : Day {
    private fun calculateSum(file: String): UInt {
        return file.lines().sumOf { line ->
            val digits = line.filter { char -> char.isDigit() }
            (digits.first().toString() + digits.last()).toUInt()
        }
    }

    override fun part1(): String {
        val file = javaClass.getResource("/day01.1.txt")!!.readText()
        return calculateSum(file).toString()
    }

    override fun part2(): String {
        val digitMap = mapOf(
            "one" to "o1e",
            "two" to "t2o",
            "three" to "t3e",
            "four" to "4",
            "five" to "5e",
            "six" to "6",
            "seven" to "7n",
            "eight" to "e8t",
            "nine" to "n9e"
        )

        val file = javaClass.getResource("/day01.2.txt")!!.readText()
        val fileWithDigits = digitMap.entries.fold(file) { acc, entry -> acc.replace(entry.key, entry.value) }
        return calculateSum(fileWithDigits).toString()
    }
}