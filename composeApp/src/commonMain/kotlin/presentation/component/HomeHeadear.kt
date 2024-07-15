package presentation.component

import CurrencyCode
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.compose_multiplatform
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import headerColor
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import utill.disaplayCurrentDateTime

@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Int,
    onAmountChange: (Double) -> Unit,
    onRefresh: () -> Unit,
    onSwitchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RatesStatus(
            status = status,
            onRefresh = onRefresh
        )
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClick = onSwitchClick
        )
        Spacer(modifier = Modifier.height( 24.dp))
        AmountInput(
            amount = amount,
            onAmountChange = onAmountChange)
    }


}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RatesStatus(
    status: RateStatus,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.compose_multiplatform), contentDescription = null
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Text(
                    text = disaplayCurrentDateTime(),
                    color = Color.White
                )
                Text(
                    status.title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = if (status == RateStatus.State)Color(0xffFFA500) else Color.Green,)
            }
        }

        if (status == RateStatus.State) {
            IconButton(onClick = onRefresh) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color(0xffFFA500)
                )
            }
        }
    }

}
@Composable
fun RowScope.CurrencyView(
    placeHolder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeHolder,
            color = Color.White,
            fontSize = MaterialTheme.typography.bodySmall.fontSize
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .height(54.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (currency.isSuccess()){
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = CurrencyCode.valueOf(currency.getSuccessData().code).name,
                    color = Color.White,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Bold)

            }else if (currency.isLoading()){
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(modifier=Modifier.size(20.dp))

            }
        }
    }
}


@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit
) {
    var animatedStarted by remember {mutableStateOf(false)}
    val animatedRotation by animateFloatAsState(
        targetValue = if (animatedStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        CurrencyView(
            placeHolder = "from",
            currency = source,
            onClick = {}
        )
        IconButton(
            modifier = Modifier.padding(top = 24.dp)
                .graphicsLayer {  rotationY = animatedRotation },
            onClick = {
                animatedStarted = !animatedStarted
                onSwitchClick()
            }
        ){
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xffFFA500),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        CurrencyView(
            placeHolder = "target",
            currency = target,
            onClick = {}
        )
    }
}

@Composable
fun AmountInput(
    amount: Int,
    onAmountChange: (Double) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .animateContentSize()
            .height(54.dp),
        value = "$amount",
        onValueChange = {t->
            onAmountChange(t.toDoubleOrNull()?:0.0)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.White.copy(alpha = 0.05f),
            errorContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )

    )


}

