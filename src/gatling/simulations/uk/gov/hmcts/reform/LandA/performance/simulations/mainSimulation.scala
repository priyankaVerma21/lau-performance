package uk.gov.hmcts.reform.LandA.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.LandA.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.LandA.performance.scenarios._

import scala.concurrent.duration._

class mainSimulation extends Simulation{

  val BaseURL = Environment.baseUrl

  val httpProtocol = http
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  val LAUSimulation = scenario("LAU Simulation")
    .exitBlockOnFail {
      exec(LAUScenario.LAUHomepage)
      .exec(LAUScenario.LAULogin)
    }
    .repeat(5) {
      exec(LAUScenario.LAUCaseAuditSearch)
        .exec(LAUScenario.LogonsAuditSearch)
    }
  setUp(
    LAUSimulation.inject(rampUsers(3) during (60 minutes))
    .protocols(httpProtocol)
  )

}

