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
                        "number" to phone?.number.asString(),
                        "type" to phone?.type.asString(),
                        "rawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_EMAIL -> {
                val email = barcode.email
                ParsedQR(
                    QRType.EMAIL,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "address" to email?.address.asString(),
                        "subject" to email?.subject.asString(),
                        "body" to email?.body.asString(),
                        "type" to email?.type.asString(),
                        "rawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_WIFI -> {
                val wifi = barcode.wifi
                ParsedQR(
                    QRType.WIFI,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "ssid" to wifi?.ssid.asString(),
                        "password" to wifi?.password.asString(),
                        "encryptionType" to wifi?.encryptionType.asString(),
                        "rawValue" to barcode.rawValue.asString()
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
                        "name" to name?.formattedName.asString(),
                        "firstName" to name?.first.asString(),
                        "lastName" to name?.last.asString(),
                        "organization" to c?.organization.asString(),
                        "title" to c?.title.asString(),
                        "phones" to c?.phones?.joinToString { it.number.toString() }.asString(),
                        "emails" to c?.emails?.joinToString { it.address.toString() }.asString(),
                        "rawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                val event = barcode.calendarEvent
                ParsedQR(
                    QRType.EVENT,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "summary" to event?.summary.asString(),
                        "description" to event?.description.asString(),
                        "location" to event?.location.asString(),
                        "start" to event?.start?.rawValue.asString(),
                        "end" to event?.end?.rawValue.asString(),
                        "rawValue" to barcode.rawValue.asString()
                    )
                )
            }

            Barcode.TYPE_URL -> {
                val url = barcode.url
                ParsedQR(
                    QRType.URL,
                    content = barcode.rawValue.orEmpty(),
                    mapOf(
                        "title" to url?.title.asString(),
                        "url" to url?.url.asString()
                    )
                )
            }

            Barcode.TYPE_TEXT -> {
                ParsedQR(
                    QRType.TEXT,
                    content = barcode.rawValue.orEmpty(),
                    mapOf("text" to barcode.rawValue.asString())
                )
            }

            else -> ParsedQR(
                QRType.TEXT,
                content = barcode.rawValue.orEmpty(),
                mapOf("rawValue" to barcode.rawValue.asString())
            )
        }
    }
}
