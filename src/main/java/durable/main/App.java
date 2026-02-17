package durable.main;

import durable.engine.*;
import durable.examples.OnboardingWorkflow;

public class App {

    public static void main(String[] args) throws Exception {

        SQLiteStore db = new SQLiteStore("workflow.db");
        StepStore store = new SQLiteStepStore(db.getConnection());

        DurableContext ctx = new DurableContext("workflow-1", store);

        new OnboardingWorkflow().run(ctx);
    }
}
