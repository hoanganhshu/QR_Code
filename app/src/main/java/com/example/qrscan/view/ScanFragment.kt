package com.example.qrscan.view


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentScanBinding
import com.example.qrscan.util.ScanSetting
import com.example.qrscan.viewmodel.ScanViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream


class ScanFragment : BaseFragment<FragmentScanBinding>(){
    private var camera : Camera? = null
    private val viewModel : ScanViewModel by activityViewModels()
    var isHandled = false
    private var beepEnabled = true
    private var vibrateEnabled = true
    private var autoScanEnabled = true
    private var cameraProvider: ProcessCameraProvider? = null
    private var analyzer: ImageAnalysis? = null
    private var preview: Preview? = null






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
        Log.e("LIFE", "onResume | isAdded=$isAdded view=$view")
        isHandled = false
        val ctx = requireContext()
        mBinding.resultImage.visibility = View.GONE
        mBinding.previewView.visibility = View.VISIBLE

        beepEnabled = ScanSettingPrefs.getBoolean(ctx, "beep", false)
        vibrateEnabled = ScanSettingPrefs.getBoolean(ctx, "vibrate", false)
        autoScanEnabled = ScanSettingPrefs.getBoolean(ctx, "auto_scan", true)

        viewModel.isSave =
            ScanSettingPrefs.getBoolean(ctx, "save_history", false)


        Log.d("SCAN", "Reset isHandled")
        (activity as? BottomNavController)?.requestBottomNav(true)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

    }
    override fun onPause() {
        super.onPause()
        Log.e("LIFE", "onPaused | isAdded=$isAdded view=$view")

        stopCamera()
        mBinding.resultImage.visibility=View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUiControler()
       

    }
    private fun scanQRFromGallery(uri : Uri){
        val image = InputImage.fromFilePath(requireContext(),uri)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    Toast.makeText(requireActivity(),getString(R.string.no_qr),Toast.LENGTH_SHORT).show()

                    return@addOnSuccessListener
                }
                val barcode = barcodes.firstOrNull()

                // ❌ QR không hợp lệ
                if (barcode == null || barcode.rawValue.isNullOrBlank()) {
                    Toast.makeText(requireActivity(),getString(R.string.no_qr),Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

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
                Toast.makeText(requireContext(),getString(R.string.notiflash), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                val isOn =cam.cameraInfo.torchState.value== TorchState.ON
                if(isOn){
                    Toast.makeText(requireActivity(),getString(R.string.on_flash), Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireActivity(),getString(R.string.off_flash), Toast.LENGTH_SHORT).show()
                }
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
        Log.e("LIFE", "onDestroy | isAdded=$isAdded view=$view")
        camera?.cameraControl?.enableTorch(false)
        stopCamera()
    }
    private fun stopCamera() {
        analyzer?.clearAnalyzer()
        cameraProvider?.unbindAll()
        camera = null
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val context = context ?: return

        if (!isAdded) return
        Log.e("START", "startCamera() CALLED")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)


        cameraProviderFuture.addListener({
             cameraProvider = cameraProviderFuture.get()
            cameraProvider?.unbindAll()


             preview = Preview.Builder().build().apply {
                setSurfaceProvider(mBinding.previewView.surfaceProvider)
            }

             analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()




            analyzer?.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->


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
                Log.e(
                    "SCAN_FLOW",
                    "QR DETECTED | before postDelayed | isAdded=$isAdded view=$view"
                )


                val image = InputImage.fromMediaImage(media, imageProxy.imageInfo.rotationDegrees)

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (!isHandled) {
                            val barcode = barcodes.firstOrNull()
                            if (barcode != null && !barcode.rawValue.isNullOrBlank()) {
                                isHandled = true

                                val bitmap= imageProxy.toBitmap().rotate(imageProxy.imageInfo.rotationDegrees)
                                val boxedBitmap =drawBoundingBox(bitmap,barcode.boundingBox!!)
                                showResultImage(boxedBitmap)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    onQrResult(barcode)
                                }, 400)
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }



                camera = cameraProvider?.bindToLifecycle(
                    this@ScanFragment,
                    cameraSelector,
                    preview,
                    analyzer
                )


            val zoomState = camera!!.cameraInfo.zoomState.value!!

            mBinding.slider.valueFrom = zoomState.minZoomRatio
            mBinding.slider.valueTo = zoomState.maxZoomRatio
            val initialZoom =
                zoomState.minZoomRatio +
                        0.3f * (zoomState.maxZoomRatio - zoomState.minZoomRatio)
            mBinding.slider.value = initialZoom
            camera!!.cameraControl.setZoomRatio(initialZoom)

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun onQrResult(barcode: Barcode) {

        val parsed = QRBarcodeParser.parseBarcode(barcode)


        val qrBitmap = GenerateQR.generateQR(parsed.content)

        viewModel.byteScanQR = qrBitmap.toByteArray()
        viewModel.content=parsed.content
        viewModel.setUserScan(parsed.data)
        viewModel.setScanOption(parsed.type)

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
        Log.e("NAV_DEBUG", "ScanFragment onQrResult → go HistoryScan (11)")
        (activity as? MainActivity)?.navigateMain(HistoryScanFragment())

        next.isUserInputEnabled = false


    }
    private fun showResultImage(bitmap: Bitmap) {
        mBinding.previewView.visibility = View.GONE
        mBinding.resultImage.visibility = View.VISIBLE
        mBinding.resultImage.setImageBitmap(bitmap)

    }
    fun Bitmap.rotate(degrees : Int) : Bitmap {
        if(degrees==0) return this

        val matrix = Matrix().apply {
            postRotate(degrees.toFloat())
        }
        return Bitmap.createBitmap(this,0,0,width,height,matrix,true)
    }




    fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    fun ImageProxy.toBitmap() : Bitmap{
        val yBuffer=planes[0].buffer
        val uBuffer=planes[1].buffer
        val vBuffer=planes[2].buffer

        val ySize=yBuffer.remaining()
        val uSize=uBuffer.remaining()
        val vSize=vBuffer.remaining()

        val nv21= ByteArray(ySize+uSize+vSize)
        yBuffer.get(nv21,0,ySize)
        uBuffer.get(nv21,ySize,vSize)
        vBuffer.get(nv21,ySize+vSize,uSize)

        val yuvImage = android.graphics.YuvImage(nv21,android.graphics.ImageFormat.NV21,width,height,null)

        val out= ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0,width,height),100,out)

        val imageBytes=out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)


    }
    fun drawBoundingBox(
        bitmap: Bitmap,
        rect: Rect
    ): Bitmap {
        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)

        val paint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }

        canvas.drawRect(rect, paint)
        return mutable
    }





}