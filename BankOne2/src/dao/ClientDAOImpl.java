package dao;

import entities.Client;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAOImpl implements ClientDAO {

    private final DataSource dataSource;

    public ClientDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Client save(Client client) {
        final String sql = "INSERT INTO Client(nom, email) VALUES(?, ?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, client.getNom());
            ps.setString(2, client.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())return client.withId(rs.getLong(1));
            }
           throw new RuntimeException("Aucune clé générée pour le client");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Client", e);
        }
    }

    @Override
    public Optional<Client> findById(Long id) {
        final String sql = "SELECT id,nom,email FROM Client WHERE id =?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1,id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                } else {
                    return Optional.empty();
                }catch (SQLException e) {
                    throw new RuntimeException("Erreur findById Client", e);
                }
            }
        }
    }

    @Override
    public List<Client> findAll() {
        final String sql = "SELECT id, nom, email FROM Client";
        List<Client> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Client", e);
        }
    }



}
