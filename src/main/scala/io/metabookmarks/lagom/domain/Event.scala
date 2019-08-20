/*
 * Copyright 2019 Olivier NOUGUIER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.metabookmarks.lagom.domain

import scala.reflect.macros._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.util.{ Failure, Success, Try }

@compileTimeOnly("@io.metabookmarks.lagom.domain.Event not expanded")
class Event extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro EventMacro.impl
}

object EventMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

    import c.universe.{ Try => _, _ }

    def modifiedClass(classDecl: ClassDef) =
      Try {
        val q"..$mods class $name[..$tparams] $tpcons(...$attr) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =
          classDecl
        (mods, name, attr, tparams, tpcons, earlydefns, parents, self, stats)
      } match {
        case Failure(_) =>
          c.abort(c.enclosingPosition, "Annotation is only supported on objects")
        case Success((mods, name, attr, tparams, tpcons, earlydefns, parents, self, stats)) =>
          c.Expr(q"""
            $mods class ${name.toTypeName}[..$tparams] $tpcons(...$attr) extends { ..$earlydefns } with ..$parents { $self => ..$stats }
            object ${name.toTermName} {
            import play.api.libs.json._
            implicit val format: Format[${name.toTypeName}] = Json.format
            }

            """)
      }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil =>
        modifiedClass(classDecl)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
