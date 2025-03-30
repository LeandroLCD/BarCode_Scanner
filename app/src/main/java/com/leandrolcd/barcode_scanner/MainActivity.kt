package com.leandrolcd.barcode_scanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.blipblipcode.scanner.ui.utilities.ACTION_BARCODE_SCAN
import com.blipblipcode.scanner.ui.utilities.EXTRA_BARCODE
import com.blipblipcode.scanner.ui.utilities.EXTRA_BARCODE_TYPE
import com.blipblipcode.scanner.ui.utilities.EXTRA_ERROR
import com.leandrolcd.barcode_scanner.ui.theme.BarCode_ScannerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val scanActivity = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { onResult->
                if (onResult.resultCode == RESULT_OK) {
                    onResult.data?.also {
                        val barcode = it.getStringExtra(EXTRA_BARCODE)
                        val barcodeType = it.getStringExtra(EXTRA_BARCODE_TYPE)
                        Log.d("scanActivity", "scan barcode: $barcode, $barcodeType")
                    }
                }else{
                    Log.d("scanActivity","scan barcode: ${onResult.data?.getStringExtra(EXTRA_ERROR)}")
                }

            }
            BarCode_ScannerTheme {
                Scaffold {
                    LaunchedEffect(Unit) {
                        println(packageName)
                    }
                    Box(Modifier.padding(it).fillMaxSize(), contentAlignment = Alignment.Center){
                        Button(onClick ={
                            scope.launch {
                                val sendIntent = Intent(ACTION_BARCODE_SCAN)
                                scanActivity.launch(sendIntent)
                            }
                        } ) {
                            Text("SCAN")
                        }
                    }
                }

            }
        }
    }
}

