package util

import scala.util.matching.Regex

/**
  *
  * @author Simon Kaltenbacher
  * @author Florian Liebhart
  */
object Color {

  val HexCode = """(#[0-9a-zA-Z]{6})""".r

  def parse(code: String) = code match {
    case HexCode(code) => new Color(code)
    case _             => throw new IllegalArgumentException("Not a valid hex color code!")
  }

  def unapply(code: String) = code match {
    case HexCode(code) => Some(new Color(code))
    case _             => None
  }

  /** Google calendar color matrix */
  val colors = Vector(
    Color.parse("#AC725E"),
    Color.parse("#D06B64"),
    Color.parse("#F83A22"),
    Color.parse("#FA573C"),
    Color.parse("#FF7537"),
    Color.parse("#FFAD46"),
    Color.parse("#42D692"),
    Color.parse("#16A765"),
    Color.parse("#7BD148"),
    Color.parse("#B3DC6C"),
    Color.parse("#FBE983"),
    Color.parse("#FAD165"),
    Color.parse("#92E1C0"),
    Color.parse("#9FE1E7"),
    Color.parse("#9FC6E7"),
    Color.parse("#4986E7"),
    Color.parse("#9A9CFF"),
    Color.parse("#B99AFF"),
    Color.parse("#C2C2C2"),
    Color.parse("#CABDBF"),
    Color.parse("#CCA6AC"),
    Color.parse("#F691B2"),
    Color.parse("#CD74E6"),
    Color.parse("#A47AE2")
  ) 
}

/**
  *
  * @author Simon Kaltenbacher
  */
class Color private(val code: String)