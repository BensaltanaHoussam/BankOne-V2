package services;

import entities.Client;
import java.util.List;

public interface ClientService {
    Client create(Client client);
    Client update(Client client);
    boolean delete(Long id);
    Client get(Long id);
    List<Client> list();
}
