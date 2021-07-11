package com.ame.mihoyosign.service;

import com.ame.mihoyosign.dao.UserRepository;
import com.ame.mihoyosign.entity.Role;
import com.ame.mihoyosign.entity.User;
import com.ame.mihoyosign.exception.MiHoYoApiException;
import com.ame.mihoyosign.exception.NullUserException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MiHoYoService miHoYoService;
    @Resource
    private MessageService messageService;

    public String bind(long qqId, String cookie) throws MiHoYoApiException {
        User user = new User(qqId, cookie, UUID.randomUUID().toString(), null);
        List<Role> roles = miHoYoService.getRoles(user, null, null);
        try {
            unbind(qqId);
        } catch (NullUserException ignored) {
        }
        messageService.sendLogToGroup("新的用户绑定:" + qqId);
        userRepository.save(user);
        return "已成功绑定或更换账号\n「米游社中所有角色」\n" + Role.getRolesInfo(roles);
    }


    public String unbind(long qqId) throws NullUserException {
        User user = getUser(qqId);
        userRepository.delete(user);
        return "已解除绑定";
    }


    public User getUser(long qqId) throws NullUserException {
        User user = userRepository.findUserById(qqId);
        if (user == null) {
            throw new NullUserException();
        }
        return user;
    }

}
