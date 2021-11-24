package com.robot.log;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhang
 */
@ComponentScan
@ConfigurationProperties("classpath:log4j.properties")
public class Log4jConfigure {
}
