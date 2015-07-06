package repositories;

import models.Username;

/**
 * Created by thomasyeung on 7/4/15.
 */
public interface UsernameRepo {

    public Username findOne ( String name );
    //public Username get ( int id );
    public boolean create ( int id, String name );
    //public boolean delete ( int id );
    //public boolean update ( int id, String name );

}
