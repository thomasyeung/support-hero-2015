package models;

import javax.persistence.*;

/**
 * Created by thomasyeung on 7/4/15.
 */
@Entity
public class Username {

    @Id
    public String id;

    public String name;
}
