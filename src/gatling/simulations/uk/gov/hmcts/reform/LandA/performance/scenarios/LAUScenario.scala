package uk.gov.hmcts.reform.LandA.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, CommonHeader}

object LAUScenario {

  val BaseURL = Environment.baseUrl
  val IdamURL = Environment.idamUrl

  val ThinkTime = Environment.thinkTime

  val Users = csv("Users.csv").circular
  val CaseAuditSearches = csv("CaseAuditSearch.csv").circular
  val LogonAuditSearches = csv("LogonAuditSearch.csv").circular


  //Get S2S tokens for authorisation in subsequent LAU calls
  val S2SAuthTokens =

    exec(http("LAU_010_LAUServiceToken")
      .post(Environment.S2S_BASE_URI + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"microservice": "lau_frontend"}""")).asJson
      .check(bodyString.saveAs("s2sTokenLAU")))

    .pause(ThinkTime)

    .exec(http("LAU_050_IDAMServiceToken")
      .post(Environment.S2S_BASE_URI + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"microservice": "idam_frontend"}""")).asJson
      .check(bodyString.saveAs("s2sTokenIDAM")))

    .pause(ThinkTime)

  val LAUHomepage =

    group("LAU_020_Homepage") {
      exec(http("LAU Homepage")
        .get(BaseURL)
        .headers(CommonHeader.homepage_header)
        .check(substring("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      )
    }
    .pause(ThinkTime)

  val LAULogin =

    feed(Users)
      .group("LAU_030_Login") {
        exec(http("LAU Login")
          .post(IdamURL + "/login?client_id=lau&response_type=code&redirect_uri=" + BaseURL + "/oauth2/callback")
          .headers(CommonHeader.navigation_headers)
          .formParam("username", "${email}")
          .formParam("password", "${password}")
          .formParam("save", "Sign in")
          .formParam("selfRegistrationEnabled", "false")
          .formParam("_csrf", "${csrfToken}")
          .check(substring("Case Audit Search")))
      }
      .pause(ThinkTime)

  //Perform a case audit search and download the CSV file
  val LAUCaseAuditSearch =

    feed(CaseAuditSearches)
      .group("LAU_040_CaseAuditSearch") {
        exec(http("LAU Case Audit Search")
          .post(BaseURL + "/case-search")
          .headers(CommonHeader.navigation_headers)
          .header("ServiceAuthorization", "${s2sTokenLAU}")
          .formParam("userId", "")
          .formParam("caseRef", "")
          .formParam("startTimestamp", "${caseStartTimestamp}")
          .formParam("caseTypeId", "")
          .formParam("caseJurisdictionId", "${caseJurisdictionId}")
          .formParam("endTimestamp", "${caseEndTimestamp}")
          .formParam("page", "1")
          .check(substring("Case Activity Results"))
          .check(substring("case-activity-next-btn").optional.saveAs("moreCasePages")))
      }
      .pause(ThinkTime)

    //only load the second page if there are more pages available
    .doIf("${moreCasePages.exists()}") {

      group("LAU_050_CaseAuditPage2") {
        exec(http("LAU Case Audit Page 2")
          .get(BaseURL + "/case-activity/page/2")
          .headers(CommonHeader.navigation_headers)
          .check(substring("Page 2")))
      }
      .pause(ThinkTime)

    }

    .group("LAU_060_CaseActivityDownload") {
      exec(http("Case Activity CSV Download")
        .get(BaseURL + "/case-activity/csv")
        .headers(CommonHeader.download_headers)
        .check(substring("Case Jurisdiction Id"))
        .check(substring("filename")))
    }
    .pause(ThinkTime)

    .group("LAU_070_CaseSearchDownload") {
      exec(http("Case Search CSV Download")
        .get(BaseURL + "/case-searches/csv")
        .headers(CommonHeader.download_headers)
        .check(substring("Case Refs"))
        .check(substring("filename")))
    }
    .pause(ThinkTime)

  //Perform a logon audit search and download the CSV file
  val LogonsAuditSearch =

    feed(LogonAuditSearches)
      .group("LAU_080_LogonAuditSearch") {
        exec(http("LAU Logon Audit Search")
          .post(BaseURL + "/logon-search")
          .headers(CommonHeader.navigation_headers)
          .header("ServiceAuthorization", "${s2sTokenIDAM}")
          .formParam("userId", "")
          .formParam("emailAddress", "${logonEmailAddress}")
          .formParam("startTimestamp", "${logonStartTimestamp}")
          .formParam("endTimestamp", "${logonEndTimestamp}")
          .formParam("page", "1")
          .check(substring("Logons Audit Results"))
          .check(substring("logons-next-btn").optional.saveAs("moreLogonPages")))
      }
      .pause(Environment.thinkTime)

      //only load the second page if there are more pages available
      .doIf("${logons-next-btn.exists()}") {

      group("LAU_090_LogonAuditPage2") {
        exec(http("LAU Logon Audit Page 2")
          .get(BaseURL + "/logons/page/2")
          .headers(CommonHeader.navigation_headers)
          .check(substring("Page 2")))
      }
      .pause(ThinkTime)

    }

    .group("LAU_100_LogonActivityDownload") {
      exec(http("Logon Activity CSV Download")
        .get(BaseURL + "/logons/csv")
        .headers(CommonHeader.download_headers)
        .check(substring("Ip Address"))
        .check(substring("filename")))

    }
    .pause(Environment.thinkTime)

}


