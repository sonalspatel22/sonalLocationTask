package com.appilary.radar

import android.app.Application
import io.realm.Realm

/**
 * Created by vi.garg on 10/2/18.
 */
class App : Application() {

    companion object {
        var isCameraIntent: Boolean = false
        lateinit var mInstance: App
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Realm.init(this)
    }
}