/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jplus.hyb.database.adapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jplus.hyb.database.util.Pager;

/**
 *
 * @author Hyberbin
 */
public class SqlserverAdapter extends AAdapter {

    private static final char[] QUOTE = new char[]{'[', ']'};

    @Override
    public ResultSet findList(Connection connection, String sql) throws SQLException {
        Statement statement = createStatement(connection, sql);
        if (statement instanceof PreparedStatement) {
            return ((PreparedStatement) statement).executeQuery();
        } else {
            return statement.executeQuery(sql);
        }
    }

    @Override
    public ResultSet findPageList(Connection connection, String sql, Pager pager) throws SQLException {
        String[] sqlAndOrder = sql.split(":");
        if (sql.contains("order") && sqlAndOrder.length == 1) {
            log.error("sqlserver: The two statement of select and order must be written separately, separated by a colon ");
        }
        String order = sqlAndOrder.length == 1 ? " order by id " : sqlAndOrder[1];
        sql = "select * from (select res.*,row_number() over (" + order + ") rn from (" + sqlAndOrder[0] + ") res ) _temp where _temp.rn>" + pager.getTop() + " and _temp.rn<=" + (pager.getTop() + pager.getSize());
        return findList(connection, sql);
    }

    @Override
    public ResultSet findSingle(Connection connection, String sql) throws SQLException {
        return findList(connection, "select top 1 res.* from(" + sql + ") res");
    }

    @Override
    public Object findUnique(Connection connection, String sql) throws SQLException {
        ResultSet singel = findSingle(connection, sql);
        return singel != null && singel.next() ? singel.getObject(1) : null;
    }

    @Override
    public int update(Connection connection, String sql) throws SQLException {
        Statement statement = createStatement(connection, sql);
        if (statement instanceof PreparedStatement) {
            return ((PreparedStatement) statement).executeUpdate();
        } else {
            return statement.executeUpdate(sql);
        }
    }

    @Override
    public char[] getQuote() {
        return QUOTE;
    }

}
