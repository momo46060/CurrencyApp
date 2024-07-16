package presentation.component

import CurrencyCode
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun CurrencyCodePickerView(
    code:CurrencyCode,
    isSelected:Boolean,
    onSelect:(CurrencyCode)->Unit,
) {
    val saturation = remember{
        Animatable(if(isSelected) 1f else 0f)
    }
    LaunchedEffect(key1 = isSelected){
        saturation.animateTo(if(isSelected) 1f else 0f)
    }

        val colorMatrix = remember(saturation.value){
            ColorMatrix().apply {
                setToSaturation(saturation.value)
            }
        }
    val animatedAlpha by animateFloatAsState(
        targetValue = if(isSelected) 1f else 0.5f,
        animationSpec = tween(300)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect(code) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = code.name,
            color = Color.White,
            modifier = Modifier.alpha(animatedAlpha),
            fontWeight = FontWeight.Bold
        )

        CurrencyCodeSelector(isSelect =  isSelected)
    }
}

@Composable
private fun CurrencyCodeSelector(isSelect:Boolean = false) {
    val animatedColor by animateColorAsState(
        targetValue = if(isSelect) Color(0xFF0D6B58) else Color(0xFF86D6BF),
        animationSpec = tween(300)
    )
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(animatedColor),
        contentAlignment = Alignment.Center
    ){
        if (isSelect){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF0F1513)
            )
        }

    }

}
