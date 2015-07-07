package repositories;

import models.Shift;
import models.Username;
import play.Logger;
import play.db.DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasyeung on 7/6/15.
 */
public class JdbcScheduleRepo implements ScheduleRepo {

    @Override
    public Shift getShift(Date date) {

        String select1 = "(select * from schedule where d = \'" + date.toString() + "\' limit 1)";
        String sql = "select * from " + select1 + " s left join username u on s.userid = u.id";

        Shift shift = new Shift();
        Connection connection = DB.getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                shift.d = rs.getDate("d");
                shift.username = new Username();
                shift.username.id = rs.getInt("userid");
                shift.username.name = rs.getString("name");
                return shift;
            }

            Logger.warn("Shift not found: " + date.toString());

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Shift> getShifts(Date start, Date end) {
        String select1 = "(select * from schedule where d >= \'" + start.toString() + "\' and d <= \'" + end.toString() + "\')";
        String sql = "select * from " + select1 + " s left join username u on s.userid = u.id";

        Connection connection = DB.getConnection();
        List<Shift> list = new ArrayList<Shift>();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Shift shift = new Shift();
                shift.username = new Username();
                shift.d = rs.getDate("d");
                shift.username.id = rs.getInt("userid");
                shift.username.name = rs.getString("name");
                list.add(shift);
            }

            return list;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Shift> getShiftsByUser(Username username, Date start, Date end) {
        String condition = "where d >= '" + start.toString() + "\' and d <= " + end.toString()
                + " and userid = \'" + username.id + "\'";
        String select1 = "(select * from schedule " + condition + ")";
        String sql = "select * from " + select1 + " s left join username u on s.userid = u.id";

        Connection connection = DB.getConnection();
        List<Shift> list = new ArrayList<Shift>();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Shift shift = new Shift();
                shift.username = new Username();
                shift.d = rs.getDate("d");
                shift.username.id = rs.getInt("userid");
                shift.username.name = rs.getString("name");
                list.add(shift);
            }

            return list;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean createShift(Date date, Username username) {
        String str = date.toString();
        Connection connection = DB.getConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into schedule values(\'" + str + "\', " + username.id + ")");

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateShift(Shift s) {
        String str = s.d.toString();
        Connection connection = DB.getConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("update schedule set userid=" + s.username.id + " where d= \'" + str + "\'");

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteShift(Shift s) {
        String str = s.d.toString();
        Connection connection = DB.getConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from schedule where d = \'" + str + "\'");

            return true;

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
