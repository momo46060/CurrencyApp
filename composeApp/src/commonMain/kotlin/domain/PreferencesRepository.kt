package domain

import CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimestamp: Long): Boolean
    suspend fun saveSourceCurrency(code: String)
    suspend fun saveTargetCurrency(code: String)
    fun readSourceCurrency(): Flow<CurrencyCode>
    fun readTargetCurrency(): Flow<CurrencyCode>
}