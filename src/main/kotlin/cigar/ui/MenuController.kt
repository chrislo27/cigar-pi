package cigar.ui

import cigar.Program
import cigar.util.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import java.util.*
import kotlin.math.roundToInt


class MenuController(val program: Program, firstMenu: Menu) {

    val menuDeque: Deque<Menu> = ArrayDeque()
    var onCancel: MenuController.() -> Unit = {}
    var onExit: MenuController.() -> Unit = {}

    private val maxItemsWindow: Int = 3
    private var scrollingTexts: List<ScrollingText> = listOf()
    private var scrollingTextsFor: Menu? = firstMenu
    private val itemHeight: Int = 11

    init {
        menuDeque.push(firstMenu)
        initTexts()
    }

    private fun initTexts() {
        scrollingTexts = listOf()
        val currentMenu = menuDeque.peekFirst() ?: return
        val display = program.display
        val font = program.manager.font3
        val menuItems: List<Triple<MenuItem, String, Int>> = currentMenu.items.map {
            val moddedText: String = if (it is Menu) "${it.text} >" else it.text
            Triple(it, moddedText, display.graphics().getFontMetrics(font).getStringBounds(moddedText, display.graphics()).width.roundToInt())
        }
        val longestMenuItem = menuItems.maxBy { it.third } ?: return
        val maxWidth = longestMenuItem.third.coerceIn(4, (display.width() * 0.85f).roundToInt() - 4)
        scrollingTexts = menuItems.map {
            ScrollingText(display, it.second, maxWidth, itemHeight - 1, TextAlign.LEFT, font = font, wrapDistance = maxWidth / 4, startingX = maxWidth / 3)
        }
        scrollingTextsFor = currentMenu
    }

    fun cycle(delta: Double, keystroke: KeyStroke?) {
        val display = program.display
        val manager = program.manager
        val currentMenu = menuDeque.peekFirst() ?: return
        if (scrollingTextsFor != currentMenu) {
            initTexts()
        }

        if (currentMenu.items.isEmpty() || scrollingTexts.isEmpty()) {
            display.fillRect(0, 0, display.width(), itemHeight, false)
            display.text(manager.font3, "<nothing in ${currentMenu.text}>", 0, 1, TextAlign.LEFT)
            display.drawRect(0, 0, display.width(), itemHeight, true)
        } else {
            val startIndex: Int = (currentMenu.itemIndex - 1).coerceAtLeast(0)
            for ((rowIndex, itemIndex) in (startIndex.coerceAtMost(currentMenu.items.size - maxItemsWindow).coerceAtLeast(0) until (startIndex + maxItemsWindow).coerceAtMost(currentMenu.items.size)).withIndex()) {
//                val item = currentMenu.items[itemIndex]
                val isHighlighted = itemIndex == currentMenu.itemIndex
                val scrollingText = scrollingTexts[itemIndex]
                val width = scrollingText.width + 4
                val y = itemHeight * rowIndex
                display.fillRect(0, y, width, itemHeight, isHighlighted)
                scrollingText.draw(delta, 2, y, invertColors = isHighlighted)
                display.drawRect(0, y, width, itemHeight, true)
            }
        }

        /*
        Controls:
        Up/Down will change the selected menu item
        Left/Right will go into/out of menus or activate their onSelect (if right)
        Escape will cancel the entire operation
        Spacebar/ENTER will go into menus or activate their onSelect
         */

        if (keystroke != null) {
            if (keystroke.keyType == KeyType.ArrowUp) {
                if (currentMenu.itemIndex > 0) {
                    currentMenu.itemIndex--
                }
            } else if (keystroke.keyType == KeyType.ArrowDown) {
                if (currentMenu.itemIndex < currentMenu.items.size - 1) {
                    currentMenu.itemIndex++
                }
            } else if (keystroke.keyType == KeyType.Escape) {
                this.onCancel()
            } else if (keystroke.keyType == KeyType.ArrowLeft) {
                if (menuDeque.size == 1) {
                    this.onCancel()
                } else {
                    menuDeque.pop()
                    initTexts()
                }
            } else if (keystroke.keyType == KeyType.ArrowRight || keystroke.character == ' ' || keystroke.keyType == KeyType.Enter) {
                currentMenu.items.getOrNull(currentMenu.itemIndex)?.onSelect?.invoke(this)
            }
        }
    }

}