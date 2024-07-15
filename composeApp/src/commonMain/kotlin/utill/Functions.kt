package utill

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun disaplayCurrentDateTime(): String {
    val currentTImeStamp = Clock.System.now()
    val date = currentTImeStamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val dayMonth = date.dayOfMonth
    val month = date.month.toString().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    val year = date.year

    val suffix = when{
        dayMonth in 11..13 ->"th"
        dayMonth %10 == 1 -> "st"
        dayMonth %10 == 2 -> "nd"
        dayMonth %10 == 2 -> "rd"
        else -> "th"
    }

    return "$dayMonth$suffix $month, $year"
}
