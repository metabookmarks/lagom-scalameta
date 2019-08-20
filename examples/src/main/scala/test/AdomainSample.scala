package test

import io.metabookmarks.lagom.domain.Event

@Event
sealed trait Model {
  def name: String
}

@Event
case class Person(name: String) extends Model

