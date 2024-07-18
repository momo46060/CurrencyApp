package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.MongoRepository
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import presentation.component.RatesStatus

sealed class HomeUiEvent {
    data object Refresh : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
    data class SaveSourceCurrency(val code: String) : HomeUiEvent()
    data class SaveTargetCurrency(val code: String) : HomeUiEvent()
}

class HomeViewModel(
    private val repository: PreferencesRepository,
    private val apiService: CurrencyApiService,
    private val mongoDB: MongoRepository
) : ScreenModel {

    init {
        screenModelScope.launch(Dispatchers.IO) {
            fetchNewRates()
            getRateStatus()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.Refresh -> {
                screenModelScope.launch(Dispatchers.IO) {
                    fetchNewRates()

                }
            }
            is HomeUiEvent.SwitchCurrencies -> {
                screenModelScope.launch(Dispatchers.IO) {
                   switchCurrencies()
                }
            }

            is HomeUiEvent.SaveSourceCurrency -> {
                saveSourceCurrencyCode(event.code)
            }
          is  HomeUiEvent.SaveTargetCurrency -> {
                saveTargetCurrencyCode(event.code)
            }
        }
    }

    private fun saveSourceCurrencyCode(code:String){
        screenModelScope.launch(Dispatchers.IO) {
            repository.saveSourceCurrency(code)
        }

    }
   private fun saveTargetCurrencyCode(code:String){
        screenModelScope.launch(Dispatchers.IO) {
            repository.saveTargetCurrency(code)
        }

    }



    private fun switchCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }

    private var _rateState: MutableStateFlow<RateStatus> =
        MutableStateFlow(RateStatus.Idle)
    val rateState: StateFlow<RateStatus> = _rateState

    private var _sourceCurrency: MutableStateFlow<RequestState<Currency>> =
        MutableStateFlow(RequestState.Idle)

    val sourceCurrency: StateFlow<RequestState<Currency>> = _sourceCurrency

    var _targetCurrency: MutableStateFlow<RequestState<Currency>> =
        MutableStateFlow(RequestState.Idle)

    val targetCurrency: StateFlow<RequestState<Currency>> = _targetCurrency

    private val _currencyList: MutableStateFlow<MutableList<Currency>> =
        MutableStateFlow(mutableListOf())

    val currencyList: StateFlow<List<Currency>> = _currencyList

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.IO) {
            repository.readTargetCurrency().collectLatest {currencyCode->
                val selectedCurrency = _currencyList.value.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(selectedCurrency)
                } else {
                    _targetCurrency.value = RequestState.Error("Currency not found")
                }

            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.IO) {
            repository.readSourceCurrency().collectLatest {currencyCode->
                val selectedCurrency = _currencyList.value.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(selectedCurrency)
                } else {
                    _sourceCurrency.value = RequestState.Error("Currency not found")
                }

            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoDB.readCurrency().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("Local Cache is not empty")
                    _currencyList.value.addAll(localCache.getSuccessData())
                    if (!repository.isDataFresh(
                        Clock.System.now().toEpochMilliseconds()
                    )){
                        println("HomeViewModel: Data is not fresh")
                        cacheTheData()

                    }else{
                        println("HomeViewModel: Data is fresh")
                        _currencyList.value.addAll(localCache.getSuccessData())
                    }
                } else {
                    println("Local Cache is empty")
                    cacheTheData()

                }

            } else if (localCache.isError()){
                println("HomeViewModel: local cache error ${localCache.getErrorMessage()}")

            }
            apiService.getLastestExchangeRates()
            getRateStatus()

        } catch (e: Exception) {
            println(e.message.toString())
        }
    }

    suspend fun cacheTheData() {
        val fetchedDate= apiService.getLastestExchangeRates()
        if (fetchedDate.isSuccess()){
            mongoDB.clearCurrency()
            fetchedDate.getSuccessData().forEach {
                println("HomeViewModel: ${it.code}")
                mongoDB.insertCurrency(it)
            }
            println("HomeViewModel: Updated _allCurrency")
            _currencyList.value.addAll(fetchedDate.getSuccessData())
        }else if (fetchedDate.isError()){
            println("HomeViewModel: fetching error ${fetchedDate.getErrorMessage()}")
        }
    }

    private suspend fun getRateStatus() {
        _rateState.value = if (repository.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.State
    }
}