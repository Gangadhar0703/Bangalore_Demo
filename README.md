# Accessibility Mapper

Access Atlas is a Java/Spring Boot website for monitoring step-free access, elevators, washrooms, tactile paving, and doorways at public parks, theatres, and malls in Bangalore. It uses server-rendered Thymeleaf pages, plain HTML/CSS/JavaScript, MySQL (or local H2), and Leaflet/OpenStreetMap.

## Run locally

Install Java 17 or newer and Maven 3.9 or newer, then run:

```powershell
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The default profile uses a local, file-backed H2 database and adds Bangalore demo places on first start. Places outside Bangalore and categories other than parks, theatres, and malls are excluded. Uploaded photos are stored in `uploads/`.

Demo accounts (created only when the database is empty):

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@access.org` | `mapaccess` |
| Community member | `mapper@access.org` | `mapaccess` |

These are local demo credentials. Disable demo seeding with `SEED_DEMO_DATA=false` outside development. The optional SQL sample users use the demo password `password`.

## MySQL

Create a database/user, then run `src/main/resources/db/mysql/schema.sql` followed by `src/main/resources/db/mysql/sample-data.sql` to load the example records. The SQL-only demo users use `password`; change or remove these demo credentials before exposing the database.

Alternatively, let Hibernate create/update the schema:

```powershell
$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:DB_URL = "jdbc:mysql://localhost:3306/accessibility_mapper?serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-password"
mvn spring-boot:run
```

For another JDBC database, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` on the default H2 profile. Optional configuration includes `PORT`, `UPLOAD_DIR`, and `SEED_DEMO_DATA`.

## What is included

- Search and map only Bangalore parks, theatres, and malls.
- Visitor facility checks record availability and their own last-checked dates for each of five access facilities.
- Admins review check-ins and photos; approval updates only the facility conditions the visitor actually checked.
- Authenticated contributions, secure passwords, MySQL schema/sample data, local H2 demo data, and MVC tests.

## Project layout

```text
src/main/java/org/accessibilitymapper/
  config/       Security, configuration, local demo data, static upload mapping
  model/        Account, Facility, Review
  repository/   Spring Data repositories
  service/      Search, account, review, and photo-storage services
  web/          MVC controllers and shared template model
src/main/resources/
  templates/    Thymeleaf pages
  static/       Vanilla JavaScript and CSS
  db/mysql/     MySQL schema and example records
```

Run the test suite with `mvn test`.

## Publish from GitHub with Render

1. On GitHub, create a **private** repository named `Bangalore_Demo` (no README or starter files).
2. Push this project folder to that repository:

   ```powershell
   git init
   git add .
   git commit -m "Initial Bangalore Accessibility Mapper"
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/Bangalore_Demo.git
   git push -u origin main
   ```

3. Sign in to [Render](https://render.com/), choose **New → Blueprint**, and connect the private GitHub repository. Render reads `render.yaml`, builds the Docker image, and provides your public service URL.

The Render setup creates a disk-backed H2 database and photo uploads so submitted updates persist across deploys. It requires a disk-compatible paid web service. Keep the attached disk; it contains the database and uploaded photos. Render generates unique seed account passwords; view them in the service's environment settings. The default account emails are `admin@access.org` and `mapper@access.org`. Change the admin email or generated credentials before sharing the site. Sample accessibility reports are demonstration data, not a guarantee of current conditions.
