package cigar.weather

import java.time.LocalTime


class CityWeather(val conditions: Conditions, val forecasts: List<Forecast>, val sunrise: LocalTime, val sunset: LocalTime)
