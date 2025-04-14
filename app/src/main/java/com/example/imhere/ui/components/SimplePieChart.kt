import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun SimplePieChart(
    data: List<Float>,
    colors: List<Color>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val total = data.sum()
    val angles = data.map { 360f * it / total }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = modifier) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val diameter = min(canvasWidth, canvasHeight)
            val radius = diameter / 2
            val center = Offset(canvasWidth / 2, canvasHeight / 2)

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 50f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                setShadowLayer(6f, 2f, 2f, android.graphics.Color.BLACK)
            }

            var startAngle = 0f
            angles.forEachIndexed { index, sweepAngle ->
                // Draw arc
                drawArc(
                    color = colors.getOrElse(index) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(diameter, diameter),
                )

                // Calculate label angle
                val angleInRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val labelRadius = radius * 0.6f // distance from center

                val labelX = center.x + cos(angleInRad).toFloat() * labelRadius
                val labelY = center.y + sin(angleInRad).toFloat() * labelRadius

                val percent = (data[index] / total * 100).roundToInt()

                // Draw text label
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "$percent%",
                        labelX,
                        labelY + 10, // vertically adjust text
                        textPaint
                    )
                }

                startAngle += sweepAngle
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            labels.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors.getOrElse(index) { Color.Gray }, shape = CircleShape)
                    )
                    Text(text = label, fontSize = 14.sp)
                }
            }
        }
    }
}
