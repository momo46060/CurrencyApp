package org.mo.currencyapp

import android.app.Application
import di.initialKoin
import org.koin.android.ext.koin.androidContext

class MyApplictaon :Application() {
    override fun onCreate() {
        super.onCreate()
        initialKoin{
            androidContext(this@MyApplictaon)


        }

    }



}