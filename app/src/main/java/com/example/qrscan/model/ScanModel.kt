package com.example.qrscan.model

import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import QrItemDao
import com.google.mlkit.vision.barcode.common.Barcode

class ScanModel(
    private val qrCodeDao: QrItemDao
) {

    suspend fun saveBarcode(barcode: Barcode) {
        val now = System.currentTimeMillis()

        when (barcode.valueType) {

            Barcode.TYPE_PHONE -> {
                val phone = barcode.phone
                val data = mapOf(
                    "number" to (phone?.number ?: ""),
                    "type" to (phone?.type ?: 0),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.PHONE,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_ISBN -> {
                val data = mapOf(
                    "isbn" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.ISBN,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_EMAIL -> {
                val email = barcode.email
                val data = mapOf(
                    "address" to (email?.address ?: ""),
                    "subject" to (email?.subject ?: ""),
                    "body" to (email?.body ?: ""),
                    "type" to (email?.type ?: 0),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.EMAIL,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_TEXT -> {
                val data = mapOf(
                    "text" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.TEXT,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_SMS -> {
                val sms = barcode.sms
                val data = mapOf(
                    "phoneNumber" to (sms?.phoneNumber ?: ""),
                    "message" to (sms?.message ?: ""),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.SMS,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_URL -> {
                val url = barcode.url
                val data = mapOf(
                    "title" to (url?.title ?: ""),
                    "url" to (url?.url ?: barcode.rawValue.orEmpty())
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.URL,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_WIFI -> {
                val wifi = barcode.wifi
                val data = mapOf(
                    "ssid" to (wifi?.ssid ?: ""),
                    "password" to (wifi?.password ?: ""),
                    "encryptionType" to (wifi?.encryptionType ?: 0),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.WIFI,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_PRODUCT -> {
                val data = mapOf(
                    "productId" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.PRODUCT,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_CONTACT_INFO -> {
                val contact = barcode.contactInfo
                val name = contact?.name

                val data = mapOf(
                    "name" to (name?.formattedName ?: ""),
                    "firstName" to (name?.first ?: ""),
                    "lastName" to (name?.last ?: ""),
                    "organization" to (contact?.organization ?: ""),
                    "title" to (contact?.title ?: ""),
                    "phones" to (contact?.phones?.joinToString { it.number ?: "" } ?: ""),
                    "emails" to (contact?.emails?.joinToString { it.address ?: "" } ?: ""),
                    "addresses" to (contact?.addresses?.joinToString { it.addressLines?.joinToString() ?: "" } ?: ""),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.CONTACT,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                val event = barcode.calendarEvent
                val data = mapOf(
                    "summary" to (event?.summary ?: ""),
                    "description" to (event?.description ?: ""),
                    "location" to (event?.location ?: ""),
                    "organizer" to (event?.organizer ?: ""),
                    "status" to (event?.status ?: ""),
                    "start" to (event?.start?.rawValue ?: ""),
                    "end" to (event?.end?.rawValue ?: ""),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.EVENT,
                        data = data,
                        createdAt = now
                    )
                )
            }

            Barcode.TYPE_UNKNOWN,
            Barcode.TYPE_GEO-> {
                val geo = barcode.geoPoint
                val data = mapOf(
                    "lat" to (geo?.lat ?: 0.0),
                    "lng" to (geo?.lng ?: 0.0),
                    "rawValue" to (barcode.rawValue ?: "")
                )
                qrCodeDao.insert(
                    QRCodeEntity(
                        type = QRType.LOCATION,
                        data = data,
                        createdAt = now
                    )
                )
            }
        }
    }


}
