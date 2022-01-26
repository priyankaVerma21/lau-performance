package uk.gov.hmcts.reform.LandA.performance.scenarios

import io.gatling.core.Predef.{BlackList, StringBody, WhiteList, bodyString, css, csv, exec, feed, substring}
import io.gatling.http.Predef.{http, status}
import io.gatling.core.Predef.{feed, _}
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.LandA.performance.scenarios.utils.CommonHeader

object LAULogonScenario {

  val httpProtocol = http
    .baseUrl("https://lau.perftest.platform.hmcts.net")
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources


  val uri1 = "https://www.google-analytics.com/j/collect"
  val uri3 = "https://idam-web-public.perftest.platform.hmcts.net/login"




  val Caseusers = csv("CaseUsers.csv").circular
  val LogonUsers = csv ("LogonUsers.csv").circular
  val LogonSearches = csv("LogonSearchInfo.csv").circular
  val baseurl = "https://lau.perftest.platform.hmcts.net"
  val S2S_BASE_URI = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support"


  val S2SAuthToken =

    group("S2S Authorisation for Logons Audit") {
      exec(http("PaymentAPIToken_020_GetServiceToken")
        .post(S2S_BASE_URI + "/lease")
        .header("Content-Type", "application/json")
        .body(StringBody(
          """{
       "microservice": "idam_frontend"
        }"""
        )).asJson
        .check(bodyString.saveAs("s2sToken"))
        .check(bodyString.saveAs("responseBody")))

    }
      .exitHereIfFailed
      .pause(6)

  val LogonCsrfCheck =

  group("Logons Audit User Login") {

    exec(http("Logons CSRF check")
      .get(uri3 + "?client_id=lau&response_type=code&redirect_uri=https://lau.perftest.platform.hmcts.net/oauth2/callback")
      .headers(CommonHeader.logons_header_csrf)
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
    )
  }
    .exitHereIfFailed
    .pause(2)

    val LogonLogin =
      feed(LogonUsers)
        .group("LAU Logons Login") {
          exec(http("Logons Audit login")

            .post(uri3 + "?client_id=lau&response_type=code&redirect_uri=https://lau.perftest.platform.hmcts.net/oauth2/callback")
            .headers(CommonHeader.logon_headers_login)

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


  val logonsAuditSearch =
    feed(LogonSearches)
     .group("LAU Case Login") {
       exec(http("Logons Search")

         .post(baseurl + "/logon-search")
         .headers(CommonHeader.logons_headers_3)
         .header("ServiceAuthorization", "${s2sToken}")
         .formParam("userId", "")
         .formParam("emailAddress", "mark.jones@gmail.com")
         .formParam("startTimestamp", "2020-09-01 12:00:00")
         .formParam("endTimestamp", "2022-01-25 12:00:00")
         .formParam("page", "1")
         .check(substring("Search"))

       )
     }
      .pause(2)


  /* val LogonNextPage =

    group("Logons Next Page") {
      exec(http("Idam Second Page Click")
        .get(baseurl + "/case-activity/page/2")
        .headers(headers_2))

    }
      .exitHereIfFailed
      .pause(3)
*/

  val LogonCsvDownload =

    group("Logons Audit CSV Download") {
      exec(http("CSV Download")
        .get(baseurl + "/logons/csv")
        .headers(CommonHeader.logons_headers_3))

    }
      .exitHereIfFailed
      .pause(3)

}
