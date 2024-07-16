package presentation.component

import CurrencyCode
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.Currency
import domain.model.CurrencyType


@Composable
fun CurrencyPickerDialog(
    currencies: List<Currency>,
    currencyType: CurrencyType,
    onPositiveClick: (CurrencyCode) -> Unit,
    onDismiss: () -> Unit,
) {
    val allCurrencies = remember {
        mutableListOf<Currency>()
    }
    LaunchedEffect(Unit) {
        allCurrencies.addAll(currencies)
    }
    var searchQueryCode by remember { mutableStateOf("") }

    var selectedCurrency by remember(currencyType)
    { mutableStateOf(currencyType.code) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onPositiveClick(selectedCurrency) }) {
                Text(
                    text = "confirm",
                    color = Color(0xFF005142)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        title = {
            Text(
                text = "Select a currency",
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = searchQueryCode,
                    onValueChange = { query ->
                        searchQueryCode = query.uppercase()
                        if (query!="") {
                           val fillterCurrencies = allCurrencies.filter {
                               it.code.contains(query.uppercase())
                           }
                            allCurrencies.clear()
                            allCurrencies.addAll(fillterCurrencies)
                            println("fillterCurrencies $allCurrencies")
                        }else{
                            allCurrencies.clear()
                            allCurrencies.addAll(currencies)
                            println("dfljhdjfhvjdahivd $allCurrencies")

                        }
                    },
                    placeholder = { Text(text = "Search here",
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    label = { Text(text = "Search") },
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(99.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(targetState = allCurrencies) { avaliableCurrencies ->
                    println("unFilterCurrencies $allCurrencies")
                    if (avaliableCurrencies.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items =avaliableCurrencies /*, key = {
                                it.id.toHexString()
                            }*/){index->
                                CurrencyCodePickerView(
                                    code =  CurrencyCode.valueOf(index.code),
                                    isSelected = selectedCurrency.name == index.code,
                                    onSelect = {selectedCurrency = it},
                                )
                            }
                        }
                    }else{
                        ErrorScreen(modifier = Modifier.height(250.dp))
                    }

                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,

        )

}
@Composable
fun ErrorScreen(
    modifier: Modifier,
    message: String?=null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = message ?: "Unknown Error",
            textAlign = TextAlign.Center,
        )

    }


}