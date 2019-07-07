package cigar.test

import cigar.Manager
import cigar.Program
import cigar.util.MathHelper
import com.googlecode.lanterna.input.KeyStroke
import java.awt.Color
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sin


class SineWaveProgram(manager: Manager) : Program(manager) {

    override val title: String = "Sinusoidal Wave"
    
    override fun cycle(delta: Double, doRendering: Boolean, keystroke: KeyStroke?) {
        if (doRendering) {
            val amplitude = 15f
            val period = 32f
            val scrollPeriod = 1f

            display.clear()
            val g = display.graphics()
            g.color = Color.WHITE
            var lastPlot = 0.0
            for (i in 0 until display.width()) {
                val plot = (sin((i / period + MathHelper.getSawtoothWave(scrollPeriod)) * Math.PI * 2) * amplitude)
                val height = if (i == 0) -1.0 else (lastPlot - plot)
                g.fillRect(i, display.height() / 2 + (if (height < 0) plot else lastPlot).roundToInt(), 1, height.roundToInt().absoluteValue)
                lastPlot = plot
            }
            display.sync()
        }
    }
}