package dao;

import entities.Transaction;
import enums.TypeTransaction;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    private static final String TABLE = "`Transaction`";

    @Override
    public Transaction save(Transaction t) {
        final String sql = "INSERT INTO " + TABLE +
                "(idCompteSource, idCompteDestination, montant, type, date, lieu) VALUES(?,?,?,?,?,?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, t.idCompteSource());
            if (t.idCompteDestination() != null) ps.setLong(2, t.idCompteDestination()); else ps.setNull(2, Types.BIGINT);
            ps.setBigDecimal(3, t.montant());
            ps.setString(4, t.type().name());
            ps.setTimestamp(5, Timestamp.valueOf(t.date()));
            ps.setString(6, t.lieu());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return t.withId(rs.getLong(1));
            }
            throw new RuntimeException("Aucune clé générée Transaction");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Transaction", e);
        }
    }

    @Override
    public List<Transaction> findByCompte(long idCompte) {
        final String sql = "SELECT id, idCompteSource, idCompteDestination, montant, type, date, lieu FROM "
                + TABLE + " WHERE idCompteSource=? OR idCompteDestination=? ORDER BY date DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idCompte);
            ps.setLong(2, idCompte);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCompte Transaction", e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        final String sql = "SELECT id, idCompteSource, idCompteDestination, montant, type, date, lieu FROM "
                + TABLE + " ORDER BY date DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Transaction", e);
        }
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        final String sql = "SELECT id, idCompteSource, idCompteDestination, montant, type, date, lieu FROM "
                + TABLE + " WHERE id=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Transaction", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "DELETE FROM " + TABLE + " WHERE id=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Transaction", e);
        }
    }

    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getLong("idCompteSource"),
                rs.getObject("idCompteDestination") == null ? null : rs.getLong("idCompteDestination"),
                rs.getBigDecimal("montant"),
                TypeTransaction.valueOf(rs.getString("type")),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getString("lieu")
        );
    }
}
