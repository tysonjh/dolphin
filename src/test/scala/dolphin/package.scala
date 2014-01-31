import dolphin.{VersionableConfig, QueryableConfig}

/**
 * User: tyson.hamilton
 * Date: 30/01/14
 * Time: 5:01 PM
 */
package object test {
  class MutableFeatureFlag(isOn: Boolean) {
    var _isOn = false
    def toggleFeature { _isOn = !_isOn }
  }

  class MutableVersionNumber(initFeatureMap: Map[String, Option[Double]]) extends VersionableConfig[Double] {
    var _curFeatureMap: Map[String, Option[Double]] = initFeatureMap
    def changeFeatureVersion(name: String, version: Option[Double]) {
      _curFeatureMap = _curFeatureMap ++ Map(name -> version)
    }
    def getEnabledFeatureVersion(name: String): Option[Double] = _curFeatureMap.get(name).flatten
  }

  def featureAlwaysOnConfig = new QueryableConfig {
    def isFeatureEnabled(name: String): Boolean = true
  }

  def featureAlwaysOffConfig = new QueryableConfig {
    def isFeatureEnabled(name: String): Boolean = false
  }

  def mutableConfig(isOn: Boolean) = new MutableFeatureFlag(isOn) with QueryableConfig {
    def isFeatureEnabled(name: String): Boolean = _isOn
  }

  def featuresByNameWithVersion(features: Map[String, Option[Double]]): MutableVersionNumber =
    new MutableVersionNumber(features)
}
