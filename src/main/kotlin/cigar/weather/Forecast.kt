package cigar.weather

data class Forecast(val period: String, val textSummary: String,
                    val tempSummary: String, val temperature: String, val tempHighLow: String,
                    val abbreviated: String, val pop: String) {

    fun toHumanReadable(): String {
        return "$period: $textSummary"
    }

}
