import models.Username;
import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;
import play.libs.F;
import repositories.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static play.db.DB.*;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class DefaultSeed implements Seed {

    Connection connection;
    UsernameRepo usernameRepository;
    ScheduleRepo scheduleRepo;

    public DefaultSeed(Connection connection) {
        this.connection = connection;
        usernameRepository = new JdbcUsernameRepo(connection);
        scheduleRepo = new JdbcScheduleRepo(connection);
    }

    public void loadInitalData() {
        Logger.info("initializing database");

        //Connection conn = getConnection();

        try {
            connection.setAutoCommit(false);
            DatabaseMetaData dm = connection.getMetaData();
            Statement st = connection.createStatement();
            ResultSet rs = dm.getTables(null, null, "username", null);

            if (!rs.next()) {
                st.executeUpdate("create table username (id varchar(255) primary key, name varchar(255))");
                seedUsernames();
            }

            rs = dm.getTables(null, null, "schedule", null);
            if (!rs.next()) {
                st.executeUpdate("create table if not exists schedule (d date primary key, userid varchar(255) references username(id))");
                seedSchedule();
            }

            st.executeUpdate("create table if not exists undoable (id varchar(255) primary key, d date, userid varchar(255) references username(id))");
            connection.commit();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        } finally {

        }
    }

    @Override
    public void clearData() {
        Logger.info("clearing database");

        try {
            connection.setAutoCommit(false);
            Statement st = connection.createStatement();
            st.executeUpdate("drop table undoable");
            st.executeUpdate("drop table schedule");
            st.executeUpdate("drop table username");
            connection.commit();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }
    }


    public void seedUsernames() {

        String users[] = {"Sherry", "Boris", "Vicente", "Matte", "Jack", "Kevin", "Zoe",
                "Jay", "Boris", "Eadon", "Franky", "Luis", "James"};


        //Connection connection = DB.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            Logger.info("seeding usernames");
            for (int i = 0; i < users.length; i++)
                usernameRepository.create(Integer.toString(i), users[i]);

            connection.commit();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }
    }

    private Date nextDay(Date sqlDate) {
        //java.sql.Date sqlDate = ...;
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlDate);
        cal.add(Calendar.DAY_OF_YEAR,1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        java.sql.Date sqlTommorow = new java.sql.Date(cal.getTimeInMillis());
        return sqlTommorow;
    }

    private Date getToday() {
        return new java.sql.Date( new java.util.Date().getTime() );
    }

    private boolean isWeekend(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return day == Calendar.SUNDAY || day == Calendar.SATURDAY;
    }

    private String getDateString(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + " " + cal.get(Calendar.DAY_OF_MONTH);
    }

    private Set<String> holidays;

    private boolean isHoliday(Date dt) {
        if (holidays == null) {
            holidays = new HashSet<String>(Arrays.asList("1 1", "New Year's Day",
                    "1 19", "Martin Luther King Jr's Birthday",
                    "2 16", "President's Day",
                    "3 31", "Cesar Chavez Day",
                    "5 25", "Memorial Day",
                    "7 4", "Independence Day",
                    "9 7", "Labor Day",
                    "11 11", "Veteran's Day",
                    "11 26", "Thanksgiving Day",
                    "11 27", "Day after Thanksgiving Day",
                    "12 25", "Christmas Day"));
        }

        String sqlDate = this.getDateString(dt);
        return holidays.contains(sqlDate);
    }

    public void seedSchedule() {
        String user[] = {"Sherry", "Boris", "Vicente", "Matte", "Jack", "Sherry",
                "Matte", "Kevin", "Kevin", "Vicente", "Zoe", "Kevin",
                "Matte", "Zoe", "Jay", "Boris", "Eadon", "Sherry",
                "Franky", "Sherry", "Matte", "Franky", "Franky", "Kevin",
                "Boris", "Franky", "Vicente", "Luis", "Eadon", "Boris",
                "Kevin", "Matte", "Jay", "James", "Kevin", "Sherry",
                "Sherry", "Jack", "Sherry", "Jack"};
        Username u;

        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            Date date = getToday();

            Logger.info("seeding schedule");
            for (int i = 0; i < user.length; i++) {

                if (isWeekend(date) || isHoliday(date)) {

                } else {
                    u = usernameRepository.findOne(user[i]);

                    while (u == null) {
                        String id = UUID.randomUUID().toString();
                        Logger.info("Create a new username (" + id + ", " + user[i] + ")");
                        usernameRepository.create(id, user[i]);
                        u = usernameRepository.findOne(user[i]);
                    }

                    scheduleRepo.createShift(date, u);
                }

                date = nextDay(date);
            }

            connection.commit();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }
    }
}
