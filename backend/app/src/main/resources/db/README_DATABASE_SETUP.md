# Setup Instructions for Graders

## Step 1: Create Fresh Database
```bash
# Drop and recreate schema
psql -h localhost -U itadministrator -d librarydb -f backend/app/src/main/resources/db/create_schema.sql
```

## Step 2: Load Data
```bash
# Load 15 entries per table
psql -h localhost -U itadministrator -d librarydb -f backend/app/src/main/resources/db/initialize_data.sql
```

## Step 3: Validate (Optional)
```bash
# Run validation queries
psql -h localhost -U itadministrator -d librarydb -f backend/app/src/main/resources/db/validate_data.sql
```

Expected output: `ALL VALIDATIONS PASSED`

## Step 4: Start Application
```bash
cd backend
./gradlew bootRun
```
