package com.example.qrscan.view

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ProgressDialog.show
import androidx.camera.core.ImageProxy

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.health.connect.datatypes.units.Length
import android.net.Uri


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentScanBinding
import com.example.qrscan.util.ScanSetting
import com.example.qrscan.viewmodel.ScanViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ScanFragment : BaseFragment<FragmentScanBinding>(){
    private var camera : Camera? = null
    private val viewModel : ScanViewModel by activityViewModels()
    var isHandled = false
    private var beepEnabled = true
    private var vibrateEnabled = true
    private var autoScanEnabled = true



    private var torchOn = false
    private  val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_QR_CODE).build())
    private var cameraSelector : CameraSelector= CameraSelector.DEFAULT_BACK_CAMERA
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScanBinding {
        return FragmentScanBinding.inflate(layoutInflater,container,false)
    }
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        grandted -> if (grandted) startCamera() else Toast.makeText(requireContext(),getString(R.string.nopermission),
        Toast.LENGTH_SHORT)
    }
    private val pickImage =registerForActivityResult(ActivityResultContracts.GetContent()){
        uri -> if(uri!=null) scanQRFromGallery(uri)


    }

    override fun onResume() {
        super.onResume()
        isHandled = false
        Log.d("SCAN", "Reset isHandled")
        (activity as? MainActivity)?.showBottomNav(true)


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {

            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }
    override fun onPause() {
        super.onPause()

        stopCamera()
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenStarted {
            viewModel.autoScanEnabled.collect {
                autoScanEnabled = it
                isHandled = false
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.beepEnabled.collect {
                beepEnabled = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.vibrateEnabled.collect {
                vibrateEnabled = it
            }
        }

        setUpUiControler()
        startScanLineAnimation()

    }

    private fun scanQRFromGallery(uri : Uri){
        val image = InputImage.fromFilePath(requireContext(),uri)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (!isHandled) {
                    val barcode = barcodes.firstOrNull()
                    if (barcode != null && !barcode.rawValue.isNullOrBlank()) {
                        isHandled = true
                        onQrResult(barcode)
                        ScanSetting.play(
                            context = requireContext(),
                            beep = beepEnabled,
                            vibrate = vibrateEnabled
                        )

                    }
                }
            }

    }
    private fun setUpUiControler(){

        mBinding.plus.setOnClickListener {
            val newValue = (mBinding.slider.value + 0.2f).coerceAtMost(mBinding.slider.valueTo)
            mBinding.slider.value = newValue
            camera?.cameraControl?.setZoomRatio(newValue)

        }

        mBinding.minus.setOnClickListener {
            val newValue = (mBinding.slider.value - 0.2f).coerceAtLeast(mBinding.slider.valueFrom)
            mBinding.slider.value = newValue
            camera?.cameraControl?.setZoomRatio(newValue)

        }

        mBinding.slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                camera?.cameraControl?.setZoomRatio(value)
            }
        }

        mBinding.flash.setOnClickListener {
            val cam = camera ?: return@setOnClickListener
            val hasFlash =cam.cameraInfo.hasFlashUnit()
            if(!hasFlash){
                Toast.makeText(requireContext(),getString(R.string.notiflash), Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            torchOn=!torchOn
            cam.cameraControl.enableTorch(torchOn)


        }
        mBinding.rotateimage.setOnClickListener {
            cameraSelector = if(cameraSelector== CameraSelector.DEFAULT_BACK_CAMERA){
                CameraSelector.DEFAULT_FRONT_CAMERA
            }else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()

        }
        mBinding.choseimage.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        camera?.cameraControl?.enableTorch(false)
        stopCamera()
    }
    private fun stopCamera() {
        val context = context ?: return
        if (!isAdded) return

        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
                camera = null
                Log.d("SCAN", "Camera stopped")
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            Log.e("SCAN", "Error stopping camera: ${e.message}")
        }
    }


    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val context = context ?: return
        if (!isAdded) return
        Log.e("START", "startCamera() CALLED")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()


            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(mBinding.previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

                analyzer.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->


                    if (!autoScanEnabled || isHandled) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    Log.d("SCAN", "autoScan=$autoScanEnabled isHandled=$isHandled")


                    val media = imageProxy.image
                    if (media == null) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val image = InputImage.fromMediaImage(media, imageProxy.imageInfo.rotationDegrees)

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (!isHandled) {
                                val barcode = barcodes.firstOrNull()
                                if (barcode != null && !barcode.rawValue.isNullOrBlank()) {
                                    isHandled = true
                                    onQrResult(barcode)
                                }
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }



                camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                analyzer
            )


            val zoomState = camera!!.cameraInfo.zoomState.value!!
            mBinding.slider.valueFrom = zoomState.minZoomRatio
            mBinding.slider.valueTo = zoomState.maxZoomRatio
            mBinding.slider.value = zoomState.zoomRatio

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun onQrResult(barcode: Barcode) {

        val parsed = QRBarcodeParser.parseBarcode(barcode)


        val qrBitmap = GenerateQR.generateQR(parsed.content)

        viewModel.byteScanQR = qrBitmap.toByteArray()
        viewModel.content=parsed.content
        viewModel.setUserScan(parsed.data)

        viewModel.setCreateOption(parsed.type)
        ScanSetting.play(
            context = requireContext(),
            beep = beepEnabled,
            vibrate = vibrateEnabled
        )




        if (viewModel.isSave) {
            viewModel.saveScanned(
                id = 0,
                type = parsed.type,
                content = parsed.content,
                data = parsed.data
            )
            Log.d("SCAN", "Saved content = ${parsed.content}")
        } else {
            Log.d("SCAN", "History saving is disabled")
        }
            Log.d("SCAN", "Saved content = ${parsed.content}")

        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.currentItem = next.currentItem + 7
        next.isUserInputEnabled = false


    }


    private fun startScanLineAnimation() {
        val frame = mBinding.framecamera
        val line = mBinding.scanLine


        frame.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                frame.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val distance = frame.height - line.height

                ObjectAnimator.ofFloat(line, "translationY", 0f, distance.toFloat()).apply {
                    duration = 1500L
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = LinearInterpolator()
                    start()
                }
            }
        })
    }
    fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }




}