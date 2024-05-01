package com.alejandrorios.bogglemultiplatform.utils

import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.models.Meanings

val mockedResults = listOf("one", "two", "art")
val mockedLocalResults = listOf("three", "rat", "flat")
val mockedDefinitions = listOf(DictionaryResponse(word = "test", meanings = arrayListOf(Meanings())))
val mockedBoard = arrayListOf("A", "R", "T", "E")
val mockedBoardMap = mapOf(0 to "A", 1 to "R", 2 to "T", 3 to "E")
