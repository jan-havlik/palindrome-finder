package cz.mendelu.genetika.user;

import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import cz.mendelu.genetika.dao.jasdb.JasUserDao;

import java.util.Objects;

/**
 * Created by xkoloma1 on 05.01.2016.
 */
@JasDBEntity(bagName = JasUserDao.BAG_NAME)
public class User {

    public static final User DEFAULT = new User("local", null) {

        @Override
        public String getPath() {
            return "";
        }
    };

    private String id;
    private String email;
    private String password;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JasDBProperty
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @JasDBProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (this.id != null && user.id != null) {
            return Objects.equals(id, user.id);
        }

        return Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        if (this.id != null) {
            Objects.hash(id);
        }
        return Objects.hash(email, password);
    }


}
