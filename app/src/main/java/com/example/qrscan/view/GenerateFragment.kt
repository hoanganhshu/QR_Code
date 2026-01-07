package com.example.qrscan.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterGenerate
import com.example.qrscan.adapter.AdapterGenerateInMonth
import com.example.qrscan.adapter.Callback
import com.example.qrscan.adapter.generateMonth
import com.example.qrscan.database.data.DataGenerateInMonth
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentGenerateBinding
import com.example.qrscan.viewmodel.ScanViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GenerateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GenerateFragment : BaseFragment<FragmentGenerateBinding>(), Callback{
    private val viewModel : ScanViewModel by activityViewModels()

    private lateinit var listview: Flow<List<QRCodeEntity>>
    private lateinit var adapter: AdapterGenerate
    private var isSelectMode = false
    private var selectedIds = listOf<Int>()

    private lateinit var childAdapter: AdapterGenerateInMonth






    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGenerateBinding {
        return FragmentGenerateBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false
        super.onViewCreated(view, savedInstanceState)
        (activity as? BottomNavController)?.requestBottomNav(true)






        mBinding.createnow.setOnClickListener {

            (activity as? MainActivity)?.navigateMain(CreateOptionFragment())
        }
        mBinding.generate.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)


            (activity as? MainActivity)?.navigateMain(CreateOptionFragment())
        }
        mBinding.root.setOnClickListener { clickedView ->
            if (isSelectMode) {
                val recyclerView = mBinding.recyclerview
                val bottomSelect = requireActivity().findViewById<View>(R.id.bottomSelect)


                if (!isViewInsideView(clickedView, recyclerView) &&
                    !isViewInsideView(clickedView, bottomSelect)) {

                    clearAllChildAdaptersSelection()
                    bottomSelect.visibility = View.GONE


                    (activity as? BottomNavController)?.requestBottomNav(true)
                }
            }
        }
        (activity as? BottomNavController)?.requestBottomNav(true)
        listview=viewModel.getAll()
        lifecycleScope.launch {
            listview.collect {
                    list ->
                if(list.isEmpty()){
                    mBinding.createnow.visibility=View.VISIBLE
                    mBinding.createsubtitle.visibility=View.VISIBLE
                    mBinding.recyclerview.visibility=View.GONE

                }else{
                    mBinding.createnow.visibility=View.GONE
                    mBinding.createsubtitle.visibility=View.GONE
                    mBinding.recyclerview.visibility=View.VISIBLE

                    val appLocales = AppCompatDelegate.getApplicationLocales()
                    val locale = appLocales.get(0) ?: Locale("en", "US")

                    val monthFormat = SimpleDateFormat("MMM yyyy", locale)
                    val dateFormat = SimpleDateFormat("dd:MM,yyyy ", locale)



                    val grouped =list.groupBy { entity -> monthFormat.format(Date(entity.createdAt)) }.map { (monthYear, items) ->

                        generateMonth(

                            createAt = monthYear,
                            items = items.map { qr ->
                                DataGenerateInMonth(id = qr.id,
                                    image = when(qr.type){
                                        QRType.EMAIL ->{
                                            R.drawable.emailvector
                                        }
                                        QRType.CONTACT -> {
                                            R.drawable.contactsvector
                                        }
                                        QRType.EVENT -> {
                                            R.drawable.calendarvector
                                        }

                                        QRType.LOCATION->R.drawable.locationvector
                                        QRType.SMS -> R.drawable.smsvector
                                        QRType.WIFI -> R.drawable.wifivector
                                        QRType.TEXT -> R.drawable.textvector
                                        QRType.URL -> R.drawable.urlvector
                                        QRType.PHONE -> R.drawable.icon_phonevector
                                        else -> R.drawable.icon_phonevector
                                    },
                                    title = qr.type.name,
                                    subtitle = dateFormat.format(Date(qr.createdAt))

                                )
                            })

                }
                   childAdapter = AdapterGenerateInMonth(this@GenerateFragment )
                    adapter = AdapterGenerate(this@GenerateFragment)
                    mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
                    mBinding.recyclerview.adapter = adapter

                    adapter.submitData(grouped)



                }
        }




    }}
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
    private fun clearAllChildAdaptersSelection() {
        Log.d("GENERATE", "clearAllChildAdaptersSelection called")


        for (i in 0 until mBinding.recyclerview.childCount) {
            val childView = mBinding.recyclerview.getChildAt(i)


            val childRecyclerView = childView.findViewById<RecyclerView>(R.id.recyclerview)

            childRecyclerView?.adapter?.let { adapter ->
                if (adapter is AdapterGenerateInMonth) {
                    Log.d("GENERATE", "Found child adapter, clearing selection")
                    adapter.clearSelectionMode()
                }
            }
        }

        Log.d("GENERATE", "Finished clearing all adapters")
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


    override fun onEdit(item: DataGenerateInMonth) {
        viewModel.itemIdCreate=item.id
        Log.d("RecyclerViewData", "Data id: ${item.id},${item.title}")
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)


        (activity as? MainActivity)?.navigateMain(CreateDetailFragment())
    }

    override fun onDelete(item: DataGenerateInMonth) {
        lifecycleScope.launch {
            viewModel.deleteById(item.id)}
    }

    override fun onShare(item: DataGenerateInMonth) {
        lifecycleScope.launch {
            val qr = viewModel.getById(item.id)
            if (qr != null) {
                val bitmap = GenerateQR.generateQR(qr.content)
                shareBitmapAsPng(bitmap)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? BottomNavController)?.requestBottomNav(true)

        if(isSelectMode){
            adapter.clearAllSelection()
            val bottomSelect =requireActivity().findViewById<View>(R.id.bottomSelect)
            bottomSelect.visibility=View.GONE

            isSelectMode=false
            selectedIds=emptyList()

            (activity as? BottomNavController)?.requestBottomNav(true)
        }
    }

    override fun onSelectionMode(isLongPressed: Boolean) {
        isSelectMode = isLongPressed
        Log.d("RecyclerViewData", "Data id: ${isSelectMode}")
        val totalSelectedIds = adapter.getAllSelectedIds()


        if (!isLongPressed || totalSelectedIds.isEmpty()) {
            if (totalSelectedIds.isEmpty()) {

                adapter.clearAllSelection()
                isSelectMode = false
                selectedIds = emptyList()
            }
        } else {
            isSelectMode = true
        }

        (activity as? BottomNavController)?.requestBottomNav(!isSelectMode)
        val bottomSelect = requireActivity().findViewById<View>(R.id.bottomSelect)
        val deleteButton = bottomSelect?.findViewById<ImageView>(R.id.delete)
        val downloadButton = bottomSelect?.findViewById<ImageView>(R.id.download)
      if (isSelectMode){
          bottomSelect.visibility=View.VISIBLE


          deleteButton?.setOnClickListener {
              lifecycleScope.launch {
                  viewModel.deleteCustomQRListByid(selectedIds)
                  selectedIds = emptyList()
                  adapter.clearAllSelection()
                  bottomSelect.visibility = View.GONE
              }
          }
          downloadButton?.setOnClickListener {

              if (selectedIds.isEmpty()) {

                  return@setOnClickListener
              }

              lifecycleScope.launch {

                  val qrCodes = selectedIds.mapNotNull { id ->
                      viewModel.getById(id)
                  }


                  val bitmaps = qrCodes.map { qr ->
                      GenerateQR.generateQR(qr.content)
                  }


                  shareMultipleBitmapsAsPng(bitmaps)


//                  selectedIds = emptyList()
//                  childAdapter.clearSelectionMode()
                  bottomSelect.visibility = View.GONE
          }
      }}
        else {
            bottomSelect.visibility=View.GONE
      }
    }

    override fun onSelectedIdsChanged(ids: List<Int>) {
        selectedIds = adapter.getAllSelectedIds()
        updateBottomSelectCount()


    }

    override fun onEnableSelectionModeForAll() {
        adapter.enableSelectionModeForAll()
    }
    private fun updateBottomSelectCount() {
        val bottomSelect = requireActivity().findViewById<View>(R.id.bottomSelect)
        val tvFileSelect = bottomSelect?.findViewById<TextView>(R.id.tvnumberselectitem)
        tvFileSelect?.text = "${selectedIds.size} File Select"
    }


}