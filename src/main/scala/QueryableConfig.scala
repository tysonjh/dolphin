package dolphin

trait QueryableConfig {
  def isFeatureOn(name: String): Boolean
}
