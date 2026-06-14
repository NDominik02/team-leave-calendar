package com.example.leavecalendar;

import com.example.leavecalendar.service.TeamMemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LeavecalendarApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeavecalendarApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(TeamMemberService teamMemberService) {
        return args -> teamMemberService.seedIfEmpty();
    }
}