package com.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.robot.chatroom.ChatRoom;
import com.robot.common.CommonConsts;
import com.robot.common.XmlParser;
import com.robot.dao.ConstellationInfoDao;
import com.robot.dao.SongInfoDao;
import com.robot.enums.Constellation;
import com.robot.log.FileInterfaceLog;
import com.robot.pojo.ConstellationInfo;
import com.robot.pojo.SongInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.StealSongListService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Michael
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Value("${api.url}")
    String api_url;
    @Autowired
    SongInfoDao songInfoDao;
    @Autowired
    ConstellationInfoDao constellationInfoDao;
    @Autowired
    StealSongListService stealSongListService;
    @RequestMapping("/testLog")
    public void testLog(@RequestParam String content) {
        FileInterfaceLog.info(content);
    }

    @RequestMapping("/testGroupMsg")
    public void testGroupMsg(@RequestParam String content) {
        String fromGroup = "21148897471@chatroom";
        String fromUser = "wxid_9wvdljd68byk21";
        if (!CommonConsts.getInstance().chatRooms.containsKey(fromGroup)) {
            ChatRoom chatRoom = new ChatRoom(fromGroup,api_url);
            CommonConsts.getInstance().chatRooms.put(fromGroup,chatRoom);
        }

        ChatRoom chatRoom = CommonConsts.getInstance().chatRooms.get(fromGroup);
        chatRoom.handleMessage(content,fromUser);
    }


    @RequestMapping("/getSong")
    public void getSong(){
        List<Integer> ids = songInfoDao.getIds();

        for (int id:ids) {
            SongInfo songInfo = songInfoDao.getSongDetail(id);
            StringBuffer sb = new StringBuffer();
            sb.append("wx-voice encode -i ");
            sb.append(songInfo.getFile().replace("silk","mp3"));
            sb.append(" -o ");
            sb.append(songInfo.getFile());
            sb.append(" -f silk");

            System.out.println(sb.toString());
        }
    }

    @RequestMapping("/testVoiceMsg")
    public void testVoiceMsg(){
        String fromGroup = "21148897471@chatroom";

        String contentUrl = "http://106.12.174.25:8080/audios/";
        String fileName = "huahuadebaby.silk";


        //发语音消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wId", CommonConsts.getInstance().wId);
        jsonObject.put("wcId", fromGroup);
        jsonObject.put("content", contentUrl + fileName);
        jsonObject.put("length",1);
        String sendUrl = api_url + "/sendVoice";

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
        HttpClientResult reslult = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
        System.out.println("send voice message result = " + reslult.getContent());
    }

    @RequestMapping("/checkSongName")
    public void checkSongName(){
        List<String> list = getFileName();
        for (String fileName : list) {
            StringBuffer sb = new StringBuffer();
            sb.append("wx-voice encode -i ");
            sb.append(fileName).append(".mp3");
            sb.append(" -o ");
            sb.append(fileName).append(".silk");;
            sb.append(" -f silk");
            System.out.println(sb.toString());
        }
    }

    @RequestMapping("/xingzuo")
    public void xingzuo() throws ParseException {
        for (Constellation constellation:Constellation.values()) {
            System.out.println(constellation.getChineseName());
            Map<String,String> params = new HashMap<>();
            params.put("key","bd031e530248692988f6dc4173957ba6");
            params.put("consName",constellation.getChineseName());
            params.put("type","today");
            String url = "http://web.juhe.cn/constellation/getAll";
            HttpClientResult result = HttpClientUtils.doGet(url,params);
            System.out.println("result = " + result);

            JSONObject jsonObject = JSONObject.parseObject(result.getContent());
            ConstellationInfo constellationInfo = new ConstellationInfo();
            System.out.println("json = " + jsonObject);
            constellationInfo.setConstellationName(jsonObject.getString("name"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = sdf.parse(jsonObject.getString("datetime"));
            constellationInfo.setCurrentDate(date.getTime());
            constellationInfo.setAllNum(jsonObject.getInteger("all"));
            constellationInfo.setColor(jsonObject.getString("color"));
            constellationInfo.setHealthNum(jsonObject.getInteger("health"));
            constellationInfo.setLoveNum(jsonObject.getInteger("love"));
            constellationInfo.setMoneyNum(jsonObject.getInteger("money"));
            constellationInfo.setLuckyNumber(jsonObject.getInteger("number"));
            constellationInfo.setqFriend(jsonObject.getString("QFriend"));
            constellationInfo.setSummary(jsonObject.getString("summary"));
            constellationInfo.setWorkNum(jsonObject.getInteger("work"));

            constellationInfoDao.add(constellationInfo);
        }
    }

    @RequestMapping("/getXingZuo")
    public void getXingZuo() {
        String constellation = "天秤座";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String time = sdf.format(date);
        ConstellationInfo constellationInfo = constellationInfoDao.queryToday(time,constellation);

        StringBuffer sb = new StringBuffer();
        sb.append(constellationInfo.getConstellationName()).append("今日运势：").append("\n");
        sb.append("综合指数：").append(constellationInfo.getAllNum()).append("\n");
        sb.append("健康指数：").append(constellationInfo.getHealthNum()).append("\n");
        sb.append("爱情指数：").append(constellationInfo.getLoveNum()).append("\n");
        sb.append("财运指数：").append(constellationInfo.getMoneyNum()).append("\n");
        sb.append("工作指数：").append(constellationInfo.getWorkNum()).append("\n");
        sb.append("幸运数字：").append(constellationInfo.getLuckyNumber()).append("\n");
        sb.append("速配星座：").append(constellationInfo.getqFriend()).append("\n");
        sb.append("总结：").append(constellationInfo.getSummary()).append("\n");

        System.out.println(sb.toString());
    }

    @RequestMapping("/downloadVoice")
    public void downloadVoice() {
        JSONObject jsonObject= JSONObject.parseObject("{\"account\":\"13261692712\",\"data\":{\"content\":\"<msg><voicemsg endflag=\\\"1\\\" length=\\\"62291\\\" voicelength=\\\"20000\\\" clientmsgid=\\\"498836e29c30ea74769b7165acfe82ed20231982895@chatroom1009_1635251734\\\" fromusername=\\\"wxid_hzmzd2gcc86j22\\\" downcount=\\\"0\\\" cancelflag=\\\"0\\\" voiceformat=\\\"4\\\" forwardflag=\\\"0\\\" bufid=\\\"4399410266807468461\\\" /></msg>\",\"fromGroup\":\"20231982895@chatroom\",\"fromUser\":\"wxid_hzmzd2gcc86j22\",\"length\":1853,\"msgId\":1730936409,\"newMsgId\":8617230325198033628,\"self\":false,\"timestamp\":1635251734,\"toUser\":\"wxid_w88ljdu240t112\",\"voiceLength\":1347,\"wId\":\"e834797c-9e74-4b65-92ee-97d7c1112699\"},\"messageType\":12,\"wcId\":\"wxid_w88ljdu240t112\"}");

        String messageType = jsonObject.getString("messageType");
        System.out.println("messageType = " + messageType);

        JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.getString("data"));

        String fromGroup = jsonObject1.getString("fromGroup");
        String fromUser = jsonObject1.getString("fromUser");

        long msgId = jsonObject1.getLong("msgId");
        System.out.println("fromGroup = " + fromGroup + ",fromUser = " + fromUser + ",msgId = " + msgId);

        String content = jsonObject1.getString("content");
        Map<String,Object> map = new HashMap<>();
        try {
            map = XmlParser.parseXml(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String length = (String) map.get("length");
        String bufId = (String) map.get("bufid");

        stealSongListService.getSongVoiceMsg(msgId,length,bufId,fromUser);
    }

    private List<String> getFileName() {
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
                list.add(fs.getName().split("\\.")[0]);
            }
        }
        return list;
    }
}
