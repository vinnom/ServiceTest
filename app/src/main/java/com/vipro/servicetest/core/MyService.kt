package com.vipro.servicetest.core

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
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


    override fun onBind(intent: Intent?): IBinder? {
        logd(MyService::class.java.simpleName) { "onBind" }
        repository.registerReceiver()

        scope.launch {
            repository.stateFlow.collect { serviceState ->
                if (serviceState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
                    logd(MyService::class.java.simpleName) { "component disabled" }
                else
                    logd(MyService::class.java.simpleName) { "component enabled" }
            }
        }

        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        logd(MyService::class.java.simpleName) { "onUnbind" }
        repository.unregisterReceiver()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        logd(MyService::class.java.simpleName) { "onDestroy" }
        job.complete()
        super.onDestroy()
    }
}