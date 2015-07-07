package repositories;

import models.Username;
import play.Logger;
import play.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by thomasyeung on 7/6/15.
 */
public class JdbcUsernameRepo implements UsernameRepo {

    Connection connection;

    public JdbcUsernameRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Username findOne(String name) {

        Username username = new Username();
        //Connection connection = DB.getConnection();
        String sql = "select * from username where name = \'" + name.toLowerCase() + "\' limit 1";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                username.id = rs.getInt("id");
                username.name = rs.getString("name");
                return username;
            }

            Logger.warn("Username not found: " + name);

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean create(int id, String name) {
        //Connection connection = DB.getConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into username values(" + id + ", \'" + name.toLowerCase() + "\')");

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
