package cigar.ui

open class MenuItem(var text: String) {
    
    var onSelect: (controller: MenuController) -> Unit = {}
    
}
