package repositories;

import models.Shift;
import models.Undoable;
import models.Username;
import play.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasyeung on 7/7/15.
 */
public class JdbcUndoableRepo implements UndoableRepo {

    Connection connection;

    private String getUuid(Date d, Username u) {
        return d.toString() + u.name;

    }

    public JdbcUndoableRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<String> getNames(Date d) {
        String select1 = "(select * from undoable where d = \'" + d.toString() + "\')";
        String sql = "select * from " + select1 + " s left join username u on s.userid = u.id";

        //Connection connection = DB.getConnection();
        List<String> list = new ArrayList<String>();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString("name"));
            }

            return list;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean create(Date d, Username u) {
        try {
            Statement statement = connection.createStatement();
            String uuid = getUuid(d, u);
            String sql = "insert into undoable values(\'" + uuid + "\', \'" + d.toString() + "\', \'" + u.id +  "\')";
            statement.executeUpdate(sql);

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(Date d, Username u) {
        try {
            Statement statement = connection.createStatement();
            String uuid = getUuid(d, u);
            String sql = "delete from undoable where id=\'" + uuid + "\'";
            statement.executeUpdate(sql);

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
