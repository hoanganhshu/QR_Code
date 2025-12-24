package com.example.qrscan.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterGenerate
import com.example.qrscan.adapter.AdapterHistory
import com.example.qrscan.adapter.AdapterHistoryInMonth
import com.example.qrscan.adapter.Callbach
import com.example.qrscan.adapter.Callback
import com.example.qrscan.adapter.generateMonth
import com.example.qrscan.database.data.DataGenerateInMonth
import com.example.qrscan.database.data.HistoryScan
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentHistoryBinding
import com.example.qrscan.viewmodel.ScanViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(), Callbach{
    private val viewModel : ScanViewModel by activityViewModels()
    private var isSelectMode = false


    private var selectedIds = listOf<Int>()


    private lateinit var adapter: AdapterHistory
    private lateinit var childAdapter : AdapterHistoryInMonth
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false

        (activity as? MainActivity)?.showBottomNav(true)
        adapter = AdapterHistory(this@HistoryFragment)
        childAdapter= AdapterHistoryInMonth(this@HistoryFragment)
        mBinding.scannow.setOnClickListener {
            next.currentItem=4
        }
        mBinding.generate.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)


            next.currentItem = 8
        }

        mBinding.itemDayHistory.layoutManager =
            LinearLayoutManager(requireContext())

        mBinding.itemDayHistory.adapter = adapter
        mBinding.root.setOnClickListener { clickedView ->
            if (isSelectMode) {
                val recyclerView = mBinding.itemDayHistory
                val bottomSelect = requireActivity().findViewById<View>(R.id.bottomSelect)


                if (!isViewInsideView(clickedView, recyclerView) &&
                    !isViewInsideView(clickedView, bottomSelect)) {

                    childAdapter.clearSelectionMode()
                    bottomSelect.visibility=View.GONE
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getAllScan().collect { list ->

                if (list.isEmpty()) {
                    mBinding.scannow.visibility = View.VISIBLE
                    mBinding.tvscannow.visibility = View.VISIBLE
                    mBinding.subtitle.visibility = View.VISIBLE
                    mBinding.itemDayHistory.visibility = View.GONE
                    return@collect
                }

                mBinding.scannow.visibility = View.GONE
                mBinding.tvscannow.visibility = View.GONE
                mBinding.subtitle.visibility = View.GONE
                mBinding.itemDayHistory.visibility = View.VISIBLE

                val monthFormat = SimpleDateFormat("MMM yyyy", Locale.US)

                val grouped: List<HistoryScan> =
                    list.groupBy { entity ->
                        monthFormat.format(Date(entity.createdAt))
                    }.map { (_, items) ->
                        HistoryScan(
                            createAt = items.first().createdAt,
                            item = items
                        )
                    }

                adapter.submitData(grouped)
            }
        }





    }
    private fun isViewInsideView(view: View, parentView: View): Boolean {
        var currentParent: ViewParent? = view.parent
        while (currentParent != null) {
            if (currentParent === parentView) {
                return true
            }
            currentParent = currentParent.parent
        }
        return false
    }

    private fun shareBitmapAsPng(bitmap: Bitmap) {
        val file = File(requireContext().cacheDir, "qr_shared.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share QR Code"))
    }

    override fun onDelete(item: QRCodeHistoryScanEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteScanById(item.id)
        }
    }

    override fun onShare(item: QRCodeHistoryScanEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val qr = viewModel.getScanById(item.id)
                if (qr != null && qr.content.isNotEmpty()) {
                    val bitmap = GenerateQR.generateQR(qr.content)
                    shareBitmapAsPng(bitmap)
                } else {

                    Log.e("HistoryFragment", "QR content is null or empty for id: ${item.id}")
                }
            } catch (e: Exception) {
                e.printStackTrace()

                Log.e("HistoryFragment", "Error sharing QR: ${e.message}")
            }
        }
    }

    override fun onSelectionMode(isLongPressed: Boolean) {
        isSelectMode = isLongPressed
        Log.d("RecyclerViewData", "Data id: ${isSelectMode}")

        (activity as? MainActivity)?.showBottomNav(!isSelectMode)
        val bottomSelect = requireActivity().findViewById<View>(R.id.bottomSelect)
        val deleteButton = bottomSelect?.findViewById<ImageView>(R.id.delete)
        val downloadButton = bottomSelect?.findViewById<ImageView>(R.id.download)
        if (isSelectMode){
            bottomSelect.visibility=View.VISIBLE


            deleteButton?.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.deleteListByid(selectedIds)
                    selectedIds = emptyList()
                    childAdapter.clearSelectionMode()
                    bottomSelect.visibility = View.GONE
                }
            }
            downloadButton?.setOnClickListener {

                if (selectedIds.isEmpty()) {

                    return@setOnClickListener
                }

                lifecycleScope.launch {

                    val qrCodes = selectedIds.mapNotNull { id ->
                        viewModel.getScanById(id)
                    }


                    val bitmaps = qrCodes.map { qr ->
                        GenerateQR.generateQR(qr.content)
                    }


                    shareMultipleBitmapsAsPng(bitmaps)


                    selectedIds = emptyList()
                    childAdapter.clearSelectionMode()
                    bottomSelect.visibility = View.GONE
            }}
        }
        else {
            bottomSelect.visibility=View.GONE
        }
    }
    private fun shareMultipleBitmapsAsPng(bitmaps: List<Bitmap>) {
        if (bitmaps.isEmpty()) return

        val uris = ArrayList<Uri>()
        val cacheDir = requireContext().cacheDir


        bitmaps.forEachIndexed { index, bitmap ->
            val file = File(cacheDir, "qr_shared_$index.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )
            uris.add(uri)
        }



        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/png"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share QR Codes"))
    }

    override fun onSelectedIdsChanged(ids: List<Int>) {
        selectedIds = ids

    }
}

