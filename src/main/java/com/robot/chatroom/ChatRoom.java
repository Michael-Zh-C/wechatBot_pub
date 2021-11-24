package com.robot.chatroom;

import com.alibaba.fastjson.JSONObject;
import com.robot.common.*;
import com.robot.dao.GroupAnalysisSwitchDao;
import com.robot.dao.GroupMemberInfoDao;
import com.robot.dao.SongInfoDao;
import com.robot.enums.GameType;
import com.robot.enums.Mode;
import com.robot.enums.SmallGameType;
import com.robot.pojo.GroupAnalysisSwitch;
import com.robot.pojo.GroupMemberInfo;

import com.robot.pojo.ProverbInfo;
import com.robot.pojo.SongInfo;
import com.robot.pool.HttpClientResult;
import com.robot.pool.HttpClientUtils;
import com.robot.service.*;
import com.robot.threads.GuessSongAlarmClockTask;
import com.robot.threads.GuessSongSendVoiceMsgTask;
import com.robot.threads.ProverbDragonAlarmClockTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import java.util.*;

/**
 * @author Michael
 */
public class ChatRoom {
    private final RandomWeaponService randomWeaponService = GetBeanUtil.getBean(RandomWeaponService.class);
    private final ImageService imageService = GetBeanUtil.getBean(ImageService.class);
    private final ScheduleService scheduleService = GetBeanUtil.getBean(ScheduleService.class);
    private final MessageAutoReplyService messageAutoReplyService = GetBeanUtil.getBean(MessageAutoReplyService.class);
    private final UserSummonService userSummonService = GetBeanUtil.getBean(UserSummonService.class);
    private final GroupMemberInfoDao groupMemberInfoDao = GetBeanUtil.getBean(GroupMemberInfoDao.class);
    private final GroupAnalysisSwitchDao groupAnalysisSwitchDao = GetBeanUtil.getBean(GroupAnalysisSwitchDao.class);
    private final SongInfoDao songInfoDao = GetBeanUtil.getBean(SongInfoDao.class);
    private final LotteryService lotteryService = GetBeanUtil.getBean(LotteryService.class);
    private final String chatRoomId;
    private final String api_url;

    private final LinkedHashMap<String,String> miniGameExecutingMap = new LinkedHashMap<>();
    private SmallGameType smallGameType = SmallGameType.NOPE;
    private long lastMiniGameTime = 0L;
    private String miniGameSessionId;

    private boolean alreadyNotified = false;

    public ChatRoom(String chatRoomId,String apiUrl) {
        this.chatRoomId = chatRoomId;
        this.api_url = apiUrl;
    }

