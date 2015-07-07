package repositories;

import models.Shift;
import models.Username;
import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Date;
import java.util.List;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class DefaultScheduleRepo implements ScheduleRepo {
    @Override
    public Shift getShift(Date date) {
        return JPA.em().find(Shift.class, date);
    }

    @Override
    public List<Shift> getShifts(Date start, Date end) {
        EntityManager em = JPA.em();
        Query q = em.createQuery("from Shift where d >= ?1 and d <= ?2", Shift.class);
        q.setParameter(1, start);
        q.setParameter(2, end);
        return (List<Shift>) q.getResultList();
    }

    @Override
    public List<Shift> getShiftsByUser(Username username, Date start, Date end) {
        EntityManager em = JPA.em();
        Query q = em.createQuery("from Shift where d >= ?1 and d <= ?2 and userid = ?3", Shift.class);
        q.setParameter(1, start);
        q.setParameter(2, end);
        q.setParameter(3, username.id);
        return (List<Shift>) q.getResultList();
    }

    @Override
    public boolean createShift(Date date, Username username) {
        Shift s = new Shift();
        s.d = date;
        s.username = username;

        try {
            JPA.em().persist(s);
            JPA.em().flush();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean updateShift(Shift s) {

        return true;
    }

    @Override
    public boolean deleteShift(Shift s) {

        try {
            JPA.em().remove(s);
            JPA.em().flush();
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
            return false;
        }

        return true;
    }
}
