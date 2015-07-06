package models;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by thomasyeung on 7/4/15.
 */
@Entity
@Table(name = "schedule")
public class Shift {

    @Id
    public Date d;

    @ManyToOne
    @JoinColumn(name = "userid")
    public Username username;

}
