import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {

    public Promise<Result> onError(RequestHeader request, Throwable t) {
        return Promise.<Result>pure(internalServerError(
            views.html.page404.render()
        ));
    }
	
	 public Promise<Result> onHandlerNotFound(RequestHeader request) {
        return Promise.<Result>pure(notFound(
            views.html.page404.render()
        ));
    }
	
	public Promise<Result> onBadRequest(RequestHeader request, String error) {
        return Promise.<Result>pure(badRequest(
		views.html.page404.render()
		));
    }

}