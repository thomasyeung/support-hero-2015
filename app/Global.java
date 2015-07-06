
import play.GlobalSettings;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by thomasyeung on 7/4/15.
 */
public class Global extends GlobalSettings {

    Seed seed = new DefaultSeed();

    @Override
    public void onStart(play.Application application) {
        super.onStart(application);

        seed.loadInitalData();
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
        super.onError(request, t);

        return null;
    }
}
