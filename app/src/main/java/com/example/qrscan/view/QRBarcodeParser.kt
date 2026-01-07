package com.example.qrscan.view

import com.example.qrscan.database.data.QRType
import com.google.mlkit.vision.barcode.common.Barcode

data class ParsedQR(
    val type: QRType,
    val content: String,
    val data: Map<String, String>
)

object QRBarcodeParser {

    fun parseBarcode(barcode: Barcode): ParsedQR {

        fun Any?.asString(): String = this?.toString() ?: ""

        return when (barcode.valueType) {

            Barcode.TYPE_PHONE -> {
                val phone = barcode.phone
                ParsedQR(
                    QRType.PHONE,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Number" to phone?.number.asString(),
                        "Type" to phone?.type.asString(),
//                        "RawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_EMAIL -> {
                val email = barcode.email
                ParsedQR(
                    QRType.EMAIL,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Address" to email?.address.asString(),
                        "Subject" to email?.subject.asString(),
                        "Body" to email?.body.asString(),
                        "Type" to email?.type.asString(),
//                        "RawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_WIFI -> {
                val wifi = barcode.wifi

                val encryption = mapWifiEncryption(wifi?.encryptionType)
                ParsedQR(
                    QRType.WIFI,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Ssid" to wifi?.ssid.asString(),
                        "Password" to wifi?.password.asString(),
                        "EncryptionType" to encryption,
//                        "RawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_CONTACT_INFO -> {
                val c = barcode.contactInfo
                val name = c?.name
                ParsedQR(
                    QRType.CONTACT,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Name" to name?.formattedName.asString(),
                        "FirstName" to name?.first.asString(),
                        "LastName" to name?.last.asString(),
                        "Organization" to c?.organization.asString(),
                        "Title" to c?.title.asString(),
                        "Phones" to c?.phones?.joinToString { it.number.toString() }.asString(),
                        "Emails" to c?.emails?.joinToString { it.address.toString() }.asString(),
//                        "RawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                val event = barcode.calendarEvent
                ParsedQR(
                    QRType.EVENT,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Summary" to event?.summary.asString(),
                        "Description" to event?.description.asString(),
                        "Location" to event?.location.asString(),
                        "Start" to event?.start?.rawValue.asString(),
                        "End" to event?.end?.rawValue.asString(),
//                        "RawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_URL -> {
                val url = barcode.url
                ParsedQR(
                    QRType.URL,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "Title" to url?.title.asString(),
                        "Url" to url?.url.asString()
                    )
                )
            }

            Barcode.TYPE_TEXT -> {
                ParsedQR(
                    QRType.TEXT,
                    content = barcode.rawValue.orEmpty(),
                    mapOf("Text" to barcode.rawValue.asString())
                )
            }

            else -> ParsedQR(
                QRType.TEXT,
                content = barcode.rawValue.orEmpty(),
                mapOf("RawValue" to barcode.rawValue.asString())
            )
        }
    }
    private fun mapWifiEncryption(type: Int?): String {
        return when (type) {
            Barcode.WiFi.TYPE_WPA -> "WPA"
            Barcode.WiFi.TYPE_WEP -> "WEP"
            Barcode.WiFi.TYPE_OPEN -> "nopass"
            else -> "unknown"
        }
    }

}
