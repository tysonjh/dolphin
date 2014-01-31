import dolphin.mutable.QueryableConfig

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

  def featureAlwaysOnConfig = new QueryableConfig {
    def isFeatureOn(name: String): Boolean = true
  }

  def featureAlwaysOffConfig = new QueryableConfig {
    def isFeatureOn(name: String): Boolean = false
  }

  def mutableConfig(isOn: Boolean) = new MutableFeatureFlag(isOn) with QueryableConfig {
    def isFeatureOn(name: String): Boolean = _isOn
  }
}
