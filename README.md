# Purview

## Project Structure

`/src/*` contains Java REST API project\
`/web/*` contains Angular project (based on CoreUI admin template)

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. JDK17
2. Any Java IDE (e.g. VS Code with Java Support, Eclipse, IntelliJ, etc.)
3. NodeJS 16+
4. Angular CLI 15+
```
npm i @angular/cli
```

Instructions
1. Clone the repository to your local machine
2. Build and run Java Backend
```
./mvnw spring-boot:run
```
3. REST API can be tested via Postman or if using VS Code (install REST client extension). HTTP sample commands are found in action-test.http

***Optional (H2 database console can be accessed via)
```
http://localhost:8080/h2-console
```
Put this value in JDBC URL and click connect
```
jdbc:h2:mem:purviewdb
```

4. Download Angular Frontend dependencies
```
cd web
npm install
```

5. Run Angular app in Development mode
```
ng serve
```