package uk.gov.hmcts.reform.LandA.performance.simulations

import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.LandA.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.LandA.performance.scenarios._

import scala.concurrent.duration._

class mainSimulation extends Simulation{

  val BaseURL = Environment.baseUrl

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match{
    case "perftest" => "perftest"
    case "pipeline" => "aat"
    case _ => "**INVALID**"
  }
  /* ******************************** */

  /* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
  val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
  val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
  /* ******************************** */

  /* PERFORMANCE TEST CONFIGURATION */
  val testDurationMins = 60
  val numberOfPerformanceTestUsers:Double = 3
  val numberOfPipelineUsers:Double = 3

  //If running in debug mode, disable pauses between steps
  val pauseOption:PauseType = debugMode match{
    case "off" => constantPauses
    case _ => disabledPauses
  }

  val httpProtocol = http
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

  val LAUSimulation = scenario("LAU Simulation")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(LAUScenario.LAUHomepage)
        .exec(LAUScenario.LAULogin)
    }
    .repeat(5) {
      exec(LAUScenario.LAUCaseAuditSearch)
        .exec(LAUScenario.LogonsAuditSearch)
    }

    .exec {
      session =>
        println(session)
        session
    }

  val LAUDeleteSimulation = scenario("LAU Simulation")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(LAUScenarioDeletion.LAUHomepage)
        .exec(LAUScenarioDeletion.LAULoginDeletion)
    }
    .repeat(1) {
      exec(LAUScenarioDeletion.LAUCaseDeletionSearch)
    }

    .exec {
      session =>
        println(session)
        session
    }

  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, numberOfPerformanceTestUsers: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsers(numberOfPerformanceTestUsers.toInt) during (testDurationMins minutes)
          )
        }
        else{
          Seq(atOnceUsers(1))
        }
      case "pipeline" =>
        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        Seq(global.successfulRequests.percent.gte(95),
          details("LAU_080_LogonAuditSearch").successfulRequests.count.gte((numberOfPipelineUsers * 0.9).ceil.toInt)
        )
      case "pipeline" =>
        Seq(global.successfulRequests.percent.gte(95),
          details("LAU_080_LogonAuditSearch").successfulRequests.count.gte((numberOfPipelineUsers * 0.9).ceil.toInt)
        )
      case _ =>
        Seq()
    }
  }

  setUp(
    LAUDeleteSimulation.inject(simulationProfile(testType, numberOfPerformanceTestUsers, numberOfPipelineUsers)).pauses(pauseOption)
  ).protocols(httpProtocol)
    .assertions(assertions(testType))

}


