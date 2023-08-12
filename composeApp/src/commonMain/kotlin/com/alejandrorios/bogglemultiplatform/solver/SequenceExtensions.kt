package com.alejandrorios.bogglemultiplatform.solver

object SequenceExtensions

inline fun <T, R : Comparable<R>> Sequence<T>.sortedByDirection(
        descending: Boolean,
        crossinline selector: (T) -> R?
): Sequence<T> {
    val comparator = if (descending) {
        compareByDescending(selector)
    } else {
        compareBy(selector)
    }

    return sortedWith(comparator)
}

fun <T> Sequence<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this, n)
}

fun <T> Sequence<T>.batchWhile(batchSize: Int, predicate: (List<T>) -> Boolean): Sequence<List<T>> {
    return this.batch(batchSize)
            .takeWhile(predicate = predicate)
}

private class BatchingSequence<T>(val source: Sequence<T>, val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()
        override fun computeNext() {
            if (iterate.hasNext()) setNext(iterate.asSequence().take(batchSize).toList())
            else done()
        }
    }
}
