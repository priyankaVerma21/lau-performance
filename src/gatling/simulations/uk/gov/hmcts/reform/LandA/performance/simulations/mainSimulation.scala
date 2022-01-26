package uk.gov.hmcts.reform.LandA.performance.simulations

import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.LandA.performance.scenarios._
import io.gatling.core.Predef._

import scala.concurrent.duration._

class mainSimulation extends Simulation{

  val CaseUsers = csv("CaseUsers.csv").circular
  val CaseSearches = csv("CaseSearchInfo.csv").circular


  val CCDlogin = scenario("CCD Login")
    .repeat(1) {
    feed(CaseUsers)
    exec(LAUCaseScenario.LAUCaselogin)
  }

  val CCDsearch = scenario("CCD Search")
    .repeat(1) {
      feed(CaseUsers)
      feed(CaseSearches)
      exec(LAUCaseScenario.LAUCaseSearch)
    }

  val CompleteCaseSim = (scenario("Complete")
        .exec(LAUCaseScenario.S2SAuthToken)
        .exec(LAUCaseScenario.LAUcsrfCheck)
        .exec(CCDlogin)
        .exec(LAUCaseScenario.LAUCaseSearch)
        .exec(LAUCaseScenario.LAUCaseNextPage)
        .exec(LAUCaseScenario.LAUcsvCaseActivityDownload)
        //.exec(LAUCaseScenario.LAUcsvCaseSearchDownload)
    )

  val CompleteLogonSim = (scenario("Complete")
    .exec(LAULogonScenario.S2SAuthToken)
    .exec(LAULogonScenario.LogonCsrfCheck)
    .exec(LAULogonScenario.LogonLogin)
    .exec(LAULogonScenario.logonsAuditSearch)
   // .exec(LandAIdamscenario.nextPage)
    .exec(LAULogonScenario.LogonCsvDownload)
    )


  setUp(
    CompleteCaseSim.inject(rampUsers(1) during (5 minutes))
  )

}

