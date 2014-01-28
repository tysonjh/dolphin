package dolphin

import scala.reflect.macros.Context
import scala.language.experimental.macros

trait Dolphin {
  def flipper[T](featureName: String, config: QueryableConfig)(fn: =>T): T = macro DolphinMacro.flipperImpl[T]

  def flipperOrDefault[T](featureName: String, config: QueryableConfig)(fn: =>T)(default: T): T = macro DolphinMacro.flipperOrDefaultImpl[T]
 }

object DolphinMacro {
  def flipperImpl[T](c: Context)
                    (featureName: c.Expr[String],
                     config: c.Expr[QueryableConfig])
                    (fn: c.Expr[T]): c.Expr[T] = {
    import c.universe._
    val result = {
      q"""
        if($config.isFeatureOn($featureName)) {
          ${fn.tree}
        } else {}
      """
    }
    c.Expr[T](result)
  }

  def flipperOrDefaultImpl[T](c: Context)
                               (featureName: c.Expr[String],
                                config: c.Expr[QueryableConfig])
                               (fn: c.Expr[T])(default: c.Expr[T]) = {
    import c.universe._
    val result = {
      q"""
        if($config.isFeatureOn($featureName)) {
          ${fn.tree}
        } else {
          ${default.tree}
        }
      """
    }
    c.Expr[T](result)
  }
}