package services.impl;

import dao.ClientDAO;
import entities.Client;
import services.ClientService;

import java.util.List;
import java.util.Objects;

public class ClientServiceImpl implements ClientService {

    private final ClientDAO clientDAO;

    public ClientServiceImpl(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public Client create(Client client) {
        Objects.requireNonNull(client);
        return clientDAO.save(client);
    }

    @Override
    public Client update(Client client) {
        Objects.requireNonNull(client);
        get(client.id());
        if (!clientDAO.update(client)) {
            throw new RuntimeException("Echec mise à jour client id=" + client.id());
        }
        return client;
    }

    @Override
    public boolean delete(Long id) {
        get(id);
        return clientDAO.delete(id);
    }

    @Override
    public Client get(Long id) {
        return clientDAO.findById(id).orElseThrow(() -> new RuntimeException("Client introuvable id=" + id));
    }

    @Override
    public List<Client> list() {
        return clientDAO.findAll();
    }
}
