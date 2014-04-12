package util

/**
  *
  * @author Simon Kaltenbacher
  */
object Color {

  val HexCode = "#[0-9]{6}".r

	def parse(code: String) = code match {
		case HexCode(code) => new Color(code)
		case _					   => throw new IllegalArgumentException("Not a valid hex color code!")
	}

	def unapply(code: String) = code match {
		case HexCode(code) => Some(new Color(code))
		case _						 => None
	}
}

/**
  *
  * @author Simon Kaltenbacher
  */
class Color private(val code: String)