package services.impl;

import dao.ClientDAO;
import dao.CompteDAO;
import entities.Client;
import entities.Compte;
import services.ClientService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;

    public ClientServiceImpl(ClientDAO clientDAO, CompteDAO compteDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
    }

    @Override
    public Client ajouterClient(String nom, String email) {
        return clientDAO.save(new Client(0L, nom, email));
    }

    @Override
    public Client modifierClient(long id, String nouveauNom, String nouvelEmail) {
        var old = clientDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client introuvable"));
        Client maj = new Client(id,
                (nouveauNom == null || nouveauNom.isBlank()) ? old.nom() : nouveauNom,
                (nouvelEmail == null || nouvelEmail.isBlank()) ? old.email() : nouvelEmail);
        if (!clientDAO.update(maj)) throw new RuntimeException("Échec update client");
        return maj;
    }

    @Override
    public boolean supprimerClient(long id) {
        return clientDAO.delete(id);
    }

    @Override
    public Optional<Client> trouverParId(long id) {
        return clientDAO.findById(id);
    }

    @Override
    public List<Client> rechercherParNom(String fragment) {
        String f = fragment == null ? "" : fragment.toLowerCase();
        return clientDAO.findAll().stream()
                .filter(c -> c.nom().toLowerCase().contains(f))
                .toList();
    }

    @Override
    public List<Client> lister() {
        return clientDAO.findAll();
    }

    @Override
    public BigDecimal soldeTotalClient(long idClient) {
        return compteDAO.findAll().stream()
                .filter(c -> c.getIdClient().equals(idClient))
                .map(Compte::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int nombreComptesClient(long idClient) {
        return (int) compteDAO.findAll().stream()
                .filter(c -> c.getIdClient().equals(idClient))
                .count();
    }

    @Override
    public Map<String, Object> infosClient(long idClient) {
        Client c = clientDAO.findById(idClient)
                .orElseThrow(() -> new NoSuchElementException("Client introuvable"));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.id());
        map.put("nom", c.nom());
        map.put("email", c.email());
        map.put("nbComptes", nombreComptesClient(idClient));
        map.put("soldeTotal", soldeTotalClient(idClient));
        return map;
    }
}
