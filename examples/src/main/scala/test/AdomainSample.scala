package test

import io.metabookmarks.lagom.domain.Event

@Event
sealed trait Model {
  def name: String
}

@Event
final case class Person(name: String) extends Model

object Person {
  def apply(name: String): Person = new Person(name)
  def ide(i: Int): Int = 1
}