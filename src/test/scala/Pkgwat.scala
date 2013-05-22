package tests

import org.scalatest.{ FunSpec, ParallelTestExecution }
import org.scalatest.matchers.ShouldMatchers._

import scala.concurrent._
import scala.concurrent.duration._

import org.fedoraproject.pkgwat._

class PkgwatSpec extends FunSpec with ParallelTestExecution {
  val pkgwat = new Pkgwat("https://apps.fedoraproject.org/packages")

  describe("The search method") {
    it("should be able to search Fedora Packages") {
      val result = Await.result(pkgwat.search("httpie"), 5.seconds)
      result.totalRows should equal (result.rows.length)
      result.rows.head.summary should equal ("A Curl-like tool for humans")
      result.rows.head.name should equal ("httpie")
      result.rows.head.subPackages.head.name should equal ("python3-httpie")
    }
  }

  describe("The get method") {
    it("should work with subpackages") {
      val p = pkgwat.get("gcc-go")
      p should not be (None)
      p.get.name should equal("gcc-go")
      p.get.summary should equal("Go support")
    }

    it("should work with main packages") {
      val p = pkgwat.get("gcc")
      p should not be (None)
      p.get.name should equal("gcc")
      p.get.summary should equal("Various compilers (C, C++, Objective-C, Java, ...)")
    }

    it("should bail out gracefully on no results") {
      val p = pkgwat.get("nonexistant-package-foo")
      p should be (None)
    }
  }
}