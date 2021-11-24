package com.robot.common;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael
 */
public class CommonGroupConst {
    public static ConcurrentHashMap<String,ConcurrentHashMap<String, AtomicInteger>> chatCountMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,ConcurrentHashMap<String, Date>> chatLastActiveMap = new ConcurrentHashMap<>();
}
