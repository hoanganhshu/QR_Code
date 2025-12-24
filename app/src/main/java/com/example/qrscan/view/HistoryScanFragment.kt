package com.example.qrscan.view


import android.R.attr.type
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterCreate
import com.example.qrscan.adapter.AdapterResult
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentHistoryScanBinding
import com.example.qrscan.viewmodel.ScanViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryScanFragment : BaseFragment<FragmentHistoryScanBinding>(){
    private  val viewModel : ScanViewModel by activityViewModels()




    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryScanBinding {
        return FragmentHistoryScanBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false
        val type = viewModel.createOption.value ?: return


        (activity as? MainActivity)?.showBottomNav(false)
        val adapter = AdapterResult(
            textColor = ContextCompat.getColor(requireContext(), R.color.black)
        )

        mBinding.recyclerviewhistoryscan.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerviewhistoryscan.adapter = adapter



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userScan.collect { dataMap ->
                val displayList = dataMap.filter { (_, value) ->
                    value.isNotEmpty()
                }.map { (key, value) ->
                    mapOf(
                        "title" to key,
                        "subtitle" to value
                    )
                }

                Log.d("HISTORY", "userScan size = ${dataMap.size}")
                Log.d("HISTORY", "Display list size = ${displayList.size}")
                adapter.submitData(displayList)
                Log.d("HISTORY", "Data submitted to adapter: $displayList")
                Log.d("HISTORY", "Adapter item count = ${adapter.itemCount}")
            }
        }

        mBinding.btnback.setOnClickListener {
          next.currentItem =4
        }

        mBinding.title.text = getTitle(type)
        mBinding.subtitle.text = getTodayFormatted()

        when (type) {

            QRType.EMAIL -> {
                mBinding.imagetitle.setImageResource(R.drawable.email)
                mBinding.action.text = getString(R.string.sendmail)
            }

            QRType.PHONE -> {
                mBinding.imagetitle.setImageResource(R.drawable.icon_phone)
                mBinding.action.text = getString(R.string.call)
            }

            QRType.LOCATION -> {
                mBinding.imagetitle.setImageResource(R.drawable.location)
                mBinding.action.text = getString(R.string.openmap)
            }

            QRType.SMS -> {
                mBinding.imagetitle.setImageResource(R.drawable.sms)
                mBinding.action.text = getString(R.string.sendsms)
            }

            QRType.CONTACT -> {
                mBinding.imagetitle.setImageResource(R.drawable.contacts)
                mBinding.action.text = getString(R.string.addtocontact)
            }

            QRType.URL -> {
                mBinding.imagetitle.setImageResource(R.drawable.url)
                mBinding.action.text = getString(R.string.openinbrowser)
            }

            QRType.WIFI -> {
                mBinding.imagetitle.setImageResource(R.drawable.wifi)
                mBinding.action.text = getString(R.string.connettonetwork)
            }

            QRType.TEXT -> {
                mBinding.imagetitle.setImageResource(R.drawable.text)
                mBinding.action.text = getString(R.string.copytext)
            }

            QRType.EVENT -> {
                mBinding.imagetitle.setImageResource(R.drawable.calendar)
                mBinding.action.text = getString(R.string.openwithcalendar)
            }

            else -> {}
        }


        mBinding.share.setOnClickListener {
            lifecycleScope.launch {
                     val content =viewModel.content
                if (content!=null){
                    val bitmap = GenerateQR.generateQR(content)
                    shareBitmapAsPng(bitmap)
                }



            }
        }
        mBinding.action.setOnClickListener {

            val content = viewModel.content ?: return@setOnClickListener

            when (type) {

                QRType.EMAIL -> {
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$content")))
                }

                QRType.PHONE -> {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$content")))
                }

                QRType.SMS -> {
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$content")))
                }

                QRType.URL -> {
                    val url = if (content.startsWith("http")) content else "https://$content"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }

                QRType.LOCATION -> {
                    val geoUri = if (content.startsWith("geo:")) content else "geo:0,0?q=$content"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
                }

                QRType.CONTACT -> {
                    startActivity(
                        Intent(Intent.ACTION_INSERT).apply {
                            this.type = ContactsContract.Contacts.CONTENT_TYPE
                            putExtra(ContactsContract.Intents.Insert.NAME, content)
                        }
                    )
                }

                QRType.TEXT -> {
                    val clipboard =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("QR Text", content))

                }

                QRType.WIFI -> {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }

                QRType.EVENT -> {
                    startActivity(
                        Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, content)
                        }
                    )
                }

                else -> {}
            }
        }


    }
    private fun getTitle(type: QRType): String {
        return when (type) {
            QRType.EMAIL    -> getString(R.string.email)
            QRType.PHONE    -> getString(R.string.phone)
            QRType.TEXT     -> getString(R.string.text)
            QRType.SMS      -> getString(R.string.sms)
            QRType.URL      -> getString(R.string.url)
            QRType.WIFI     -> getString(R.string.wifi)
            QRType.CONTACT  -> getString(R.string.contact)
            QRType.LOCATION -> getString(R.string.location)
            QRType.EVENT    -> getString(R.string.calendar)
            else -> "NO"
        }
    }

    override fun onResume() {
        super.onResume()
        // Removed duplicate collector - already handled in onViewCreated
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
    fun getTodayFormatted(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val suffix = when {
            day % 10 == 1 && day != 11 -> "st"
            day % 10 == 2 && day != 12 -> "nd"
            day % 10 == 3 && day != 13 -> "rd"
            else -> "th"
        }
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val locale = appLocales.get(0) ?: Locale("en", "US")


        val monthFormat = SimpleDateFormat("MMM", locale)
        val monthStr = monthFormat.format(calendar.time)

        return "$day$suffix $monthStr, $year"
    }


}