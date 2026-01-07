package com.example.qrscan.view


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterResult
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentHistoryScanBinding
import com.example.qrscan.viewmodel.ScanViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        val type = viewModel.scannedType.value
        if (type == null) {
            Log.e("NAV_DEBUG", "HistoryScan opened without scan → ignore")
            return
        }

        (activity as? BottomNavController)?.requestBottomNav(false)
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
            (activity as? MainActivity)?.navigateMain(ScanFragment())
        }

        mBinding.title.text = getTitle(type)
        mBinding.subtitle.text = getTodayFormatted()

        when (type) {

            QRType.EMAIL -> {
                mBinding.imagetitle.setImageResource(R.drawable.emailvector)
                mBinding.action.text = getString(R.string.sendmail)
            }

            QRType.PHONE -> {
                mBinding.imagetitle.setImageResource(R.drawable.icon_phonevector)
                mBinding.action.text = getString(R.string.call)
            }

            QRType.LOCATION -> {
                mBinding.imagetitle.setImageResource(R.drawable.locationvector)
                mBinding.action.text = getString(R.string.openmap)
            }

            QRType.SMS -> {
                mBinding.imagetitle.setImageResource(R.drawable.smsvector)
                mBinding.action.text = getString(R.string.sendsms)
            }

            QRType.CONTACT -> {
                mBinding.imagetitle.setImageResource(R.drawable.contactsvector)
                mBinding.action.text = getString(R.string.addtocontact)
            }

            QRType.URL -> {
                mBinding.imagetitle.setImageResource(R.drawable.urlvector)
                mBinding.action.text = getString(R.string.openinbrowser)
            }

            QRType.WIFI -> {
                mBinding.imagetitle.setImageResource(R.drawable.wifivector)
                mBinding.action.text = getString(R.string.connettonetwork)
            }

            QRType.TEXT -> {
                mBinding.imagetitle.setImageResource(R.drawable.textvector)
                mBinding.action.text = getString(R.string.copytext)
            }

            QRType.EVENT -> {
                mBinding.imagetitle.setImageResource(R.drawable.calendarvector)
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
            val data = viewModel.userScan.value

            when (type) {
                QRType.EMAIL -> {

                    val email = data["address"] ?: content.removePrefix("mailto:").split("?").first()
                    val subject = data["subject"] ?: parseQueryParam(content, "subject")
                    val body = data["body"] ?: parseQueryParam(content, "body")
                    val cc = parseQueryParam(content, "cc")


                    val mailtoUri = buildMailtoUri(email, subject, body, cc)
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(mailtoUri)))
                }

                QRType.PHONE -> {

                    val number = data["number"] ?: content.removePrefix("tel:")
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
                }

                QRType.SMS -> {

                    val phone: String
                    val message: String

                    if (content.startsWith("SMSTO:")) {
                        val parts = content.removePrefix("SMSTO:").split(":", limit = 2)
                        phone = parts.getOrNull(0) ?: data["phone"] ?: ""
                        message = parts.getOrNull(1) ?: data["body"] ?: ""
                    } else {
                        phone = data["phone"] ?: ""
                        message = data["body"] ?: ""
                    }

                    val smsUri = Uri.parse("smsto:$phone").buildUpon()
                        .appendQueryParameter("body", message)
                        .build()
                    startActivity(Intent(Intent.ACTION_SENDTO, smsUri))
                }

                QRType.URL -> {
                    val url = if (content.startsWith("http")) content else "https://$content"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }

                QRType.LOCATION -> {

                    val geoUri: Uri
                    if (content.startsWith("geo:")) {
                        val geoContent = content.removePrefix("geo:")
                        val parts = geoContent.split("?")
                        val coordinates = parts[0].split(",")
                        val lat = coordinates.getOrNull(0) ?: "0"
                        val lon = coordinates.getOrNull(1) ?: "0"


                        geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                    } else {

                        val lat = data["latitude"] ?: "0"
                        val lon = data["longitude"] ?: "0"
                        geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                    }
                    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {

                        startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                    }
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
                    Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                QRType.WIFI -> {
                    val ssid: String
                    val password: String?
                    val securityType: String
                    if (!ensureWriteSettingsPermission(requireActivity())) return@setOnClickListener

                    if (content.startsWith("WIFI:")) {
                        val wifiContent = content.removePrefix("WIFI:")

                        ssid = extractWifiParam(wifiContent, "S") ?: ""
                        password = extractWifiParam(wifiContent, "P")
                        securityType = extractWifiParam(wifiContent, "T") ?: "nopass"
                    } else {
                        ssid = data["ssid"] ?: data["network"] ?: ""
                        password = data["password"]
                        securityType = data["security"] ?: "nopass"
                    }

                    if (ssid.isBlank()) {
                        Toast.makeText(requireContext(), "SSID không hợp lệ", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // 🔥 ANDROID 10+ : dùng WifiNetworkSpecifier (ĐÚNG)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        connectWifi(
                            requireContext(),
                            ssid,
                            password,
                            securityType
                        )
                    } else {
                        // Android < 10 (fallback)
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        Toast.makeText(
                            requireContext(),
                            "SSID: $ssid\nPassword: ${password.orEmpty()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


                QRType.EVENT -> {
                    val name = data["name"] ?: data["summary"] ?: ""
                    val start = data["start"] ?: data["dtstart"] ?: ""
                    val end = data["end"] ?: data["dtend"] ?: ""
                    val location = data["location"] ?: ""
                    val description = data["description"] ?: ""

                    startActivity(
                        Intent(Intent.ACTION_INSERT).apply {
                            setData(CalendarContract.Events.CONTENT_URI)
                            putExtra(CalendarContract.Events.TITLE, name)
                            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                            putExtra(CalendarContract.Events.DESCRIPTION, description)


                            if (start.isNotEmpty()) {
                                try {
                                    val format = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
                                    format.parse(start)?.let { date ->
                                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            if (end.isNotEmpty()) {
                                try {
                                    val format = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
                                    format.parse(end)?.let { date ->
                                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }

                else -> {}
            }
        }
        viewModel.clearCreateOption()



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


    fun checkWriteSettingsPermission(context: Context): Boolean {
        return Settings.System.canWrite(context)
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

    // Helper functions
    private fun buildMailtoUri(email: String, subject: String?, body: String?, cc: String?): String {
        val params = mutableListOf<String>()
        if (!subject.isNullOrBlank()) params += "subject=${Uri.encode(subject)}"
        if (!body.isNullOrBlank()) params += "body=${Uri.encode(body)}"
        if (!cc.isNullOrBlank()) params += "cc=${Uri.encode(cc)}"

        return if (params.isEmpty()) "mailto:$email"
        else "mailto:$email?${params.joinToString("&")}"
    }

    private fun parseQueryParam(uri: String, paramName: String): String {
        return try {
            val uriObj = Uri.parse(uri)
            uriObj.getQueryParameter(paramName) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractWifiParam(wifiContent: String, param: String): String? {
        val pattern = "$param:(.*?);".toRegex()
        return pattern.find(wifiContent)?.groupValues?.get(1)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectWifi(
        context: Context,
        ssid: String,
        password: String?,
        security: String
    ) {
        val cleanSsid = ssid.trim()

        Log.e(
            "WIFI_DEBUG",
            "REQUEST → SSID='$cleanSsid' security='$security' password='$password'"
        )

        val builder = WifiNetworkSpecifier.Builder()
            .setSsid(cleanSsid)

        when {
            security.contains("wpa", ignoreCase = true) -> {
                if (password.isNullOrBlank()) {
                    Toast.makeText(context, "WiFi cần mật khẩu", Toast.LENGTH_SHORT).show()
                    return
                }
                builder.setWpa2Passphrase(password)
            }

            security.equals("nopass", true) ||
                    security.equals("open", true) -> {
                // WiFi mở – không làm gì
            }

            security.equals("wep", true) -> {
                Toast.makeText(
                    context,
                    "WiFi WEP không được Android hỗ trợ",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            else -> {
                Toast.makeText(
                    context,
                    "Bảo mật WiFi không hỗ trợ: $security",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(builder.build())
            .build()

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm.requestNetwork(
            request,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    Log.e("WIFI_DEBUG", "CONNECTED ✅")
                    cm.bindProcessToNetwork(network)
                    Toast.makeText(context, "Đã kết nối WiFi $cleanSsid", Toast.LENGTH_SHORT).show()
                }

                override fun onUnavailable() {
                    Log.e(
                        "WIFI_DEBUG",
                        "UNAVAILABLE ❌ (SSID mismatch / saved network / system reject)"
                    )
                    Toast.makeText(
                        context,
                        "Không thể kết nối WiFi. Vui lòng kết nối thủ công.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Fallback chuẩn UX
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }
        )
    }



    private fun parseCalendarDate(dateString: String): Long {
        // Hỗ trợ nhiều format: "20240101T090000", "2024-01-01 09:00:00", etc.
        return try {
            val formats = listOf(
                java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault()),
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            )

            for (format in formats) {
                try {
                    return format.parse(dateString)?.time ?: 0L
                } catch (e: Exception) {
                    continue
                }
            }
            0L
        } catch (e: Exception) {
            0L
        }
    }
    private fun ensureWriteSettingsPermission(context: Context): Boolean {
        return if (!Settings.System.canWrite(context)) {
            Toast.makeText(
                context,
                "Vui lòng cho phép Modify system settings để kết nối Wi-Fi",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            startActivity(intent)
            false
        } else {
            true
        }
    }



}