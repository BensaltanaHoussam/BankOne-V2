package dao;

import entities.Transaction;
import enums.TypeTransaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    private final DataSource dataSource;

    public TransactionDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction save(Transaction t) {
        final String sql = "INSERT INTO Transaction(date, montant, type, lieu, idCompte) VALUES(?,?,?,?,?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(t.date()));
            ps.setBigDecimal(2, t.montant());
            ps.setString(3, t.type().name());
            ps.setString(4, t.lieu());
            ps.setLong(5, t.idCompteSource());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return t.withId(rs.getLong(1));
            }
            throw new RuntimeException("Aucune clé générée pour Transaction");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Transaction", e);
        }
    }

    @Override
    public Optional<Transaction> findById(long id) {
        final String sql = "SELECT id, date, montant, type, lieu, idCompte FROM Transaction WHERE id=?";
        try (Connection cn = dataSource.getConnection();
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
    public List<Transaction> findByCompte(long idCompte) {
        final String sql = "SELECT id, date, montant, type, lieu, idCompte FROM Transaction WHERE idCompte=?";
        List<Transaction> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idCompte);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCompte Transaction", e);
        }
    }





}
