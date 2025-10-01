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

    @Override
    public List<Transaction> search(Long idCompte,
                                    LocalDate dateDebut,
                                    LocalDate dateFin,
                                    TypeTransaction type,
                                    BigDecimal minMontant,
                                    BigDecimal maxMontant,
                                    String lieu) {
        StringBuilder sql = new StringBuilder("SELECT id, date, montant, type, lieu, idCompte FROM Transaction WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (idCompte != null) { sql.append(" AND idCompte=?"); params.add(idCompte); }
        if (dateDebut != null) { sql.append(" AND date >= ?"); params.add(Timestamp.valueOf(dateDebut.atStartOfDay())); }
        if (dateFin != null) { sql.append(" AND date < ?"); params.add(Timestamp.valueOf(dateFin.plusDays(1).atStartOfDay())); }
        if (type != null) { sql.append(" AND type=?"); params.add(type.name()); }
        if (minMontant != null) { sql.append(" AND montant >= ?"); params.add(minMontant); }
        if (maxMontant != null) { sql.append(" AND montant <= ?"); params.add(maxMontant); }
        if (lieu != null && !lieu.isBlank()) { sql.append(" AND LOWER(lieu) LIKE ?"); params.add("%" + lieu.toLowerCase() + "%"); }

        List<Transaction> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Timestamp ts) ps.setTimestamp(i + 1, ts);
                else if (p instanceof BigDecimal bd) ps.setBigDecimal(i + 1, bd);
                else if (p instanceof Long l) ps.setLong(i + 1, l);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur search Transaction", e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        final String sql = "SELECT id, date, montant, type, lieu, idCompte FROM Transaction";
        List<Transaction> list = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Transaction", e);
        }
    }

    @Override
    public boolean delete(long id) {
        final String sql = "DELETE FROM Transaction WHERE id=?";
        try (Connection cn = dataSource.getConnection();
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
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getBigDecimal("montant"),
                TypeTransaction.valueOf(rs.getString("type")),
                rs.getString("lieu"),
                rs.getLong("idCompte")
        );
    }
}
