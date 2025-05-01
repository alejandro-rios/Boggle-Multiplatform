package com.alejandrorios.bogglemultiplatform.domain.utils

import com.alejandrorios.bogglemultiplatform.data.Language
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

interface DictionaryProvider {
    suspend fun getWordsFromDictionary(language: Language): List<String>
}

class ResourceDictionaryProvider : DictionaryProvider {
    @OptIn(InternalResourceApi::class)
    override suspend fun getWordsFromDictionary(language: Language): List<String> =
        readResourceBytes(language.filePath).decodeToString().split("\r?\n|\r".toRegex()).toList()
}
