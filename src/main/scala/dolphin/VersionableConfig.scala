package dolphin

/**
 * User: tyson.hamilton
 * Date: 31/01/14
 * Time: 12:55 PM
 */
trait VersionableConfig[T] {
  def getEnabledFeatureVersion(name: String): Option[T]
}
