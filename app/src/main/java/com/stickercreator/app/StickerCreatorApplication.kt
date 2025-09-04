package com.stickercreator.app

import android.app.Application
import com.stickercreator.app.utils.FeatureFlags
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StickerCreatorApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize feature flags
        FeatureFlags.initialize(this)
    }
}
