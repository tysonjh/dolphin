package dolphin.mutable

import scala.reflect.macros.Context
import scala.language.experimental.macros

/**
 * Object containing the flipper macros.
 */
object Macro {
  /**
   * Macro that surrounds `fn` with an <code>if</code> construct that
   * branches based on the result of querying the `config` parameter.
   * @param featureName the feature name to query
   * @param config the config holding the feature status
   * @param fn the function to execute if feature is enabled
   * @return a Unit
   */
  def flipper[T](featureName: String, config: QueryableConfig)
                (fn: => T): Unit = macro MacroImpl.flipper[T]

  /**
   * Macro that surrounds `fn` with an <code>if</code> construct that
   * branches based on the result of querying the `config` parameter
   * and returns a default value if the feature is disabled.
   * @param featureName the feature name to query
   * @param config the config holding the feature status
   * @param fn the function to execute if feature is enabled
   * @param default the default function to execute if feature is disabled
   * @return the result of calling `fn` or `default`
   */
  def flipperWithDefault[T](featureName: String, config: QueryableConfig)
                           (fn: => T)(default: => T): T = macro MacroImpl.flipperWithDefault[T]
}

object MacroImpl {
  def flipper[T](c: Context)
                (featureName: c.Expr[String],
                 config: c.Expr[QueryableConfig])
                (fn: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._
    val result = q"if($config.isFeatureOn($featureName)) $fn else {}"
    c.Expr[Unit](result)
  }

  def flipperWithDefault[T](c: Context)
                           (featureName: c.Expr[String],
                            config: c.Expr[QueryableConfig])
                           (fn: c.Expr[T])(default: c.Expr[T]) = {
    import c.universe._
    val result = q"if($config.isFeatureOn($featureName)) $fn else $default"
    c.Expr[T](result)
  }
}