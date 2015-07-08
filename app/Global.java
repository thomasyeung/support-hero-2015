
import play.Application;
import play.GlobalSettings;
import play.db.DB;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class Global extends GlobalSettings {

    Seed seed;

    @Override
    public void onStart(play.Application application) {
        super.onStart(application);

        seed = new DefaultSeed(DB.getConnection());

        seed.clearData();
        seed.loadInitalData();
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
        super.onError(request, t);

        return null;
    }

    @Override
    public void onStop(Application var1) {
        super.onStop(var1);

        //seed.clearData();
    }

}
