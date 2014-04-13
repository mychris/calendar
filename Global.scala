import play.api._
import java.util.TimeZone

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started.")
    Logger.info("Setting default java.util.TimeZone to \"UTC\".")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
}