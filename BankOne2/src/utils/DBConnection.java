package utils;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        String resourceName = "db.properties";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL locatedAt = cl.getResource(resourceName);

        try (InputStream in = cl.getResourceAsStream(resourceName)) {
            if (in == null) {
                String where = (locatedAt == null) ? "(introuvable sur le classpath)" : locatedAt.toString();
                throw new IllegalStateException("Fichier 'db.properties' introuvable sur le classpath. Localisation: " + where);
            }
            props.load(in);

            url = trimOrNull(props.getProperty("db.url"));
            user = trimOrNull(props.getProperty("db.user"));
            password = props.getProperty("db.password"); // peut rester vide

            if (url == null || user == null) {
                throw new IllegalStateException("Propriétés db.url et db.user obligatoires.");
            }

            String driverClass = trimOrNull(props.getProperty("db.driverClass"));
            if (driverClass == null) {
                driverClass = "com.mysql.cj.jdbc.Driver";
            }

            try {
                Class.forName(driverClass);
                System.out.println("[DB] Driver chargé: " + driverClass);
            } catch (ClassNotFoundException e) {
                throw new ExceptionInInitializerError(
                        "Driver JDBC introuvable sur le classpath: " + driverClass + " (" + e.getMessage() + ")"
                );
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Erreur lecture 'db.properties': " + e.getMessage());
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
