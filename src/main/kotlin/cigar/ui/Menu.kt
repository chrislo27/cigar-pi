package cigar.ui


class Menu(text: String = "") : MenuItem(text) {
    
    val items: MutableList<MenuItem> = mutableListOf()
    var itemIndex: Int = 0
    
    init {
        onSelect = { controller ->  
            controller.menuDeque.push(this)
        }
    }
}