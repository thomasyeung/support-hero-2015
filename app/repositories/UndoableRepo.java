package repositories;

import models.Undoable;
import models.Username;

import java.sql.Date;
import java.util.List;

/**
 * Created by thomasyeung on 7/7/15.
 */
public interface UndoableRepo {
    //List<Undoable> getUndoables(Date d);
    List<String> getNames(Date d);
    boolean create(Date d, Username u);
    boolean delete(Date d, Username u);
}
