package cigar

import cigar.test.SineWaveProgram
import cigar.ui.Menu
import cigar.ui.MenuController
import cigar.ui.MenuItem
import cigar.util.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import java.time.LocalDateTime


class HomeMenu(manager: Manager) : Program(manager) {

    override val title: String = "Home Menu"

    private var returnToText: ScrollingText? = null
    private var menuController: MenuController? = null
    private val useStartMenuHint = ScrollingText(display, "Use the Start Menu (TAB) to go into programs", display.width(), 11, TextAlign.CENTRE)

    override fun show() {
        super.show()
        updateReturnTo()
    }
    
    private fun updateReturnTo() {
        val lastSus = manager.getProgramTag(manager.lastProgramID)?.takeUnless { it == manager.homeMenuProgramTag }
        if (lastSus != null) {
            returnToText = ScrollingText(display, lastSus.program.title, 75, 11, TextAlign.LEFT, font = manager.font3, startingX = 30, wrapDistance = 35)
        } else {
            returnToText = null
        }
    }

    private fun createStartMenu(): MenuController {
        val startMenu = Menu("Start Menu").apply {
            this.items += Menu("Programs").apply {
                this.items += MenuItem("Sinusoidal Wave").apply {
                    this.onSelect = {
                        val firstInstance = manager.allProgramTags().find { it.program is SineWaveProgram }
                        manager.switchToProgram((firstInstance ?: manager.spawnProgram(SineWaveProgram(manager))).id)
                        menuController = null
                    }
                }
            }
            this.items += MenuItem("Running Prgms").apply {
                this.onSelect = {
                    menuController = MenuController(this@HomeMenu, Menu("Running Programs").apply {
                        val programs = manager.allProgramTags()
                        if (programs.isEmpty()) {
                            this.items += MenuItem("<no running programs>")
                        } else {
                            programs.sortedBy { pt -> pt.program.title }.mapTo(this.items) { pt ->
                                MenuItem(pt.program.title).apply {
                                    this.onSelect = {
                                        manager.switchToProgram(pt.id)
                                        menuController = null
                                    }
                                }
                            }
                        }
                    }).apply {
                        this.onCancel = {
                            menuController = null
                        }
                        this.onExit = {
                            menuController = null
                        }
                    }
                }
            }
            this.items += MenuItem("Kill Prgms").apply {
                this.onSelect = {
                    menuController = MenuController(this@HomeMenu, Menu("Kill Programs").apply menu@{
                        val programs = manager.allProgramTags()
                        if (programs.isEmpty()) {
                            this.items += MenuItem("<no running programs>")
                        } else {
                            programs.sortedBy { pt -> pt.program.title }.mapTo(this.items) { pt ->
                                MenuItem(pt.program.title).apply {
                                    this.onSelect = {
                                        manager.killProgram(pt.id)
                                        menuController = null
                                        this@menu.items.remove(this)
                                        if (this@menu.items.isNotEmpty()) {
                                            this@menu.itemIndex = this@menu.itemIndex.coerceIn(0, this@menu.items.size - 1)
                                        }
                                        updateReturnTo()
                                    }
                                }
                            }
                        }
                    }).apply {
                        this.onCancel = {
                            menuController = null
                        }
                        this.onExit = {
                            menuController = null
                        }
                    }
                }
            }
            this.items += Menu("Cigar Sys.").apply {
                this.items += MenuItem("Crash!").apply {
                    this.onSelect = {
                        error("test error")
                    }
                }
            }
        }
        return MenuController(this, startMenu).apply {
            this.onCancel = {
                menuController = null
            }
            this.onExit = {
                menuController = null
            }
        }
    }

    override fun cycle(delta: Double, doRendering: Boolean, keystroke: KeyStroke?) {
        if (doRendering) {
            if (keystroke != null && keystroke.keyType == KeyType.Tab && menuController == null) {
                menuController = createStartMenu()
            }

            display.clear()
            val now = LocalDateTime.now()
            display.text(manager.font3, "${now.year}/${now.monthValue.toString().padStart(2, '0')}/${now.dayOfMonth.toString().padStart(2, '0')}",
                    0, 0, TextAlign.LEFT)
            display.text(manager.font3, "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}",
                    display.width(), 0, TextAlign.RIGHT)
            display.image(AssetRegistry["icon_home"], display.width() / 2 - 5, 0)

//            display.image(AssetRegistry["icon_tab"], 0, 11)
            display.text(manager.font3, "TAB: Start Menu", 0, 11, TextAlign.LEFT)

            if (returnToText != null) {
                display.text(manager.font3, "^B: Rtn to ", 0, 22, TextAlign.LEFT)
                returnToText?.draw(delta, display.width() - (returnToText?.width ?: 0), 22)
            }

            val menu = menuController
            if (menu != null) {
                menu.cycle(delta, keystroke)
            } else {
                useStartMenuHint.draw(delta, 0, 22)
            }

            display.sync()
        }
    }
}