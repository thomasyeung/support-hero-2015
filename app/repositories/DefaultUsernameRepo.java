package repositories;

import models.Username;
import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.Query;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class DefaultUsernameRepo implements UsernameRepo {

    public Username findOne(String name) {
        Query q = JPA.em().createQuery("from Username where name = ?1", Username.class);
        q.setParameter(1, name.toLowerCase());
        q.setMaxResults(1);
        return (Username) q.getSingleResult();
    }

    /*public Username get (int id) {
        return null;
    }*/

    public boolean create (int id, String name) {
        Username username = new Username();
        username.id = id;
        username.name = name.toLowerCase();

        try {
            JPA.em().persist(username);
            JPA.em().flush();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            return false;
        }

        return true;
    }

    /*
    public boolean delete (int id) {
        return false;
    }

    public boolean update (int id, String name) {
        return false;
    }*/
}
