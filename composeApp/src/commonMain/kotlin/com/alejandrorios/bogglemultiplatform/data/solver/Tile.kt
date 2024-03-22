package com.alejandrorios.bogglemultiplatform.data.solver

import kotlin.math.sqrt

/**
 * Represents one Tile on a Boggle board.
 * There is no board class.  Use the properties in Tile to create a graph to represent the board.
 */
class Tile(val character: Char) {

    var up: Tile? = null
    var upright: Tile? = null
    var right: Tile? = null
    var downRight: Tile? = null
    var down: Tile? = null
    var downLeft: Tile? = null
    var left: Tile? = null
    var upLeft: Tile? = null


    fun neighbors(): Sequence<Tile> {
        return sequenceOf(up, upright, right, downRight, down, downLeft, left, upLeft)
                .filter { it != null }
                .map { it as Tile }
    }
}

/**
 * Create a Tile for each Char in the input, and link them together logically as a board
 */
fun buildBoard(input: Array<Array<Char>>): Set<Tile> {
    val tileMap = mutableMapOf<Pair<Int, Int>, Tile>()

    //TODO: verbose (but fairly fast and functional)
    input.forEachIndexed { y, xAxis ->
        xAxis.forEachIndexed { x, element ->
            val tile = Tile(element)
            tileMap[x to y] = tile

            if(tileMap.containsKey(x-1 to y)){
                val foreigner = tileMap[x-1 to y]!!
                tile.left = foreigner
                foreigner.right = tile
            }

            if(tileMap.containsKey(x-1 to y-1)){
                val foreigner = tileMap[x-1 to y-1]!!
                tile.upLeft = foreigner
                foreigner.downRight = tile
            }

            if(tileMap.containsKey(x to y-1)){
                val foreigner = tileMap[x to y-1]!!
                tile.up = foreigner
                foreigner.down = tile
            }

            if(tileMap.containsKey(x+1 to y-1)){
                val foreigner = tileMap[x+1 to y-1]!!
                tile.upright = foreigner
                foreigner.downLeft = tile
            }
        }
    }

    return tileMap.values.toSet()
}

/**
 * Parse a String into a square board.
 * Characters are placed from left to right, top to bottom.
 */
fun parseSquareBoard(input: String): Set<Tile> {
    val dimension = sqrt(input.length.toDouble()).toInt()
    if (dimension * dimension != input.length) {
        throw Exception("Not a square board.")
    }

    val arrayArray = input.asSequence()
            .batch(dimension)
            .map { it.toTypedArray() }
            .toList().toTypedArray()

    return buildBoard(arrayArray)
}
