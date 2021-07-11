package com.ame.mihoyosign.service;

import com.ame.mihoyosign.dao.RoleRepository;
import com.ame.mihoyosign.entity.Role;
import com.ame.mihoyosign.entity.User;
import com.ame.mihoyosign.exception.MiHoYoApiException;
import com.ame.mihoyosign.exception.NoGameException;
import com.ame.mihoyosign.exception.NullRoleException;
import com.ame.mihoyosign.exception.NullUserException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {

    @Resource
    private RoleRepository roleRepository;
    @Resource
    private MiHoYoService miHoYoService;
    @Resource
    private UserService userService;
    @Resource
    private MessageService messageService;


    public String getSignOnRolesInfo(long qqId) {
        return "「已开启签到角色」\n" + Role.getRolesInfo(roleRepository.findRolesByUserId(qqId));
    }

    public void updateNicknameAndLevel(User user) throws MiHoYoApiException {
        List<Role> signOnRoles = user.getRoleList();
        List<Role> allRoles = miHoYoService.getRoles(user, null, null);
        for (Role role : signOnRoles) {
            for (Role r : allRoles) {
                if (role.equals(r)) {
                    r.setNickname(role.getNickname());
                    r.setLevel(role.getLevel());
                    roleRepository.save(r);
                    break;
                }
            }
        }
    }

    public String turnOnSign(long qqId, String gameName, String gameUid) throws NullUserException, NoGameException, NullRoleException, MiHoYoApiException {
        User user = userService.getUser(qqId);
        String gameBiz = gameName == null ? null : Role.getGameBiz(gameName);
        List<Role> roles = miHoYoService.getRoles(user, gameBiz, gameUid);
        if (roles.isEmpty()) {
            throw new NullRoleException();
        }

        StringBuilder s = new StringBuilder();
        for (Role role : roles) {
            if (Role.getGameName(role.getGameBiz()) != null) {
                s.append("\n\n");
                role.setUser(user);
                if (roleRepository.findRoleByUserIdAndGameBizAndGameUid(user.getId(), role.getGameBiz(), role.getGameUid()) != null) {
                    s.append(role.getInfo()).append("\n失败:已开启自动签到,无需重复开启");
                } else {
                    roleRepository.save(role);
                    s.append(role.getInfo());
                }
            }
        }
        String rolesInfo = s.substring(2);

        messageService.sendLogToGroup("「开启签到」\nQQ:" + user.getId() + "\n「角色」\n" + rolesInfo);
        return "「开启签到」\n" + rolesInfo;
    }

    public String turnOffSign(long qqId, String gameName, String gameUid) throws NoGameException, NullRoleException {
        List<Role> roles = getRoles(qqId, gameName, gameUid);
        roleRepository.deleteAll(roles);
        return "「关闭签到」\n" + Role.getRolesInfo(roles);
    }

    public String changeNotice(long qqId, String gameName, String gameUid, boolean flag) throws NoGameException, NullRoleException {
        List<Role> roles = getRoles(qqId, gameName, gameUid);
        for (Role role : roles) {
            role.setNotifications(flag);
        }
        roleRepository.saveAll(roles);
        if (flag) {
            return "「开启通知」\n" + Role.getRolesInfo(roles);
        } else {
            return "「关闭通知」\n" + Role.getRolesInfo(roles);
        }
    }

    public List<Role> getRoles(long qqId, String gameName, String gameUid) throws NoGameException, NullRoleException {
        List<Role> roles;
        if (gameUid == null && gameName == null) {
            roles = roleRepository.findRolesByUserId(qqId);
        } else if (gameName == null) {
            roles = roleRepository.findRolesByUserIdAndGameUid(qqId, gameUid);
        } else if (gameUid == null) {
            roles = roleRepository.findRolesByUserIdAndGameBiz(qqId, Role.getGameBiz(gameName));
        } else {
            roles = roleRepository.findRolesByUserIdAndGameBizAndGameUid(qqId, Role.getGameBiz(gameName), gameUid);
        }
        if (roles == null || roles.isEmpty()) {
            throw new NullRoleException();
        }
        return roles;
    }

}
