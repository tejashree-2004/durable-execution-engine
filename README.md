# Durable Execution Engine (Assignment 1)

## Overview

This project implements a minimal **durable workflow execution engine** in Java inspired by systems like Temporal, Cadence, and Azure Durable Functions.

The goal of the engine is to execute workflows safely such that:

- Completed steps are never re-executed
- Workflows can resume after crashes
- Execution is deterministic and recoverable
- Parallel steps are supported
- State is persisted across restarts

The engine provides a `step()` primitive that guarantees durable execution.

If the program crashes midway, it resumes from the exact failure point on the next run.

---

## Architecture

Workflow Code
↓
DurableContext (step engine)
↓
StepStore (Persistence API)
↓
SQLite Storage


Each workflow step is:

1. Identified with a deterministic step key
2. Checked against persistent storage
3. Skipped if already completed
4. Executed otherwise
5. Result serialized and stored

This ensures **exactly-once step semantics**.

---

## Core Components

### DurableContext

Central execution engine.

Responsibilities:

- Step sequencing
- Memoization of results
- Crash recovery logic
- JSON serialization
- Idempotent execution

Provides API:

<T> T step(String id, Callable<T> fn, Class<T> type)


---

### StepStore Interface

Abstract persistence layer.

Allows engine to work with any storage backend.

Current implementation uses SQLite, but could be extended to:

- PostgreSQL
- Redis
- DynamoDB
- Distributed storage

---

### SQLiteStepStore

Concrete persistence implementation.

Stores step state in table:

steps(
workflow_id TEXT,
step_key TEXT,
status TEXT,
output TEXT
)


Statuses:

- RUNNING
- COMPLETED

---

### OnboardingWorkflow (Example)

Demonstrates:

- Sequential execution
- Parallel execution
- Crash recovery
- Step skipping
- Workflow resumption

Parallel steps implemented via `CompletableFuture`.

---

## Durability Model

The engine guarantees:

- Completed steps never rerun
- Incomplete steps retry safely
- Workflow resumes after crash
- Output is memoized
- Execution order is deterministic

Example step sequence:

create-user-0
laptop-1
access-2
email-3


Even with loops or parallelism, keys remain stable.

---

## Crash Recovery Demo

To simulate failure:

System.exit(1);


inside a step.

Run once → crash  
Run again → resume

Completed steps are skipped automatically.

This demonstrates durability guarantees.

---

## Concurrency Model

Parallel execution is supported via:

CompletableFuture


The persistence layer is synchronized to prevent race conditions.

This ensures safe concurrent step writes.

---

## Security Considerations (Bonus)

- SQLite access is encapsulated behind StepStore
- No direct SQL exposed to workflows
- JSON serialization prevents unsafe object persistence
- Engine prevents duplicate side-effects
- Thread-safe storage prevents corruption

This design protects workflow integrity.

---

## Performance Considerations (Bonus)

- Step memoization prevents recomputation
- Minimal disk writes
- Lightweight SQLite backend
- Constant-time step lookup
- Locking only at persistence layer
- Parallel execution improves throughput

Engine is optimized for correctness first, performance second.

---

## Assumptions

- Steps are idempotent
- Single-node execution
- One workflow per workflow ID
- SQLite file is durable storage
- Workflows are deterministic

---

## Limitations

- Not distributed
- No scheduling layer
- No timeouts/retries
- No external orchestration
- Single-machine execution

These are deliberate scope limits for the assignment.

---

## Setup

### Requirements

- Java 8+
- Maven

### Build

mvn clean package


### Run

mvn exec:java


or run `App.java` from IDE.

SQLite database file is created automatically.

---

## Testing Durability

1. Run application
2. Force crash during a step
3. Restart program
4. Observe skipped steps

Engine resumes automatically.

---

## Development Notes

This project was developed using standard research, documentation, and reference materials. All architectural and implementation decisions were reviewed, understood, and validated during development.

---

## Author

Tejashree
Software Engineer Intern Assignment
