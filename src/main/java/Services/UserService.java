package Services;

import Models.Personne;
import Models.User;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class UserService implements  IService<User>{

    Connection conn;

    public UserService(){
        this.conn = MyDb.getInstance().getConn();

    }

    @Override
    public void Create(User user) throws Exception {
        String req = "INSERT INTO user (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, user.getNom());
        stmt.setString(2, user.getPrenom());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getTelephone());  // Assurez-vous que le téléphone est bien inclus
        stmt.executeUpdate();
    }

    @Override
    public void Update(User user) throws Exception {

    }

    @Override
    public List<User> Display() throws Exception {
        return List.of();
    }

    @Override
    public void Delete() throws Exception {

    }
}
