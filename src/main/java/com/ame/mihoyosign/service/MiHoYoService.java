package com.ame.mihoyosign.service;

import com.alibaba.fastjson.JSONObject;
import com.ame.mihoyosign.dao.MiHoYoRepository;
import com.ame.mihoyosign.entity.Role;
import com.ame.mihoyosign.entity.User;
import com.ame.mihoyosign.entity.Reward;
import com.ame.mihoyosign.exception.MiHoYoApiException;
import com.ame.mihoyosign.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
public class MiHoYoService {

    @Value("#{${ys.headers}}")
    private HttpHeaders ysHeaders;
    @Value("#{${bh3.headers}}")
    private HttpHeaders bh3Headers;
    @Value("${ys.salt}")
    private String ysSalt;

    @Resource
    private MiHoYoRepository miHoYoRepository;
    @Resource
    private RewardService rewardService;

    /**
     * 获取DS
     */
    private String getDS() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String t = Integer.toString((int) ((ts.getTime()) / 1000));
        String r = Utils.getRandomFromArray(null, 6);
        String c = DigestUtils.md5DigestAsHex(("salt=" + ysSalt + "&t=" + t + "&r=" + r).getBytes());
        return t + "," + r + "," + c;
    }

    public HttpHeaders getYsHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(ysHeaders);
        headers.add("DS", getDS());
        headers.add("x-rpc-device_id", user.getDeviceId());
        headers.add(HttpHeaders.COOKIE, user.getCookie());
        return headers;
    }

    public HttpHeaders getBh3Headers(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(bh3Headers);
        headers.add("x-rpc-device_id", user.getDeviceId());
        headers.add(HttpHeaders.COOKIE, user.getCookie());
        return headers;
    }

    public List<Role> getRoles(User user, String gameBiz, String gameUid) throws MiHoYoApiException {
        JSONObject rolesByCookie = miHoYoRepository.getRolesByCookie(user.getCookie(), gameBiz);
        if (rolesByCookie.getIntValue("retcode") != 0) {
            throw new MiHoYoApiException(rolesByCookie.getString("message"));
        }

        List<Role> roles = rolesByCookie.getJSONObject("data").getJSONArray("list").toJavaList(Role.class);

        if (gameUid != null) {
            for (Role role : roles) {
                if (gameBiz.equals(role.getGameBiz()) && gameUid.equals(role.getGameUid())) {
                    return Collections.singletonList(role);
                }
            }
            return new ArrayList<>();
        }
        return roles;
    }

    public void updateYsRewards() {
        JSONObject ysSignRewards = miHoYoRepository.getYsSignRewards();
        List<Reward> rewards = ysSignRewards.getJSONObject("data").getJSONArray("awards").toJavaList(Reward.class);
        int month = ysSignRewards.getJSONObject("data").getIntValue("month");
        rewardService.deleteRewards("hk4e_cn");
        for (int i = 0; i < rewards.size(); i++) {
            Reward reward = rewards.get(i);
            reward.setDay(i + 1);
            reward.setGameBiz("hk4e_cn");
            reward.setMonth(month);
        }
        rewardService.updateReward(rewards);
    }

    public Reward ysSign(Role role, User user) throws MiHoYoApiException {
        HttpHeaders headers = getYsHeaders(user);
        JSONObject sign = miHoYoRepository.ysSign(role, headers);
        if (sign.getIntValue("retcode") == 0) {
            JSONObject signInInfo = miHoYoRepository.getYsSignInfo(role, headers);
            JSONObject data = signInInfo.getJSONObject("data");
            int day = data.getIntValue("total_sign_day");
            Reward reward = rewardService.getReward("hk4e_cn", day);
            int month;
            String today = data.getString("today").substring(5, 7);
            if (reward == null || today.equals((month = reward.getMonth()) >= 10 ? "" + month : "0" + month)) {
                updateYsRewards();
                reward = rewardService.getReward("hk4e_cn", day);
            }
            return reward;
        } else {
            throw new MiHoYoApiException(sign.getString("message"));
        }
    }

    public Reward bh3Sign(Role role, User user) throws ParseException, MiHoYoApiException {
        JSONObject sign = miHoYoRepository.bh3Sign(role, getBh3Headers(user));
        if (sign.getIntValue("retcode") == 0) {
            JSONObject data = sign.getJSONObject("data");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = format.parse(data.getString("start_at"));
            Date endDate = format.parse(data.getString("now"));
            int day = (int) (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
            return data.getJSONArray("list").getObject(day, Reward.class);
        } else {
            throw new MiHoYoApiException(sign.getString("message"));
        }
    }
}
