package cigar.init

import cigar.util.AssetRegistry
import cigar.util.classpathImage


class AssetLoader {
    init {
        AssetRegistry["icon_home"] = classpathImage("icons/home.png")
        AssetRegistry["icon_tab"] = classpathImage("icons/tab.png")
    }
}