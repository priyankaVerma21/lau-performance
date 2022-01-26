package uk.gov.hmcts.reform.LandA.performance.scenarios.utils

object CommonHeader {

  val baseurl = "https://lau.perftest.platform.hmcts.net"
  val S2S_BASE_URI = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support"


  val case_headers_login = Map(
    "origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val case_headers_1 = Map(
    "origin" -> "https://lau.perftest.platform.hmcts.net",
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val case_headers_2 = Map(
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val case_headers_3 = Map(
    "accept" -> "*/*",
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val logon_headers_login = Map(
    "origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val logons_header_csrf = Map(
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "none",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val logons_headers_3 = Map(
    "origin" -> "https://lau.perftest.platform.hmcts.net",
    "sec-ch-ua" -> """ Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val thinkTime = 10

}
