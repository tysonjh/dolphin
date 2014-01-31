package dolphin.immutable

import scala.reflect.macros.Context
import scala.language.experimental.macros

/**
 * Object containing the flipper macros.
 */
object Macro {
  /**
   * Macro that surrounds `fn` with an <code>if</code> construct that
   * branches based on the `enableFeature` parameter.
   * @param enableFeature is the feature on (true) or off (false)
   * @param fn the function to execute if feature is enabled
   * @return a Unit
   */
  def flipper[T](enableFeature: Boolean)
                (fn: => T): Unit = macro MacroImpl.flipper[T]

  /**
   * Macro that surrounds `fn` with an <code>if</code> construct that
   * branches based on the `enableFeature` parameter
   * and returns a default value if the feature is disabled.
   * @param enableFeature is the feature on (true) or off (false)
   * @param fn the function to execute if feature is enabled
   * @param default the default function to execute if feature is disabled
   * @return the result of calling `fn` or `default`
   */
  def flipperWithDefault[T](enableFeature: Boolean)
                           (fn: => T)(default: => T): T = macro MacroImpl.flipperWithDefault[T]
}

object MacroImpl {
  def flipper[T](c: Context)
                (enableFeature: c.Expr[Boolean])
                (fn: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._
    val result = q"if($enableFeature) $fn else {}"
    c.Expr[Unit](result)
  }

  def flipperWithDefault[T](c: Context)
                           (enableFeature: c.Expr[Boolean])
                           (fn: c.Expr[T])(default: c.Expr[T]) = {
    import c.universe._
    val result = q"if($enableFeature) $fn else $default"
    c.Expr[T](result)
  }
}