package com.example.qrscan.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.aigestudio.wheelpicker.WheelPicker
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentSettingBinding
import com.example.qrscan.viewmodel.ScanViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class SettingFragment : BaseFragment<FragmentSettingBinding>(){

    private val viewModel : ScanViewModel by activityViewModels()
    val EXTRA_DESTINATION = "destination"

    val DEST_SETTING = "setting"

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
        (activity as? MainActivity)?.showBottomNav(true)
        mBinding.privatelinear.isClickable = false
        mBinding.privatelinear.isEnabled = false

        // ✅ enable sau khi UI ổn định
        view.post {
            mBinding.privatelinear.isClickable = true
            mBinding.privatelinear.isEnabled = true
        }



        val ctx = requireContext()

        mBinding.switchhistory.isChecked =
            ScanSettingPrefs.getBoolean(ctx, "save_history", false)

        mBinding.switchvibeep.isChecked =
            ScanSettingPrefs.getBoolean(ctx, "beep", false)

        mBinding.switchvibrate.isChecked =
            ScanSettingPrefs.getBoolean(ctx, "vibrate", false)

        mBinding.switchscan.isChecked =
            ScanSettingPrefs.getBoolean(ctx, "auto_scan", true)

        mBinding.switchhistory.setOnCheckedChangeListener { _, checked ->
            ScanSettingPrefs.saveBoolean(ctx, "save_history", checked)
            viewModel.isSave = checked
        }

        mBinding.switchvibeep.setOnCheckedChangeListener { _, checked ->
            ScanSettingPrefs.saveBoolean(ctx, "beep", checked)
            viewModel.setBeepEnabled(checked)
        }

        mBinding.switchvibrate.setOnCheckedChangeListener { _, checked ->
            ScanSettingPrefs.saveBoolean(ctx, "vibrate", checked)
            viewModel.setVibrateEnabled(checked)
        }

        mBinding.switchscan.setOnCheckedChangeListener { _, checked ->
            ScanSettingPrefs.saveBoolean(ctx, "auto_scan", checked)
            viewModel.setAutoScanEnabled(checked)
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
            (activity as? MainActivity)?.navigateMain(PrivacyFragment())
        }


        mBinding.languagelinear.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.diafglog_change_language, null)
            val btnSubmit = dialogView.findViewById<MaterialCardView>(R.id.accept)
            val txtlanguage=dialogView.findViewById<WheelPicker>(R.id.language)
            val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.regular)





            val items= listOf<String>("English", "VietNam", "Japan", "Germany", "India")
            txtlanguage.data = items
            val savedLanguage = LanguagePrefs.get(requireContext())


            txtlanguage.typeface=typeface



            var selectedLanguage = savedLanguage



            txtlanguage.setOnItemSelectedListener { _, data, position ->
                selectedLanguage = data as String
                Log.d("DEBUG", "Wheel settled -> position=$position value=$selectedLanguage")
            }


            btnSubmit.setOnClickListener {



                if (selectedLanguage != null) {
                    when (selectedLanguage.trim()) {
                        "English" -> changeLanguage("en")
                        "VietNam" -> changeLanguage("vi")
                        "Japan" -> changeLanguage("ja")
                        "Germany" -> changeLanguage("de")
                        "India" -> changeLanguage("hi")
                    }
                    LanguagePrefs.save(requireContext(), selectedLanguage)
                    restartAppToSetting()


                }
                Log.d("DEBUG","index=${selectedLanguage}")
                builder.dismiss()
            }



            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
            builder.show()

            txtlanguage.postDelayed({

                val locale = resources.configuration.locales[0]
                val currentLanguage = when (locale.language) {
                    "en" -> "English"
                    "vi" -> "VietNam"
                    "ja" -> "Japan"
                    "de" -> "Germany"
                    "hi" -> "India"
                    else -> "English"
                }

                val index = items.indexOf(currentLanguage)
                Log.e("LANG", "FINAL index=$index language=$currentLanguage")

                if (index >= 0) {
                    txtlanguage.selectedItemPosition = index
                }

            }, 0)

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


    }
    override fun onResume() {
        super.onResume()
        (activity as? BottomNavController)?.requestBottomNav(true)
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
    private fun restartAppToSetting() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra(EXTRA_DESTINATION, DEST_SETTING)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }





}