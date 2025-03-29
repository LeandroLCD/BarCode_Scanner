package com.blipblipcode.scanner.ui.component

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@RequiresPermission(Manifest.permission.CAMERA)
@Composable
fun CameraXScreen(preview: PreviewView,
                  modifier: Modifier = Modifier,
                  facadeScreen: @Composable ()->Unit ={
                      ScannerFacade()
                  },
                  buttonContent: @Composable ()->Unit
                  ){

    Box(modifier.fillMaxSize()) {
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = {
                preview
            }
        )
        facadeScreen()
        Row(modifier.align(Alignment.BottomCenter)) {
            buttonContent.invoke()
        }

    }
}