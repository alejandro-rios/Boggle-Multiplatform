package com.alejandrorios.bogglemultiplatform.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryResponse(
    val word: String? = null,
    val meanings: ArrayList<Meanings> = arrayListOf(),
) {
    fun getWordAsHint(): String {
        var wordAsHint = word!![0].toString()

        for (i in 1 until word.length) {
            wordAsHint += " _"
        }

        return wordAsHint
    }
}

@Serializable
data class Meanings(
    val definitions: ArrayList<Definitions> = arrayListOf()
)

@Serializable
data class Definitions(val definition: String? = null)
