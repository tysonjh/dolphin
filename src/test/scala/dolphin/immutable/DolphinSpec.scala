package dolphin.immutable

import org.scalatest.FunSpec

class DolphinSpec extends FunSpec {
  import Macro._ // import macros
  import test._ // import test helpers

  describe("A flipper macro") {
    it("should execute function if feature is on"){
      var cnt = 0
      flipper(true){cnt = cnt + 1}
      assert(cnt == 1)
    }
    
    it("should not execute function if feature is off"){
      var cnt = 0
      flipper(false){cnt = cnt + 1}
      assert(cnt == 0)
    }

    it("is not mutable"){
      var cnt = 0
      val config = mutableConfig(false)
      flipper(config.isFeatureOn("any")){cnt = cnt + 1}
      assert(cnt == 0)

      config.toggleFeature
      flipper(config.isFeatureOn("any")){cnt = cnt + 1}
      assert(cnt == 1)
    }
  }

  describe("A flipper with default"){
    it("should return the function result if feature is on"){
      val result = flipperWithDefault(true){
        "on"
      } {
        "default"
      }
      assert(result == "on")
    }

    it("should return the default result if feature is off"){
      val result = flipperWithDefault(false){
        "off"
      } {
        "default"
      }
      assert(result == "default")
    }

    it("is not mutable"){
      var cnt = 0
      val config = mutableConfig(false)
      def fn() = flipperWithDefault(config.isFeatureOn("any")){cnt = cnt + 1}{cnt = cnt + 99}
      fn()
      assert(cnt == 99)

      config.toggleFeature
      fn()
      assert(cnt == 100)
    }
  }
}

