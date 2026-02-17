package durable.engine;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteStore {

    private Connection conn;

    public SQLiteStore(String file) throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite:" + file);
        init();
    }

    private void init() throws Exception {
    	String sql =
    		    "CREATE TABLE IF NOT EXISTS steps (" +
    		    "workflow_id TEXT, " +
    		    "step_key TEXT, " +
    		    "status TEXT, " +
    		    "output TEXT, " +
    		    "PRIMARY KEY(workflow_id, step_key)" +
    		    ");";

        conn.createStatement().execute(sql);
    }

    public Connection getConnection() {
        return conn;
    }
}

