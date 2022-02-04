/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkywordApp()
        }
    }
}


