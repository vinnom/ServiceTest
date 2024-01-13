package com.vipro.servicetest.core

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.vipro.servicetest.log.logd
import com.vipro.servicetest.repository.BroadcastRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyService : Service() {

    private val mBinder = Binder()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job)
    private val repository by lazy { BroadcastRepository(this, scope) }

    private val obj = this

    override fun onCreate() {
        super.onCreate()
        logd(MyService::class.java.simpleName) { "$obj onCreate" }
    }


    override fun onBind(intent: Intent?): IBinder? {
        logd(MyService::class.java.simpleName) { "$obj onBind" }
        repository.registerReceiver()

        scope.launch {
            repository.stateFlow.collect { serviceState ->
                logd(MyService::class.java.simpleName) { "$obj onBind serviceState=$serviceState" }
            }
        }

        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        logd(MyService::class.java.simpleName) { "$obj onUnbind" }
        scope.launch {
            repository.stateFlow.collect { serviceState ->
                logd(MyService::class.java.simpleName) { "$obj onUnbind serviceState=$serviceState" }
            }
        }
        repository.unregisterReceiver()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        logd(MyService::class.java.simpleName) { "$obj onDestroy" }
        job.complete()
        super.onDestroy()
    }
}