package com.ame.mihoyosign.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Log4j2
@Service
public class MessageService {
    @Resource
    private RestTemplate restTemplate;
    @Value("${app-config.url}")
    private String url;
    @Value("${app-config.log-group-id}")
    private long LogGroupId;

    public void sendMsg(long id, String msg, String msgType, String idType) {
        try {
            JSONObject json = new JSONObject();
            json.put(idType + "_id", id);
            json.put("message", msg);
            JSONObject re = restTemplate.postForObject(url + "/send_" + msgType + "_msg", json, JSONObject.class);
            if (re == null || re.getIntValue("retcode") != 0) {
                log.warn("发送消息失败");
            }
            log.info("-->  QQ[{}]  Message:[{}]", id, msg.replace("\n", " "));
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("发送消息失败");
        }
    }

    public void sendPrivateMsg(long id, String msg) {
        sendMsg(id, msg, "private", "user");
    }

    public void sendGroupMsg(long id, String msg) {
        sendMsg(id, msg, "group", "group");
    }

    @Async
    public void sendLogToGroup(String msg) {
        log.info(msg.replace("\n", " "));
        if (LogGroupId != -1) {
            sendGroupMsg(LogGroupId, msg);
        }
    }

}
