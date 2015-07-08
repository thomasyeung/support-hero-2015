package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import models.Shift;
import models.Undoable;
import models.Username;
import play.*;
import play.data.Form;
import play.db.DB;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;

import repositories.*;
import views.html.*;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Application extends Controller {

    Connection connection = DB.getConnection();
    UsernameRepo usernameRepo = new JdbcUsernameRepo(connection);
    ScheduleRepo scheduleRepo = new JdbcScheduleRepo(connection);
    UndoableRepo undoableRepo = new JdbcUndoableRepo(connection);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Result index() {

        return ok(index.render("Your new application is ready."));
    }

    public Result getShift(String date) {
        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);
            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Shift s = scheduleRepo.getShift(sqlDate);

            connection.commit();

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(s);

            return ok(json);
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return ok();
    }


    // assign shifts by date and name
    //@Transactional
    public Result assignShiftByName() {
        //Map<String, String[]> values = request().body().asFormUrlEncoded();
        String date = Form.form().bindFromRequest().get("date");
        String name = Form.form().bindFromRequest().get("name").toLowerCase();

        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);

            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Username u = usernameRepo.findOne(name);

            while (u == null) {
                String id = UUID.randomUUID().toString();
                Logger.info("Create a new username (" + id + ", " + name + ")");
                usernameRepo.create(id, name);
                u = usernameRepo.findOne(name);
            }

            List<String> ul = undoableRepo.getNames(sqlDate);

            if (ul.contains(name) || !scheduleRepo.createShift(sqlDate, u)) {
                Logger.warn("Can't assign shift on " + date + " to " + name);
                return badRequest();
            }
            connection.commit();

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return redirect(routes.Application.index());
    }

    //@Transactional
    public Result unassignShift(String date) {

        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);
            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Shift s = scheduleRepo.getShift(sqlDate);

            boolean deleted = scheduleRepo.deleteShift(s);

            if (!deleted)
                Logger.warn("Can't delete shift on" + date);
            connection.commit();

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return redirect(routes.Application.index());
    }

    //@Transactional
    public Result getSchedule(String startDate, String endDate) {
        java.sql.Date start, end;
        List<Shift> schedule;

        try {
            connection.setAutoCommit(false);
            start = new java.sql.Date(dateFormat.parse(startDate).getTime());
            end = new java.sql.Date(dateFormat.parse(endDate).getTime());

            schedule = scheduleRepo.getShifts(start, end);
            connection.commit();

            return ok(Json.toJson(schedule));

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return badRequest();
    }

    //@Transactional
    public Result getScheduleByName(String name, String startDate, String endDate) {
        java.sql.Date start, end;
        List<Shift> schedule;
        Username username;

        try {
            connection.setAutoCommit(false);
            start = (startDate.equals("") ? null : new java.sql.Date(dateFormat.parse(startDate).getTime()));
            end = (endDate.equals("") ? null : new java.sql.Date(dateFormat.parse(endDate).getTime()));
            username = usernameRepo.findOne(name);

            schedule = scheduleRepo.getShiftsByUser(username, start, end);
            connection.commit();

            return ok(Json.toJson(schedule));

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return badRequest();
    }

    //@Transactional
    public Result swapShifts(String date1, String date2) {
        try {
            connection.setAutoCommit(false);
            Date d1 = new Date(dateFormat.parse(date1).getTime());
            Date d2 = new Date(dateFormat.parse(date2).getTime());

            Shift s1 = scheduleRepo.getShift(d1);
            Shift s2 = scheduleRepo.getShift(d2);

            List<String> ul1 = undoableRepo.getNames(d1);
            List<String> ul2 = undoableRepo.getNames(d2);

            if (!ul1.contains(s2.username.name) && !ul2.contains(s1.username.name)) {
                Username u = s1.username;
                s1.username = s2.username;
                s2.username = u;

                scheduleRepo.updateShift(s1);
                scheduleRepo.updateShift(s2);

                connection.commit();

                String json = "{\"name1\":\"" + s1.username.name + "\",\"name2\":\"" + s2.username.name + "\"}";

                return ok(json);
            }
        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return badRequest();
    }

    public Result getUndoables(String date) {
        java.sql.Date d;
        List<String> ul;

        try {
            connection.setAutoCommit(false);
            d = new java.sql.Date(dateFormat.parse(date).getTime());

            ul = undoableRepo.getNames(d);
            connection.commit();

            return ok(Json.toJson(ul));

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return badRequest();
    }

    public Result createUndoable() {
        String date = Form.form().bindFromRequest().get("date");
        String name = Form.form().bindFromRequest().get("name");

        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);
            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Username u = usernameRepo.findOne(name);

            if (u != null && !undoableRepo.create(sqlDate, u))
                Logger.warn("Can't mark undoable: " + date + ", " + name);
            connection.commit();

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return redirect(routes.Application.index());
    }

    public Result deleteUndoable(String date, String name) {

        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);
            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Username u = usernameRepo.findOne(name);

            boolean deleted = undoableRepo.delete(sqlDate, u);

            if (!deleted)
                Logger.warn("Can't delete shift on" + date);
            connection.commit();

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return redirect(routes.Application.index());
    }
}
