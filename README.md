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

## Deploy on Oracle Cloud Always Free

The project includes a Docker Compose deployment for an Oracle Cloud Infrastructure (OCI) Always Free Ubuntu ARM VM. The app, H2 database, and uploaded photos run on the VM; Caddy provides HTTPS. No paid web-hosting service or external database is needed.

OCI's Always Free A1 allowance is up to 2 OCPUs and 12 GB of memory in your home region. Oracle may require phone and card verification to create an account; its Free Tier documentation says the card is not charged unless you upgrade. Do not upgrade or provision resources that are not marked Always Free. Availability depends on region, and Oracle may reclaim idle Always Free compute instances. Keep a separate backup of important data. See [OCI Free Tier](https://www.oracle.com/cloud/free/) and [Always Free resource limits](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm).

### 1. Create an Always Free VM

In the OCI console, create an Ubuntu 24.04 ARM VM in your home region using the `VM.Standard.A1.Flex` shape, within the Always Free limits (for example, 1 OCPU and 6 GB RAM). Use the default boot volume size. Add your own SSH public key at creation and retain the private key securely.

In the VM's VCN security rules, allow inbound TCP 22 only from your own IP address, and TCP 80 and 443 from the internet. Do not open port 8080. Note the VM's public IP.

### 2. Install the server runtime and get the project

These commands install Docker on the cloud VM, not on your computer. The GitHub repository is public, so it can be cloned without a deploy key.

```bash
sudo apt update
sudo apt install -y git docker.io docker-compose-v2
sudo systemctl enable --now docker
sudo git clone https://github.com/Gangadhar0703/Bangalore_Demo.git /opt/accessibility-mapper
sudo chown -R "$USER":"$USER" /opt/accessibility-mapper
cd /opt/accessibility-mapper
```

### 3. Configure and start the website

Copy `.env.example` to `.env`. Set `SITE_DOMAIN` to `<your-public-ip>.sslip.io` (for example, `203.0.113.10.sslip.io`) and replace both sample passwords with long, unique passwords. This hostname maps to the VM's IP; Caddy obtains and renews the HTTPS certificate automatically. Keep `.env` private; it is ignored by Git.

```bash
cp .env.example .env
nano .env
sudo docker compose up -d --build
sudo docker compose logs -f
```

Open `https://<your-public-ip>.sslip.io`. The first boot seeds demonstration accounts and sample places. The administrator email and password are the values in `.env`; keep the password private. Demo accessibility reports may be out of date and are not a guarantee of current conditions.

Persistent application data is stored in `data/` and uploaded photos in `uploads/`. Back up both directories before replacing or deleting the VM. After pushing code changes to GitHub, update the deployment with:

```bash
cd /opt/accessibility-mapper
git pull
sudo docker compose up -d --build
```
