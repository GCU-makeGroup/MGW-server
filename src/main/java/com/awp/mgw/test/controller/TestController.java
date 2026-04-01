package com.awp.mgw.test.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Profile("local | dev")
@RestController
@RequestMapping("/test")
@Tag(name = "000 Test | 일반 테스트", description = "개발 및 테스트 용 API 입니다.")
@Slf4j
public class TestController {

    @Operation(summary = "헬스 체크 API")
    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("log-test")
    public void logTest() {
        log.trace("TRACE");
        log.debug("DEBUG");
        log.info("INFO");
        log.warn("WARN");
        log.error("ERROR");
    }
}
