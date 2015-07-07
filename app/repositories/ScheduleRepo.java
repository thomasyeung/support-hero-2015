package repositories;

import models.Shift;
import models.Username;

import java.sql.Date;
import java.util.List;

/**
 * Created by thomasyeung on 7/4/15.
 */
public interface ScheduleRepo {

    public Shift getShift ( Date date );
    public List<Shift> getShifts (Date start, Date end);
    public List<Shift> getShiftsByUser (Username username, Date start, Date end);
    public boolean createShift (Date date, Username username);
    public boolean updateShift (Shift s);
    public boolean deleteShift (Shift s);
}
