# How to Run Photo Color Grading Analyzer in IntelliJ IDEA

This guide explains step-by-step how to open, build, and run the **Photo Color Grading Analyzer** in **IntelliJ IDEA** using **Maven, JSP, Servlets, JDBC, and Apache Tomcat 10**.

---

## 1. Project Stack Summary

- **IDE**: IntelliJ IDEA (Community or Ultimate Edition)
- **Build Tool**: Apache Maven (`pom.xml`)
- **Backend**: JavaServer Pages (JSP) + Jakarta Servlets 6
- **Web Server**: Apache Tomcat 10.1.57 (Port `8081`)
- **Database (JDBC)**: Oracle Database XE (`localhost:1521:XE`) or MySQL (`localhost:3306`)
- **Computer Vision Service**: Python 3.12 + FastAPI + OpenCV (`localhost:8000`)

---

## 2. Step 1: Open Project in IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Click **Open** (or `File` ➔ `Open...`).
3. Select the project folder:
   - Recommended: `C:\Users\pavan\OneDrive\Desktop\fsd`
   - (Or navigate directly to `fsd\photo-color-grading-analyzer\jsp-backend`)
4. When prompted, choose **"Trust Project"**.
5. IntelliJ will automatically detect `pom.xml` and download required dependencies:
   - `jakarta.servlet-api` (6.0.0)
   - `jakarta.servlet.jsp-api` (3.1.1)
   - `ojdbc14` (Oracle JDBC Driver)
   - `mysql-connector-j` (8.3.0)

> **Maven Tool Window**: Look at the right sidebar in IntelliJ. Click the **M (Maven)** icon to see your project goals: `clean`, `compile`, `package`.

---

## 3. Step 2: Run with Apache Tomcat in IntelliJ

Choose the option matching your IntelliJ IDEA edition:

### Option A: IntelliJ IDEA Community Edition (Using Smart Tomcat Plugin - Recommended)

1. **Install Plugin**:
   - Go to `File` ➔ `Settings` (or press `Ctrl + Alt + S`).
   - Select **Plugins** ➔ click the **Marketplace** tab.
   - Search for **`Smart Tomcat`** ➔ Click **Install** ➔ Click **OK**.
2. **Run Configuration**:
   - Pre-configured run configurations have already been placed in `.run/`!
   - In IntelliJ's top toolbar, click the Run Configuration dropdown next to the green Play button.
   - Select **`2. Smart Tomcat (photo-analyzer)`**.
   - Click the **Green Run (▶️)** button!
3. **If setting up manually**:
   - `Run` ➔ `Edit Configurations...` ➔ Click `+` ➔ select **Smart Tomcat**.
   - **Tomcat Server**: Browse to `C:\Users\pavan\tomcat\apache-tomcat-10.1.57`.
   - **Deployment Directory**: Browse to `photo-color-grading-analyzer/jsp-backend/src/main/webapp`.
   - **Context Path**: `/photo-analyzer`
   - **Server Port**: `8081`
   - Click **Apply** and **OK**, then click **Run**.

---

### Option B: IntelliJ IDEA Ultimate Edition (Native Tomcat)

1. Go to `Run` ➔ `Edit Configurations...`.
2. Click **`+`** (Add New Configuration) ➔ Select **Tomcat Server** ➔ **Local**.
3. **Server Configuration**:
   - Name: `Tomcat 10.1`
   - Application server: Click **Configure...** ➔ Select `C:\Users\pavan\tomcat\apache-tomcat-10.1.57`.
   - HTTP Port: `8081`
4. **Deployment Tab**:
   - Click the **Deployment** tab at the top.
   - Click **`+`** ➔ select **Artifact...** ➔ Choose `photo-analyzer:war exploded`.
   - Set **Application context**: `/photo-analyzer`
5. Click **Apply** and **OK**.
6. Click the **Green Run (▶️)** button!

---

### Option C: IntelliJ Built-in Terminal (Direct Launcher)

