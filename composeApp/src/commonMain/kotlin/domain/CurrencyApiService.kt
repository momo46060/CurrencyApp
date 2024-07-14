package domain

import domain.model.Currency
import domain.model.RequestState

interface CurrencyApiService {

     suspend fun getLastestExchangeRates(): RequestState<List<Currency>>
}