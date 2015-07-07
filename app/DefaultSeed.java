import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;
import play.libs.F;
import repositories.DefaultUsernameRepo;
import repositories.JdbcUsernameRepo;
import repositories.UsernameRepo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import static play.db.DB.*;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class DefaultSeed implements Seed {

    UsernameRepo usernameRepository = new JdbcUsernameRepo();

    public void loadInitalData() {

        Logger.info("loading");

        Connection conn = getConnection();

        try {
            DatabaseMetaData dm = conn.getMetaData();
            Statement st = conn.createStatement();
            ResultSet rs = dm.getTables(null, null, "username", null);

            if (!rs.next()) {
                st.executeUpdate("create table username (id int primary key, name varchar(255))");
                seedUsernames();
            }

            st.executeUpdate("create table if not exists schedule (d date primary key, userid int references username(id))");

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        } finally {

        }
    }

    public void seedUsernames() {

        String users[] = {"Sherry", "Boris", "Vicente", "Matte", "Jack", "Kevin", "Zoe",
                "Jay", "Boris", "Eadon", "Franky", "Luis", "James"};


        Connection connection = DB.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            Logger.info("seeding usernames");
            for (int i = 0; i < users.length; i++)
                statement.executeUpdate("insert into username values(" + i + ", \'" + users[i] + "\')");

            connection.commit();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            e.printStackTrace();
        }

        /*JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {

            for (int i = 0; i < users.length; i++)
                usernameRepository.create(i, users[i]);
        //    }
        //});*/
    }
}
