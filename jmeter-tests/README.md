# JMeter Tests for LabWork System

This directory contains JMeter test plans to verify the concurrent user operations and transaction isolation in the LabWork system.

## Test Scenarios

### 1. Concurrent CRUD Operations
- Simulates 10 concurrent users performing create, read, update, and delete operations
- Each user performs 5 iterations of the CRUD cycle
- Users ramp up over 30 seconds

### 2. Concurrent Import Operations
- Simulates 5 concurrent users importing JSON files
- Each user performs 3 import operations
- Users ramp up over 15 seconds

### 3. Conflict Testing
- Simulates 3 concurrent users trying to create LabWorks with the same name
- Tests the unique name constraint implementation
- Each user performs 5 attempts to create a LabWork with the same name

## Running the Tests

1. Make sure Apache JMeter is installed on your system
2. Start the LabWork application server
3. Open the JMeter test plan:
   ```
   jmeter -t concurrent_users_test.jmx
   ```
4. Run the test plan
5. View results in the "View Results Tree" and "Aggregate Report" listeners

## Expected Results

- All CRUD operations should complete successfully with HTTP 200/201/204 status codes
- Import operations should complete successfully with HTTP 201 status codes
- Conflict testing should result in HTTP 400 errors for duplicate name violations
- No data inconsistencies should occur during concurrent operations