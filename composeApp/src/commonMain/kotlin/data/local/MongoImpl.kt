package data.local

import domain.MongoRepository
import domain.model.Currency
import domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.delete
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoImpl : MongoRepository {
    init {
        configureTheRealm()
    }
    private var realm: Realm? = null
    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.
            Builder(schema = setOf(Currency::class))
                .compactOnLaunch().build()
            realm = Realm.open(config)

        }
    }

    override suspend fun insertCurrency(currency: Currency) {
        realm?.write { copyToRealm(currency) }
    }

    override fun readCurrency(): Flow<RequestState<List<Currency>>> {
        return realm?.query<Currency>()
            ?.asFlow()
            ?.map{result ->
                RequestState.Success(data = result.list)
            }
            ?: flow {RequestState.Error(message = "Realm is null")  }
    }

    override suspend fun clearCurrency() {
        realm?.write {
            val currency=this.query<Currency>()
            delete(currency)
        }
    }
}