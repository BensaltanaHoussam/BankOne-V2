package dao;

import entities.Compte;
import entities.CompteCourant;
import entities.CompteEpargne;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteDAOImpl implements CompteDAO {

    private final DataSource dataSource;

    public CompteDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Compte save(Compte compte) {
        final String sql = "INSERT INTO Compte(numero, solde, typeCompte, decouvertAutorise, tauxInteret, idClient) VALUES(?,?,?,?,?,?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, compte.getNumero());
            ps.setBigDecimal(2, compte.getSolde());

            if (compte instanceof CompteCourant cc) {
                ps.setString(3, "COURANT");
                ps.setBigDecimal(4, cc.getDecouvertAutorise());
                ps.setNull(5, Types.DECIMAL);
            } else if (compte instanceof CompteEpargne ce) {
                ps.setString(3, "EPARGNE");
                ps.setNull(4, Types.DECIMAL);
                ps.setBigDecimal(5, ce.getTauxInteret());
            } else {
                throw new IllegalArgumentException("Type de compte inconnu");
            }

            ps.setLong(6, compte.getIdClient());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idGen = rs.getLong(1);
                    return withId(compte, idGen);
                }
            }
            throw new RuntimeException("Aucune clé générée pour Compte");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Compte", e);
        }
    }

    @Override
    public Optional<Compte> findById(Long id) {
        final String sql = baseSelect() + " WHERE c.id=?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Compte", e);
        }
    }

    @Override
    public Optional<Compte> findByNumero(String numero) {
        final String sql = baseSelect() + " WHERE c.numero=?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNumero Compte", e);
        }
    }

    @Override
    public List<Compte> findByClient(Long idClient) {
        final String sql = baseSelect() + " WHERE c.idClient=?";
        List<Compte> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByClient Compte", e);
        }
    }

    @Override
    public List<Compte> findAll() {
        final String sql = baseSelect();
        List<Compte> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Compte", e);
        }
    }

    @Override
    public boolean update(Compte compte) {
        final String sql = "UPDATE Compte SET numero=?, solde=?, typeCompte=?, decouvertAutorise=?, tauxInteret=?, idClient=? WHERE id=?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String type;
            BigDecimal dec = null;
            BigDecimal taux = null;

            if (compte instanceof CompteCourant cc) {
                type = "COURANT";
                dec = cc.getDecouvertAutorise();
            } else if (compte instanceof CompteEpargne ce) {
                type = "EPARGNE";
                taux = ce.getTauxInteret();
            } else {
                throw new IllegalArgumentException("Type compte inconnu");
            }

            ps.setString(1, compte.getNumero());
            ps.setBigDecimal(2, compte.getSolde());
            ps.setString(3, type);
            if (dec != null) ps.setBigDecimal(4, dec); else ps.setNull(4, Types.DECIMAL);
            if (taux != null) ps.setBigDecimal(5, taux); else ps.setNull(5, Types.DECIMAL);
            ps.setLong(6, compte.getIdClient());
            ps.setLong(7, compte.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Compte", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "DELETE FROM Compte WHERE id=?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Compte", e);
        }
    }

    private String baseSelect() {
        return "SELECT c.id, c.numero, c.solde, c.typeCompte, c.decouvertAutorise, c.tauxInteret, c.idClient FROM Compte c";
    }

    private Compte map(ResultSet rs) throws SQLException {
        String type = rs.getString("typeCompte");
        if ("COURANT".equalsIgnoreCase(type)) {
            return new CompteCourant(
                    rs.getLong("id"),
                    rs.getString("numero"),
                    rs.getBigDecimal("solde"),
                    rs.getLong("idClient"),
                    rs.getBigDecimal("decouvertAutorise")
            );
        } else {
            return new CompteEpargne(
                    rs.getLong("id"),
                    rs.getString("numero"),
                    rs.getBigDecimal("solde"),
                    rs.getLong("idClient"),
                    rs.getBigDecimal("tauxInteret")
            );
        }
    }

    private Compte withId(Compte original, Long newId) {
        if (original instanceof CompteCourant cc) {
            return new CompteCourant(newId, cc.getNumero(), cc.getSolde(), cc.getIdClient(), cc.getDecouvertAutorise());
        } else if (original instanceof CompteEpargne ce) {
            return new CompteEpargne(newId, ce.getNumero(), ce.getSolde(), ce.getIdClient(), ce.getTauxInteret());
        }
        throw new IllegalArgumentException("Type inconnu");
    }
}
