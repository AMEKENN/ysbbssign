package com.ame.mihoyosign.dao;


import com.ame.mihoyosign.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface RewardRepository extends JpaRepository<Reward, Integer> {

    Reward findRewardByGameBizAndDay(String gameBiz,int day);

    @Transactional
    void deleteRewardsByGameBiz(String gameBiz);
}
