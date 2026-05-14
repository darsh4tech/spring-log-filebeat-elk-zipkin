package com.mine.test.springtestgottraceid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringTestGotTraceIdApplication {

    Logger logger = LoggerFactory.getLogger(SpringTestGotTraceIdApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringTestGotTraceIdApplication.class, args);
    }

    @PostMapping("/second")
    public String getTraceId(@RequestBody Employee employee) {
        logger.info("employee: {}", employee);
        return "SUCESS";
    }

}
