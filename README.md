![web page one](./client/public/wb1.png)

![web page two](./client/public/wb2.png)

# spring-auth-system

## start the project

clone the repo

```bash
git clone https://github.com/RaoufGhezal/spring-auth-system
cd spring-auth-system
```

create postgresql container

```bash
docker run --name spring-auth-db -e POSTGRES_DB=spring_auth_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

open client api file

```bash
/client/src/services/api.ts
```

set backend endpoint

```ts
const baseURL = "http://localhost:8080"
```

open server config file

```bash
/server/src/main/resources/application.properties
```

set config

```properties
#DB CONFIG

spring.datasource.url=jdbc:postgresql://localhost:5432/spring_auth_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

#JWT SECRET

jwt.secret=your-secret-key

#FRONT-END ENDPOINT

app.front_endpoint=http://localhost:5173
```

start client

```bash
cd client
npm install
npm run dev
```

start server

```bash
cd server
./mvnw spring-boot:run
```
