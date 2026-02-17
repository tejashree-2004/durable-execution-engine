package durable.engine;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DurableContext {

    private final String workflowId;
    private final StepStore store;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicInteger clock = new AtomicInteger();

    public DurableContext(String workflowId, StepStore store) {
        this.workflowId = workflowId;
        this.store = store;
    }

    private String nextKey(String id) {
        return id + "-" + clock.getAndIncrement();
    }

    public <T> T step(String id, Callable<T> fn, Class<T> clazz) {
        String key = nextKey(id);

        Optional<StepRecord> existing = store.get(workflowId, key);

        if (existing.isPresent() && "COMPLETED".equals(existing.get().status)) {
            try {
                return mapper.readValue(existing.get().output, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        store.saveRunning(workflowId, key);

        try {
            T result = fn.call();
            String json = mapper.writeValueAsString(result);
            store.saveCompleted(workflowId, key, json);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
