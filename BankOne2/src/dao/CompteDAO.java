package dao;

import java.util.List;
import java.util.Optional;

import entities.Compte;

public interface CompteDAO {
    Compte save(Compte compte);
    Optional<Compte> findById(Long id);
    Optional<Compte> findByNumero(String numero);
    List<Compte> findByClient(long clientId);
    List<Compte> findAll();
    boolean update(Compte compte);
    boolean delete(Long id);
}
