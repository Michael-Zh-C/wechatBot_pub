package com.robot.service.impl;

import com.robot.common.CommonConsts;
import com.robot.common.CommonReply;
import com.robot.common.EnumHelperUtil;
import com.robot.common.TencentBotUtil;
import com.robot.dao.MessageAutoReplyDao;
import com.robot.enums.Constellation;
import com.robot.enums.GameType;
import com.robot.pojo.MessageAutoReply;
import com.robot.service.ConstellationService;
import com.robot.service.MessageAutoReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Michael
 */
@Service
public class MessageAutoReplyServiceImpl implements MessageAutoReplyService {
    @Autowired
    private MessageAutoReplyDao messageAutoReplyDao;
    @Autowired
    private ConstellationService constellationService;

    private String FROM_GROUP_ALL = "all";

    @Override
    public String handleAutoReply(String fromGroup, String content) {
        content = content.toLowerCase();
        //判断是否为星座运势
        Constellation constellation = EnumHelperUtil.getByStringTypeName(Constellation.class,"getChineseName", content);
        if (constellation != null) {
            //星座运势流程
            return constellationService.getConstellationReply(content);
        }

        //先走公共配置的
        Map<String,String> map1 = CommonConsts.getInstance().autoReplyDictionary.get(FROM_GROUP_ALL);
        if (map1.containsKey(content)) {
            return map1.get(content).replace("\\n","\n");
        }

        if (CommonConsts.getInstance().autoReplyDictionary.containsKey(fromGroup)) {
            Map<String,String> map = CommonConsts.getInstance().autoReplyDictionary.get(fromGroup);
            if (map.containsKey(content)) {
                return map.get(content).replace("\\n","\n");
            }
        }

        return TencentBotUtil.getAutoReply(content);
    }

    @Override
    public String study(String fromGroup, String content) {
        if (!content.startsWith("学习")){
            return CommonReply.AUTO_FAIL_REPLY;
        }

        String[] strs = content.split(" ");
        if (strs.length != 3&&strs[0] != "学习") {
            return CommonReply.AUTO_STUDY_FORMAT_REPLY;
        }

        if (strs[2].startsWith("!") || strs[2].startsWith("！")) {
            return CommonReply.AUTO_STUDY_ERROR_CONTENT_REPLY;
        }

        MessageAutoReply messageAutoReply = new MessageAutoReply();
        messageAutoReply.setFromGroup(fromGroup);
        messageAutoReply.setMessageKey(strs[1].toLowerCase());
        messageAutoReply.setMessageValue(strs[2]);

        if (CommonConsts.getInstance().autoReplyDictionary.containsKey(fromGroup)) {
            Map<String,String> map = CommonConsts.getInstance().autoReplyDictionary.get(fromGroup);
            if (map.containsKey(messageAutoReply.getMessageKey())) {
                //这个值曾经设置过，进入update流程
                int messageId = messageAutoReplyDao.getMessageId(messageAutoReply);
                messageAutoReply.setId(messageId);
                messageAutoReplyDao.edit(messageAutoReply);

                //内存字典刷新
                CommonConsts.getInstance().autoReplyDictionary = messageAutoReplyDao.selectAll();

                return CommonReply.AUTO_STUDY_AGAIN;

            }
        }
        messageAutoReplyDao.add(messageAutoReply);

        //内存字典刷新
        CommonConsts.getInstance().autoReplyDictionary = messageAutoReplyDao.selectAll();

        return CommonReply.AUTO_STUDY_FIRST_TIME;
    }
}
