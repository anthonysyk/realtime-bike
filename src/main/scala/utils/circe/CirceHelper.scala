package utils.circe

import io.circe
import io.circe.Json
import utils.circe
import utils.generic.Pure

import scala.reflect.ClassTag

object CirceHelper {

  implicit class EitherCirceEnricher[T](either: Either[io.circe.Error, T])(implicit pure: Pure[T]) {

    private implicit val pureString = new Pure[String] {
      override def empty: String = ""
    }

    private implicit val pureJson = new Pure[Json] {
      override def empty: Json = Json.Null
    }

    private implicit def pureArr[T: ClassTag] = new Pure[Array[T]] {
      override def empty: Array[T] = Array.empty[T]
    }

    def getRight: T = either.right.toOption.getOrElse(pure.empty)
  }

}
