package cigar.util

import java.awt.image.BufferedImage
import javax.imageio.ImageIO


fun BufferedImage.convertImageToType(type: Int = BufferedImage.TYPE_BYTE_BINARY): BufferedImage {
    val newImage = BufferedImage(width, height, type)
    val graphics = newImage.createGraphics()
    graphics.drawImage(this, 0, 0, width, height, null)
    graphics.dispose()
    return newImage
}

fun classpathImage(path: String, type: Int = BufferedImage.TYPE_BYTE_BINARY): BufferedImage {
    return ImageIO.read(classpathResource(path)).convertImageToType()
}