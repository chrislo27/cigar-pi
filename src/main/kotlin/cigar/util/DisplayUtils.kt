package cigar.util

import dburyak.pi.ssd1306.Display
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage


fun Display.text(font: Font, text: String, x: Int, y: Int, align: TextAlign, on: Boolean = true) {
    val g = this.graphics()
    val bounds = g.getFontMetrics(font).getStringBounds(text, g)
    g.font = font
    g.color = if (on) Color.WHITE else Color.BLACK
    val drawX: Int = when (align) {
        TextAlign.LEFT -> x
        TextAlign.CENTRE -> (x - bounds.width / 2).toInt()
        TextAlign.RIGHT -> (x - bounds.width).toInt()
    }
    g.drawString(text, drawX, y + bounds.height.toInt() - 1)
}

fun Display.fillRect(x: Int, y: Int, w: Int, h: Int, on: Boolean) {
    val g = this.graphics()
    g.color = if (on) Color.WHITE else Color.BLACK
    g.fillRect(x, y, w, h)
}

fun Display.drawRect(x: Int, y: Int, w: Int, h: Int, on: Boolean) {
    val g = this.graphics()
    g.color = if (on) Color.WHITE else Color.BLACK
    g.drawRect(x, y, w, h)
}

fun Display.image(img: BufferedImage, x: Int, y: Int, w: Int, h: Int) {
    this.image(img, Display.Position.of(x, y), Display.Position.of(x + w, y + h))
}

fun Display.image(img: BufferedImage, x: Int, y: Int) {
    this.image(img, x, y, img.width, img.height)
}
