package domain.model

import CurrencyCode

sealed class CurrencyType(
    val code: CurrencyCode
) {
    data class Source(val currencyCode: CurrencyCode) : CurrencyType(currencyCode)
    data class Target(val currencyCode: CurrencyCode) : CurrencyType(currencyCode)
    data class None(val currencyCode: CurrencyCode) : CurrencyType(currencyCode)
}