package com.vipro.servicetest.repository

import android.content.*
import android.content.pm.PackageManager
import android.os.PatternMatcher
import com.vipro.servicetest.core.MyService
import com.vipro.servicetest.log.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BroadcastRepository(
    private val context: Context,
    private val scope: CoroutineScope,
) {

    private var _stateFlow = MutableStateFlow(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)

    val stateFlow = _stateFlow.asStateFlow()
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_PACKAGE_CHANGED -> {
                    scope.launch {
                        logd { "onReceive emitting" }
                        val ctx = this@BroadcastRepository.context
                        val state = ctx.packageManager.getComponentEnabledSetting(
                            ComponentName(ctx.applicationContext, MyService::class.java)
                        )
                        _stateFlow.emit(state)
                    }
                }
            }
        }
    }

    fun registerReceiver() {
        val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_CHANGED)
        intentFilter.addDataScheme("package")
        intentFilter.addDataSchemeSpecificPart("com.vipro.servicetest", PatternMatcher.PATTERN_LITERAL)
        context.registerReceiver(mReceiver, intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(mReceiver)
    }
}