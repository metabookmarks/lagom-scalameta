package test

import io.metabookmarks.lagom.domain.Event

@Event
case class Person(name: String)
