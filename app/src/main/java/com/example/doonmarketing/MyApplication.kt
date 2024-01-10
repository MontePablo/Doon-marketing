package com.example.doonmarketing

import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.kaamwaale.daos.FirebaseDao

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MyPreferences.init(applicationContext)
        FirebaseDao

    }
}