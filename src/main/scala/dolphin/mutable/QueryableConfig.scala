package dolphin.mutable

trait QueryableConfig {
  def isFeatureOn(name: String): Boolean
}
