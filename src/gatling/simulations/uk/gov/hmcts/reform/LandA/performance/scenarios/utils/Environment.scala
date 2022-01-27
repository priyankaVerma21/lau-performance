package uk.gov.hmcts.reform.LandA.performance.scenarios.utils

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseUrl = "https://lau.perftest.platform.hmcts.net"
  val S2S_BASE_URI = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support"
  val idamUrl = "https://idam-web-public.perftest.platform.hmcts.net/login"
  val caseUsers = csv("CaseUsers.csv").circular
  val caseSearches = csv("CaseSearchInfo.csv").circular
  val logonUsers = csv ("LogonUsers.csv").circular
  val logonSearches = csv("LogonSearchInfo.csv").circular


  val thinkTime = 2

}
