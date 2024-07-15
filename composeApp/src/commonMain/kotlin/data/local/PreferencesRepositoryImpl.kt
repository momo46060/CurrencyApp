package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesRepositoryImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object{
        const val TIMESTAMP_KEY = "last_updated"
    }
    private val  flowSettings:FlowSettings = (settings as ObservableSettings).toFlowSettings()
    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key=TIMESTAMP_KEY,
            value= Instant.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    override suspend fun isDataFresh(currentTimestamp: Long): Boolean {
        val savedTimeStamp = flowSettings.getLong(
            key=TIMESTAMP_KEY,
            defaultValue = 0L
        )
        return if (savedTimeStamp !=0L){
            val currentInstant = Instant.fromEpochMilliseconds(currentTimestamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimeStamp)
            val currentDateTime = currentInstant
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val dayDiff = currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear
            dayDiff >1
        }else false
    }
}