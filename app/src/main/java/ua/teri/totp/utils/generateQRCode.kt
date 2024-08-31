package ua.teri.totp.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

fun generateQRCode(text: String, width: Int, height: Int): Bitmap {
    val hints = hashMapOf<EncodeHintType, Any>(
        EncodeHintType.CHARACTER_SET to "UTF-8",
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
    )

    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text, BarcodeFormat.QR_CODE, width, height, hints
    )

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
        }
    }

    return bitmap
}
