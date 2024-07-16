/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:51 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/16/24, 5:44 AM
 *
 */

package ee.erfan.resistor

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ee.erfan.resistor.ui.CameraScreen
import ee.erfan.resistor.ui.theme.ResistorScannerTheme
import org.opencv.android.OpenCVLoader
import org.opencv.android.OpenCVLoader.OPENCV_VERSION
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private var viewModel: MainViewModel? = null

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    run {
                        viewModel?.processImageProxy(imageProxy)
                    }
                }
            }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialize OpenCV
        if (!OpenCVLoader.initLocal()) {
            Toast.makeText(this, "Error Initializing OpenCv", Toast.LENGTH_SHORT).show()

            finish()
            return
        }
        Timber.tag("OpenCv").d("OpenCV Version: $OPENCV_VERSION")

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel?.getResistors()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewModel?.let { mainViewModel ->

            setContent {
                ResistorScannerTheme {

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val context = LocalContext.current
                        val permissionsState = rememberMultiplePermissionsState(
                            permissions = listOf(
                                Manifest.permission.CAMERA
                            )
                        )
                        val lifecycleOwner = LocalLifecycleOwner.current

                        DisposableEffect(
                            key1 = lifecycleOwner,
                            effect = {
                                val observer = LifecycleEventObserver { _, event ->
                                    if (event == Lifecycle.Event.ON_START) {
                                        permissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)

                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }
                        )
                        permissionsState.permissions.forEach { perm ->
                            when (perm.permission) {
                                Manifest.permission.CAMERA -> {
                                    val systemUiController = rememberSystemUiController()
                                    systemUiController.setSystemBarsColor(
                                        color = Color.Transparent
                                    )
                                    CameraScreen(
                                        onPreviewView = {
                                            startCamera(it)
                                        }, mainViewModel = mainViewModel,
                                        onScan = {
//                                            textReaderAnalyzer.startScan()
                                        },
                                        onFlash = {
                                            imageAnalyzer.camera?.let {
                                                if (it.cameraInfo.hasFlashUnit()) {
                                                    it.cameraControl.enableTorch(!mainViewModel.isFlashOn)
                                                    mainViewModel.isFlashOn =
                                                        !mainViewModel.isFlashOn
                                                }
                                            }
                                        }, permissionState = (when {
                                            perm.status.isGranted -> 0
                                            perm.status.shouldShowRationale -> 1
                                            !perm.status.isGranted -> 2
                                            else -> 1
                                        }),
                                        onRequestPermission = { permissionsState.launchMultiplePermissionRequest() })
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera(cameraPreview: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val preview = androidx.camera.core.Preview.Builder()
            .build()

        cameraProviderFuture.addListener(
            Runnable {
                preview.setSurfaceProvider(cameraPreview.surfaceProvider)
                cameraProviderFuture.get().bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            },
            ContextCompat.getMainExecutor(this)
        )

        cameraPreview.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                MotionEvent.ACTION_UP -> {
                    val factory =
                        cameraPreview.meteringPointFactory

                    val point = factory.createPoint(
                        motionEvent.x,
                        motionEvent.y
                    )

                    val action =
                        FocusMeteringAction.Builder(
                            point
                        ).build()

                    preview.camera?.cameraControl?.startFocusAndMetering(
                        action
                    )
                    view.performClick()
                    viewModel?.cameraFocusPoint = Offset(motionEvent.x, motionEvent.y)
                    return@setOnTouchListener true
                }

                else -> return@setOnTouchListener false
            }
        }


    }
}
