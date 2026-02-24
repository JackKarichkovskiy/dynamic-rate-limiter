package org.example.zilchinterview;

import org.springframework.boot.SpringApplication;

public class TestZilchInterviewApplication {

    static void main(String[] args) {
        SpringApplication.from(ZilchInterviewApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
