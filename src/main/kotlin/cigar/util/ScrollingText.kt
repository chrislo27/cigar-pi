package cigar.util

import dburyak.pi.ssd1306.Display
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class ScrollingText(val display: Display, val text: String, val width: Int, val height: Int,
                    val textAlign: TextAlign?,
                    val font: Font = Display.FONT_DEFAULT, var speed: Int = 64, var wrapDistance: Int = width,
                    startingX: Int = width) {

    private var currentX: Int = startingX
    private val buffer: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
    private val graphics: Graphics2D = buffer.createGraphics()
    private val textBounds: Rectangle2D = graphics.getFontMetrics(font).getStringBounds(text, graphics)

    fun draw(delta: Double, x: Int, y: Int, w: Int = width, h: Int = height,
             overlayType: Display.OverlayType = Display.OverlayType.FULL, invertColors: Boolean = false) {
        val textWidth: Int = textBounds.width.roundToInt()
        val textHeight: Int = textBounds.height.roundToInt()

        graphics.color = if (!invertColors) Color.BLACK else Color.WHITE
        graphics.fillRect(0, 0, width, height)

        graphics.font = font
        graphics.color = if (invertColors) Color.BLACK else Color.WHITE
        if (textAlign == null || textWidth > width) {
            graphics.drawString(text, currentX, textHeight - 1)
            graphics.drawString(text, currentX + textWidth + wrapDistance, textHeight - 1)
        } else {
            val startX: Int = when (textAlign) {
                TextAlign.LEFT -> 0
                TextAlign.CENTRE -> width / 2 - textWidth / 2
                TextAlign.RIGHT -> width - textWidth
            }
            graphics.drawString(text, startX, textHeight - 1)
        }

        currentX -= (speed * delta).roundToInt().absoluteValue
        while (currentX <= -textWidth) {
            currentX += textWidth + wrapDistance
        }

        display.image(buffer, Display.Position.of(x, y), Display.Position.of(x + w, y + h), overlayType)
    }

}