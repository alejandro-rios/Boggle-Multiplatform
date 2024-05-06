package com.alejandrorios.bogglemultiplatform.data

import com.alejandrorios.bogglemultiplatform.data.solver.asString
import com.alejandrorios.bogglemultiplatform.data.solver.generateBoardFromString
import com.alejandrorios.bogglemultiplatform.data.solver.solver_one.buildTrie
import com.alejandrorios.bogglemultiplatform.data.solver.solver_one.parseSquareBoard
import com.alejandrorios.bogglemultiplatform.data.solver.solver_one.solveBoard
import com.alejandrorios.bogglemultiplatform.data.solver.solver_two.Combinations
import com.alejandrorios.bogglemultiplatform.data.solver.solver_two.createPrefixTree
import com.alejandrorios.bogglemultiplatform.utils.Boards4x4
import com.alejandrorios.bogglemultiplatform.utils.OpenForMokkery
import kotlin.random.Random

@OpenForMokkery
enum class Language(val filePath: String) {
    EN("files/en_dictionary.txt"),
    SPA("files/es_dictionary.txt")
}

@OpenForMokkery
class BoardGenerator {

    var language: Language = Language.EN

    fun generateBoard(): ArrayList<String> {
        val dice = (1..16).shuffled()
        val boardCombination = Boards4x4.random()
        val board = MutableList(16) { "" }

        for (i in dice) {
            val index = i - 1
            val ranVal = Random.nextInt(0, 5)

            val combination = boardCombination[index].substring(ranVal, ranVal + 1)

            if (language == Language.EN) {
                board[index] = combination
            } else {
                board[index] = combination.replace("Q", "Qu")
            }
        }

        return ArrayList(board)
    }

    fun getBoardSolutionOne(board: ArrayList<String>, dictionary: List<String>): List<String> {
        val trie = buildTrie(dictionary.asSequence())
        val s = board.asString()
        val tiles = parseSquareBoard(s)

        return solveBoard(tiles, trie)
    }

    fun getBoardSolutionTwo(board: List<String>, dictionary: List<String>): List<String> {
        val newBoard = board.asString().generateBoardFromString()

        val prefixTree = createPrefixTree(dictionary)
        val combinationInstance = Combinations(newBoard, prefixTree)
        val allCombinations = combinationInstance.allSearches()
        val validCombinations = mutableListOf<String>()
        validCombinations.addAll(allCombinations.filter { prefixTree.contains(it) })

        return validCombinations
    }
}
