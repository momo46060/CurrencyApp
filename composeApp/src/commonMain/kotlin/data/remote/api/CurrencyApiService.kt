package data.remote.api

import CurrencyCode
import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.ApiResponse
import domain.model.Currency
import domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl(
    private val preferance: PreferencesRepository
) : CurrencyApiService {
    companion object{
        const val END_POINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_PMDiLn338OyjahR15yBFMyTG41cu75RmbOsQo5Tg"

    }
    private val httpClient = HttpClient() {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation){
            json(Json{
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout){
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest){
            headers{
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLastestExchangeRates(): RequestState<List<Currency>> {
       return try {
            val response = httpClient.get(END_POINT)
            if (response.status.value in 200..299){
                println(response.body<String>())
                    val apiResponse = Json.decodeFromString<ApiResponse>(response.body())
                    val avalibleCurrenciesCodes = apiResponse.data.keys
                        .filter {
                            CurrencyCode.entries
                                .map {code -> code.name}
                                .toSet()
                                .contains(it)
                        }
                val avalibleCurrencies = apiResponse.data.values
                    .filter { currency ->
                        avalibleCurrenciesCodes.contains(currency.code)
                    }
                    val lastUpdate = apiResponse.meta.lastUpdatedAt
                    preferance.saveLastUpdated(lastUpdate)
                RequestState.Success(data = avalibleCurrencies)
            }else{
                println(response.status.value)
                RequestState.Error(message = "Error ${response.status.value}")
            }

        }catch (e:Exception){
           println(e.message.toString() )
            RequestState.Error(message = e.message.toString())
        }
    }

}


