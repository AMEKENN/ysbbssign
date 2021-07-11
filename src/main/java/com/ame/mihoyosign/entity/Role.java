package com.ame.mihoyosign.entity;

import com.ame.mihoyosign.exception.NoGameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Role {
    @Id
    @GeneratedValue
    private long id;
    private String gameBiz;
    private String region;
    private String gameUid;
    private String regionName;
    private String nickname;
    private int level;
    private boolean notifications = false;
    //可选属性optional=false,表示user不能为空。删除角色，不影响用户
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    //设置在role表中的关联字段(外键)
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "role_user_fk"))
    private User user;

    public String getInfo() {
        return "游戏:" + getGameName(gameBiz) +
                "\n昵称:" + getNickname() +
                "\n等级:" + getLevel() +
                "\nUID:" + getGameUid() +
                "\n区服:" + getRegionName();
    }

    public static String getRolesInfo(List<Role> roles) {
        StringBuilder allRoles = new StringBuilder();
        for (Role r : roles) {
            allRoles.append("\n\n").append(r.getInfo());
        }
        return allRoles.substring(2);
    }

    public static String getGameBiz(String gameName) throws NoGameException {
        switch (gameName) {
            case "原神":
                return "hk4e_cn";
            case "崩坏3":
                return "bh3_cn";
            default:
                throw new NoGameException();
        }
    }

    public static String getGameName(String gameBiz) {
        switch (gameBiz) {
            case "hk4e_cn":
                return "原神";
            case "bh3_cn":
                return "崩坏3";
            default:
                return null;
        }
    }

}



