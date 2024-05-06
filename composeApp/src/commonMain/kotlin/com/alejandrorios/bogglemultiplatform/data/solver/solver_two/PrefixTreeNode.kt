package com.alejandrorios.bogglemultiplatform.data.solver.solver_two

/**
 * PrefixTreeNode: A node for use in a prefix tree that stores a single
 *     character from a string and a structure of children nodes below it, which
 *     associates the next character in a string to the next node along its path from
 *     the tree's root node to a terminal node that marks the end of the string.
 */
class PrefixTreeNode(val char: Char? = null) {
    val children = mutableListOf<PrefixTreeNode>()

    /**
     * Return True if this prefix tree node terminates a string.
     */
    var isTerminal: Boolean = false

    /**
     * Return the number of children nodes this prefix tree node has.
     */
    fun numChildren(): Int = children.size

    private fun indexOfChild(char: Char): Int? = children.indexOfFirst { it.char == char }

    /**
     * Return True if this prefix tree node has a child node that
     *         represents the given character amongst its children.
     */
    private fun hasChild(char: Char): Boolean = indexOfChild(char) != null

    /**
     * Return this prefix tree node's child node that represents the given
     *         character if it is amongst its children, or raise ValueError if not
     */
    fun getChild(char: Char): PrefixTreeNode {
        val index = indexOfChild(char) ?: throw IllegalArgumentException("No child exists for character '$char'")
        return children[index]
    }

    /**
     * Add the given character and child node as a child of this node, or
     *         raise ValueError if given character is amongst this node's children.
     */
    fun addChild(char: Char, childNode: PrefixTreeNode) {
        if (hasChild(char)) {
            throw IllegalArgumentException("Child exists for character '$char'")
        }
        children.add(childNode)
    }

    /**
     * Return a string view of this prefix tree node.
     */
    override fun toString(): String = "($char)"
}
