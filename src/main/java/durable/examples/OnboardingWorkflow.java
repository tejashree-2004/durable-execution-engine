package durable.examples;

import java.util.concurrent.CompletableFuture;

import durable.engine.DurableContext;

public class OnboardingWorkflow {

    public void run(final DurableContext ctx) {

        // STEP 1 — simulate crash
        ctx.step("create-user", new java.util.concurrent.Callable<String>() {
            public String call() {
                System.out.println("Creating user...");

                // simulate crash on first run
                if (Math.random() < 0.7) {
                    System.out.println("Simulated crash!");
                    System.exit(1);
                }

                return "user123";
            }
        }, String.class);

        // STEP 2 — parallel steps
        CompletableFuture<Void> laptop =
            CompletableFuture.runAsync(new Runnable() {
                public void run() {
                    ctx.step("laptop", new java.util.concurrent.Callable<String>() {
                        public String call() {
                            System.out.println("Provision laptop");
                            return "done";
                        }
                    }, String.class);
                }
            });

        CompletableFuture<Void> access =
            CompletableFuture.runAsync(new Runnable() {
                public void run() {
                    ctx.step("access", new java.util.concurrent.Callable<String>() {
                        public String call() {
                            System.out.println("Grant access");
                            return "done";
                        }
                    }, String.class);
                }
            });

        CompletableFuture.allOf(laptop, access).join();

        // STEP 3 — final step
        ctx.step("email", new java.util.concurrent.Callable<String>() {
            public String call() {
                System.out.println("Send email");
                return "sent";
            }
        }, String.class);
    }
}

