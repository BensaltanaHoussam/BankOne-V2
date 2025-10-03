package services;

import entities.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClientService {
    Client ajouterClient(String nom, String email);
    Client modifierClient(long id, String nouveauNom, String nouvelEmail);
    boolean supprimerClient(long id);
    Optional<Client> trouverParId(long id);
    List<Client> rechercherParNom(String fragment);
    List<Client> lister();
    BigDecimal soldeTotalClient(long idClient);
    int nombreComptesClient(long idClient);
    Map<String, Object> infosClient(long idClient);
}
