package com.robot.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhang
 */
public class FileInterfaceLog {
    private static final Logger log = LoggerFactory.getLogger("fileInterface");
    public static void error(String content) {
        log.error(content);
    }

    public static void info(String content) {
        log.info(content);
    }
}
