package com.ame.mihoyosign.service;

import com.ame.mihoyosign.entity.Reward;
import com.ame.mihoyosign.entity.Role;
import com.ame.mihoyosign.entity.User;
import com.ame.mihoyosign.exception.NoGameException;
import com.ame.mihoyosign.exception.NullRoleException;
import com.ame.mihoyosign.exception.NullUserException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SignService {

    @Resource
    private MiHoYoService miHoYoService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserService userService;
    @Resource
    private MessageService messageService;

    @Async
    public void sign(User user) {
        try {
            System.out.println("当前运行的线程名称：" + Thread.currentThread().getName());
            List<Role> roles = roleService.getRoles(user.getId(), null, null);
            messageService.sendPrivateMsg(user.getId(), sign(user, roles));
        } catch (NullRoleException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sign(long qqId, String gameName, String gameUid) throws NullRoleException, NullUserException, NoGameException {
        User user = userService.getUser(qqId);
        List<Role> roles = roleService.getRoles(qqId, gameName, gameUid);
        return sign(user, roles);
    }

    public String sign(User user, List<Role> roles) {
        StringBuilder s = new StringBuilder();
        for (Role role : roles) {
            String gameBiz = role.getGameBiz();
            try {
                Reward reward = null;
                switch (gameBiz) {
                    case "hk4e_cn":
                        reward = miHoYoService.ysSign(role, user);
                        break;
                    case "bh3_cn":
                        reward = miHoYoService.bh3Sign(role, user);
                        break;
                }
                if (reward != null) {
                    s.append("「签到 成功」\n").append(reward.getInfo()).append(role.getInfo()).append("\n\n");
                } else {
                    throw new Exception("程序执行错误");
                }
            } catch (Exception e) {
                s.append("「签到 失败」\n" + "原因:").append(e.getMessage()).append("\n").append(role.getInfo()).append("\n\n");
            }
        }
        return s.substring(0, s.length() - 2);
    }

}
