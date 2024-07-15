package domain

import domain.model.Currency
import domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    fun configureTheRealm()
    suspend fun insertCurrency(currency: Currency)
    fun readCurrency(): Flow<RequestState<List<Currency>>>
   suspend fun clearCurrency()
}