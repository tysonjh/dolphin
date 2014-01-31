package dolphin.mutable

import org.scalatest.FunSpec

class DolphinSpec extends FunSpec {
  import dolphin.mutable.Macro._ // import macros
  import test._ // import test helpers

  describe("A mutable flipper macro") {
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

    it("is mutable"){
      var cnt = 0
      val config = mutableConfig(false)
      def fn() = flipper("a", config){cnt = cnt + 1}
      fn()
      assert(cnt == 0)

      config.toggleFeature
      fn()
      assert(cnt == 1)
    }
  }

  describe("A mutable flipper with default"){
    it("should return the function result if feature is on"){
      val result = flipperWithDefault("a", featureAlwaysOnConfig){
        "on"
      } {
        "default"
      }
      assert(result == "on")
    }

    it("should return the default result if feature is off"){
      val result = flipperWithDefault("a", featureAlwaysOffConfig){
        "off"
      } {
        "default"
      }
      assert(result == "default")
    }

    it("is mutable"){
      var cnt = 0
      val config = mutableConfig(false)
      def fn() = flipperWithDefault("a", config){cnt = cnt + 1}{cnt = cnt + 99}
      fn()
      assert(cnt == 99)

      config.toggleFeature
      fn()
      assert(cnt == 100)
    }
  }
}

