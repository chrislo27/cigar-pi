package cigar

import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.terminal.Terminal
import dburyak.pi.ssd1306.Display


abstract class Program(val manager: Manager) {
    
    open val display: Display get() = manager.display
    open val terminal: Terminal get() = manager.terminal
    abstract val title: String
    
    abstract fun cycle(delta: Double, doRendering: Boolean, keystroke: KeyStroke?)
    
    open fun show() {
        
    }
    
    open fun hide() {
        
    }
    
    open fun dispose() {
        
    }
}