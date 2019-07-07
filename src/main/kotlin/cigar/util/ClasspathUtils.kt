package cigar.util

import cigar.Cigar
import java.io.InputStream
import java.net.URL


fun classpathResource(path: String): URL = Cigar::class.java.classLoader.getResource(path) ?: error("Could not find classpath resource $path")

fun classpathResourceStream(path: String): InputStream = Cigar::class.java.classLoader.getResourceAsStream(path) ?: error("Could not find classpath resource $path")
