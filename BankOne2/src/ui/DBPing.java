package ui;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class DBPing {
    public static void main(String[] args) {
        try (Connection cnx = DBConnection.getConnection()) {
            DatabaseMetaData meta = cnx.getMetaData();
            System.out.println("OK: connecté à " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
        } catch (Exception e) {
            System.err.println("Échec connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
