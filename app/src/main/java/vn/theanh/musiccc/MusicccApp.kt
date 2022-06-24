package vn.theanh.musiccc

import android.app.Application
import android.content.Context

class MusicccApp : Application() {
    private lateinit var context: Context
    fun getContext(): Context {
        return context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}