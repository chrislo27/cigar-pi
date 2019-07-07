package cigar.weather


data class Conditions(val time: String, val condition: String, val warnings: List<String>,
                 val temperature: String, val dewpoint: String,
                 val pressure: String, val pressureTendency: String, val pressureChange: String,
                 val visibility: String,
                 val humidity: String,
                 val wind: String, val windGust: String, val windDirection: String, val windBearing: String)