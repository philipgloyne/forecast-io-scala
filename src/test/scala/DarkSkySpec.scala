import java.text.SimpleDateFormat
import java.util.{Calendar, Date, GregorianCalendar, TimeZone}

import com.film42.forecastioapi._
import com.film42.forecastioapi.extras.LocationPoint
import com.film42.forecastioapi.extras.CSVSerializer
import org.scalatest._
import org.scalatest.time.Day

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

class DarkSkySpec extends FunSpec {

  val apiKey = System.getenv("DARKSKY_SECRET_KEY")
  val bigBen = LocationPoint("51.510357", "-0.116773")
  val brisbane = LocationPoint("-27.470125","153.021072")

  describe("Starting the test suite") {
    it("should have a set apiKey system var"){
      assert(apiKey != null)
    }
  }

  describe("Test Output To CSV") {

    it("Can forecast") {

      val resp = ForecastIO(apiKey).forecast(bigBen)
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)

      val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
      CSVSerializer.serialize(forecast, "forecast-" + format.format(new Date()));

      println(forecast.currently.time + " " + forecast.currently.summary + " " + forecast.currently.temperature)
    }

    it("Can get historical weather") {

      val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
      var cal = new GregorianCalendar(2016,0,1)
      1.to(100).foreach(_ => {
        val day = cal.getTime
        println(day)
        val resp = ForecastIO(apiKey).forecast(bigBen, day)
        assert(resp.isInstanceOf[Success[Forecast]])
        val Success(forecast) = resp
        assert(forecast.currently.summary != null)

        CSVSerializer.serialize(forecast, "historical-" + format.format(day));

        cal.add(Calendar.DATE, 1)
      })
    }

    it ("test historical dates") {

      val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
      var cal = new GregorianCalendar(2016,0,1)
      cal.setTimeZone(TimeZone.getTimeZone("UTC"))
      0.to(1).foreach(_ => {
        println(cal.getTime.getTime / 1000)
        cal.add(Calendar.DATE, 1)
      })

    }

  }


}
