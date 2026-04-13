# <u>crd-technical-assessment</u>

# CRD Technical Assessment - Rebalancing Framework

## Overview
This project is a Java-based test framework built to validate a portfolio rebalancing service.  
It ensures that securities in a portfolio are correctly adjusted based on target allocations.

The framework uses scenario-driven test cases defined in JSON and validates buy/sell decisions based on asset allocation rules.

---

## Architecture

The project is structured into:

- **Service Layer**
    - `RebalanceService` :  Core business logic for portfolio rebalancing

- **Test Layer**
    - `RebalanceServiceTest` : Scenario-based tests

- **Test Data Layer**
    - `securitiesAllocation.json` -: JSON-based scenarios (happy path, edge cases, invalid inputs)

---

## JSON Input Format

Each scenario contains:

```json
{
  "totalAsset": 100000,
  "securities": [
    {
      "name": "IBM",
      "targetPercentage": 20,
      "currentPercentage": 10,
      "unitPrice": 150
    }
  ]
}
```

## Tech Stack
Java 8+ / 11+
JUnit (testing framework)
Jackson (JSON parsing)
Gradle (build tool)

## How to Build Project and Run Tests
```bash
./gradlew clean build
```

if project is already build and dependencies has already obtained:
```bash
./gradlew test
```

## Added Jacoco Test coverage report 
Project makes use of Jacoco plugin for Test coverage