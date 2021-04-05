# Getting started

## Project Setup

### Setup NEXUSe2e

- Import the project
- IntelliJ: Activate the Maven Tool Window (right-click root module in Project Tool Window > "Add Framework Support" > "
  Maven")
- IntelliJ: Import Maven projects using the "+" sign in the Maven Tool Window (choose the pom.xml from the folders
  nexuse2e-parent, nexuse2e-core und nexuse2e-webapp)
- nexuse2e-parent mvn install
- nexuse2e-core mvn install
- nexuse2e-webapp mvn install

### Active Maven profile "with-new-frontend"

### Execute "mvn package" in nexuse2e-webapp

- npm and node are installed automatically including all dependencies
- The HTML, CSS and JS files resulting from the frontend build can be found in the folder
  nexuse2e-webapp\src\main\webapp\WEB-INF\ui
- After deployment, the new frontend can be found at /ui/, relative to your running nexus instance (
  e.g. http://localhost:8080/ui/)

## Run Configuration

### Server

- Application Server: Tomcat
- JRE: Java 8

### Deployment

- Artifact: nexuse2e-webapp:war exploded

### Before Launch

- Maven Goal: nexuse2e-core package
- Build artifact "nexuse2e-webapp:war exploded"

## Development

If you want to be able to make changes without rebuilding nexuse2e-webapp...

- Install the Angular CLI
- Use the exploded war artifact in your Run Configuration
- IntelliJ: In the tab "Server" in your Run Configuration, choose Update Action > Reload classes and resources
- Run "ng build --watch" to rebuild the Angular App on save
- IntelliJ: After each rebuild, run "Update Project" (white reload arrow) from the Services Tab

