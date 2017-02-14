package com.film42.forecastioapi.extras

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, TimeZone}

import com.film42.forecastioapi.Forecast
import com.github.tototoshi.csv.CSVWriter

import scala.collection.mutable.ListBuffer

object CSVSerializer {

  val header = List(
    "latitude", "longitude", "created_at_datetime", "forecast_datetime",
    "summary", "icon", "precipIntensity", "precipProbability",
    "temperature", "apparentTemperature", "dewPoint", "humidity",
    "windSpeed", "windBearing", "visibility", "cloudCover",
    "pressure", "ozone"
  )

  def serialize(forecast: Forecast, filename: String) {

    val fromTime: Calendar = Calendar.getInstance
    fromTime.setTimeZone(TimeZone.getTimeZone(forecast.timezone))

    val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

    val rows = forecast.hourly.data.map(hour => {
      fromTime.setTimeInMillis(hour.time*1000);
      val row = new ListBuffer[Any]
      row += forecast.latitude
      row += forecast.longitude
      row += sdf.format(forecast.datetime)
      row += sdf.format(new Date(hour.time * 1000))
      row += hour.summary
      row += hour.icon
      row += hour.precipIntensity.getOrElse("")
      row += hour.precipProbability.getOrElse("")
      row += hour.temperature.getOrElse("")
      row += hour.apparentTemperature.getOrElse("")
      row += hour.dewPoint.getOrElse("")
      row += hour.humidity.getOrElse("")
      row += hour.windSpeed.getOrElse("")
      row += hour.windBearing.getOrElse("")
      row += hour.visibility.getOrElse("")
      row += hour.cloudCover.getOrElse("")
      row += hour.pressure.getOrElse("")
      row += hour.ozone.getOrElse("")
      row.toList
    }).toList

    val outputFile = new File("target/" + filename + ".csv")
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(rows)
    writer.close

    println("wrote file: " + outputFile.getAbsolutePath)
  }

}
