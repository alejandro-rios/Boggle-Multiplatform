package com.alejandrorios.bogglemultiplatform.data.solver

fun String.generateBoardFromString(): List<List<Char>> {
    require(this.length == 16) { "Input string must have exactly 16 characters" }

    val board = mutableListOf<MutableList<Char>>()
    val charArray = this.toCharArray()

    for (i in 0 until 16 step 4) {
        val row = charArray.sliceArray(i until i + 4).toMutableList()
        board.add(row)
    }

    return board
}

fun ArrayList<String>.asString(): String = this.joinToString("").lowercase()

fun List<String>.asString(): String = this.joinToString("").lowercase()
