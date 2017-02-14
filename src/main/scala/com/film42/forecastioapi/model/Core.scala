package com.film42.forecastioapi.model

import java.util.Date

import com.eclipsesource.json._
import spray.json._

import scala.util.Try

sealed trait DT { def datetime: Date }

case class Alert(title: String, time: Long, expires: Long, description: String, uri: String)
  extends DT { def datetime = new Date(time * 1000L) }
case class Alerts(alerts: Array[Alert])

case class MinuteDataPoint(time: Long, precipIntensity: Double, precipProbability: Double)
  extends DT { def datetime = new Date(time * 1000L) }
case class Minutely(summary: String, icon: String, data: Array[MinuteDataPoint])

class Flags(json: JsonObject) {
  private def asStringArray(v: JsonValue) =
    v.asArray.values.toArray.map(x => x.toString)

  def sources: Array[String] = asStringArray( json.get("sources") )
  def station(source: String): Array[String] = {
    try asStringArray( json.get(s"$source-stations") )
    catch { case e: Exception => Array() }
  }
  def units: String = json.get("units").asString
}

case class CurrentDataPoint(
  time: Long,
  summary: String,
  icon: String,
  nearestStormDistance: Option[Double],
  nearestStormBearing: Option[Double],
  precipIntensity: Option[Double],
  precipProbability: Option[Double],
  temperature: Option[Double],
  apparentTemperature: Option[Double],
  dewPoint: Option[Double],
  humidity: Option[Double],
  windSpeed: Option[Double],
  windBearing: Option[Double],
  visibility: Option[Double],
  cloudCover: Option[Double],
  pressure: Option[Double],
  ozone: Option[Double]) extends DT { def datetime = new Date(time * 1000L) }

case class HourDataPoint(
  time: Long,
  summary: String,
  icon: String,
  precipIntensity: Option[Double],
  precipProbability: Option[Double],
  temperature: Option[Double],
  apparentTemperature: Option[Double],
  dewPoint: Option[Double],
  humidity: Option[Double],
  windSpeed: Option[Double],
  windBearing: Option[Double],
  visibility: Option[Double],
  cloudCover: Option[Double],
  pressure: Option[Double],
  ozone: Option[Double]) extends DT { def datetime = new Date(time * 1000L) }

case class Hourly(
  summary: String,
  icon: String,
  data: Array[HourDataPoint])


/*
  Why is this so ugly? Because case class limits of 22, that's why!
 */
class Daily(json: JsonObject) {
  def summary: String =
    try json.get("summary").asString
    catch { case e: Exception => "" }
  def icon: String  =
    try json.get("icon").asString
    catch { case e: Exception => "" }
  def data: Array[DayDataPoint] = {
    val data = json.get("data").asArray.values.toArray
    data.map(x => new DayDataPoint(x.asInstanceOf[JsonValue].asObject))
  }
}

class DayDataPoint(json: JsonObject) extends DT {
  def time: Long = json.get("time").asLong
  def datetime = new Date(time * 1000L)
  def summary: String  = json.get("summary").asString
  def icon: String  = json.get("icon").asString
  def sunriseTime: Long = json.get("sunriseTime").asLong
  def sunsetTime: Long = json.get("sunsetTime").asLong
  def moonPhase: Option[Double] = Try(json.get("moonPhase").asDouble).toOption
  def precipIntensity: Option[Double] = Try(json.get("precipIntensity").asDouble).toOption
  def precipIntensityMax: Option[Double] = Try(json.get("precipIntensityMax").asDouble).toOption
  def precipProbability: Option[Double] = Try(json.get("precipProbability").asDouble).toOption
  def temperatureMin: Option[Double] = Try(json.get("temperatureMin").asDouble).toOption
  def temperatureMinTime: Option[Long] = Try(json.get("temperatureMinTime").asLong).toOption
  def temperatureMax: Option[Double] = Try(json.get("temperatureMax").asDouble).toOption
  def temperatureMaxTime: Option[Long] = Try(json.get("temperatureMaxTime").asLong).toOption
  def apparentTemperatureMin: Option[Double] = Try(json.get("apparentTemperatureMin").asDouble).toOption
  def apparentTemperatureMinTime: Option[Long] = Try(json.get("apparentTemperatureMinTime").asLong).toOption
  def apparentTemperatureMax: Option[Double] = Try(json.get("apparentTemperatureMax").asDouble).toOption
  def apparentTemperatureMaxTime: Option[Long] = Try(json.get("apparentTemperatureMaxTime").asLong).toOption
  def dewPoint: Option[Double] = Try(json.get("dewPoint").asDouble).toOption
  def humidity: Option[Double] = Try(json.get("humidity").asDouble).toOption
  def windSpeed: Option[Double] = Try(json.get("windSpeed").asDouble).toOption
  def windBearing: Option[Double] = Try(json.get("windBearing").asDouble).toOption
  def visibility: Option[Double] = Try(json.get("visibility").asDouble).toOption
  def cloudCover: Option[Double] = Try(json.get("cloudCover").asDouble).toOption
  def pressure: Option[Double] = Try(json.get("pressure").asDouble).toOption
  def ozone: Option[Double] = Try(json.get("ozone").asDouble).toOption
}

object ForecastJsonProtocol extends DefaultJsonProtocol {
  implicit val currentDataPointFormat = jsonFormat17(CurrentDataPoint)
  implicit val hourDataPointFormat = jsonFormat15(HourDataPoint)
  implicit val hourlyDataFormat = jsonFormat3(Hourly)
  implicit val alertDataFormat = jsonFormat5(Alert)
  implicit val minuteDataFormat = jsonFormat3(MinuteDataPoint)
  implicit val minutelyFormat = jsonFormat3(Minutely)

  // Root is an Array
  implicit object AlertsApiResultsFormat extends RootJsonFormat[Alerts] {
    def read(value: JsValue) = Alerts(value.convertTo[Array[Alert]])
    def write(obj: Alerts) = obj.alerts.toJson
  }
}