If you prefer launching Tomcat with one command inside IntelliJ:
1. Open the built-in **Terminal** tab at the bottom of IntelliJ (`Alt + F12`).
2. Run:
   ```cmd
   .\run-jsp-tomcat.bat
   ```
   *This automatically builds with Maven, packages the WAR, verifies Python is running, and starts Tomcat!*

---

## 4. Step 3: Run the Python OpenCV Engine

The image analysis engine calculates histograms, K-Means dominant colors, focus sharpness, and dynamic range using Python OpenCV:

1. Open the **Terminal** tab in IntelliJ (`Alt + F12`).
2. Run:
   ```cmd
   cd photo-color-grading-analyzer\analyzer
   python -m uvicorn app.main:app --host 127.0.0.1 --port 8000
   ```
   *(Or click `0. Start Python OpenCV Engine` in the Run configurations dropdown!)*
3. Check health in your browser: [http://127.0.0.1:8000/health](http://127.0.0.1:8000/health)

---

## 5. Step 4: Access the Application

Once Tomcat and the Python analyzer are running, open your browser:

| Feature | URL |
|---|---|
| **Home Page** | [http://localhost:8081/photo-analyzer/](http://localhost:8081/photo-analyzer/) (or [http://localhost:8081/](http://localhost:8081/)) |
| **Upload Photo** | [http://localhost:8081/upload.jsp](http://localhost:8081/upload.jsp) |
| **Analysis History** | [http://localhost:8081/history](http://localhost:8081/history) |
| **System Health Check** | [http://localhost:8081/health](http://localhost:8081/health) |

---

## 6. Project Architecture in IntelliJ

```
photo-color-grading-analyzer/jsp-backend/
├── pom.xml                                  <-- Maven build file
├── src/main/java/com/photoanalyzer/
│   ├── dao/
│   │   ├── AnalysisDAO.java                 <-- Thread-safe DAO with memory cache
│   │   └── AnalysisJdbcDAO.java             <-- JDBC PreparedStatement / ResultSet CRUD
│   ├── model/
│   │   ├── AnalysisReport.java              <-- Core metrics data model
│   │   ├── DominantColor.java               <-- Centroid color & percentage
│   │   └── Recommendation.java              <-- Lightroom grading recommendations
│   ├── servlet/
│   │   ├── AnalysisServlet.java             <-- Upload handling (/analyze, /report)
│   │   ├── ApiAnalysisServlet.java          <-- REST API for React (/api/analysis/*)
│   │   ├── DeleteServlet.java               <-- Report deletion (/delete)
│   │   ├── HealthServlet.java               <-- System health probe (/health)
│   │   └── HistoryServlet.java              <-- Database history viewer (/history)
│   └── util/
│       ├── DBConnection.java                <-- JDBC Connection manager
│       ├── JsonUtil.java                    <-- Lightweight JSON parser/serializer
│       └── PythonAnalyzerClient.java        <-- HTTP/1.1 client to Python engine
├── src/main/resources/
│   └── db.properties                        <-- Oracle / MySQL JDBC connection settings
└── src/main/webapp/
    ├── css/style.css                        <-- Photography dark theme styling
    ├── WEB-INF/web.xml                      <-- Servlet mappings & multipart config
    ├── WEB-INF/lib/                         <-- JDBC driver JARs
    ├── index.jsp                            <-- Landing page
    ├── upload.jsp                           <-- Drag-and-drop preview form
    ├── report.jsp                           <-- Full metrics & Canvas histogram dashboard
    └── history.jsp                          <-- Database history log
```

---

## 7. Troubleshooting in IntelliJ

- **"Port 8081 is already in use"**:
  If Tomcat is already running in the background, you can either stop it in Task Manager or run `netstat -ano | findstr :8081` and kill the PID.
- **"Cannot connect to Python analyzer"**:
  Make sure `uvicorn app.main:app --port 8000` is active. Check [http://127.0.0.1:8000/health](http://127.0.0.1:8000/health).
- **"JDBC Connection Failed"**:
  Verify Oracle XE service is running (`OracleServiceXE` and `OracleXETNSListener`). The credentials are set in `db.properties` (`wildlife`/`wildlife`).
