package uk.gov.hmcts.reform.LandA.performance.simulations

import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.LandA.performance.scenarios._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.concurrent.duration._

class mainSimulation extends Simulation{

  val BaseURL = Environment.baseUrl
  val CaseUsers = csv("CaseUsers.csv").circular
  val CaseSearches = csv("CaseSearchInfo.csv").circular

  val httpProtocol = http
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources


  val CompleteCaseSim = scenario("Case Complete")

        .exec(LAUCaseScenario.S2SAuthToken)
        .exec(LAUCaseScenario.LAUcsrfCheck)
        .exec(LAUCaseScenario.LAUCaselogin)
        .exec(LAUCaseScenario.LAUCaseSearch)
        .exec(LAUCaseScenario.LAUCaseNextPage)
        .exec(LAUCaseScenario.LAUcsvCaseActivityDownload)
        //.exec(LAUCaseScenario.LAUcsvCaseSearchDownload)


  val CompleteLogonSim = scenario("Logon Complete")
        .exec(LAULogonScenario.S2SAuthToken)
        .exec(LAULogonScenario.LogonCsrfCheck)
        .exec(LAULogonScenario.LogonLogin)
        .exec(LAULogonScenario.logonsAuditSearch)
        // .exec(LandAIdamscenario.nextPage)
        .exec(LAULogonScenario.LogonCsvDownload)


  setUp(
  CompleteCaseSim.inject(rampUsers(1) during (5 minutes))
  .protocols(httpProtocol)
  )

}

