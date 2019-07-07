package cigar

import cigar.init.AssetLoader
import cigar.util.AssetRegistry
import cigar.util.Version
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.terminal.ansi.UnixTerminal
import dburyak.pi.ssd1306.Display
import dburyak.pi.ssd1306.SSD1306_I2C
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.concurrent.thread


object Cigar {
    
    val VERSION: Version = Version(0, 1, 0, "DEV")
    val LOGGER: Logger = LogManager.getContext(Cigar::class.java.classLoader, false).getLogger("Cigar")
    
    @JvmStatic
    fun main(args: Array<String>) {
        LOGGER.info("Starting Cigar $VERSION...")

        val display: Display = Display(Display.Dimensions.W128_H32, SSD1306_I2C.newInstance(1, 0x3C))

        display.stopScroll()
        display.begin()
        display.dim(true)
        LOGGER.info("Display initialized")
        
        val terminal: Terminal = UnixTerminal()
        
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            AssetRegistry.dispose()
//            display.stop()
        })
        
        AssetLoader()
        
        val manager = Manager(args.toList(), display, terminal)
        manager.mainLoop()
    }
    
}