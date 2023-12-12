package days

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class Day12Test {

    @Test
    fun part1() {
        assertEquals("21", Day12().part1())
    }

    @Test
    fun part2() {
        assertEquals("525152", Day12().part2())
    }
}