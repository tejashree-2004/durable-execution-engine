package durable.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class SQLiteStepStore implements StepStore {

    private final Connection conn;

    public SQLiteStepStore(Connection conn) {
        this.conn = conn;
    }

    public synchronized Optional<StepRecord> get(String wf, String key) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT status, output FROM steps WHERE workflow_id=? AND step_key=?"
            );
            ps.setString(1, wf);
            ps.setString(2, key);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            StepRecord r = new StepRecord();
            r.workflowId = wf;
            r.stepKey = key;
            r.status = rs.getString("status");
            r.output = rs.getString("output");

            return Optional.of(r);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void saveRunning(String wf, String key) {
        upsert(wf, key, "RUNNING", null);
    }

    public synchronized void saveCompleted(String wf, String key, String output) {
        upsert(wf, key, "COMPLETED", output);
    }

    private void upsert(String wf, String key, String status, String out) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO steps (workflow_id, step_key, status, output) VALUES (?, ?, ?, ?)"
            );

            ps.setString(1, wf);
            ps.setString(2, key);
            ps.setString(3, status);
            ps.setString(4, out);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
