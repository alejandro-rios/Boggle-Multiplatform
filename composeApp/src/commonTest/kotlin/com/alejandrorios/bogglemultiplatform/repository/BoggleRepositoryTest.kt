package com.alejandrorios.bogglemultiplatform.repository

import com.alejandrorios.bogglemultiplatform.data.repository.BoggleRepositoryImpl
import com.alejandrorios.bogglemultiplatform.data.service.BoggleService
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse
import com.alejandrorios.bogglemultiplatform.utils.mockedDefinitions
import com.alejandrorios.bogglemultiplatform.utils.mockedResults
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BoggleRepositoryTest {

    private val service = mock<BoggleService> {
        everySuspend { fetchWordsFromAPI(any()) } returns CallResponse.Success(mockedResults)
        everySuspend { getDefinition("test") } returns CallResponse.Success(mockedDefinitions)
    }

    private lateinit var repository: BoggleRepositoryImpl

    @BeforeTest
    fun setUp() {
        repository = BoggleRepositoryImpl(service)
    }

    @Test
    fun given_repository_when_fetchWordsFromAPI_is_called_then_should_get_a_List_String() = runTest {
        val result = repository.fetchWordsFromAPI(listOf("a", "b")).single()

        assertEquals(CallResponse.Success(mockedResults), result)

        verifySuspend {
            service.fetchWordsFromAPI(listOf("a", "b"))
        }
    }

    @Test
    fun given_repository_when_getDefinition_is_called_then_should_get_a_List_DictionaryResponse() = runTest {
        val result = repository.getDefinition("test").single()

        assertEquals(CallResponse.Success(mockedDefinitions), result)

        verifySuspend {
            service.getDefinition("test")
        }
    }
}
