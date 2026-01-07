package com.wallverse.config;

public class DatabaseConfig {
    public static final String DB_URL = "jdbc:mariadb://localhost:3306/wallverse";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "kali";
    public static final String DB_DRIVER = "org.mariadb.jdbc.Driver";
    public static final int DB_PORT = "3306";

}

private DatabaseConfig() {
    // Un constructeur privé pour empêcher l'instanciation de cette classe iwa sf hdchi li kayn " Ayoub kan hna "
throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
}

