package org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef

import com.intellij.diagnostic.StartUpMeasurer.Level
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import org.jetbrains.plugins.scala.extensions.ResolvesTo
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiUtil
import org.jetbrains.plugins.scala.lang.psi.api.base.ScAnnotation
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScExpression
import org.jetbrains.plugins.scala.lang.psi.types.ScalaTypePresentation

class LagomJsonScalaMetaInjector extends SyntheticMembersInjector {

  private val Log = Logger.getInstance(classOf[LagomJsonScalaMetaInjector])

  Log.info(s"Injector loaded")

  private def hasJson(source: ScTypeDefinition): Boolean =
    source.findAnnotationNoAliases("io.metabookmarks.lagom.domain.Event") != null

  private def getJson(source: ScTypeDefinition): Option[ScAnnotation] =
    source.annotations("io.metabookmarks.lagom.domain.Event").headOption

  override def needsCompanionObject(source: ScTypeDefinition): Boolean = hasJson(source)


  // add implicits to case object / case class companions
  override def injectMembers(source: ScTypeDefinition): Seq[String] =
    source match {
      case cob: ScObject if hasJson(cob) =>
        genImplicits(cob.name + ScalaTypePresentation.ObjectTypeSuffix, getJson(cob))
      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScTypeDefinition if hasJson(clazz) =>
            genImplicits(clazz.name, getJson(clazz))
          case _ => Nil
        }
      case _ => Nil
    }

  private def genImplicits(clazz: String, ann: Option[ScAnnotation]): Seq[String] =
          Seq(s"""implicit val format: play.api.libs.json.Format[$clazz] = ???""")
}
