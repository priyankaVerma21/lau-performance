package uk.gov.hmcts.reform.LandA.performance.scenarios

import io.gatling.core.Predef.{feed, _}
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.LandA.performance.scenarios.utils.CommonHeader


object LAUCaseScenario {


  val httpProtocol = http
    .baseUrl("https://lau.perftest.platform.hmcts.net")
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  val uri3 = "https://idam-web-public.perftest.platform.hmcts.net/login"


  val CaseUsers = csv("CaseUsers.csv").circular
  val CaseSearches = csv("CaseSearchInfo.csv").circular
  val S2S_BASE_URI = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support"
  val baseurl = "https://lau.perftest.platform.hmcts.net"

  val S2SAuthToken =

  group("S2S Authorisation") {
    exec(http("PaymentAPIToken_020_GetServiceToken")
      .post(S2S_BASE_URI + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        """{
       "microservice": "lau_frontend"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sToken"))
      .check(bodyString.saveAs("responseBody")))


  }
    .exitHereIfFailed
    .pause(10)



  val LAUcsrfCheck =
  group("LAU csrf Check") {
    exec(http("CSRF check")
      .get(uri3 + "?client_id=lau&response_type=code&redirect_uri=https://lau.perftest.platform.hmcts.net/oauth2/callback")
      .headers(CommonHeader.case_headers_login)
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
    )
  }

  val LAUCaselogin =
    feed(CaseUsers)
    .group("LAU Case Login") {
     exec(http("LAU Case login")
        .post(uri3 + "?client_id=lau&response_type=code&redirect_uri=https://lau.perftest.platform.hmcts.net/oauth2/callback")
        .headers(CommonHeader.case_headers_login)

        .formParam("username", "${email}")
        .formParam("password", "${password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrfToken}")
        .check(status is 200)
        .check(substring("User ID")))

  }
      .exitHereIfFailed
    .pause(5)


  val LAUCaseSearch =

    feed(CaseSearches)
    .group("LAU Case Search") {

      exec(http("LAU Case Search")
       .post(baseurl + "/case-search")
       .headers(CommonHeader.case_headers_1)
       .header("ServiceAuthorization", "${s2sToken}")
       .formParam("userId", "")
       .formParam("caseRef", "")
       .formParam("startTimestamp", "${startTimestamp}")
       .formParam("caseTypeId", "")
       .formParam("caseJurisdictionId", "${caseJurisdictionId}")
       .formParam("endTimestamp", "${endTimestamp}")
       .formParam("page", "1")
       .check(substring("Search")))

   }
      .pause(2)


       val LAUCaseNextPage =

         group("LAU Case Next Page") {
            exec(http("LAU Case Second Page Click")
              .get(baseurl + "/case-activity/page/2")
              .headers(CommonHeader.case_headers_2))
           //need to check if the get is different for Idam


    }

           .exitHereIfFailed
           .pause(5)



  val LAUcsvCaseActivityDownload =
    group("LAU Case Activity CSV Download") {
      exec(http("Case Activity CSV Download")
        .get(baseurl + "/case-activity/csv")
        .headers(CommonHeader.case_headers_3))
    }
      .exitHereIfFailed
      .pause(3)

  val LAUcsvCaseSearchDownload =
    group("LAU Case Search CSV Download") {
      exec(http("Case Search CSV Download")
        .get(baseurl + "/case-searches/csv")
        .headers(CommonHeader.case_headers_3))
    }
      .exitHereIfFailed
      .pause(3)
}


