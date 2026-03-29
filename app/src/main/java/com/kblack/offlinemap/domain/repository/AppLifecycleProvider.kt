package com.kblack.offlinemap.domain.repository

interface AppLifecycleProvider {
    var isAppInForeground: Boolean
}