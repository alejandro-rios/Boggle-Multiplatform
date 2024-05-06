package com.alejandrorios.bogglemultiplatform.data.repository

import com.alejandrorios.bogglemultiplatform.domain.repository.LocalRepository
import com.alejandrorios.bogglemultiplatform.domain.utils.dispatchers.AppCoroutineDispatchers
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LocalRepositoryImpl(
    private val boggleStore: KStore<BoggleUiState>,
    private val appCoroutineDispatchers: AppCoroutineDispatchers
) : LocalRepository {
    override suspend fun getBoggleUiState(): Flow<BoggleUiState?> = flow {
        emit(boggleStore.get())
    }.flowOn(appCoroutineDispatchers.io)

    override suspend fun saveBoggleUiState(uiState: BoggleUiState) = withContext(appCoroutineDispatchers.io) {
        boggleStore.set(uiState)
    }

    override suspend fun clearData() = withContext(appCoroutineDispatchers.io) {
        boggleStore.delete()
    }
}
