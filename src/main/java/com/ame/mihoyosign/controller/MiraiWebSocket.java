package com.ame.mihoyosign.controller;

import com.ame.mihoyosign.service.MiraiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


@Slf4j
@ServerEndpoint(value = "/mihoyosign")
@Controller
public class MiraiWebSocket {

    //记录当前在线连接数  private static final AtomicInteger onlineCount = new AtomicInteger(0);
    //存放所有在线的客户端  private static final Map<String, Session> clients = new ConcurrentHashMap<>();

    private static MiraiService miraiService;

    @Resource
    public void setMiraiService(MiraiService miraiService) {
        MiraiWebSocket.miraiService = miraiService;
    }

    @OnOpen
    public void onOpen() {
        log.info("已连接mirai");
    }

    @OnClose
    public void onClose() {
        log.info("已断开mirai");
    }

    @OnError
    public void onError(Throwable error) {
        log.error("mirai意外掉线");
        error.printStackTrace();
    }

    @OnMessage
    public void onMessage(String messageStr) {
        miraiService.onMessage(messageStr);
    }
}