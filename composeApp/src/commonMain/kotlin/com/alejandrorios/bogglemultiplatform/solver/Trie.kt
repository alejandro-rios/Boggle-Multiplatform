package com.alejandrorios.bogglemultiplatform.solver

/**
 * Simple Trie class.  Logic to build the Trie lives outside of the class, but in this file.
 */
//TODO: Optimization (minor): Use array instead of map
class Trie {

    private val children = mutableMapOf<Char, Trie>()
    var fullString: String? = null

    fun next(c: Char): Trie? {
        return children[c]
    }

    fun setNext(c: Char, trie: Trie) {
        children[c] = trie
    }

}

/**
 * Transform a sequence of Strings into a Trie
 */
fun buildTrie(input: Sequence<String>): Trie {
    val root = Trie()

    input.forEach { addSequenceToTrie(it.iterator(), it, root) }

    return root
}

/**
 * Adds a single sequence of Chars to a Trie.
 * If the Trie doesn't exist, create it.
 */
fun addSequenceToTrie(seq: Iterator<Char>,
                      fullString: String,
                      maybeNode: Trie?): Trie {

    val node = maybeNode ?: Trie()

    if (!seq.iterator().hasNext()) {
        node.fullString = fullString
    } else {
        val nextC = seq.next()
        val nextNode = node.next(nextC)
        node.setNext(nextC, addSequenceToTrie(seq, fullString, nextNode))
    }

    return node
}
