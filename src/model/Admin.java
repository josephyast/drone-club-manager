package model;
import java.util.Objects;

public class Admin {
    private int id;
    private String username;
    private String password;
    private String shopname;

    public Admin(int id, String username, String password, String shopname) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.shopname = shopname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShopMame() {
        return shopname;
    }

    public void setShopName(String shopName) {
        shopname = shopName;
    }

    @Override
    public String toString() {
        return "Admin{" + "id=" + id + ", username='" + username + '\'' + '\'' + ", shopname='" + shopname + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id && Objects.equals(username, admin.username) && Objects.equals(shopname, admin.shopname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}

