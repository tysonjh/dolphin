package dolphin

trait QueryableConfig {
  def isFeatureEnabled(name: String): Boolean
}
