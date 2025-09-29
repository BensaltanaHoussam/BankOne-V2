package entities;

public record Client (Long id , String nom , String email) {
    public Client{
        if (nom == null || nom.isBlank()){
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
    }

    public Client withId(Long newId) {
        return new Client(newId, this.nom, this.email);
    }
}
