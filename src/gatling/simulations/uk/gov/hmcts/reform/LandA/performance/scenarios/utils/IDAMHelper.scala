package uk.gov.hmcts.reform.LandA.performance.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Environment._;

object IDAMHelper {

  val getIdamToken = 

    exec(http("Token_010_015_GetAuthToken")
    .post(idamURL + "/o/token?grant_type=password&username=" + idamUsername + "&password=" + idamPassword + "&client_id=" + idamClient + "&client_secret=" + idamSecret + "&redirect_uri=" + idamRedirectURL + "&scope=" + idamScope)
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status.is(200))
         .check(jsonPath("$.access_token").saveAs("accessToken")))
    .pause(2)



}
