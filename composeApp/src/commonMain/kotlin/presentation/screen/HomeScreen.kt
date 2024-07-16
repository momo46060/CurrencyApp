package presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import domain.model.CurrencyType
import presentation.component.CurrencyPickerDialog
import presentation.component.HomeHeader
import surfaceColor

class HomeScreen : Screen {

    @Composable
    override fun Content() {
       val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateState.collectAsState()
        val allCurrencies by viewModel.currencyList.collectAsState()
        println("allCurrencies ${allCurrencies.size}")
        val source by viewModel.sourceCurrency.collectAsState()
        val target by viewModel.targetCurrency.collectAsState()
        var amount by remember { mutableStateOf(0) }

//        var selectedCurrencyType: CurrencyType by remember {


        var dialogOpened by remember {mutableStateOf(true)}

       val selectedCurrencyType by remember{
           mutableStateOf(CurrencyType.None)
       }
        if (dialogOpened){
            CurrencyPickerDialog(
                currencies = allCurrencies,
                currencyType = selectedCurrencyType,
                onDismiss = { dialogOpened = false },
                onPositiveClick = {dialogOpened = false}

            )

        }

        Column(
            modifier = Modifier.fillMaxSize()
                .background(surfaceColor),
        ) {
            HomeHeader(
                status = rateStatus,
                source = source,
                target = target,
                amount = amount,
                onAmountChange = {
                    amount = it.toInt()
            },
                onRefresh = {
                viewModel.sendEvent(
                    HomeUiEvent.Refresh,
                )
            },

                onSwitchClick ={
                    viewModel.sendEvent(
                        HomeUiEvent.SwitchCurrencies,
                    )

                })

        }
    }
}