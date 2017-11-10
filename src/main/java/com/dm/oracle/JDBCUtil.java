package com.dm.oracle;

import com.dm.oracle.config.Constants;

import java.sql.*;

/**
 * Created by khobsyzl28 on 11/8/2017.
 */
public class JDBCUtil {

    public static Connection getDBConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(Constants.DATA_STORAGE_DATABASE_CONNECTION_URL);
        conn.setAutoCommit(true);
        return conn;
    }

    public static void close(Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void close(Connection conn, PreparedStatement ps) {
        try {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void close(PreparedStatement ps) {
        try {
            if (ps != null)
                ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
