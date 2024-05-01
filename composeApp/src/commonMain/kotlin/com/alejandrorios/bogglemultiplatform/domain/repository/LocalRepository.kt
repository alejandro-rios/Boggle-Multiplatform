package com.alejandrorios.bogglemultiplatform.domain.repository

import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun getBoggleUiState(): Flow<BoggleUiState?>

    suspend fun saveBoggleUiState(uiState: BoggleUiState): Flow<Unit>

    suspend fun clearData(): Flow<Unit>
}