    public void handleMessage(String content,String fromUser) {
        String resultContent = "";
        System.out.println("content = " + content);
        System.out.println("fromGroup = " + chatRoomId);
        System.out.println("api_urp = " + api_url);

        //如果小游戏正在运行，处理
        switch (smallGameType) {
            case PROVERB_DRAGON:
                resultContent = handleProverbDragon(content,fromUser);
                break;
            case GUESS_SONG:
                resultContent = handleGuessSong(content,fromUser);
                break;
            default:
        }

        //0:文字 1:图片
        int msgType = 0;
        boolean isBlank = StringUtils.isBlank(resultContent);
        if ((content.startsWith("!") || content.startsWith("！")) && isBlank) {
            System.out.println("以叹号起始，开始处理");
            content = content.substring(1);
            char c = content.charAt(0);
            switch (c) {
                case '工':
                    resultContent = scheduleService.getSalmonRunSchedule(content, 0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '图':
                    resultContent = scheduleService.getBattleSchedule(content,0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '下':
                    resultContent = checkNextTime(content);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '单':
                case '组':
                    resultContent = checkMode(content,0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '区':
                case '抢':
                case '蛤':
                case '推':
                case '鱼':
                case '塔':
                    resultContent = notifySingleOrTeam(content);
                    break;
                case '学':
                    if (content.startsWith("学习")) {
                        resultContent = messageAutoReplyService.study(chatRoomId,content);
                        break;
                    }
                case '随':
                    if ("随机武器".equals(content)) {
                        //进入随机武器处理流程
                        resultContent = randomWeaponService.getRandomWeapon();
                        msgType = 1;
                        break;
                    }
                    if (content.startsWith("随机召唤")){
                        resultContent = summon(content,1,fromUser);
                        break;
                    }
                case '前':
                case '中':
                case '后':
                    if (checkPositionRandomValid(content)) {
                        //进入位置选择武器处理流程
                        resultContent = randomWeaponService.getPositionRandomWeapon(content);
                        msgType = 1;
                        break;
                    }
                case '涩':
                case '美':
                    if (content.length() == 2 && content.charAt(1) == '图'){
                        //进入涩图处理流程
                        resultContent = imageService.getBeautyImgUrl();
                        msgType = 1;
                        break;
                    }
                case '本':
                    if (content.startsWith("本命召唤")) {
                        resultContent = summon(content,0,fromUser);
                        break;
                    }
                case '召':
                    if ("召唤".equals(content)) {
                        resultContent = userSummonService.summon(fromUser);
                        break;
                    } else if (content.startsWith("召唤")) {
                        resultContent = CommonReply.AUTO_SUMMON_WRONG_STR;
                        break;
                    }
                case '放':
                    if (content.startsWith("放生")) {
                        resultContent = dropPet(content,fromUser);
                        break;
                    }
                case '播':
                    if (content.startsWith("播报")) {
                        resultContent = handleSwitch(content,chatRoomId,fromUser);
                        break;
                    }
                case '成':
                    if ("成语接龙".equals(content)) {
                        resultContent = startProverbDragon();
                        break;
                    }
                case '猜':
                    if ("猜歌名".equals(content)) {
                        resultContent = startGuessSong();
                        break;
                    }
                case '抽':
                    if ("抽签".equals(content)) {
                        resultContent = lotteryService.getLotteryTicket(fromUser,chatRoomId);
                        break;
                    }
                case '解':
                    if ("解签".equals(content)) {
                        resultContent = lotteryService.getLotteryAnswer(fromUser,chatRoomId);
                        break;
                    }
                default:
                    resultContent = messageAutoReplyService.handleAutoReply(chatRoomId,content);
            }

        }

        System.out.println("resultContent = " + resultContent);
        if (StringUtils.isNotBlank(resultContent)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("wId", CommonConsts.getInstance().wId);
            jsonObject.put("wcId", chatRoomId);
            jsonObject.put("content", resultContent);
            String sendUrl;
            if (msgType == 0) {
                sendUrl = api_url + "/sendText";
            } else {
                sendUrl = api_url + "/sendImage";
            }
            Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, CommonConsts.getInstance().authorization);
            HttpClientResult result = HttpClientUtils.doPostJson(sendUrl, jsonObject.toJSONString(), header);
            assert result != null;
            System.out.println("send message result = " + result.getContent());

        }
    }

    private boolean checkPositionRandomValid(String content) {
        if (content.length() != 4) {
            return false;
        }

        Set<Character> charSet = new HashSet<>();
        for (char c:content.toCharArray()){
            charSet.add(c);
        }

        charSet.remove('前');
        charSet.remove('中');
        charSet.remove('后');
        return charSet.size() == 0;
    }

    private String checkNextTime(String content) {
        int nextCount = 0;
        while (content.length() > 0) {
            switch (content.charAt(0)) {
                case '下':
                    nextCount++;
                    content = content.substring(1);
                    break;
                case '工':
                    if (nextCount <9) {
                        return scheduleService.getSalmonRunSchedule(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                case '图':
                    if (nextCount <9) {
                        return scheduleService.getBattleSchedule(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                case '区':
                case '抢':
                case '蛤':
                case '推':
                case '鱼':
                case '塔':
                    return notifySingleOrTeam(content);
                case '单':
                case '组':
                    if (nextCount <9) {
                        return checkMode(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                default:
                    return CommonReply.AUTO_SCHEDULE_INPUT_ERROR;
            }
        }
        //输入有误，机器人有尊严
        return CommonReply.AUTO_FAIL_REPLY;
    }

    private String notifySingleOrTeam(String content) {
        if ("区域".equals(content)|| "抢鱼".equals(content)|| "推塔".equals(content)
                || "蛤蜊".equals(content)|| "塔".equals(content)|| "鱼".equals(content)) {
            return CommonReply.AUTO_SCHEDULE_SINGLE_OR_GROUP;
        }
        return CommonReply.AUTO_FAIL_REPLY;
    }

    private String checkMode(String content,int nextTime) {
        GameType gameType;
        Mode mode;
        if (content.startsWith("单排")) {
            gameType = GameType.RANKED_BATTLE;
        } else if (content.startsWith("组排")) {
            gameType = GameType.LEAGUE_BATTLE;
        } else {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        int plusOrMinusPosition = content.indexOf("+") > 0?content.indexOf("+"):content.indexOf("-");
        int timeZone = 0;

        if (plusOrMinusPosition > 0) {
            //处理时差
            try {
                timeZone = Integer.parseInt(content.substring(plusOrMinusPosition));
                content = content.substring(0,plusOrMinusPosition);
            } catch (Exception e) {
                return  content.substring(0,plusOrMinusPosition) + CommonReply.AUTO_SCHEDULE_ERROR_TIMEZONE;
            }
        }

        content = content.substring(2);
        switch (content) {
            case "区域":
                mode = Mode.SPLAT_ZONES;
                break;
            case "推塔":
            case "塔":
                mode = Mode.TOWER_CONTROL;
                break;
            case "抢鱼":
            case "鱼":
                mode = Mode.RAINMAKER;
                break;
            case "蛤蜊":
                mode = Mode.CLAM_BLITZ;
                break;
            default:
                if (gameType == GameType.RANKED_BATTLE) {
                    return CommonReply.AUTO_SCHEDULE_ERROR_SINGLE;
                } else {
                    return CommonReply.AUTO_SCHEDULE_ERROR_GROUP;
                }
        }

        return scheduleService.getTypeModeSchedule(gameType,mode,nextTime,timeZone);
    }

    private String summon(String content, int randomFlag, String fromUser) {
        //字段校验
        String[] strings = content.split(" ");
        if (strings.length != 2) {
            return CommonReply.AUTO_SUMMON_WRONG_FORMAT;
        }

        if (!("本命召唤".equals(strings[0])|| "随机召唤".equals(strings[0]))) {
            return CommonReply.AUTO_SUMMON_WRONG_FORMAT;
        }

        if (randomFlag == 0) {
            return userSummonService.trueSummon(fromUser,strings[1]);
        } else if (randomFlag == 1) {
            return userSummonService.randomSummon(fromUser,strings[1]);
        }

        return CommonReply.AUTO_FAIL_REPLY;
    }

    private String dropPet(String content,String fromUser) {
        //字段校验
        String[] strings = content.split(" ");
        if (strings.length != 2) {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        if (!("放生".equals(strings[0]))) {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        return userSummonService.dropPet(fromUser,strings[1]);
    }

    private String handleSwitch(String content,String fromGroup, String fromUser) {
        String[] strs = content.split(" ");
        if(!"播报".equals(strs[0])) {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
        groupMemberInfo.setChatRoomId(fromGroup);
        groupMemberInfo.setUserName(fromUser);
        int isManager = groupMemberInfoDao.queryIsManager(groupMemberInfo);

        if (isManager != 1) {
            return CommonReply.AUTO_GROUP_SWITCH_NOT_MANAGER;
        }

        if (strs.length != 2) {
            return CommonReply.AUTO_GROUP_SWITCH_WRONG_FORMAT;
        }

        int state;
        //只支持on off
        switch (strs[1]) {
            case "on" :
                state = 0;
                break;
            case "off":
                state = 1;
                break;
            default:
                return CommonReply.AUTO_GROUP_SWITCH_WRONG_FORMAT;
        }

        GroupAnalysisSwitch groupAnalysisSwitch = new GroupAnalysisSwitch();
        groupAnalysisSwitch.setGroupId(fromGroup);
        groupAnalysisSwitch.setState(state);
        groupAnalysisSwitchDao.edit(groupAnalysisSwitch);

        if (state == 0) {
            return CommonReply.AUTO_GROUP_SWITCH_ON_SUCCESS;
        }
        return CommonReply.AUTO_GROUP_SWITCH_OFF_SUCCESS;
    }

    /**-------------------------------------------成语接龙小游戏----------------------------------------*/
    private String startProverbDragon() {
        System.out.println("isProverbDragonOn = " + smallGameType);
        if (smallGameType.equals(SmallGameType.PROVERB_DRAGON)) {
            //游戏已开始
            Map.Entry<String,String> entry =CommonUtils.getInstance().getTail(miniGameExecutingMap);
            return CommonReply.AUTO_PROVERB_DRAGON_ALREADY_START + entry.getKey();
        }

        if (!smallGameType.equals(SmallGameType.NOPE)) {
            //其他游戏进行中
            return CommonReply.AUTO_SMALL_GAME_RUNNING;
        }

        System.out.println("成语接龙开始");

        //获取随机一个成语作为第一个
        String [] keys = CommonConsts.getInstance().proverbInfos.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];

        System.out.println("randomKey = " + randomKey);
        String sb = "成语接龙游戏开始，第一个成语为：\n" +
                randomKey + "\n" + "每个成语之间限时60秒";

        this.lastMiniGameTime = System.currentTimeMillis();
        this.smallGameType = SmallGameType.PROVERB_DRAGON;
        this.miniGameExecutingMap.clear();
        this.miniGameExecutingMap.put(randomKey,"");

        resetChatRoomInfo();

        //开启线程，定时任务
        ProverbDragonAlarmClockTask task = new ProverbDragonAlarmClockTask(chatRoomId);
        new Thread(task).start();


        return sb;
    }

    private String handleProverbDragon(String content,String fromUser) {
        if (!CommonConsts.getInstance().proverbInfos.containsKey(content)){
            return "";
        }

        synchronized (this){
            ProverbInfo newProverb = CommonConsts.getInstance().proverbInfos.get(content);
            Map.Entry<String,String> entry =CommonUtils.getInstance().getTail(miniGameExecutingMap);
            ProverbInfo oldProverb = CommonConsts.getInstance().proverbInfos.get(entry.getKey());
            System.out.println("newProverb = " + newProverb.getWord() + ",oldProverb = " + oldProverb.getWord());

            String[] newPinyin = newProverb.getPinyin().split(" ");
            String[] oldPinyin = oldProverb.getPinyin().split(" ");



            if (newPinyin[0].equals(oldPinyin[oldPinyin.length - 1]) || oldProverb.getWord().charAt(oldProverb.getWord().length() - 1) == newProverb.getWord().charAt(0)) {
                //最后一个字和第一个字同音，或者为同一个字（多音字可以不同音） 判断为校验通过
                if (miniGameExecutingMap.containsKey(content)) {
                    return "这个成语用过了，当前成语为：" + oldProverb.getWord();
                }
                miniGameExecutingMap.put(content,fromUser);
                lastMiniGameTime = System.currentTimeMillis();
                resetChatRoomInfo();
                return "成语接龙成功！当前成语为：" + content;
            }
        }
        resetChatRoomInfo();

        return "";
    }


    public void finishProverbDragon(){
        System.out.println("成语接龙时间到，结束");
        //重置成语接龙状态
        lastMiniGameTime = 0L;
        smallGameType = SmallGameType.NOPE;

        Map.Entry<String,String> entry = CommonUtils.getInstance().getHead(miniGameExecutingMap);
        miniGameExecutingMap.remove(entry.getKey());

        Map<String, Integer> checkMap = new HashMap<>();
        for (String key: miniGameExecutingMap.keySet()) {
            String value = miniGameExecutingMap.get(key);
            if (checkMap.containsKey(value)) {
                checkMap.put(value,checkMap.get(value) + 1);
            } else {
                checkMap.put(value,1);
            }
        }

        LinkedHashMap<String, Integer> sorted = (LinkedHashMap<String, Integer>) MapSortUtil.sortByValueDesc(checkMap);

        StringBuilder sb = new StringBuilder();
        sb.append("成语接龙游戏结束！总计接龙");
        sb.append(miniGameExecutingMap.size());
        sb.append("次\n");
        for (String key:sorted.keySet()) {

            GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
            groupMemberInfo.setChatRoomId(chatRoomId);
            groupMemberInfo.setUserName(key);

            String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);
            sb.append(nickName);
            sb.append("，");
            sb.append(sorted.get(key));
            sb.append("次");
            sb.append("\n");
        }

        miniGameExecutingMap.clear();
        resetChatRoomInfo();

        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    /**-------------------------------------------猜歌名小游戏----------------------------------------*/
    private String startGuessSong(){
        System.out.println("miniGameStatus = " + smallGameType);
        if (smallGameType.equals(SmallGameType.GUESS_SONG)) {
            //游戏已开始
            return CommonReply.AUTO_GUESS_SONG_ALREADY_START;
        }

        if (!smallGameType.equals(SmallGameType.NOPE)) {
            //其他游戏进行中
            return CommonReply.AUTO_SMALL_GAME_RUNNING;
        }

        System.out.println("猜歌名开始");

        SongInfo songInfo = getNextSong();
        String sb = "猜歌名游戏开始，游戏时间为60秒，请听语音猜歌";

        this.lastMiniGameTime = System.currentTimeMillis();
        this.smallGameType = SmallGameType.GUESS_SONG;
        this.miniGameExecutingMap.clear();
        this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");

        //为了防止游戏提前结束导致计时器混乱，特别增加小游戏的ID
        this.miniGameSessionId = UUID.randomUUID().toString();

        resetChatRoomInfo();
        sendSongVoiceMsg(songInfo.getFile());

        //开启线程，定时任务
        GuessSongAlarmClockTask task = new GuessSongAlarmClockTask(chatRoomId,this.miniGameSessionId);
        new Thread(task).start();

        return sb;
    }

    private String handleGuessSong(String content,String fromUser) {
        synchronized (this){
            String[] answers = getAnswerStrings(CommonUtils.getInstance().getTail(miniGameExecutingMap).getKey());
            String answer = answers[0];
            String singer = answers[1];
            System.out.println("answer = " + answer + ",content = " + content + ",singer = " + singer);

            if (answer.equalsIgnoreCase(content)) {
                StringBuilder sb = new StringBuilder();
                int count = miniGameExecutingMap.size();
                miniGameExecutingMap.put(getSongNameKey(answer,singer),fromUser);
                sb.append("恭喜猜对，答案是：").append(content).append("\n");
                sb.append("歌手是：").append(singer).append("\n");
                sb.append("游戏第").append(count).append("轮已结束").append("\n");

                if (count <= 4) {
                    //游戏还未结束，下一轮开始
                    count++;
                    SongInfo songInfo = getNextSong();

                    sb.append("猜歌名游戏第").append(count).append("轮开始，游戏时间为60秒，请听语音猜歌");

                    this.lastMiniGameTime = System.currentTimeMillis();
                    this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");
                    this.alreadyNotified = false;

                    resetChatRoomInfo();
                    sendSongVoiceMsg(songInfo.getFile());
                    return sb.toString();
                } else {
                    //游戏已结束
                    sb.append(closeGuessSongGame());
                    return sb.toString();
                }
            }
        }

        return "";
    }

    public void notifyGuessSong(String sessionId) {
        if (!sessionId.equals(this.miniGameSessionId)) {
            return;
        }

        if (this.alreadyNotified) {
            return;
        }

        System.out.println("猜歌名本轮还剩30秒");
        StringBuilder sb = new StringBuilder();
        sb.append("本轮游戏还剩30秒，大家加油哦").append("\n");
        String[] answers = getAnswerStrings(CommonUtils.getInstance().getTail(miniGameExecutingMap).getKey());
        String answer = answers[0];
        String singer = answers[1];

        StringBuilder sb1 = new StringBuilder();
        sb1.append(answer.charAt(0));
        for (int i = 1; i < answer.length(); i++) {
            sb1.append(" _");
        }

        sb.append("这首歌是：").append(sb1).append("\n");
        sb.append("歌手是：").append(singer);

        this.alreadyNotified = true;
        resetChatRoomInfo();

        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    public void finishGuessSong(String sessionId) {
        if (!sessionId.equals(this.miniGameSessionId)) {
            return;
        }
        System.out.println("猜歌名本轮结束");

        String[] answers = getAnswerStrings(CommonUtils.getInstance().getTail(miniGameExecutingMap).getKey());
        String answer = answers[0];
        String singer = answers[1];

        //判断游戏是否结束
        StringBuilder sb = new StringBuilder();
        int count = miniGameExecutingMap.size();

        sb.append("游戏第").append(count).append("轮已结束，本轮无人猜对，答案是");
        sb.append(answer).append("\n");
        sb.append("歌手是：").append(singer).append("\n");

        if (miniGameExecutingMap.size() < 5) {
            //游戏未结束，下一轮游戏开始
            SongInfo songInfo = getNextSong();

            count++;
            sb.append("猜歌名游戏第").append(count).append("轮开始，游戏时间为60秒，请听语音猜歌");

            this.lastMiniGameTime = System.currentTimeMillis();
            this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");
            this.alreadyNotified = false;

            resetChatRoomInfo();
            sendSongVoiceMsg(songInfo.getFile());

            //由于上一个定时器已经到期结束，重新开始定时任务
            GuessSongAlarmClockTask task = new GuessSongAlarmClockTask(chatRoomId,this.miniGameSessionId);
            new Thread(task).start();
        } else {
            //游戏已结束
            sb.append(closeGuessSongGame());
        }
        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    private String closeGuessSongGame() {
        //小游戏结束，进入结算流程
        //重置小游戏状态
        this.lastMiniGameTime = 0L;
        this.smallGameType = SmallGameType.NOPE;
        this.alreadyNotified = false;
        this.miniGameSessionId = "";

        Map<String, Integer> checkMap = new HashMap<>();
        for (String key: miniGameExecutingMap.keySet()) {
            String value = miniGameExecutingMap.get(key);

            if (StringUtils.isNotBlank(value)) {
                if (checkMap.containsKey(value)) {
                    checkMap.put(value,checkMap.get(value) + 1);
                } else {
                    checkMap.put(value,1);
                }
            }
        }

        LinkedHashMap<String, Integer> sorted = (LinkedHashMap<String, Integer>) MapSortUtil.sortByValueDesc(checkMap);
        StringBuilder sb = new StringBuilder();

        sb.append("猜歌名小游戏结束，成绩为：").append("\n");

        for (String key:sorted.keySet()) {
            GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
            groupMemberInfo.setChatRoomId(chatRoomId);
            groupMemberInfo.setUserName(key);

            String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);
            sb.append(nickName);
            sb.append("，");
            sb.append(sorted.get(key));
            sb.append("次");
            sb.append("\n");
        }

        miniGameExecutingMap.clear();
        resetChatRoomInfo();

        return sb.toString();
    }

    /**随机获取一首歌*/
    private SongInfo getNextSong() {
        //获取随机一首歌
        SongInfo songInfo;
        do {
            int id = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().songIds);
            songInfo = songInfoDao.getSongDetail(id);
            //防止出现重复的
        } while (miniGameExecutingMap.containsKey(songInfo.getSongName()));

        return songInfo;
    }

    private String getSongNameKey(String songName,String singerName) {
        return songName + ";;" + getSingerName(singerName);
    }

    private String[] getAnswerStrings(String answer) {
        return answer.split(";;");
    }

    /**数据库中，少部分歌没有歌手，防止为空将它们重置为未知*/
    private String getSingerName(String singer) {
        if (StringUtils.isEmpty(singer)) {
            return "未知";
        }
        return singer;
    }

    private void sendSongVoiceMsg(String fileName) {
        //开启线程，发送语音消息
        GuessSongSendVoiceMsgTask sendVoiceMsgTask = new GuessSongSendVoiceMsgTask(chatRoomId,api_url,fileName);
        new Thread(sendVoiceMsgTask).start();
    }

    public long getLastMiniGameTime() {
        return lastMiniGameTime;
    }

    private void resetChatRoomInfo() {
        CommonConsts.getInstance().chatRooms.put(this.chatRoomId,this);
    }
}
