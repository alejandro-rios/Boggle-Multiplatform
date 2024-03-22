package com.alejandrorios.bogglemultiplatform.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WordPair(
    val wordsTotal: Int,
    val wordsFound: List<String>
)
