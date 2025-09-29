-- ========================
-- Table CLIENT
-- ========================
CREATE TABLE Client (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nom VARCHAR(100) NOT NULL,
                        email VARCHAR(150) UNIQUE NOT NULL
);

-- ========================
-- Table COMPTE
-- ========================
CREATE TABLE Compte (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        numero VARCHAR(50) UNIQUE NOT NULL,
                        solde DECIMAL(15,2) NOT NULL DEFAULT 0,
                        typeCompte ENUM('COURANT', 'EPARGNE') NOT NULL,
                        decouvertAutorise DECIMAL(15,2), -- nullable for COURANT only
                        tauxInteret DECIMAL(5,2),       -- nullable for EPARGNE only
                        idClient BIGINT NOT NULL,
                        CONSTRAINT fk_compte_client FOREIGN KEY (idClient)
                            REFERENCES Client(id) ON DELETE CASCADE
);

-- ========================
-- Table TRANSACTION
-- ========================
CREATE TABLE Transaction (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             montant DECIMAL(15,2) NOT NULL,
                             type ENUM('VERSEMENT', 'RETRAIT', 'VIREMENT') NOT NULL,
                             lieu VARCHAR(100),
                             idCompte BIGINT NOT NULL,
                             CONSTRAINT fk_transaction_compte FOREIGN KEY (idCompte)
                                 REFERENCES Compte(id) ON DELETE CASCADE
);
