package com.vipro.servicetest.core

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.vipro.servicetest.R
import com.vipro.servicetest.log.logd
import com.vipro.servicetest.setting.Setting

class MainActivity : AppCompatActivity() {

    private var setting = Setting()

    private val switchLayout by lazy { findViewById<ImageView>(R.id.switch_layout) }
    private val switch by lazy { findViewById<SwitchMaterial>(R.id.switch_button) }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logd { "onServiceConnected" }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logd { "onServiceDisconnected" }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logd(MainActivity::class.java.simpleName) { "onCreate" }
        logd { "creating $setting" }
    }

    override fun onResume() {
        super.onResume()
        switch.setOnCheckedChangeListener { _, isChecked ->
            setting = Setting(isChecked, "${Setting.settingName}=$isChecked")
            val componentName = ComponentName(applicationContext, MyService::class.java)

            val serviceState =
                if (isChecked) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                else PackageManager.COMPONENT_ENABLED_STATE_DISABLED

            if (isChecked) {
                changeServiceState(componentName, serviceState)
                bind()
            } else {
                unbind()
                changeServiceState(componentName, serviceState)
            }

        }
    }

    private fun changeServiceState(componentName: ComponentName, enableService: Int) {
        val flag = PackageManager.DONT_KILL_APP
        packageManager.setComponentEnabledSetting(componentName, enableService, flag)
    }

    private fun unbind() {
        unbindService(connection)
        logd { "unbinding $setting" }
    }

    private fun bind() {
        bindService(
            Intent(this.applicationContext, MyService::class.java),
            connection,
            BIND_AUTO_CREATE or BIND_NOT_PERCEPTIBLE
        )
        switchLayout.setBackgroundColor(Color.parseColor("#eeeeee"))
        logd { "binding $setting" }
    }

    override fun onDestroy() {
        logd(MainActivity::class.java.simpleName) { "onDestroy" }
        super.onDestroy()
    }
}