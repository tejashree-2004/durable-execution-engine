package durable.engine;

import java.util.Optional;

public interface StepStore {

    Optional<StepRecord> get(String wf, String key);

    void saveRunning(String wf, String key);

    void saveCompleted(String wf, String key, String output);
}
