package com.company;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by mac on 2016. 12. 14..
 */
public interface ConnectionMaker{
    public Connection makeConnection() throws ClassNotFoundException, SQLException;


}
