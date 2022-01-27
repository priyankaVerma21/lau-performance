package uk.gov.hmcts.reform.LandA.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, CommonHeader}


object LAUCaseScenario {


  val BaseURL = Environment.baseUrl
  val IdamURL = Environment.idamUrl
  val CaseUsers = Environment.caseUsers
  val CaseSearches = Environment.caseSearches



  val S2SAuthToken =

      group("LAU_010_Case_S2S") {
      exec(http("PaymentAPIToken_020_GetServiceToken")
        .post(Environment.S2S_BASE_URI + "/lease")
        .header("Content-Type", "application/json")
        .body(StringBody(
          """{
          "microservice": "lau_frontend"
           }"""
        )).asJson
        .check(bodyString.saveAs("s2sToken")))

      }
      .exitHereIfFailed
      .pause(Environment.thinkTime)



  val LAUcsrfCheck =
      group("LAU_020_Case_CSRF") {
      exec(http("CSRF check")
        .get(IdamURL + "?client_id=lau&response_type=code&redirect_uri=" + BaseURL + "/oauth2/callback")
        .headers(CommonHeader.case_headers_login)
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      )
      }

  val LAUCaselogin = {
      feed(CaseUsers)
      .group("LAU_030_Case_Login") {
      exec(http("LAU Case login")
        .post(IdamURL + "?client_id=lau&response_type=code&redirect_uri=" + BaseURL + "/oauth2/callback")
        .headers(CommonHeader.case_headers_login)
        .formParam("username", "${email}")
        .formParam("password", "${password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrfToken}")
        .check(substring("User ID")))

      }
      .exitHereIfFailed
      .pause(Environment.thinkTime)
      }


  val LAUCaseSearch = {
      feed(CaseSearches)
      .group("LAU_040_Case_Search") {
      exec(http("LAU Case Search")
        .post(BaseURL + "/case-search")
        .headers(CommonHeader.case_headers_1)
        .header("ServiceAuthorization", "${s2sToken}")
        .formParam("userId", "")
        .formParam("caseRef", "")
        .formParam("startTimestamp", "${startTimestamp}")
        .formParam("caseTypeId", "")
        .formParam("caseJurisdictionId", "${caseJurisdictionId}")
        .formParam("endTimestamp", "${endTimestamp}")
        .formParam("page", "1")
        .check(substring("Case Activity Results")))

      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)
      }


  val LAUCaseNextPage =

      group("LAU_050_Case_Next") {
      exec(http("LAU Case Second Page Click")
        .get(BaseURL + "/case-activity/page/2")
        .headers(CommonHeader.case_headers_2)
        .check(substring("Page 2")))
           //need to check if the get is different for Idam
       }
        .exitHereIfFailed
        .pause(Environment.thinkTime)



  val LAUcsvCaseActivityDownload =
      group("LAU_060_Activity_Download") {
      exec(http("Case Activity CSV Download")
        .get(BaseURL + "/case-activity/csv")
        .headers(CommonHeader.case_headers_3))
      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)

  val LAUcsvCaseSearchDownload =
      group("LAU_070_Search_Download") {
      exec(http("Case Search CSV Download")
        .get(BaseURL + "/case-searches/csv")
        .headers(CommonHeader.case_headers_3))
      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)
      }
