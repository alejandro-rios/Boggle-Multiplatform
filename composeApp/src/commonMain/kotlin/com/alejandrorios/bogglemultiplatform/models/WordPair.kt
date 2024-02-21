package com.alejandrorios.bogglemultiplatform.models

import kotlinx.serialization.Serializable

@Serializable
data class WordPair(
    val wordsTotal: Int,
    val wordsFound: List<String>
)
