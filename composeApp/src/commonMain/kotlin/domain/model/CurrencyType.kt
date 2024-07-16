package domain.model

import CurrencyCode

sealed class CurrencyType(
    val code: CurrencyCode=CurrencyCode.EGP
) {
    data class Source(val currencyCode: CurrencyCode) : CurrencyType(currencyCode)
    data class Target(val currencyCode: CurrencyCode) : CurrencyType(currencyCode)
   data object None   : CurrencyType(CurrencyCode.EGP)
}