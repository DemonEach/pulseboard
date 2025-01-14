# pulseboard

## Docker image

You can clone project and change

`env/backend.env`

```
DB_HOST=db # database host, don't change it, it references docker image "db"
DB_NAME=pulseboard_db # database name, if you change it here make sure to change it in "env/db.env" in POSTGRES_DB parameter 
DB_PASS=changeit # database password if you change it here make sure to change it in "env/db.env" in POSTGRES_DB
DB_PORT=5432 # postgres default port
DB_USER=postgres # postgres default user
JWT_SECRET=MyC001AndTot@1y$ecur3JwtS3cr3tButCh@ngetIt # JWT secret MAKE SURE TO CHANGE IT
SERVER_PORT=8880
LOGS_PATH=/app/logs/ # logs path you can change if you want
MAIL_HOST=smtp.gmail.com # smtp server host
MAIL_PORT=587 # smtp server port
MAIL_USER=test@gmail.com # smtp server username
MAIL_PASS=password # smtp server password
```

`env/db.env`, you can read [postgresql docker](https://hub.docker.com/_/postgres/) documentation to add more parameters

To launch use `docker compose up` command