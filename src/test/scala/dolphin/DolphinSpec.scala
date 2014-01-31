package dolphin

import org.scalatest.FunSpec

class DolphinSpec extends FunSpec {
  import Macro._ // import macros

  describe("A mutable flipper macro") {
    it("should execute function if feature is on") {
      var cnt = 0
      flipper("a", test.featureAlwaysOnConfig) {
        cnt = cnt + 1
      }
      assert(cnt == 1)
    }

    it("should not execute function if feature is off") {
      var cnt = 0
      flipper("a", test.featureAlwaysOffConfig) {
        cnt = cnt + 1
      }
      assert(cnt == 0)
    }

    it("is mutable") {
      var cnt = 0
      val config = test.mutableConfig(false)
      def fn() = flipper("a", config) {
        cnt = cnt + 1
      }
      fn()
      assert(cnt == 0)

      config.toggleFeature
      fn()
      assert(cnt == 1)
    }
  }

  describe("A mutable flipper with default") {
    it("should return the function result if feature is on") {
      val result = flipperWithDefault("a", test.featureAlwaysOnConfig) {
        "on"
      } {
        "default"
      }
      assert(result == "on")
    }

    it("should return the default result if feature is off") {
      val result = flipperWithDefault("a", test.featureAlwaysOffConfig) {
        "off"
      } {
        "default"
      }
      assert(result == "default")
    }

    it("is mutable") {
      var cnt = 0
      val config = test.mutableConfig(false)
      def fn() = flipperWithDefault("a", config) {
        cnt = cnt + 1
      } {
        cnt = cnt + 99
      }
      fn()
      assert(cnt == 99)

      config.toggleFeature
      fn()
      assert(cnt == 100)
    }
  }

  describe("A mutable flipper with versions") {
    it("should return the function corresponding to a version") {
      val config = test.featuresByNameWithVersion(Map("featureOne" -> Some(3.0), "featureTwo" -> None))
      def productUrlV1(p: Int) = s"/product/v1/$p/"
      def productUrlV2(p: Int) = s"/product/v2/product-$p/"
      def productUrlV3(p: Int) = s"/product/v3/product/$p/"
      def productUrlDefault(p: Int) = productUrlV1(p)

      def productUrl = flipperVersionMux[Double, Int, String]("featureOne", config)(
        Map(
          1.0 -> productUrlV1,
          2.0 -> productUrlV2,
          3.0 -> productUrlV3),
        productUrlDefault)
      assert(productUrl(1337) == s"/product/v3/product/1337/")
    }

    it("should return the default function if the feature is not found") {
      val config = test.featuresByNameWithVersion(Map("featureOne" -> Some(3.0), "featureTwo" -> None))
      def productUrlV1(p: Int) = s"/product/v1/$p/"
      def productUrlV2(p: Int) = s"/product/v2/product-$p/"
      def productUrlV3(p: Int) = s"/product/v3/product/$p/"
      def productUrlDefault(p: Int) = s"/product/$p/"

      def productUrl = flipperVersionMux[Double, Int, String]("featureDNE", config)(
        Map(
          1.0 -> productUrlV1,
          2.0 -> productUrlV2,
          3.0 -> productUrlV3),
        productUrlDefault)
      assert(productUrl(1337) == s"/product/1337/")
    }

    it("should return the default function if the feature version is not found") {
      val config = test.featuresByNameWithVersion(Map("featureOne" -> Some(3.0), "featureTwo" -> None))
      def productUrlV1(p: Int) = s"/product/v1/$p/"
      def productUrlV2(p: Int) = s"/product/v2/product-$p/"
      def productUrlV3(p: Int) = s"/product/v3/product/$p/"
      def productUrlDefault(p: Int) = s"/product/$p/"

      def productUrl = flipperVersionMux[Double, Int, String]("featureOne", config)(
        Map(
          1.0 -> productUrlV1,
          2.0 -> productUrlV2,
          3.1 -> productUrlV3),
        productUrlDefault)
      assert(productUrl(1337) == s"/product/1337/")
    }

    it("is mutable") {
      val config = test.featuresByNameWithVersion(Map("featureOne" -> Some(3.0), "featureTwo" -> None))
      def productUrlV1(p: Int) = s"/product/v1/$p/"
      def productUrlV2(p: Int) = s"/product/v2/product-$p/"
      def productUrlV3(p: Int) = s"/product/v3/product/$p/"
      def productUrlDefault(p: Int) = s"/product/$p/"

      def productUrl = flipperVersionMux[Double, Int, String]("featureOne", config)(
        Map(
          1.0 -> productUrlV1,
          2.0 -> productUrlV2,
          3.0 -> productUrlV3),
        productUrlDefault)

      assert(productUrl(1337) == s"/product/v3/product/1337/")

      config.changeFeatureVersion("featureOne", Some(2.0))
      assert(config.getEnabledFeatureVersion("featureOne") == Some(2.0))
      assert(productUrl(1337) == s"/product/v2/product-1337/")
    }
  }
}

