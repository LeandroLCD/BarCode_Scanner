package com.leandrolcd.barcode_scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.leandrolcd.barcode_scanner.ui.camera.CameraXScreen
import com.leandrolcd.barcode_scanner.ui.theme.BarCode_ScannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarCode_ScannerTheme {
                CameraXScreen()

            }
        }
    }
}

