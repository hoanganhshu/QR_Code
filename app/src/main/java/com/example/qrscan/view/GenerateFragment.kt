package com.example.qrscan.view

import android.R.attr.data
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterGenerate
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
class GenerateFragment : BaseFragment<FragmentGenerateBinding>(){
    private val viewModel : ScanViewModel by activityViewModels()

    private lateinit var listview: Flow<List<QRCodeEntity>>
    private lateinit var adapter: AdapterGenerate


    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGenerateBinding {
        return FragmentGenerateBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBinding.createnow.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem = next.currentItem + 2
        }
        (activity as? MainActivity)?.showBottomNav(true)
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
                    val monthFormat = SimpleDateFormat("MMM yyyy", Locale.US)
                    val dateFormat = SimpleDateFormat("dd:MM,yyyy ", Locale.US)



                    val grouped =list.groupBy { entity -> monthFormat.format(Date(entity.createdAt)) }.map { (monthYear, items) ->

                        generateMonth(

                            createAt = monthYear,
                            items = items.map { qr ->
                                DataGenerateInMonth(id = qr.id,
                                    image = when(qr.type){
                                        QRType.EMAIL ->{
                                            R.drawable.email
                                        }
                                        QRType.CONTACT -> {
                                            R.drawable.contacts
                                        }
                                        QRType.EVENT -> {
                                            R.drawable.calendar
                                        }

                                        QRType.LOCATION->R.drawable.location
                                        QRType.SMS -> R.drawable.sms
                                        QRType.WIFI -> R.drawable.wifi
                                        QRType.TEXT -> R.drawable.text
                                        QRType.URL -> R.drawable.url
                                        QRType.PHONE -> R.drawable.icon_phone
                                        else -> R.drawable.icon_phone
                                    },
                                    title = qr.type.name,
                                    subtitle = dateFormat.format(Date(qr.createdAt))

                                )
                            })

                }
                   adapter = AdapterGenerate(object : Callback{
                        override fun onEdit(item: DataGenerateInMonth) { lifecycleScope.launch {

                            viewModel.itemIdCreate=item.id
                            Log.d("RecyclerViewData", "Data id: ${item.id},${item.title}")

                            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                            next.currentItem = 9
                        }



                        }

                        override fun onDelete(item: DataGenerateInMonth) {
                            lifecycleScope.launch {
                                viewModel.deleteById(item.id)}

                        }

                        override fun onShare(item: DataGenerateInMonth) {
                            lifecycleScope.launch {
                                val qr=viewModel.getById(item.id)
                                if(qr!=null){
                                    val bitmap = GenerateQR.generateQR(qr.content)
                                    shareBitmapAsPng(bitmap)
                                }

                                }


                        }

                    })
                    mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
                    mBinding.recyclerview.adapter = adapter

                    adapter.submitData(grouped)

                }
        }




    }}

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)


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


}