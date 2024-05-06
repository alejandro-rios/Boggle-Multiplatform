package com.alejandrorios.bogglemultiplatform.data.solver.solver_two

/**
 * PrefixTree: A multi-way prefix tree that stores strings with efficient
methods to insert a string into the tree, check if it contains a matching
string, and retrieve all strings that start with a given prefix string.
Time complexity of these methods depends only on the number of strings
retrieved and their maximum length (size and height of subtree searched),
but is independent of the number of strings stored in the prefix tree, as
its height depends only on the length of the longest string stored in it.
This makes a prefix tree effective for spell-checking and autocompletion.
Each string is stored as a sequence of characters along a path from the
tree's root node to a terminal node that marks the end of the string.
 */
class PrefixTree {
    val root = PrefixTreeNode(null)
    var size = 0

    /**
     * Return True if this prefix tree is empty (contains no strings).
     */
    fun isEmpty() = size == 0

    /**
     * Return True if this prefix tree contains the given string.
     */
    fun contains(string: String): Boolean {
        var currentNode = root
        for (char in string) {
            if (!currentNode.children.any { it.char == char }) {
                return false
            }
            currentNode = currentNode.children.first { it.char == char }
        }
        return currentNode.isTerminal
    }

    /**
     * Insert the given string into this prefix tree.
     */
    fun insert(string: String) {
        var currentNode = root
        for (char in string) {
            val childNode = currentNode.children.firstOrNull { it.char == char }
            if (childNode == null) {
                val newNode = PrefixTreeNode(char)
                currentNode.children.add(newNode)
                currentNode = newNode
            } else {
                currentNode = childNode
            }
        }
        if (!currentNode.isTerminal) {
            currentNode.isTerminal = true
            size++
        }
    }

    /**
     * Return a tuple containing the node that terminates the given string
     *         in this prefix tree and the node's depth, or if the given string is not
     *         completely found, return None and the depth of the last matching node.
     *         Search is done iteratively with a loop starting from the root node.
     */
    fun findNode(string: String): Pair<PrefixTreeNode?, Int> {
        if (string.isEmpty()) return Pair(root, 0)
        var currentNode = root
        var depth = 0
        for (char in string) {
            val childNode = currentNode.children.firstOrNull { it.char == char } ?: return Pair(null, depth)
            currentNode = childNode
            depth++
        }
        return Pair(currentNode, depth)
    }

    /**
     * Return a list of all strings stored in this prefix tree that start with the given prefix string.
     */
    fun complete(prefix: String): List<String> {
        val completions = mutableListOf<String>()
        val (node, _) = findNode(prefix)
        node?.let {
            if (it.isTerminal) completions.add(prefix)
            traverse(it, prefix, completions::add)
        }
        return completions
    }

    /**
     * Return a list of all strings stored in this prefix tree.
     */
    fun strings(): List<String> {
        val allStrings = mutableListOf<String>()
        traverse(root, "", allStrings::add)
        return allStrings
    }

    /**
     * Traverse this prefix tree with recursive depth-first traversal.
     * Start at the given node and visit each node with the given function.
     */
    fun traverse(node: PrefixTreeNode, prefix: String, visit: (String) -> Unit) {
        for (child in node.children) {
            val newPrefix = prefix + child.char
            if (child.isTerminal) visit(newPrefix)
            traverse(child, newPrefix, visit)
        }
    }
}

fun createPrefixTree(strings: List<String>): PrefixTree {
    val tree = PrefixTree()
//    println("\ntree: $tree")
//    println("root: ${tree.root}")
//    println("strings: ${tree.strings()}")

//    println("\nInserting strings:")
    for (string in strings) {
        tree.insert(string)
//        println("insert($string), size: ${tree.size}")
    }

//    println("\ntree: $tree")
//    println("root: ${tree.root}")
//
//    println("\nSearching for strings in tree:")
//    for (string in strings.toSet().sorted()) {
//        val result = tree.contains(string)
//        println("contains($string): $result")
//    }

//    println("\nSearching for strings not in tree:")
//    val prefixes = strings.map { it.take(it.length / 2) }.toSet().sorted().filter { it !in strings && it.isNotEmpty() }
//    for (prefix in prefixes) {
//        val result = tree.contains(prefix)
//        println("contains($prefix): $result")
//    }

//    println("\nCompleting prefixes in tree:")
//    for (prefix in prefixes) {
//        val completions = tree.complete(prefix)
//        println("complete($prefix): $completions")
//    }

//    println("\nTraversing tree:")
//    tree.traverse(tree.root, "") { println(it) }

//    println("\nFinding prefixes in tree:")
//    println("Note: below should all be Nodes")
//    for (prefix in prefixes) {
//        val findings = tree.findNode(prefix)
//        println("_find_node($prefix): $findings")
//    }
//    for (string in strings.toSet().sorted()) {
//        val findings = tree.findNode(string)
//        println("_find_node($string): $findings")
//    }
//    println("Note: below should all be None")
//    println("_find_node(\"Shells\"): ${tree.findNode("Shells")}")

//    println("\nRetrieving all strings:")
//    val retrievedStrings = tree.strings()
//    println("strings: $retrievedStrings")
//    val matches = retrievedStrings.toSet() == strings.toSet()
//    println("matches? $matches")
    return tree
}

