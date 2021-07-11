package com.ame.mihoyosign.dao;

import com.alibaba.fastjson.JSONObject;
import com.ame.mihoyosign.entity.Role;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Repository
public class MiHoYoRepository {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送 验证码
     *
     * @deprecated 已失效
     */
    @Deprecated
    public JSONObject createMobileCaptcha(String phone) {
        String url = "https://webapi.account.mihoyo.com/Api/create_mobile_captcha";
        String body = "action_type=login&mobile=" + phone + "&action_ticket=&no_sms=false";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, requestEntity, JSONObject.class);
    }

    /**
     * 通过 验证码 获取 LoginTicket
     */
    @Deprecated
    public JSONObject loginByMobileCaptcha(String phone, String captcha) {
        String url = "https://webapi.account.mihoyo.com/Api/login_by_mobilecaptcha";
        String p = "mobile=" + phone + "&mobile_captcha=" + captcha;
        return restTemplate.getForObject(url + "?" + p, JSONObject.class);
    }

    /**
     * 通过 LoginTicket 获取 token
     */
    @Deprecated
    public JSONObject getMultiTokenByLoginTicket(String webLoginToken, String accountId) {
        String url = "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket";
        String p = "login_ticket=" + webLoginToken
                + "&token_types=3&uid=" + accountId;
        return restTemplate.getForObject(url + "?" + p, JSONObject.class);
    }

    /**
     * 通过 LoginTicket 获取 cookie
     */
    @Deprecated
    public JSONObject getCookieAccountInfoByLoginTicket(String webLoginToken) {
        String url = "https://webapi.account.mihoyo.com/Api/cookie_accountinfo_by_loginticket";
        String p = "login_ticket=" + webLoginToken;
        return restTemplate.getForObject(url + "?" + p, JSONObject.class);
    }
    
    /**
     * 传 cookie 获取 游戏角色
     */
    public JSONObject getRolesByCookie(String cookie, String gameBiz) {
        if (gameBiz == null) {
            gameBiz = "";
        }
        String url = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=" + gameBiz;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> re = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);
        return re.getBody();
    }

    /**
     * 获取 签到奖励列表
     */
    public JSONObject getYsSignRewards() {
        String url = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=e202009291139501";
        return restTemplate.getForObject(url, JSONObject.class);
    }

    /**
     * 获取 累计签到天数
     */
    public JSONObject getYsSignInfo(Role role, HttpHeaders headers) {
        String url = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info";
        String p = "act_id=e202009291139501" +
                "&region=" + role.getRegion() +
                "&uid=" + role.getGameUid();
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> re = restTemplate.exchange(url + "?" + p, HttpMethod.GET, httpEntity, JSONObject.class);
        return re.getBody();
    }

    public JSONObject ysSign(Role role, HttpHeaders headers) {
        String url = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("act_id", "e202009291139501");
        jsonObject.put("region", role.getRegion());
        jsonObject.put("uid", role.getGameUid());
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, headers);
        return restTemplate.postForObject(url, requestEntity, JSONObject.class);
    }

    public JSONObject bh3Sign(Role role, HttpHeaders headers) {
        String url = "https://api-takumi.mihoyo.com/common/euthenia/sign";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("act_id", "e202104072769");
        jsonObject.put("region", role.getRegion());
        jsonObject.put("uid", role.getGameUid());
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, headers);
        return restTemplate.postForObject(url, requestEntity, JSONObject.class);
    }
    
}
