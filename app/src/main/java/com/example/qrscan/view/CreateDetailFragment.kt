package com.example.qrscan.view

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterCreate
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import com.example.qrscan.databinding.FragmentCreateDetailBinding
import com.example.qrscan.viewmodel.ScanViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URLEncoder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateDetailFragment : BaseFragment<FragmentCreateDetailBinding>(){
    private val viewModel : ScanViewModel by activityViewModels()
    private lateinit var adapter: AdapterCreate
    private var selectedSecurity: SecurityType = SecurityType.FREE



    enum class SecurityType {
        FREE, WPA, WEP
    }

    private var isKeyboardVisible = false
    var qrid : Int =0

    private var data =listOf<Map<String,Any>>()
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateDetailBinding {
        return FragmentCreateDetailBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false
        adapter = AdapterCreate()
        mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerview.adapter = adapter
        mBinding.recyclerview.itemAnimator = null
       qrid = viewModel.itemIdCreate
        adapter.setRecyclerView(mBinding.recyclerview)


        setupKeyboardListener()

        Log.d("TEST_ID", "ID nhận từ ViewModel = $qrid")
        Log.d("TEST_ID", "ID  = $id")
        lifecycleScope.launch {

            if (qrid != 0) {
                val qr = viewModel.getById(qrid)
                if (qr != null) {
                    fillUI(qr)
                }
                else {
                    Log.d("RecyclerView", "Data size: ${data.size}")

                }


            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createOption.collect { type ->
                    if (type == null) return@collect
                    if (qrid != 0) return@collect
                    mBinding.create.text = getTitle(type)
                    fillInforGenerate(type)
                    adapter.clearInputMap()
                    adapter.submitData(data)
                }
            }
        }
        mBinding.root.setOnClickListener {
            hideKeyboard()

        }
        val isWifi = viewModel.createOption.value
        if (isWifi == QRType.WIFI) {
            mBinding.optionwifi.visibility=View.VISIBLE
            selectedSecurity = SecurityType.FREE
            mBinding.selectwifi.post {
                mBinding.selectwifi.check(R.id.free)
            }

        } else {
            mBinding.optionwifi.visibility= View.GONE
        }




        mBinding.selectwifi.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.free -> {
                    selectedSecurity = SecurityType.FREE
                    mBinding.recyclerview.visibility = View.GONE
                }

                R.id.wpa -> {
                    selectedSecurity = SecurityType.WPA
                    mBinding.recyclerview.visibility = View.VISIBLE
                }

                R.id.wep -> {
                    selectedSecurity = SecurityType.WEP
                    mBinding.recyclerview.visibility = View.VISIBLE
                }
            }
        }

        (activity as? MainActivity)?.showBottomNav(false)
        mBinding.btnback.setOnClickListener {
            (activity as? MainActivity)?.navigateMain(CreateOptionFragment())

            viewModel.clearCreateOption()
        }
        mBinding.btncreate.setOnClickListener {
            try {
                val adapter = mBinding.recyclerview.adapter as AdapterCreate

                val opt = viewModel.createOption.value ?: return@setOnClickListener

                 adapter.saveCurrentValuesFromViews()

                val input: Map<String, String> = adapter.getUserInput()
                var hasError =false

                data.forEach { item ->
                    val key = item["key"]?.toString() ?: return@forEach
                    val value = input[key]?.trim() ?: ""

                    if (value.isEmpty()) {
                        adapter.setError(key)
                        hasError = true
                    } else {
                        adapter.clearError(key)
                    }
                }
                if(hasError){
                    adapter.notifyDataSetChanged()
                    return@setOnClickListener
                }

                viewModel.content = when (opt) {
                    QRType.EMAIL -> {



                        val email = input["email"].orEmpty()
                        val cc = input["cc"].orEmpty()
                        val subject = input["subject"].orEmpty()
                        val body = input["body"].orEmpty()

                        if (email.isBlank())
                            throw Exception("Email is required")

                        buildMailto(email, subject, body, cc)
                    }
                    QRType.URL -> {
                        val url = input["url"].orEmpty()
                        if (url.isBlank()) throw Exception("URL cannot be empty")
                        url
                    }
                    QRType.TEXT  -> {
                        val text = input["text"].orEmpty()
                        if (text.isBlank()) throw Exception("Text is empty")
                        text
                    }
                    QRType.PHONE -> {
                        val number = input["number"].orEmpty()
                        if (number.isBlank()) throw Exception("Phone number required")

                        "tel:$number"
                    }
                    QRType.SMS -> {
                        val phone = input["phone"].orEmpty()
                        val body = input["body"].orEmpty()
                        if (phone.isBlank()) throw Exception("Phone required")

                        "SMSTO:$phone:$body"
                    }
                    QRType.WIFI -> {
                        val ssid = input["network"].orEmpty()
                        val pass = input["password"].orEmpty()

                        if (ssid.isBlank()) throw Exception("SSID is required")

                        val authType = when (selectedSecurity) {
                            SecurityType.FREE -> "nopass"
                            SecurityType.WPA -> "WPA"
                            SecurityType.WEP -> "WEP"
                        }

                        buildString {
                            append("WIFI:")
                            append("T:$authType;")
                            append("S:$ssid;")

                            if (selectedSecurity != SecurityType.FREE) {
                                append("P:$pass;")
                            }

                            append(";")
                        }
                    }
                    QRType.CONTACT -> {
                        val name = input["name"].orEmpty()
                        val phone = input["phone"].orEmpty()
                        val address = input["address"].orEmpty()
                        val nickname = input["nickname"].orEmpty()

                        if (name.isBlank() && phone.isBlank())
                            throw Exception("At least Name or Phone is required")

                        """
                BEGIN:VCARD
                VERSION:3.0
                FN:$name
                N:$name;;;
                NICKNAME:$nickname
                TEL:$phone
                ADR:$address
                END:VCARD
                """.trimIndent()
                    }
                    QRType.LOCATION  -> {
                        val lat = input["latitude"].orEmpty()
                        val lon = input["longitude"].orEmpty()
                        if (lat.isBlank() || lon.isBlank())
                            throw Exception("Latitude & longitude required")

                        "geo:$lat,$lon"
                    }
                    QRType.EVENT -> {
                        val name = input["name"].orEmpty()
                        val start = input["start"].orEmpty()
                        val end = input["end"].orEmpty()
                        val loc = input["location"].orEmpty()
                        val description = input["description"].orEmpty()

                        if (name.isBlank() || start.isBlank() || end.isBlank())
                            throw Exception("Name, start, end required")

                        """
                BEGIN:VCALENDAR
                VERSION:2.0
                BEGIN:VEVENT
                SUMMARY:$name
                DTSTART:$start
                DTEND:$end
                LOCATION:$loc
                DESCRIPTION:$description
                END:VEVENT
                END:VCALENDAR
                """.trimIndent()
                    }

                    else -> throw Exception("Unsupported QR type: $opt")
                }

                viewModel.userInput = input

                val content = viewModel.content
                if (content.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Nội dung QR rỗng!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val bitmap = GenerateQR.generateQR(content)

                val stream= ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                viewModel.byteQR=stream.toByteArray()
                Log.d("QR_DEBUG", "content = $content")
                Log.d("QR_DEBUG", "byteQR size = ${viewModel.byteQR?.size}")
                (activity as? MainActivity)?.navigateMain(ResultFragment())



            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        }
    private fun fillUI(qr: QRCodeEntity) {

        val type = qr.type


        mBinding.create.text = getTitle(type)


        viewModel.setCreateOption(type)

        val prefill = mutableMapOf<String, String>()

        when (type) {
            QRType.EMAIL -> {
                prefill["email"] = qr.data["email"] as? String ?: ""
                prefill["cc"] = qr.data["cc"] as? String ?: ""
                prefill["subject"] = qr.data["subject"] as? String ?: ""
                prefill["body"] = qr.data["body"] as? String ?: ""


            }

            QRType.PHONE -> {
                prefill["number"] =
                    qr.data["number"] as? String ?: qr.content.removePrefix("tel:")
            }

            QRType.URL -> {
                prefill["url"] = qr.content
            }

            QRType.TEXT -> {
                prefill["text"] = qr.content
            }

            QRType.SMS -> {
                prefill["phone"] = qr.data["phone"] as? String ?: ""
                prefill["body"] = qr.data["body"] as? String ?: ""
            }

            QRType.WIFI -> {



                prefill["network"] = qr.data["network"] as? String ?: ""
                prefill["password"] = qr.data["password"] as? String ?: ""
            }

            QRType.CONTACT -> {
                prefill["name"] = qr.data["name"] as? String ?: ""
                prefill["nickname"] = qr.data["nickname"] as? String ?: ""
                prefill["phone"] = qr.data["phone"] as? String ?: ""
                prefill["address"] = qr.data["address"] as? String ?: ""
            }

            QRType.LOCATION -> {
                val parts = qr.content.removePrefix("geo:").split(",")
                prefill["latitude"] = parts.getOrNull(0) ?: ""
                prefill["longitude"] = parts.getOrNull(1) ?: ""
            }

            QRType.EVENT -> {
                prefill["name"] = qr.data["name"] as? String ?: ""
                prefill["start"] = qr.data["start"] as? String ?: ""
                prefill["end"] = qr.data["end"] as? String ?: ""
                prefill["location"] = qr.data["location"] as? String ?: ""
                prefill["description"] = qr.data["description"] as? String ?: ""
            }

            else -> {}
        }

        fillInforGenerate(type)
        adapter.submitData(data)
        adapter.prefillInput(prefill)
    }
    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager

        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }



    fun fillInforGenerate(opt: QRType) {
        data = when (opt) {

            QRType.EMAIL  -> listOf(
                mapOf(
                    "key" to "email",
                    "title" to "Email:",
                    "subtitle" to "example@123.com",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                ),
                mapOf(
                    "key" to "cc",
                    "title" to "CC/BCC:",
                    "subtitle" to "example@123.com",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                ),
                mapOf(
                    "key" to "subject",
                    "title" to "Subject:",
                    "subtitle" to "Example 123",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                ),
                mapOf(
                    "key" to "body",
                    "title" to "Body:",
                    "subtitle" to "Lorem ipsum dolor sit amet...",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                )
            )

            QRType.PHONE -> listOf(
                mapOf(
                    "key" to "number",
                    "title" to "Number:",
                    "subtitle" to "012 8498 3849",
                    "inputType" to InputType.TYPE_CLASS_PHONE
                )
            )

            QRType.LOCATION -> listOf(
                mapOf(
                    "key" to "latitude",
                    "title" to "Latitude:",
                    "subtitle" to "20.12345",
                    "inputType" to (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
                ),
                mapOf(
                    "key" to "longitude",
                    "title" to "Longitude:",
                    "subtitle" to "105.12345",
                    "inputType" to (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
                )
            )

            QRType.SMS -> listOf(
                mapOf(
                    "key" to "phone",
                    "title" to "Phone:",
                    "subtitle" to "018 8294 8347",
                    "inputType" to InputType.TYPE_CLASS_PHONE
                ),
                mapOf(
                    "key" to "body",
                    "title" to "Body:",
                    "subtitle" to "Message here",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                )
            )

            QRType.CONTACT -> listOf(
                mapOf(
                    "key" to "name",
                    "title" to "Name:",
                    "subtitle" to "Nicken",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                ),
                mapOf(
                    "key" to "nickname",
                    "title" to "Nickname:",
                    "subtitle" to "Nick",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                ),
                mapOf(
                    "key" to "phone",
                    "title" to "Phone:",
                    "subtitle" to "012 8498 3849",
                    "inputType" to InputType.TYPE_CLASS_PHONE
                ),
                mapOf(
                    "key" to "address",
                    "title" to "Address:",
                    "subtitle" to "ABC Street",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                )
            )

            QRType.URL -> listOf(
                mapOf(
                    "key" to "url",
                    "title" to "Url:",
                    "subtitle" to "http://example.com",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI)
                )
            )

            QRType.WIFI ->{
                mBinding.optionwifi.visibility=View.VISIBLE
                listOf(
                mapOf(
                    "key" to "network",
                    "title" to "Network:",
                    "subtitle" to "ABC123",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                ),
                mapOf(
                    "key" to "password",
                    "title" to "Password:",
                    "subtitle" to "12345678",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                )
            )}

            QRType.TEXT -> listOf(
                mapOf(
                    "key" to "text",
                    "title" to "Text:",
                    "subtitle" to "Input text here",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                )
            )

            QRType.EVENT -> listOf(
                mapOf(
                    "key" to "name",
                    "title" to "Name:",
                    "subtitle" to "Event",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                ),
                mapOf(
                    "key" to "start",
                    "title" to "Starting date:",
                    "subtitle" to "09:00 dd/mm/yyyy",
                    "inputType" to (InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL)
                ),
                mapOf(
                    "key" to "end",
                    "title" to "Ending date:",
                    "subtitle" to "11:00 dd/mm/yyyy",
                    "inputType" to (InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL)
                ),
                mapOf(
                    "key" to "location",
                    "title" to "Location:",
                    "subtitle" to "City",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                ),
                mapOf(
                    "key" to "description",
                    "title" to "Description:",
                    "subtitle" to "Details...",
                    "inputType" to (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                )
            )

            else -> emptyList()
        }
    }

    fun buildMailto(email: String, subject: String?, body: String?, cc: String?): String {
        val params = mutableListOf<String>()

        if (!subject.isNullOrBlank()) params += "subject=" + URLEncoder.encode(subject, "UTF-8")
        if (!body.isNullOrBlank()) params += "body=" + URLEncoder.encode(body, "UTF-8")
        if (!cc.isNullOrBlank()) params += "cc=" + URLEncoder.encode(cc, "UTF-8")

        val query = params.joinToString("&")

        return if (query.isBlank()) "mailto:$email"
        else "mailto:$email?$query"
    }
    private fun setupKeyboardListener() {
        val rootView = view ?: return
        val rootViewTreeObserver = rootView.viewTreeObserver

        rootViewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom


                if (keypadHeight > 200) {
                    if (!isKeyboardVisible) {
                        isKeyboardVisible = true
                    }
                } else {
                    isKeyboardVisible = false
                }
            }
        })
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
            else -> "No"
        }
    }





}