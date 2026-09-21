package com.hwz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class LabCoreApplication {

    public static final ZoneId APPLICATION_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(APPLICATION_ZONE_ID));
        SpringApplication.run(LabCoreApplication.class, args);
    }

}
