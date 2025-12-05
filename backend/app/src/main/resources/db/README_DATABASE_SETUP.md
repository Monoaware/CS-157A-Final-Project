# Database Setup Instructions

## Prerequisites
- PostgreSQL installed and running
- Database `librarydb` created
- A PostgreSQL user with access to `librarydb`

---

## Quick Start (For Team Members)

### Option 1: Using Default Credentials (postgres/postgres)
If your PostgreSQL uses the default `postgres` user with password `postgres`:

```bash
cd backend
./gradlew bootRun
```

That's it! The app will connect automatically.

---

### Option 2: Using Custom Credentials (Recommended for Different Setups)
If you use different database credentials (e.g., `itadministrator` with no password):

```bash
cd backend
SPRING_DATASOURCE_USERNAME="your_username" \
SPRING_DATASOURCE_PASSWORD="your_password" \
./gradlew bootRun
```

**Example for itadministrator:**
```bash
SPRING_DATASOURCE_USERNAME="itadministrator" \
SPRING_DATASOURCE_PASSWORD="" \
./gradlew bootRun
```

---

## Initial Database Setup

### Step 1: Create Fresh Database with Initial Data
Run these commands **once** to set up your database with 15 entries per table:

```bash
# Drop and recreate schema (WARNING: This deletes all existing data!)
psql -h localhost -U your_username -d librarydb -f backend/app/src/main/resources/db/create_schema.sql

# Load initial data (15 entries per table)
psql -h localhost -U your_username -d librarydb -f backend/app/src/main/resources/db/initialize_data.sql
```

**Example for postgres user:**
```bash
psql -U postgres -d librarydb -f backend/app/src/main/resources/db/create_schema.sql
psql -U postgres -d librarydb -f backend/app/src/main/resources/db/initialize_data.sql
```

**Example for itadministrator user:**
```bash
psql -U itadministrator -d librarydb -f backend/app/src/main/resources/db/create_schema.sql
psql -U itadministrator -d librarydb -f backend/app/src/main/resources/db/initialize_data.sql
```

### Step 2: Validate Data (Optional)
```bash
psql -U your_username -d librarydb -f backend/app/src/main/resources/db/validate_data.sql
```

Expected output: `ALL VALIDATIONS PASSED`

---

## Data Persistence

### Default Behavior (Data Persists)
By default, `spring.sql.init.mode=never`, which means:
- Data survives application restarts
- Renew counts, new loans, fines, etc. are preserved
- Good for normal development and demos

### Reset Database on Every Startup (Optional)
To reset the database to initial state on **every** application restart:

1. Edit `backend/app/src/main/resources/application.properties`
2. Change `spring.sql.init.mode=never` to `spring.sql.init.mode=always`
3. Add this line: `spring.jpa.defer-datasource-initialization=true`

**Warning:** With `mode=always`, you'll lose all runtime changes when you restart the server!

---

## Team Collaboration Notes

- **Don't commit your personal database credentials** - use environment variables
- **The `application.properties` has default values** - override them locally as needed
- **Everyone can use different credentials** - just set the environment variables when starting the app
- **Initial data is in `initialize_data.sql`** - this is what everyone should start with

---

## Login Credentials

After running the initial data scripts, you can log in with:

**Admin/Staff Account:**
- Email: `Admin@account`
- Password: `Admin`

**Member Account:**
- Email: `Member@account`  
- Password: `Member`

---

## Troubleshooting

**Problem: Connection refused / authentication failed**
- Check that PostgreSQL is running: `psql -U your_username -d librarydb`
- Verify your username and password are correct
- Make sure `librarydb` database exists: `CREATE DATABASE librarydb;`

**Problem: Data not initializing**
- Make sure you ran the SQL scripts manually (Step 1 above)
- Check that `spring.sql.init.mode=never` in application.properties (default)

**Problem: Can't connect with environment variables**
- Make sure to set them in the same command as bootRun (see examples above)
- On Windows, use `set` instead of `export`