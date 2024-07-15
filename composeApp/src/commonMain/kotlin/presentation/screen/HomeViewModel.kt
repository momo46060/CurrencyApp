package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import presentation.component.RatesStatus

sealed class HomeUiEvent {
    data object Refresh : HomeUiEvent()
}

class HomeViewModel(
    private val repository: PreferencesRepository,
    private val apiService: CurrencyApiService
) : ScreenModel {

    init {
        screenModelScope.launch(Dispatchers.IO) {
            fetchNewRates()
            getRateStatus()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.Refresh -> {
                screenModelScope.launch(Dispatchers.IO) {
                    fetchNewRates()

                }
            }
        }
    }

    private var _rateState: MutableState<RateStatus> =
        mutableStateOf(RateStatus.Idle)
    val rateState: State<RateStatus> = _rateState

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)

    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

  val _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)

    val targetCurrency: State<RequestState<Currency>> = _targetCurrency










    private suspend fun fetchNewRates() {
        try {
            apiService.getLastestExchangeRates()
            getRateStatus()

        } catch (e: Exception) {
            println(e.message.toString())
        }
    }

    private suspend fun getRateStatus() {

        _rateState.value = if (repository.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh else RateStatus.State
    }
}