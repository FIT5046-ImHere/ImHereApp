import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun QrCode(
    modifier: Modifier = Modifier,
    data: String,
    size: Int = 512,
) {
    val qrBitmap = remember(data) { generateQrCodeBitmap(data, size) }

    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
        )
    }
}

fun generateQrCodeBitmap(data: String, size: Int): Bitmap? {
    return try {
        val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
        BarcodeEncoder().createBitmap(bitMatrix)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
