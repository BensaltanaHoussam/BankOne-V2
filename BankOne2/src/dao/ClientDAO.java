package dao;

import java.util.List;
import java.util.Optional;

import entities.Client;

public interface ClientDAO {
    Client save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    boolean update(Client client);
    boolean delete(Long id);
}
