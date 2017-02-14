package com.film42.forecastioapi

import java.net.URL
import java.util.Date

import com.eclipsesource.json.JsonObject
import com.film42.forecastioapi.extras.LocationPoint
import com.film42.forecastioapi.model.ForecastJsonProtocol._
import com.film42.forecastioapi.model._
import org.apache.commons.io.IOUtils
import spray.json._

import scala.Option.empty
import scala.util.Try

case class ForecastIO(apiKey: String, units: String = "si") {

  def forecast(apiKey: String, lat: String, lon: String, date: Option[Date]): Try[Forecast] = {
    Try( new Forecast(apiKey, lat, lon, units, date) )
  }

  def forecast(lat: String, lon: String, date: Date): Try[Forecast] = {
    forecast(apiKey, lat, lon, Some(date))
  }

  def forecast(lat: String, lon: String): Try[Forecast] = {
    forecast(apiKey, lat, lon, empty)
  }

  def forecast(location: LocationPoint): Try[Forecast] = {
    forecast(apiKey, location.lat, location.lon, empty)
  }

  def forecast(location: LocationPoint, date: Date): Try[Forecast] = {
    forecast(apiKey, location.lat, location.lon, Some(date))
  }

}

class Forecast(apiKey: String, lat: String, lon: String, units: String, date: Option[Date]) {

  // Timestamp constructor
  def this(apiKey: String, lat: String, lon: String, units: String, timestamp: Long) =
    this(apiKey, lat, lon, units, Some(new Date(timestamp * 1000L)))

  private val forecastBaseUrl = "https://api.darksky.net/forecast";
  private val forecastJson = getForecast.asJsObject

  private def getForecast = {
    val u = {
      if (date.nonEmpty) {
        val ts = date.get.getTime / 1000
        new URL(s"$forecastBaseUrl/$apiKey/$lat,$lon,$ts?units=$units")
      } else {
        new URL(s"$forecastBaseUrl/$apiKey/$lat,$lon?units=$units")
      }
    }
    try {
      new String(IOUtils.toByteArray(u.openStream())).parseJson
    } catch {
      case e: Exception => throw new Exception(e.getMessage)
    }
  }

  def latitude: String = lat

  def longitude: String = lon

  def datetime: Date = if (date.isDefined) date.get else new Date()

  def time: Long = { date.get.getTime / 1000L }

  def timezone: String = {
    forecastJson.getFields("timezone")(0).convertTo[String]
  }

  def offset: Int = {
    forecastJson.getFields("offset")(0).convertTo[Int]
  }

  def currently: CurrentDataPoint = {
    forecastJson.getFields("currently")(0).convertTo[CurrentDataPoint]
  }

  def minutely: Minutely = {
    forecastJson.getFields("minutely")(0).convertTo[Minutely]
  }

  def hourly: Hourly = {
    forecastJson.getFields("hourly")(0).convertTo[Hourly]
  }

  def flags: Flags = {
    // Use separate json parser until Case Class limit is lifted
    val jsonString = forecastJson.getFields("flags")(0).toJson.toString()
    val json = JsonObject.readFrom(jsonString)
    new Flags(json)
  }

  def daily: Daily = {
    // Use separate json parser until Case Class limit is lifted
    val jsonString = forecastJson.getFields("daily")(0).toJson.toString()
    val json = JsonObject.readFrom(jsonString)
    new Daily(json)
  }

  def alerts: Array[Alert] = {
    val size = forecastJson.getFields("alerts").size
    if(size == 0) return Array()

    val a = forecastJson.getFields("alerts")(0).convertTo[Alerts]
    a.alerts
  }

}
