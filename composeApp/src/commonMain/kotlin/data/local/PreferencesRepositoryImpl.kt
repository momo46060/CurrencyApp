package data.local

import CurrencyCode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesRepositoryImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object{
        const val TIMESTAMP_KEY = "last_updated"
        const val SOURCE_CURRENCY_KEY = "source_currency"
        const val TARGET_CURRENCY_KEY = "target_currency"
        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.EUR.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.EGP.name
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
            val savedDateTime = savedInstant.
            toLocalDateTime(TimeZone.currentSystemDefault())

            val dayDiff = currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear
            dayDiff <1
        }else false
    }

    override suspend fun saveSourceCurrency(code: String) {
        flowSettings.putString(
            key = SOURCE_CURRENCY_KEY,
            value = code
        )
    }

    override suspend fun saveTargetCurrency(code: String) {
        flowSettings.putString(
            key = TARGET_CURRENCY_KEY,
            value = code
        )
    }

    override fun readSourceCurrency(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).map { CurrencyCode.valueOf(it) }
    }

    override fun readTargetCurrency(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENCY_KEY,
            defaultValue = DEFAULT_TARGET_CURRENCY
        ).map { CurrencyCode.valueOf(it) }
    }
}