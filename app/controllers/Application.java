package controllers;

import models.Shift;
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

public class Application extends Controller {

    Connection connection = DB.getConnection();
    UsernameRepo usernameRepo = new JdbcUsernameRepo(connection);
    ScheduleRepo scheduleRepo = new JdbcScheduleRepo(connection);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Result index() {

        return ok(index.render("Your new application is ready."));
    }


    // assign shifts by date and name
    //@Transactional
    public Result assignShiftByName() {
        //Map<String, String[]> values = request().body().asFormUrlEncoded();
        String date = Form.form().bindFromRequest().get("date");
        String name = Form.form().bindFromRequest().get("name");

        try {
            connection.setAutoCommit(false);
            java.util.Date parsed = dateFormat.parse(date);

            java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

            Username u = usernameRepo.findOne(name);

            if (!scheduleRepo.createShift(sqlDate, u))
                Logger.warn("Can't assign shift on " + date + " to " + name);
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
            start = new java.sql.Date(dateFormat.parse(startDate).getTime());
            end = new java.sql.Date(dateFormat.parse(endDate).getTime());
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

            Username u = s1.username;
            s1.username = s2.username;
            s2.username = u;

            scheduleRepo.updateShift(s1);
            scheduleRepo.updateShift(s2);

            connection.commit();

            return ok();

        } catch (Throwable e) {
            Logger.warn(e.getMessage());
        }

        return badRequest();
    }
}
