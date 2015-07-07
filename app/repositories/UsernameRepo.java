package repositories;

import models.Username;

/**
 * Created by thomasyeung on 7/4/15.
 */
public interface UsernameRepo {

    public Username findOne ( String name );
    //public Username get ( String id );
    public boolean create ( String id, String name );
    //public boolean delete ( String id );
    //public boolean update ( String id, String name );

}
