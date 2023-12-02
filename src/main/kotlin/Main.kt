import kotlin.system.exitProcess

fun printUsage() {
    println("Usage: aoc-2023-kotlin {day} {part}")
}

fun main(args: Array<String>) {
    val dayObject = when (val day = args.getOrNull(0)?.toUIntOrNull()) {
        1u -> days.Day01()
        2u -> days.Day02()
        3u -> days.Day03()
        4u -> days.Day04()
        5u -> days.Day05()
        6u -> days.Day06()
        7u -> days.Day07()
        8u -> days.Day08()
        9u -> days.Day09()
        10u -> days.Day10()
        11u -> days.Day11()
        12u -> days.Day12()
        13u -> days.Day13()
        14u -> days.Day14()
        15u -> days.Day15()
        16u -> days.Day16()
        17u -> days.Day17()
        18u -> days.Day18()
        19u -> days.Day19()
        20u -> days.Day20()
        21u -> days.Day21()
        22u -> days.Day22()
        23u -> days.Day23()
        24u -> days.Day24()
        25u -> days.Day25()
        else -> {
            println("Unrecognised day: $day")
            printUsage()
            exitProcess(1)
        }
    }

    val result = when (val part = args.getOrNull(1)?.toUIntOrNull()) {
        1u -> dayObject.part1()
        2u -> dayObject.part2()
        else -> {
            println("Unrecognised part: $part")
            printUsage()
            exitProcess(1)
        }
    }

    println(result)
}