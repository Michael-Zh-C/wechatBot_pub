package com.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.robot.common.CommonConsts;
import com.robot.common.CommonGroupConst;
import com.robot.dao.GroupAnalysisSwitchDao;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.GroupChatService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Michael
 */
@Controller
@RequestMapping("/groupInfo")
public class GroupInfoController {
    @Value("${api.url}")
    String api_url;

    @Autowired
    GroupAnalysisSwitchDao groupAnalysisSwitchDao;
    @Autowired
    GroupChatService groupChatService;

    @RequestMapping("/getMemberChatCount")
    @ResponseBody
    public void getMemberChatCount(@RequestParam String groupId) {
        groupChatService.updateGroupMemberInfo(groupId);
    }
}
