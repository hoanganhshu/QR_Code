package com.example.qrscan.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.adapter.AdapterLanguage
import com.example.qrscan.databinding.FragmentSettingBinding
import com.example.qrscan.viewmodel.ScanViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.Locale

class SettingFragment : BaseFragment<FragmentSettingBinding>(){
    private val viewModel : ScanViewModel by activityViewModels()

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val next = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        next.isUserInputEnabled = false

        mBinding.switchvibeep.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBeepEnabled(isChecked)
        }

        mBinding.switchvibrate.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVibrateEnabled(isChecked)
        }
        mBinding.switchhistory.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSave=isChecked

        }
        mBinding.sharelinear.setOnClickListener {
            shareApp()

        }
        updateLanguageText()
        mBinding.feedbacklinear.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_feedback, null)

            val btnSubmit = dialogView.findViewById<MaterialCardView>(R.id.accept)


            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            builder.show()
            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
            btnSubmit.setOnClickListener {
                builder.dismiss()
            }

        }
        mBinding.privatelinear.setOnClickListener {

            next.currentItem = 12
        }
//        mBinding.languagelinear.setOnClickListener {
//
//                val dialogView = layoutInflater.inflate(R.layout.diafglog_change_language, null)
//            val btnSubmit = dialogView.findViewById<MaterialCardView>(R.id.accept)
//                val builder = MaterialAlertDialogBuilder(requireContext())
//                    .setView(dialogView)
//                    .setCancelable(true)
//                    .create()
//            val languages = listOf("English"," VietNam","Japan","Germany","India")
//            val appLocales = AppCompatDelegate.getApplicationLocales()
//            val locale = appLocales.get(0) ?: Locale("en", "US")
//            val currentLanguage = when (locale.language) {
//                "en" -> "English"
//                "vi" -> " VietNam"
//                "ja" -> "Japan"
//                "de" -> "Germany"
//                "hi" -> "India"
//                else -> "English"
//            }
//
//
//
////            val languageAdapter = AdapterLanguage { selectedLanguage ->
////                    Toast.makeText(requireContext(), "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
////                    when (selectedLanguage) {
////                        "English" -> changeLanguage("en")
////                        "VietNam" -> changeLanguage("vi")
////                        "Japan" -> changeLanguage("ja")
////                        "Germany" -> changeLanguage("de")
////                        "India" -> changeLanguage("hi")
////                    }
////                    updateLanguageText()
////                }
//
////            val languageAdapter = AdapterLanguage(
////                selectedLanguage = currentLanguage,
////                onClick = { selectedLanguage ->
////                    Toast.makeText(requireContext(), "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
////                    when (selectedLanguage.trim()) {
////                        "English" -> changeLanguage("en")
////                        " VietNam" -> changeLanguage("vi")
////                        "Japan" -> changeLanguage("ja")
////                        "Germany" -> changeLanguage("de")
////                        "India" -> changeLanguage("hi")
////                    }
////                    updateLanguageText()
////                    builder.dismiss()
////                }
////            )
//            var pendingSelection: String? = currentLanguage
//            val languageAdapter = AdapterLanguage(
//                selectedLanguage = currentLanguage,
//                onClick = { selectedLanguage ->
//                    // Chỉ update highlight, không apply language
//                    pendingSelection = selectedLanguage.trim()
//                    languageAdapter.setSelectedLanguage(pendingSelection)
//                    Toast.makeText(requireContext(), "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
//                }
//            )
//
//            btnSubmit.setOnClickListener {
//                if (pendingSelection != null) {
//                    when (pendingSelection) {
//                        "English" -> changeLanguage("en")
//                        " VietNam" -> changeLanguage("vi")
//                        "Japan" -> changeLanguage("ja")
//                        "Germany" -> changeLanguage("de")
//                        "India" -> changeLanguage("hi")
//                    }
//                    updateLanguageText()
//                }
//                builder.dismiss()
//            }
//
//
//            btnSubmit.setOnClickListener {
//                builder.dismiss()
//            }
//            languageAdapter.submitData(languages)
//                dialogView.findViewById<RecyclerView>(R.id.recyclerviewlanguage).apply {
//                    layoutManager = LinearLayoutManager(requireContext())
//                    adapter = languageAdapter
//                }
//
//            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
//                builder.show()
//
//
//
//        }

        mBinding.languagelinear.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.diafglog_change_language, null)
            val btnSubmit = dialogView.findViewById<MaterialCardView>(R.id.accept)
            val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val languages = listOf("English", " VietNam", "Japan", "Germany", "India")
            val appLocales = AppCompatDelegate.getApplicationLocales()
            val locale = appLocales.get(0) ?: Locale("en", "US")
            val currentLanguage = when (locale.language) {
                "en" -> "English"
                "vi" -> " VietNam"
                "ja" -> "Japan"
                "de" -> "Germany"
                "hi" -> "India"
                else -> "English"
            }

            var pendingSelection: String? = currentLanguage
            lateinit var languageAdapter: AdapterLanguage

             languageAdapter = AdapterLanguage(
                selectedLanguage = currentLanguage,
                onClick = { selectedLanguage ->
                    pendingSelection = selectedLanguage.trim()
                    languageAdapter.setSelectedLanguage(pendingSelection)
                    Toast.makeText(requireContext(), "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
                }
            )

            btnSubmit.setOnClickListener {
                if (pendingSelection != null) {
                    when (pendingSelection) {
                        "English" -> changeLanguage("en")
                        " VietNam" -> changeLanguage("vi")
                        "Japan" -> changeLanguage("ja")
                        "Germany" -> changeLanguage("de")
                        "India" -> changeLanguage("hi")
                    }
                    updateLanguageText()
                }
                builder.dismiss()
            }

            languageAdapter.submitData(languages)
            dialogView.findViewById<RecyclerView>(R.id.recyclerviewlanguage).apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = languageAdapter


                val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                val drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.divider_horizontal_dark)
                divider.setDrawable(drawable!!)
                addItemDecoration(divider)
            }

            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
            builder.show()
        }
        mBinding.ratelinear.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_rate, null)
            val ratingBar =dialogView.findViewById<RatingBar>(R.id.ratingBar)
            val btnSubmit = dialogView.findViewById<TextView>(R.id.accept)
            val btnCancel = dialogView.findViewById<TextView>(R.id.cancel)

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
            btnCancel.setOnClickListener {
                builder.dismiss()
            }
            btnSubmit.setOnClickListener {
                builder.dismiss()
            }

            builder.show()
            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)


        }
        mBinding.switchscan.setOnCheckedChangeListener {_, isChecked ->
            viewModel.setAutoScanEnabled(isChecked)

        }


    }
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)
    }
    private fun shareApp() {
        val shareText = getString(R.string.shareapp)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(intent, "Chia sẻ ứng dụng qua..."))
    }
    private fun changeLanguage(languageCode: String) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
        recreate(requireActivity())

    }
    private fun updateLanguageText() {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val locale = appLocales.get(0) ?: Locale.getDefault()
        val languageCode = locale.language

        val languageName = when (languageCode) {
            "en" -> "English"
            "vi" -> "Việt Nam"
            "ja" -> "日本"
                "de" -> "Deutschland"
            "hi" -> "भारत"
            else -> {

                locale.getDisplayLanguage(Locale.ENGLISH)
            }
        }

        mBinding.txtlanguage.text = languageName
    }




}