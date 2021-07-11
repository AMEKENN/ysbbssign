package com.ame.mihoyosign.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Reward {

    @Id
    @GeneratedValue
    private int id;
    private int month;
    private int day;
    private String gameBiz;
    private String icon;
    private String name;
    private int cnt;

    public String getInfo(){
        return "奖励:" + name + "×" + cnt +
                "\n[CQ:image,file=" + icon + "]";
    }

}
