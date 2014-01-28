package dolphin

import scala.language.experimental.macros

trait Dolphin {
  def flipper[T,U](featureName: String, config: QueryableConfig)(block: T => U): U = macro flipperImpl
}

object DolphinImpl extends Dolphin {
  def flipperImpl[T,U](featureName: String, config: QueryableConfig)(block: T => U): U = {
    import c.universe._
    reify(Some("TODO"))
  }
}