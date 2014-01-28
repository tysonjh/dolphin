package switch

import dolphin.{Dolphin, QueryableConfig}
import org.scalatest.FunSpec

class DolphinSpec extends FunSpec with Dolphin {
  def featureAlwaysOnConfig = new QueryableConfig {
    def isFeatureOn(name: String): Boolean = true
  }

  def featureAlwaysOffConfig = new QueryableConfig {
    def isFeatureOn(name: String): Boolean = false
  }

  describe("Flipper") {
    it("should execute function if feature is on"){
      var cnt = 0
      flipper("a", featureAlwaysOnConfig){cnt = cnt + 1}
      assert(cnt == 1)
    }
    
    it("should not execute function if feature is off"){
      var cnt = 0
      flipper("a", featureAlwaysOffConfig){cnt = cnt + 1}
      assert(cnt == 0)
    }
  }

  describe("Flipper with default"){
    it("should return the function result if feature is on"){
      val result = flipperOrDefault("a", featureAlwaysOnConfig){
        "on"
      } {
        "default"
      }
      assert(result == "on")
    }

    it("should return the default result if feature is off"){
      val result = flipperOrDefault("a", featureAlwaysOffConfig){
        "off"
      } {
        "default"
      }
      assert(result == "default")
    }
  }
}

