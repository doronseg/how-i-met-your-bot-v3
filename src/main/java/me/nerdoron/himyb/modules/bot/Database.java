package me.nerdoron.himyb.modules.bot;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

public class Database {
    private static final Logger logger = LoggingHandler.logger(Database.class);

    public static Connection connect() {
        Connection con;
        Dotenv dotenv = Dotenv.load();

        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(requireNonNull(dotenv.get("DB")));
            return con;
        } catch (ClassNotFoundException ex) {
            logger.error("An exception (ClassNotFound) occurred while trying to connect to the database!", ex);
        } catch (SQLException ex) {
            logger.error("An exception occurred (SQLException) while trying to connect to the database!", ex);
        }
        logger.error("Could not connect to DB due to an unknown error.");
        return null;
    }

}