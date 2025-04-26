package Services;

import Models.Personne;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements  IService<Personne>{
    Connection conn;

    public PersonneService(){
        this.conn = MyDb.getInstance().getConn();

    }
    @Override
    public void Create(Personne personne)throws Exception {
String req ="insert into personne (nom,prenom,age) values ('"+personne.getNom()+ "','"+personne.getPrenom()+"','"+personne.getAge()+ "')";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(req);

    }

    @Override
    public void Update(Personne personne)throws Exception  {
        String req = "update personne set nom = ?, prenom = ?, age = ? where id = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, personne.getNom());
        stmt.setString(2, personne.getPrenom());
        stmt.setInt(3,personne.getAge());
        stmt.setInt(4,personne.getId());
        stmt.executeUpdate();

    }

    @Override
    public List<Personne> Display()throws Exception  {
        String req = "select * from personne";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        List<Personne> listPers = new ArrayList<>();
        while (rs.next()){
            Personne p1 = new Personne();
            p1.setId(rs.getInt("id"));
            p1.setNom(rs.getString("nom"));
            p1.setPrenom(rs.getString("prenom"));
            p1.setAge(rs.getInt("age"));
            listPers.add(p1);
        }

        return listPers;
    }

    @Override
    public void Delete()throws Exception  {

    }
}
