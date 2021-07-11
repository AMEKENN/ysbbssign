package com.ame.mihoyosign.dao;

import com.ame.mihoyosign.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(long qqId);

}
