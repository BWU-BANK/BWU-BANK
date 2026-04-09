# BWU-BANK Deployment Guide

This guide provides instructions to deploy and run the BWU-BANK system. The system consists of two separate components:
1. **Frontend**: A static HTML/JS/CSS client using local storage.
2. **Backend**: A Java CLI application connecting to a remote Supabase PostgreSQL database.

---

## 1. Environment Variables setup

The Java backend has been configured to read from Environment Variables for enhanced security. If these variables are not set, it will fall back to using default credentials (useful for local development). 

For production, before running the Java application, ensure you set the following environment variables on your system/server:

- `SUPABASE_DB_URL`: The JDBC connection string for your Supabase database (e.g., `jdbc:postgresql://db.YOUR_ID.supabase.co:5432/postgres`)
- `SUPABASE_DB_USER`: Your Supabase database user (e.g., `postgres`)
- `SUPABASE_DB_PASSWORD`: Your database password

**Setting Environment Variables:**
- **On Linux/macOS:**
  ```bash
  export SUPABASE_DB_URL="jdbc:postgresql://......"
  export SUPABASE_DB_USER="postgres"
  export SUPABASE_DB_PASSWORD="your-secure-password"
  ```
- **On Windows:**
  ```powershell
  $env:SUPABASE_DB_URL="jdbc:postgresql://......"
  $env:SUPABASE_DB_USER="postgres"
  $env:SUPABASE_DB_PASSWORD="your-secure-password"
  ```

---

## 2. Deploying the Java Backend

The Java Application is packaged as a "Fat JAR", meaning it includes all its dependencies (like the PostgreSQL JDBC driver) within a single `.jar` file. This allows it to run on any machine with Java installed.

### Build Instructions

To build the executable JAR, you'll need [Apache Maven](https://maven.apache.org/) installed along with Java 17+.

1. Open a terminal in the root path of the project.
2. Run the full compilation package step:
   ```bash
   mvn clean package
   ```
3. Once completed successfully, check your `target/` directory. You will find a file named `banking-simulation-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### Running the Backend

Upload the generated `.jar` file to your server (VPS, EC2, or any machine).

Execute it directly via the terminal:

```bash
java -jar target/banking-simulation-1.0-SNAPSHOT-jar-with-dependencies.jar
```

*Note: The backend is a CLI terminal simulation. It needs to run interactively.*

---

## 3. Deploying the Web Frontend

The Web Frontend consists of static files located in the `frontend` directory. It can be easily deployed to any static web host such as **Vercel**, **Netlify**, or **GitHub Pages**.

### Deploying to Vercel (Recommended)

Since the project contains a `vercel.json` file in the root directory, deploying to Vercel is extremely simple.

1. Create a `Vercel` account and add the project via the Vercel CLI or link your Git repository.
2. If using Vercel CLI, simply run:
   ```bash
   vercel
   ```
3. Accept the default prompts. Vercel will automatically look at `vercel.json` to route traffic appropriately into the `frontend/` directory.

### Running the Frontend Locally

We've added a `package.json` with a script to serve your files locally for testing.
Make sure you have Node.js installed.

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the local server:
   ```bash
   npm start
   ```
3. Open `http://localhost:3000` in your web browser.
