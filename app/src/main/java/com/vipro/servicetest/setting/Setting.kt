package com.vipro.servicetest.setting

data class Setting(
    val enabled: Boolean = false,
    val setting: String = "state_for_testing=false"
) {
    companion object {
        const val settingName = "state_for_testing"
    }
}