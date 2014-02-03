package dolphin

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

  /**
   * Macro that queries the provided version to function map which
   * returns the proper version of the function. If the version
   * is not found it falls back to `default`.
   * @param featureName the feature name to query
   * @param config the config holding the current feature version
   * @param versionToFn the version to function mapping
   * @param default the default function to execute if no proper version is found
   * @tparam T map key
   * @tparam P function parameter
   * @tparam R function return value
   * @return the versioned function
   */
  def flipperVersionMux[T, P, R](featureName: String, config: VersionableConfig[T])
                                (versionToFn: Map[T, P => R],
                                 default: P => R): P => R = macro MacroImpl.flipperVersionMux[T, P, R]
}

object MacroImpl {
  def flipper[T](c: Context)
                (featureName: c.Expr[String],
                 config: c.Expr[QueryableConfig])
                (fn: c.Expr[T]): c.Expr[Unit] = {
    import c.universe._
    val result = q"if($config.isFeatureEnabled($featureName)) $fn else {}"
    c.Expr[Unit](result)
  }

  def flipperWithDefault[T](c: Context)
                           (featureName: c.Expr[String],
                            config: c.Expr[QueryableConfig])
                           (fn: c.Expr[T])(default: c.Expr[T]) = {
    import c.universe._
    val result = q"if($config.isFeatureEnabled($featureName)) $fn else $default"
    c.Expr[T](result)
  }

  def flipperVersionMux[T, P, R](c: Context)
                          (featureName: c.Expr[String],
                            config: c.Expr[VersionableConfig[T]])
                          (versionToFn: c.Expr[Map[T, P => R]],
                           default: c.Expr[P => R]): c.Expr[P => R] = {
    import c.universe._
    val result = q"""
      val fVer = $config.getEnabledFeatureVersion($featureName)
      val fFn = fVer.flatMap(v => $versionToFn.get(v))
      fFn.getOrElse($default)
    """
    c.Expr[P => R](result)
  }
}