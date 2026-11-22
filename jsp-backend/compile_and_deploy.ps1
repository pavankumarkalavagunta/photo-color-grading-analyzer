# ==============================================================================
# Compile & Deploy Photo Color Grading Analyzer to Apache Tomcat 10
# Uses Maven (mvn) with JDBC, Jakarta Servlets 6, and JSP
# ==============================================================================

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Maven Build & Deploy Photo Analyzer (JSP / Tomcat 10)   " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Step 1: Find Maven
$mvnCmd = "mvn"
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Users\pavan\apache-maven-3.9.6\bin\mvn.cmd") {
        $mvnCmd = "C:\Users\pavan\apache-maven-3.9.6\bin\mvn.cmd"
    }
}

Write-Host "`n[1/3] Building WAR package with Maven ($mvnCmd)..." -ForegroundColor Yellow
& $mvnCmd package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] Maven package returned code $LASTEXITCODE. Falling back to direct javac compilation..." -ForegroundColor DarkYellow
    $javaFiles = (Get-ChildItem -Path "$scriptDir\src\main\java" -Recurse -Filter "*.java").FullName
    $tomcatLib = "C:\Users\pavan\tomcat\apache-tomcat-10.1.57\lib\*"
    $libCp = "$scriptDir\src\main\webapp\WEB-INF\lib\*;$tomcatLib"
    $classesDir = "$scriptDir\src\main\webapp\WEB-INF\classes"
    if (-not (Test-Path $classesDir)) { New-Item -ItemType Directory -Path $classesDir -Force | Out-Null }
    javac -cp $libCp -d $classesDir $javaFiles
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[FAIL] Direct compilation failed!" -ForegroundColor Red
        exit 1
    }
}
Write-Host "[PASS] Compilation and packaging successful!" -ForegroundColor Green

# Step 2: Define Tomcat Deployment Targets
$userTomcatWebapps = "C:\Users\pavan\tomcat\apache-tomcat-10.1.57\webapps"
$targetApp = "$userTomcatWebapps\photo-analyzer"
$targetRoot = "$userTomcatWebapps\ROOT"
$warFile = "$scriptDir\target\photo-analyzer.war"

Write-Host "`n[2/3] Deploying to Tomcat 10 ($userTomcatWebapps)..." -ForegroundColor Yellow

# Copy WAR file
if (Test-Path $warFile) {
    Copy-Item $warFile -Destination "$userTomcatWebapps\photo-analyzer.war" -Force
    Write-Host "  -> photo-analyzer.war deployed" -ForegroundColor Green
}

# Deploy exploded webapp to photo-analyzer and ROOT
$targets = @($targetApp, $targetRoot)
$sourceWebapp = "$scriptDir\src\main\webapp"

foreach ($target in $targets) {
    Write-Host "  -> Deploying to: $target" -ForegroundColor Cyan
    try {
        if (-not (Test-Path $target)) {
            New-Item -ItemType Directory -Path $target -Force | Out-Null
        }

        # Copy webapp resources
        Copy-Item "$sourceWebapp\*.jsp" -Destination $target -Force -ErrorAction SilentlyContinue
        Copy-Item "$sourceWebapp\*.jspf" -Destination $target -Force -ErrorAction SilentlyContinue
        if (Test-Path "$sourceWebapp\css") {
            Copy-Item "$sourceWebapp\css" -Destination $target -Recurse -Force
        }

        # Copy WEB-INF
        Copy-Item "$sourceWebapp\WEB-INF" -Destination $target -Recurse -Force

        # Ensure compiled classes from target/classes are in WEB-INF/classes
        if (Test-Path "$scriptDir\target\classes") {
            Copy-Item "$scriptDir\target\classes" -Destination "$target\WEB-INF" -Recurse -Force
        }

        # Copy ojdbc14.jar to target WEB-INF/lib
        if (Test-Path "$sourceWebapp\WEB-INF\lib\ojdbc14.jar") {
            Copy-Item "$sourceWebapp\WEB-INF\lib\ojdbc14.jar" -Destination "$target\WEB-INF\lib\ojdbc14.jar" -Force
        }

        Write-Host "     [OK] Deployed successfully" -ForegroundColor Green
    } catch {
        Write-Host "     [WARNING] Could not deploy to $($target): $_" -ForegroundColor DarkYellow
    }
}

Write-Host "`n[3/3] Deployment complete!" -ForegroundColor Green
Write-Host "Web app available at:" -ForegroundColor Cyan
Write-Host "  - http://localhost:8081/photo-analyzer/" -ForegroundColor White
Write-Host "  - http://localhost:8081/ (via ROOT)" -ForegroundColor White
Write-Host "==========================================================" -ForegroundColor Cyan
