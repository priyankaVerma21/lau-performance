package uk.gov.hmcts.reform.LandA.performance.simulations

import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.LandA.performance.scenarios._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.concurrent.duration._

class mainSimulation extends Simulation{

  val BaseURL = Environment.baseUrl
  val CaseUsers = csv("Users.csv").circular
  val CaseSearches = csv("CaseAuditSearch.csv").circular

  val httpProtocol = http
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  val LAUSimulation = scenario("LAU Simulation")
    .exitBlockOnFail {
      exec(LAUScenario.S2SAuthTokens)
      .exec(LAUScenario.LAUHomepage)
      .exec(LAUScenario.LAULogin)
    }
    .exec(LAUScenario.LAUCaseAuditSearch)
    .exec(LAUScenario.LogonsAuditSearch)

  setUp(
    LAUSimulation.inject(rampUsers(1) during (5 minutes))
    .protocols(httpProtocol)
  )

}

