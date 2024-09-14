

package com.example.sacrenabymehuljadhav.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

/**
 * Base Activity for a logged in user in authorized zone.
 */
open class BaseConnectedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The application was killed in the background. Starting from the launcher activity.
        if (savedInstanceState != null && lastNonConfigurationInstance == null) {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
            finishAffinity()
        }
    }
}
