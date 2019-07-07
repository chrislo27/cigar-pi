package cigar.weather

import java.awt.image.BufferedImage
import javax.imageio.ImageIO


enum class WeatherIcon(val day: String, val night: String = day) {

    NO_DATA("nodata"),
    CLEAR("sun", "sun-night"),
    CLOUDY("cloud"), FOG("fog"),
    MAINLY_SUNNY("lightcloud", "lightcloud-night"),
    PARTLY_CLOUDY("partlycloud", "partlycloud-night"),
    INCREASING_CLOUDINESS("increasingcloudiness", "increasingcloudiness-night"),
    CLEARING("clearing", "clearing-night"),
    RAIN("rain"),
    LIGHT_RAIN("lightrain"), LIGHT_RAIN_SUN("lightrainsun", "lightrain"),
    LIGHT_RAIN_THUNDER("lightrainthunder"),
    THUNDERSTORM("rainthunder"),
    SNOW("snow"),
    SLEET("sleet");

    companion object {
        val VALUES: List<WeatherIcon> = values().toList()

        fun attemptMatch(conditions: String): WeatherIcon {
            val str = conditions.toLowerCase()

            return when {
                "clearing" in str -> CLEARING
                "increasing cloudiness" in str -> INCREASING_CLOUDINESS
                "clear" in str || "sunny" in str -> CLEAR
                "fog" in str || "haze" in str || "blowing" in str || "smoke" in str || "mist" in str -> FOG
                "partly cloudy" in str || "mostly cloudy" in str || "cloudy periods" in str || "a few clouds" in str -> PARTLY_CLOUDY
                "mainly sunny" in str || "mix of sun and cloud" in str -> MAINLY_SUNNY
                "cloudy" in str || "overcast" in str -> CLOUDY
                "freezing" in str -> SNOW
                "light rain" in str || "drizzle" in str -> LIGHT_RAIN
                "thunder" in str -> THUNDERSTORM
                "showers" in str -> LIGHT_RAIN_SUN
                "chance of thunder" in str -> LIGHT_RAIN_THUNDER
                "sleet" in str || "hail" in str -> SLEET
                "snow" in str || "flurries" in str || "blizzard" in str -> SNOW
                "rain" in str -> RAIN
                "not observed" in str -> NO_DATA
                else -> {
                    println("Didn't find icon for \"$conditions\"")
                    NO_DATA
                }
            }
        }
    }

    private fun BufferedImage.convertImageToType(type: Int = BufferedImage.TYPE_BYTE_BINARY): BufferedImage {
        val newImage = BufferedImage(width, height, type)
        val graphics = newImage.createGraphics()
        graphics.drawImage(this, 0, 0, width, height, null)
        graphics.dispose()
        return newImage
    }

    val imageDay: BufferedImage by lazy { ImageIO.read(this::class.java.classLoader.getResource("weathericons/$day.png")).convertImageToType() }
    val imageNight: BufferedImage by lazy {
        if (night == day) imageDay else ImageIO.read(this::class.java.classLoader.getResource("weathericons/$night.png")).convertImageToType()
    }

    fun getImage(day: Boolean): BufferedImage = if (day) imageDay else imageNight

}