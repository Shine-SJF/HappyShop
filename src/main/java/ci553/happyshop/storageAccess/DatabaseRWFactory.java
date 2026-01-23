package ci553.happyshop.storageAccess;

/**
 * The DatabaseRWFactory class centralizes database configuration and object creation.
 * It acts as a factory pattern to provide flexible instantiation of the DatabaseRW interface.
**/

public class DatabaseRWFactory {

    public static String dbURL = "jdbc:derby:happyShopDB";

    public static DatabaseRW createDatabaseRW() {
        return new DerbyRW();
    }
}

