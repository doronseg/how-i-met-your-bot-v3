package me.nerdoron.himyb.modules.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.nerdoron.himyb.Global;
import org.slf4j.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class Database {
    private static final Logger logger = Global.logger(Database.class);

    public static Connection connect() {
        Connection con = null;
        Dotenv dotenv = Dotenv.load();

        try {
            Class.forName("org.sqlite.JDBC");
            assert false;
            con = DriverManager.getConnection((dotenv.get("DB")));
        } catch (ClassNotFoundException ex) {
            logger.error("An exception (ClassNotFound) occurred while trying to connect to the database!", ex);
        } catch (SQLException ex) {
            logger.error("An exception occurred (SQLException) while trying to connect to the database!", ex);
        }

        return con;
    }

}