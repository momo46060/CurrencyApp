package domain.model

import androidx.compose.ui.graphics.Color
import primaryLight

enum class RateStatus (
    val title:String,
    val color: Color
){
    Idle("Idle", Color.White),
    Fresh("Fresh", primaryLight ),
    State("State",Color(0xFFFFA500)),
}