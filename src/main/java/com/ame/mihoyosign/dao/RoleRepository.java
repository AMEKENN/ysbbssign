package com.ame.mihoyosign.dao;


import com.ame.mihoyosign.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    List<Role> findRolesByUserId(long userId);

    List<Role> findRolesByUserIdAndGameBiz(long userId, String gameBiz);

    List<Role> findRolesByUserIdAndGameUid(long userId, String gameUid);

    List<Role> findRolesByUserIdAndGameBizAndGameUid(long userId, String gameBiz, String gameUid);

    Role findRoleByUserIdAndGameBizAndGameUid(long userId, String gameBiz, String gameUid);

}
