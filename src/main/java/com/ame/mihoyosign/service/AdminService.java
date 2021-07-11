package com.ame.mihoyosign.service;

import com.ame.mihoyosign.dao.RoleRepository;
import com.ame.mihoyosign.dao.UserRepository;
import com.ame.mihoyosign.entity.Role;
import com.ame.mihoyosign.entity.User;
import com.ame.mihoyosign.exception.NotAdminException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private SignService signService;
    @Resource
    private MessageService messageService;
    @Value("${app-config.admin-qq-id}")
    private long AdminQQId;

    public void checkAdmin(long qqId) throws NotAdminException {
        if (qqId != AdminQQId) {
            throw new NotAdminException();
        }
    }

    public String sendNotice(long qqId, String notice, String toQQId) throws NotAdminException {
        checkAdmin(qqId);
        if (toQQId != null) {
            try {
                long to = Long.parseLong(toQQId);
                messageService.sendPrivateMsg(to, notice);
            } catch (Exception e) {
                return "请输入正确的QQ号";
            }
        } else {
            List<User> allUser = userRepository.findAll();
            for (User user : allUser) {
                messageService.sendPrivateMsg(user.getId(), notice);
            }
        }
        return "已发送\n注:未加为好友会无法接收到";
    }

    public String getAllRole(long qqId) throws NotAdminException {
        checkAdmin(qqId);
        StringBuilder re = new StringBuilder("「所有用户角色」");
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            re.append("\n").append(role.getInfo()).append("\nQQ:").append(role.getUser().getId()).append("\n");
        }
        return re.toString();
    }

    @Async
    @Scheduled(cron = "${app-config.sign-cron}")
    public void signAll() {
        for (User user : userRepository.findAll()) {
            signService.sign(user);
        }
    }
}
