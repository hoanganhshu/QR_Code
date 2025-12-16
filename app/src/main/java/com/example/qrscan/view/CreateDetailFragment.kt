package com.example.qrscan.view

import android.R.attr.data
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var data =listOf<Map<String,Any>>()
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateDetailBinding {
        return FragmentCreateDetailBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.createOption.collect {

                        opt ->
                        val adapter = AdapterCreate()
                        mBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
                        mBinding.recyclerview.adapter = adapter

                        mBinding.create.setText(opt)
                        fillInforGenerate(opt)
                        adapter.submitData(data)



                    }
                }
            }
        (activity as? MainActivity)?.showBottomNav(false)
        mBinding.btnback.setOnClickListener {
            val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem = next.currentItem - 1
        }
        mBinding.btncreate.setOnClickListener {
            try {

                val adapter = mBinding.recyclerview.adapter as AdapterCreate
                val input: Map<String, String> = adapter.getUserInput()


                val opt = viewModel.createOption.value ?: return@setOnClickListener


                val content = when (opt) {


                    "Email" -> {
                        val email = input["email"].orEmpty()
                        val cc = input["cc"].orEmpty()
                        val subject = input["subject"].orEmpty()
                        val body = input["body"].orEmpty()

                        if (email.isBlank())
                            throw Exception("Email is required")

                        buildMailto(email, subject, body, cc)
                    }


                    "Url" -> {
                        val url = input["url"].orEmpty()
                        if (url.isBlank()) throw Exception("URL cannot be empty")
                        url
                    }


                    "Text" -> {
                        val text = input["text"].orEmpty()
                        if (text.isBlank()) throw Exception("Text is empty")
                        text
                    }


                    "Phone" -> {
                        val number = input["number"].orEmpty()
                        if (number.isBlank()) throw Exception("Phone number required")

                        "tel:$number"
                    }


                    "Sms" -> {
                        val phone = input["phone"].orEmpty()
                        val body = input["body"].orEmpty()
                        if (phone.isBlank()) throw Exception("Phone required")

                        "SMSTO:$phone:$body"
                    }


                    "Wifi" -> {
                        val ssid = input["network"].orEmpty()
                        val pass = input["password"].orEmpty()
                        if (ssid.isBlank()) throw Exception("SSID is required")

                        "WIFI:T:WPA;S:$ssid;P:$pass;;"
                    }


                    "Contact" -> {
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


                    "Location" -> {
                        val lat = input["latitude"].orEmpty()
                        val lon = input["longitude"].orEmpty()
                        if (lat.isBlank() || lon.isBlank())
                            throw Exception("Latitude & longitude required")

                        "geo:$lat,$lon"
                    }


                    "Calendar" -> {
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


               val bitmap =GenerateQR.generateQR(content)
                val stream= ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                viewModel.byteQR=stream.toByteArray()

                val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                next.currentItem = next.currentItem +1


            } catch (e: Exception) {
                e.printStackTrace()

            }
        }




    }
    fun fillInforGenerate(opt: String?) {
        data = when (opt) {

            "Email" -> listOf(
                mapOf("key" to "email", "title" to "Email:", "subtitle" to "example@123.com"),
                mapOf("key" to "cc", "title" to "CC/BCC:", "subtitle" to "example@123.com"),
                mapOf("key" to "subject", "title" to "Subject:", "subtitle" to "Example 123"),
                mapOf("key" to "body", "title" to "Body:", "subtitle" to "Lorem ipsum dolor sit amet...")
            )

            "Phone" -> listOf(
                mapOf("key" to "number", "title" to "Number:", "subtitle" to "012 8498 3849")
            )

            "Location" -> listOf(
                mapOf("key" to "latitude", "title" to "Latitude:", "subtitle" to "20.12345"),
                mapOf("key" to "longitude", "title" to "Longitude:", "subtitle" to "105.12345")
            )

            "Sms" -> listOf(
                mapOf("key" to "phone", "title" to "Phone:", "subtitle" to "018 8294 8347"),
                mapOf("key" to "body", "title" to "Body:", "subtitle" to "Message here")
            )

            "Contact" -> listOf(
                mapOf("key" to "name", "title" to "Name:", "subtitle" to "Nicken"),
                mapOf("key" to "nickname", "title" to "Nickname:", "subtitle" to "Nick"),
                mapOf("key" to "phone", "title" to "Phone:", "subtitle" to "012 8498 3849"),
                mapOf("key" to "address", "title" to "Address:", "subtitle" to "ABC Street")
            )

            "Url" -> listOf(
                mapOf("key" to "url", "title" to "Url:", "subtitle" to "http://example.com")
            )

            "Wifi" -> listOf(
                mapOf("key" to "network", "title" to "Network:", "subtitle" to "ABC123"),
                mapOf("key" to "password", "title" to "Password:", "subtitle" to "12345678")
            )

            "Text" -> listOf(
                mapOf("key" to "text", "title" to "Text:", "subtitle" to "Input text here")
            )

            "Calendar" -> listOf(
                mapOf("key" to "name", "title" to "Name:", "subtitle" to "Event"),
                mapOf("key" to "start", "title" to "Starting date:", "subtitle" to "09:00 dd/mm/yyyy"),
                mapOf("key" to "end", "title" to "Ending date:", "subtitle" to "11:00 dd/mm/yyyy"),
                mapOf("key" to "location", "title" to "Location:", "subtitle" to "City"),
                mapOf("key" to "description", "title" to "Description:", "subtitle" to "Details...")
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




}