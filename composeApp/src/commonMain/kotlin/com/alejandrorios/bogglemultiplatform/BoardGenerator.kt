package com.alejandrorios.bogglemultiplatform

import com.alejandrorios.bogglemultiplatform.solver.buildTrie
import com.alejandrorios.bogglemultiplatform.solver.parseSquareBoard
import com.alejandrorios.bogglemultiplatform.solver.solveBoard
import com.alejandrorios.bogglemultiplatform.utils.Boards4x4
import kotlin.random.Random

enum class Language { EN, ES}

data class BoardGenerator(private val language: Language) {
    fun generateBoard(): ArrayList<String> {
        val dice = (1..16).shuffled()
        val boardCombination = Boards4x4.random()
        val board = MutableList(16) { "" }

        for (i in dice) {
            val index = i - 1
            val ranVal = Random.nextInt(0, 5)

            val combination  = boardCombination[index].substring(ranVal, ranVal + 1)

            if (language == Language.EN) {
                board[index] = combination
            } else {
                board[index] = combination.replace("Q", "Qu")
            }
        }

        return ArrayList(board)
    }

    fun solveBoard(board: ArrayList<String>, dictionary: List<String>): List<String> {
        val trie = buildTrie(dictionary.asSequence())
        val s = board.joinToString("").lowercase()
        val tiles = parseSquareBoard(s)

        return solveBoard(tiles, trie)
            .sorted()
            .filter { it.length > 2 }
            .toList()
    }
}
