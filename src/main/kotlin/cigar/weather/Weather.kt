package cigar.weather

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.time.LocalTime
import javax.xml.parsers.DocumentBuilderFactory


object Weather {

    // List
    // http://dd.weatheroffice.ec.gc.ca/citypage_weather/xml/siteList.xml
    // Example
    // http://dd.weatheroffice.ec.gc.ca/citypage_weather/xml/BC/s0000141_e.xml
    const val VANCOUVER_CODE = "BC/s0000141"

    fun getWeatherUrl(provinceAndCode: String): String =
            "http://dd.weatheroffice.ec.gc.ca/citypage_weather/xml/${provinceAndCode}_e.xml"

    fun fetch(provinceAndCode: String): CityWeather? {
        val url = getWeatherUrl(provinceAndCode)
        return try {
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url)
            doc.documentElement.normalize()

            fun NodeList.findFirstDateTime(filter: (Element) -> Boolean = { true }): Element? {
                return (0 until this.length).map(this::item).map { it as Element }
                        .firstOrNull {
                            it.getAttribute("zone")?.startsWith("UTC") == false && filter(it)
                        } ?: (this.item(0) as Element)
            }

            val currentConditions: Element = doc.getElementsByTagName("currentConditions").item(0) as Element
            val dateTimes = currentConditions.getElementsByTagName("dateTime")
            val riseSet = doc.getElementsByTagName("riseSet").item(0) as Element
            val timeSummary: String = dateTimes.findFirstDateTime()?.getElementsByTagName("textSummary")?.item(
                    0)?.textContent ?: "???"
            val pressure = currentConditions.getElementsByTagName("pressure").item(0)
            val wind = currentConditions.getElementsByTagName("wind").item(0) as Element
            val forecastGroup: Element = doc.getElementsByTagName("forecastGroup").item(0) as Element
            val forecastElements = forecastGroup.getElementsByTagName("forecast")
            val sunrise: LocalTime = riseSet.getElementsByTagName("dateTime").findFirstDateTime {
                it.getAttribute("name") == "sunrise"
            }?.let {
                LocalTime.of(it.getElementsByTagName("hour").item(0).textContent.toInt(),
                             it.getElementsByTagName("minute").item(0).textContent.toInt())
            } ?: LocalTime.NOON
            val sunset: LocalTime = riseSet.getElementsByTagName("dateTime").findFirstDateTime {
                it.getAttribute("name") == "sunset"
            }?.let {
                LocalTime.of(it.getElementsByTagName("hour").item(0).textContent.toInt(),
                             it.getElementsByTagName("minute").item(0).textContent.toInt())
            } ?: LocalTime.of(23, 59)

            val conditions = Conditions(timeSummary,
                                        currentConditions.getElementsByTagName("condition").item(0).textContent,
                                        (doc.getElementsByTagName("warnings").item(0) as Element).getElementsByTagName(
                                                "event").let { nl ->
                                            (0 until nl.length).map(nl::item).map {
                                                (it as Element).getAttribute("description")?.trim() ?: "???"
                                            }
                                        },
                                        currentConditions.getElementsByTagName("temperature").item(0).textContent,
                                        currentConditions.getElementsByTagName("dewpoint").item(0).textContent,
                                        pressure.textContent,
                                        pressure.attributes.getNamedItem("tendency").textContent,
                                        pressure.attributes.getNamedItem("change").textContent,
                                        currentConditions.getElementsByTagName("visibility").item(0).textContent,
                                        currentConditions.getElementsByTagName("relativeHumidity").item(0).textContent,
                                        wind.getElementsByTagName("speed").item(0).textContent ?: "",
                                        wind.getElementsByTagName("gust").item(0).textContent ?: "",
                                        wind.getElementsByTagName("direction").item(0).textContent ?: "",
                                        wind.getElementsByTagName("bearing").item(0).textContent ?: ""
                                       )
            val forecasts: List<Forecast> = (0 until forecastElements.length).map(forecastElements::item)
                    .map { it as Element }
                    .map {
                        Forecast(
                                (it.getElementsByTagName("period").item(0) as Element).getAttribute("textForecastName"),
                                it.getElementsByTagName("textSummary").item(0).textContent,
                                (it.getElementsByTagName("temperatures").item(0) as Element).getElementsByTagName(
                                        "textSummary").item(0).textContent,
                                (it.getElementsByTagName("temperatures").item(0) as Element).getElementsByTagName(
                                        "temperature").item(0).textContent,
                                ((it.getElementsByTagName("temperatures").item(0) as Element).getElementsByTagName(
                                        "temperature").item(0) as Element).getAttribute("class") ?: "",
                                (it.getElementsByTagName("abbreviatedForecast").item(
                                        0) as Element).getElementsByTagName("textSummary").item(0).textContent,
                                (it.getElementsByTagName("abbreviatedForecast").item(
                                        0) as Element).getElementsByTagName("pop").item(0).textContent ?: ""
                                )
                    }
            CityWeather(conditions, forecasts, sunrise, sunset)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}