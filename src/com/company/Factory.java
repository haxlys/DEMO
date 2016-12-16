package com.company;

/**
 * Created by mac on 2016. 12. 16..
 */
public class Factory {

    public SkillStatsDAO skillStatsDAO(){
        return new SkillStatsDAO(connectionMaker());
    }

    public ConnectionMaker connectionMaker(){
        return new DBConnection();
    }
}
