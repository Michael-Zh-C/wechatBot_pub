package com.robot;


import com.robot.enums.Constellation;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;

import java.io.File;
import java.util.*;


public class Test {
    private final static String SECRET_ID = "AKIDoQP7ZkeGbgTzEG1UgcKlvoQlpc9PO6Yj";
    private final static String SECRET_KEY = "uY0B5i4AzWmlTu4xZuxjFU0ECtIQHsDG";

    public static void main(String [] args) {
        List<String> fileList = getFileName();
        for (String fileName : fileList) {
            StringBuffer sb = new StringBuffer();
            sb.append("wx-voice encode -i ");
            sb.append(fileName);
            sb.append(" -o ");
            sb.append(fileName.replace("mp3","silk"));
            sb.append(" -f silk");
            System.out.println(sb.toString());
        }
    }




    private static List<String> getFileName() {
        String path = "C:/Users/zhang/Desktop/musicCut";
        List<String> list = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return list;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                list.add(fs.getName());
            }
        }
        return list;
    }
}