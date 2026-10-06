# OTP1 In-Class Assignments – Kalle Toivonen

## 1. Assignment Description

This repository contains a series of in-class assignments for single **Temperature Converter** application. The project
evolved step by step from a simple unit-tested Java class into a full JavaFX desktop
application with a MariaDB database backend, continuous integration via Jenkins, and
containerized deployment via Docker.

Key requirements addressed across the assignments:
- Unit testing with JUnit 5
- Code coverage analysis with JaCoCo
- Continuous integration with Jenkins (Freestyle project and Pipeline/Jenkinsfile)
- Containerization with Docker, including publishing the image to Docker Hub
- A JavaFX GUI backed by a relational database (MariaDB) with two related tables
- Running a GUI application from inside a Docker container (displayed via Xming/X server)

## 2. Technologies & Tools Used

- **Language:** Java
- **Build tool:** Maven
- **GUI framework:** JavaFX (javafx-controls, javafx-fxml)
- **Database:** MariaDB (accessed via the `mariadb-java-client` JDBC driver)
- **Testing:** JUnit 5
- **Code coverage:** JaCoCo (`jacoco-maven-plugin`)
- **CI/CD:** Jenkins (Freestyle project + Declarative Pipeline via `Jenkinsfile`)
- **Containerization:** Docker, Docker Hub
- **X server (for GUI-in-container):** Xming
- **IDE:** IntelliJ IDEA
- **Version control:** GitHub

## 3. Design Approach & Implementation Method

**Core logic**
`TemperatureConverter` is a plain Java class implementing the conversion formulas:
- Fahrenheit → Celsius
- Celsius → Fahrenheit
- Kelvin → Celsius
- Extreme temperature detection (below -40°C or above 50°C)

This class has no external dependencies, which keeps it easily unit-testable.

**Database design**
The database (`temperature_converter_db`) has two related tables:
- `temperature_unit` — a lookup table of supported units (Celsius, Fahrenheit, Kelvin)
- `temp_record` — a log of every conversion performed, storing the input value, output
  value, and foreign keys referencing `temperature_unit` for both the source and target
  unit, plus a timestamp

This satisfies the "two related tables" requirement: `temp_record` has two foreign keys
pointing back to `temperature_unit`.

**Data access layer**
`DBConnection` centralizes JDBC connection setup. The database host is read from the
`DB_HOST` environment variable (defaulting to `localhost`), which allows the exact same
code to run locally and inside a Docker container (where the host is reached via
`host.docker.internal`). `TemperatureUnitDAO` and `TempRecordDAO` encapsulate all SQL
queries behind simple Java methods, keeping SQL out of the UI layer.

**GUI design**
`Main` extends `javafx.application.Application` and builds a simple form (input field,
"From"/"To" unit dropdowns, Convert button, result label, and a history list) using
`GridPane` and `VBox`. On startup it loads the available units from the database; every
conversion is both displayed and persisted via `TempRecordDAO`, and the history list is
populated from the database.

**Key decisions**
- Kept the DB host configurable via an environment variable rather than hardcoding
  `localhost`, specifically to support running the same image locally and in Docker.
- Used a lookup table for units instead of a plain string column, in order to have a
  genuine foreign-key relationship between two tables rather than a single flat table.
- Used `mvn javafx:run` (via the `javafx-maven-plugin`) as the container's entry point
  instead of manually wiring JavaFX module paths, since it reliably resolves the
  platform-specific JavaFX native libraries.

## 4. Testing & Quality Assurance Steps

**Automated unit tests** (`TemperatureConverterTest`) cover:
- `fahrenheitToCelsius` and `celsiusToFahrenheit` with multiple representative inputs
- `kelvinToCelsius` with multiple representative inputs
- `isExtremeTemperature` with edge cases (just below/above the -40/50 boundaries, and a
  normal mid-range value)

**Integration tests** (`DatabaseIntegrationTest`) cover:
- That a connection can be opened via `DBConnection`
- That `TemperatureUnitDAO` retrieves all seeded units and a unit by id
- That `TempRecordDAO` can insert a record and retrieve the full history
- Getter behavior of the `TemperatureUnit` and `TempRecord` model classes

**Test results:** all tests pass (`mvn clean test` → `BUILD SUCCESS`, 0 failures,
0 errors).

**Code coverage:** generated with JaCoCo (`mvn clean verify` locally, and as a dedicated
stage in the Jenkins pipeline). The HTML report is published to GitHub Pages from the
`/docs` folder. The core conversion logic and the DAO layer have the highest coverage;
the JavaFX UI code in `Main` (event handlers, layout) is intentionally not unit-tested,
as GUI testing was outside the scope of these assignments.

**Continuous integration verification:**
- A Jenkins Freestyle project runs `mvn clean verify`, publishes JUnit test results, and
  publishes the JaCoCo coverage report.
- A Jenkins Pipeline (`Jenkinsfile`) automates: checkout → build → test → code coverage
  → publish test results → publish coverage report → build Docker image → push the
  image to Docker Hub.
- The Docker image was verified by running it locally (`docker run`) and by running the
  JavaFX GUI from inside the container, displayed on the host via Xming.

## 5. How to Run

### Prerequisites
- JDK 17
- Maven
- MariaDB running locally, with the database and tables created (see `docs/schema.sql`
  if present, or run the `CREATE DATABASE`/`CREATE TABLE` statements described below)
- Docker Desktop (for the containerized version)
- An X server such as Xming (only needed to view the GUI when running via Docker)

### Database setup
```sql
CREATE DATABASE IF NOT EXISTS temperature_converter_db;
USE temperature_converter_db;

CREATE TABLE temperature_unit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);
INSERT INTO temperature_unit (name) VALUES ('Celsius'), ('Fahrenheit'), ('Kelvin');

CREATE TABLE temp_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input_value DOUBLE NOT NULL,
    input_unit_id INT NOT NULL,
    output_value DOUBLE NOT NULL,
    output_unit_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (input_unit_id) REFERENCES temperature_unit(id),
    FOREIGN KEY (output_unit_id) REFERENCES temperature_unit(id)
);
```

### Run locally
```bash
mvn clean test          # run unit + integration tests
mvn clean verify        # run tests and generate the JaCoCo coverage report
mvn javafx:run          # launch the JavaFX application
```
The JaCoCo HTML report is generated at `target/site/jacoco/index.html`.

### Run with Docker
```bash
docker build -t temperature-converter .
docker run -e DISPLAY=host.docker.internal:0.0 temperature-converter
```
(Requires Xming or another X server running locally, configured with
"No Access Control".)

### Pre-built image
```bash
docker pull keltzu/temperature-converter
```
Available on Docker Hub: https://hub.docker.com/r/keltzu/temperature-converter

### CI/CD
- Jenkins Freestyle job: builds the project and publishes test/coverage reports.
- Jenkins Pipeline (`Jenkinsfile`): builds, tests, generates coverage, builds the Docker
  image, and pushes it to Docker Hub.
