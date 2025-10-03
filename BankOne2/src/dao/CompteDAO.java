package dao;

import entities.Compte;
import java.util.List;
import java.util.Optional;

public interface CompteDAO {
    Compte save(Compte compte);                     // INSERT
    Optional<Compte> findById(Long id);
    Optional<Compte> findByNumero(String numero);
    List<Compte> findByClient(Long idClient);
    List<Compte> findAll();
    boolean update(Compte compte);                  // UPDATE complet
    boolean delete(Long id);
}