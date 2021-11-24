package com.robot;


import com.robot.common.CommonConsts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        CommonConsts.getInstance();
        System.out.println("WebSocket Application started.....");
    }
}
