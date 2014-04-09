package views.helper.bootstrap

import views.html.helper.bootstrap._
import views.html.helper.FieldConstructor

/**
 * https://github.com/playframework/Play20/tree/master/framework/src/play/src/main/scala/views/helper
 */
object LoginField {
  implicit val bootstrapConstructor = FieldConstructor(loginField.f)
}