package com.alejandrorios.bogglemultiplatform.models

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryResponse(
    val word: String? = null,
    val meanings: ArrayList<Meanings> = arrayListOf(),
)

@Serializable
data class Meanings(
    val definitions: ArrayList<Definitions> = arrayListOf()
)

@Serializable
data class Definitions(val definition: String? = null)
