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

        //????????????????????????????????????
        switch (smallGameType) {
            case PROVERB_DRAGON:
                resultContent = handleProverbDragon(content,fromUser);
                break;
            case GUESS_SONG:
                resultContent = handleGuessSong(content,fromUser);
                break;
            default:
        }

        //0:?????? 1:??????
        int msgType = 0;
        boolean isBlank = StringUtils.isBlank(resultContent);
        if ((content.startsWith("!") || content.startsWith("???")) && isBlank) {
            System.out.println("??????????????????????????????");
            content = content.substring(1);
            char c = content.charAt(0);
            switch (c) {
                case '???':
                    resultContent = scheduleService.getSalmonRunSchedule(content, 0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '???':
                    resultContent = scheduleService.getBattleSchedule(content,0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '???':
                    resultContent = checkNextTime(content);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '???':
                case '???':
                    resultContent = checkMode(content,0);
                    if (resultContent.startsWith("http://")){
                        msgType = 1;
                    }
                    break;
                case '???':
                case '???':
                case '???':
                case '???':
                case '???':
                case '???':
                    resultContent = notifySingleOrTeam(content);
                    break;
                case '???':
                    if (content.startsWith("??????")) {
                        resultContent = messageAutoReplyService.study(chatRoomId,content);
                        break;
                    }
                case '???':
                    if ("????????????".equals(content)) {
                        //??????????????????????????????
                        resultContent = randomWeaponService.getRandomWeapon();
                        msgType = 1;
                        break;
                    }
                    if (content.startsWith("????????????")){
                        resultContent = summon(content,1,fromUser);
                        break;
                    }
                case '???':
                case '???':
                case '???':
                    if (checkPositionRandomValid(content)) {
                        //????????????????????????????????????
                        resultContent = randomWeaponService.getPositionRandomWeapon(content);
                        msgType = 1;
                        break;
                    }
                case '???':
                case '???':
                    if (content.length() == 2 && content.charAt(1) == '???'){
                        //????????????????????????
                        resultContent = imageService.getBeautyImgUrl();
                        msgType = 1;
                        break;
                    }
                case '???':
                    if (content.startsWith("????????????")) {
                        resultContent = summon(content,0,fromUser);
                        break;
                    }
                case '???':
                    if ("??????".equals(content)) {
                        resultContent = userSummonService.summon(fromUser);
                        break;
                    } else if (content.startsWith("??????")) {
                        resultContent = CommonReply.AUTO_SUMMON_WRONG_STR;
                        break;
                    }
                case '???':
                    if (content.startsWith("??????")) {
                        resultContent = dropPet(content,fromUser);
                        break;
                    }
                case '???':
                    if (content.startsWith("??????")) {
                        resultContent = handleSwitch(content,chatRoomId,fromUser);
                        break;
                    }
                case '???':
                    if ("????????????".equals(content)) {
                        resultContent = startProverbDragon();
                        break;
                    }
                case '???':
                    if ("?????????".equals(content)) {
                        resultContent = startGuessSong();
                        break;
                    }
                case '???':
                    if ("??????".equals(content)) {
                        resultContent = lotteryService.getLotteryTicket(fromUser,chatRoomId);
                        break;
                    }
                case '???':
                    if ("??????".equals(content)) {
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

        charSet.remove('???');
        charSet.remove('???');
        charSet.remove('???');
        return charSet.size() == 0;
    }

    private String checkNextTime(String content) {
        int nextCount = 0;
        while (content.length() > 0) {
            switch (content.charAt(0)) {
                case '???':
                    nextCount++;
                    content = content.substring(1);
                    break;
                case '???':
                    if (nextCount <9) {
                        return scheduleService.getSalmonRunSchedule(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                case '???':
                    if (nextCount <9) {
                        return scheduleService.getBattleSchedule(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                case '???':
                case '???':
                case '???':
                case '???':
                case '???':
                case '???':
                    return notifySingleOrTeam(content);
                case '???':
                case '???':
                    if (nextCount <9) {
                        return checkMode(content,nextCount);
                    } else {
                        return CommonReply.AUTO_SCHEDULE_TOO_MANY;
                    }
                default:
                    return CommonReply.AUTO_SCHEDULE_INPUT_ERROR;
            }
        }
        //?????????????????????????????????
        return CommonReply.AUTO_FAIL_REPLY;
    }

    private String notifySingleOrTeam(String content) {
        if ("??????".equals(content)|| "??????".equals(content)|| "??????".equals(content)
                || "??????".equals(content)|| "???".equals(content)|| "???".equals(content)) {
            return CommonReply.AUTO_SCHEDULE_SINGLE_OR_GROUP;
        }
        return CommonReply.AUTO_FAIL_REPLY;
    }

    private String checkMode(String content,int nextTime) {
        GameType gameType;
        Mode mode;
        if (content.startsWith("??????")) {
            gameType = GameType.RANKED_BATTLE;
        } else if (content.startsWith("??????")) {
            gameType = GameType.LEAGUE_BATTLE;
        } else {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        int plusOrMinusPosition = content.indexOf("+") > 0?content.indexOf("+"):content.indexOf("-");
        int timeZone = 0;

        if (plusOrMinusPosition > 0) {
            //????????????
            try {
                timeZone = Integer.parseInt(content.substring(plusOrMinusPosition));
                content = content.substring(0,plusOrMinusPosition);
            } catch (Exception e) {
                return  content.substring(0,plusOrMinusPosition) + CommonReply.AUTO_SCHEDULE_ERROR_TIMEZONE;
            }
        }

        content = content.substring(2);
        switch (content) {
            case "??????":
                mode = Mode.SPLAT_ZONES;
                break;
            case "??????":
            case "???":
                mode = Mode.TOWER_CONTROL;
                break;
            case "??????":
            case "???":
                mode = Mode.RAINMAKER;
                break;
            case "??????":
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
        //????????????
        String[] strings = content.split(" ");
        if (strings.length != 2) {
            return CommonReply.AUTO_SUMMON_WRONG_FORMAT;
        }

        if (!("????????????".equals(strings[0])|| "????????????".equals(strings[0]))) {
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
        //????????????
        String[] strings = content.split(" ");
        if (strings.length != 2) {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        if (!("??????".equals(strings[0]))) {
            return CommonReply.AUTO_FAIL_REPLY;
        }

        return userSummonService.dropPet(fromUser,strings[1]);
    }

    private String handleSwitch(String content,String fromGroup, String fromUser) {
        String[] strs = content.split(" ");
        if(!"??????".equals(strs[0])) {
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
        //?????????on off
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

    /**-------------------------------------------?????????????????????----------------------------------------*/
    private String startProverbDragon() {
        System.out.println("isProverbDragonOn = " + smallGameType);
        if (smallGameType.equals(SmallGameType.PROVERB_DRAGON)) {
            //???????????????
            Map.Entry<String,String> entry =CommonUtils.getInstance().getTail(miniGameExecutingMap);
            return CommonReply.AUTO_PROVERB_DRAGON_ALREADY_START + entry.getKey();
        }

        if (!smallGameType.equals(SmallGameType.NOPE)) {
            //?????????????????????
            return CommonReply.AUTO_SMALL_GAME_RUNNING;
        }

        System.out.println("??????????????????");

        //???????????????????????????????????????
        String [] keys = CommonConsts.getInstance().proverbInfos.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];

        System.out.println("randomKey = " + randomKey);
        String sb = "????????????????????????????????????????????????\n" +
                randomKey + "\n" + "????????????????????????60???";

        this.lastMiniGameTime = System.currentTimeMillis();
        this.smallGameType = SmallGameType.PROVERB_DRAGON;
        this.miniGameExecutingMap.clear();
        this.miniGameExecutingMap.put(randomKey,"");

        resetChatRoomInfo();

        //???????????????????????????
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
                //?????????????????????????????????????????????????????????????????????????????????????????? ?????????????????????
                if (miniGameExecutingMap.containsKey(content)) {
                    return "??????????????????????????????????????????" + oldProverb.getWord();
                }
                miniGameExecutingMap.put(content,fromUser);
                lastMiniGameTime = System.currentTimeMillis();
                resetChatRoomInfo();
                return "???????????????????????????????????????" + content;
            }
        }
        resetChatRoomInfo();

        return "";
    }


    public void finishProverbDragon(){
        System.out.println("??????????????????????????????");
        //????????????????????????
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
        sb.append("???????????????????????????????????????");
        sb.append(miniGameExecutingMap.size());
        sb.append("???\n");
        for (String key:sorted.keySet()) {

            GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
            groupMemberInfo.setChatRoomId(chatRoomId);
            groupMemberInfo.setUserName(key);

            String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);
            sb.append(nickName);
            sb.append("???");
            sb.append(sorted.get(key));
            sb.append("???");
            sb.append("\n");
        }

        miniGameExecutingMap.clear();
        resetChatRoomInfo();

        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    /**-------------------------------------------??????????????????----------------------------------------*/
    private String startGuessSong(){
        System.out.println("miniGameStatus = " + smallGameType);
        if (smallGameType.equals(SmallGameType.GUESS_SONG)) {
            //???????????????
            return CommonReply.AUTO_GUESS_SONG_ALREADY_START;
        }

        if (!smallGameType.equals(SmallGameType.NOPE)) {
            //?????????????????????
            return CommonReply.AUTO_SMALL_GAME_RUNNING;
        }

        System.out.println("???????????????");

        SongInfo songInfo = getNextSong();
        String sb = "???????????????????????????????????????60????????????????????????";

        this.lastMiniGameTime = System.currentTimeMillis();
        this.smallGameType = SmallGameType.GUESS_SONG;
        this.miniGameExecutingMap.clear();
        this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");

        //??????????????????????????????????????????????????????????????????????????????ID
        this.miniGameSessionId = UUID.randomUUID().toString();

        resetChatRoomInfo();
        sendSongVoiceMsg(songInfo.getFile());

        //???????????????????????????
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
                sb.append("???????????????????????????").append(content).append("\n");
                sb.append("????????????").append(singer).append("\n");
                sb.append("?????????").append(count).append("????????????").append("\n");

                if (count <= 4) {
                    //????????????????????????????????????
                    count++;
                    SongInfo songInfo = getNextSong();

                    sb.append("??????????????????").append(count).append("???????????????????????????60????????????????????????");

                    this.lastMiniGameTime = System.currentTimeMillis();
                    this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");
                    this.alreadyNotified = false;

                    resetChatRoomInfo();
                    sendSongVoiceMsg(songInfo.getFile());
                    return sb.toString();
                } else {
                    //???????????????
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

        System.out.println("?????????????????????30???");
        StringBuilder sb = new StringBuilder();
        sb.append("??????????????????30?????????????????????").append("\n");
        String[] answers = getAnswerStrings(CommonUtils.getInstance().getTail(miniGameExecutingMap).getKey());
        String answer = answers[0];
        String singer = answers[1];

        StringBuilder sb1 = new StringBuilder();
        sb1.append(answer.charAt(0));
        for (int i = 1; i < answer.length(); i++) {
            sb1.append(" _");
        }

        sb.append("???????????????").append(sb1).append("\n");
        sb.append("????????????").append(singer);

        this.alreadyNotified = true;
        resetChatRoomInfo();

        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    public void finishGuessSong(String sessionId) {
        if (!sessionId.equals(this.miniGameSessionId)) {
            return;
        }
        System.out.println("?????????????????????");

        String[] answers = getAnswerStrings(CommonUtils.getInstance().getTail(miniGameExecutingMap).getKey());
        String answer = answers[0];
        String singer = answers[1];

        //????????????????????????
        StringBuilder sb = new StringBuilder();
        int count = miniGameExecutingMap.size();

        sb.append("?????????").append(count).append("?????????????????????????????????????????????");
        sb.append(answer).append("\n");
        sb.append("????????????").append(singer).append("\n");

        if (miniGameExecutingMap.size() < 5) {
            //???????????????????????????????????????
            SongInfo songInfo = getNextSong();

            count++;
            sb.append("??????????????????").append(count).append("???????????????????????????60????????????????????????");

            this.lastMiniGameTime = System.currentTimeMillis();
            this.miniGameExecutingMap.put(getSongNameKey(songInfo.getSongName(),songInfo.getSingerName()),"");
            this.alreadyNotified = false;

            resetChatRoomInfo();
            sendSongVoiceMsg(songInfo.getFile());

            //?????????????????????????????????????????????????????????????????????
            GuessSongAlarmClockTask task = new GuessSongAlarmClockTask(chatRoomId,this.miniGameSessionId);
            new Thread(task).start();
        } else {
            //???????????????
            sb.append(closeGuessSongGame());
        }
        CommonUtils.getInstance().sendTextMsg(chatRoomId,sb.toString(),api_url);
    }

    private String closeGuessSongGame() {
        //????????????????????????????????????
        //?????????????????????
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

        sb.append("???????????????????????????????????????").append("\n");

        for (String key:sorted.keySet()) {
            GroupMemberInfo groupMemberInfo = new GroupMemberInfo();
            groupMemberInfo.setChatRoomId(chatRoomId);
            groupMemberInfo.setUserName(key);

            String nickName = groupMemberInfoDao.queryNickName(groupMemberInfo);
            sb.append(nickName);
            sb.append("???");
            sb.append(sorted.get(key));
            sb.append("???");
            sb.append("\n");
        }

        miniGameExecutingMap.clear();
        resetChatRoomInfo();

        return sb.toString();
    }

    /**?????????????????????*/
    private SongInfo getNextSong() {
        //?????????????????????
        SongInfo songInfo;
        do {
            int id = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().songIds);
            songInfo = songInfoDao.getSongDetail(id);
            //?????????????????????
        } while (miniGameExecutingMap.containsKey(songInfo.getSongName()));

        return songInfo;
    }

    private String getSongNameKey(String songName,String singerName) {
        return songName + ";;" + getSingerName(singerName);
    }

    private String[] getAnswerStrings(String answer) {
        return answer.split(";;");
    }

    /**??????????????????????????????????????????????????????????????????????????????*/
    private String getSingerName(String singer) {
        if (StringUtils.isEmpty(singer)) {
            return "??????";
        }
        return singer;
    }

    private void sendSongVoiceMsg(String fileName) {
        //?????????????????????????????????
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
