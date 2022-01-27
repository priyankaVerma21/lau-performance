package uk.gov.hmcts.reform.LandA.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.LandA.performance.scenarios.utils.{CommonHeader, Environment}

object LAULogonScenario {

  val BaseURL = Environment.baseUrl
  val IdamURL = Environment.idamUrl
  val LogonUsers = Environment.logonUsers
  val LogonSearches = Environment.logonSearches


  val S2SAuthToken =
      group("LAU_010_Case_S2S") {
      exec(http("S2S Authorisation for Logons Audit")
        .post(Environment.S2S_BASE_URI + "/lease")
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
        .pause(Environment.thinkTime)

  val LogonCsrfCheck =

      group("LAU_020_Case_CSRF") {
      exec(http("LAU Logons CSRF check")
        .get(IdamURL + "?client_id=lau&response_type=code&redirect_uri=" + BaseURL + "/oauth2/callback")
        .headers(CommonHeader.logons_header_csrf)
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      )
      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)

  val LogonLogin =
      feed(LogonUsers)
      .group("LAU_030_Logons_Login") {
        exec(http("LAU Logons Audit login")
          .post(IdamURL + "?client_id=lau&response_type=code&redirect_uri=" + BaseURL + "/oauth2/callback")
          .headers(CommonHeader.logon_headers_login)
          .formParam("username", "${email}")
          .formParam("password", "${password}")
          .formParam("save", "Sign in")
          .formParam("selfRegistrationEnabled", "false")
          .formParam("_csrf", "${csrfToken}")
          .check(substring("User ID")))
      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)


  val logonsAuditSearch =
      feed(LogonSearches)
      .group("LAU_040_Logon_Search") {
        exec(http("Logons Search")
         .post(BaseURL + "/logon-search")
         .headers(CommonHeader.logons_headers_3)
         .header("ServiceAuthorization", "${s2sToken}")
         .formParam("userId", "")
         .formParam("emailAddress", "mark.jones@gmail.com")
         .formParam("startTimestamp", "2020-09-01 12:00:00")
         .formParam("endTimestamp", "2022-01-25 12:00:00")
         .formParam("page", "1")
         .check(substring("Logons Audit Results"))
      )
      }
        .exitHereIfFailed
        .pause(Environment.thinkTime)


  /* val LogonNextPage =

    group("LAU_050_Logon_Next") {
      exec(http("Idam Second Page Click")
        .get(baseurl + "/case-activity/page/2")
        .headers(headers_2))
                      .check(substring("Page 2")))

    }
      .exitHereIfFailed
           .pause(Environment.thinkTime)
*/

  val LogonCsvDownload =
    group("LAU_060_Logons_Download") {
      exec(http("CSV Download")
        .get(BaseURL + "/logons/csv")
        .headers(CommonHeader.logons_headers_3))

    }
      .exitHereIfFailed
      .pause(Environment.thinkTime)

}
