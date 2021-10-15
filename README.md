[![Maven](https://github.com/thomariobros/finance-report/actions/workflows/maven.yml/badge.svg)](https://github.com/thomariobros/finance-report/actions/workflows/maven.yml)

# About

Java program to compute [Williams %R](https://en.wikipedia.org/wiki/Williams_%25R) of default Boursorama list, [SBF 120](https://en.wikipedia.org/wiki/SBF_120) and UK markets during the last 14 weeks.
The program can send the report as email and triggered by cron.

The only supported datasource is [Boursorama](https://www.boursorama.com/).

# Build

```bash
mvn package
```

# Run

```bash
export DATA_SOURCE_BOURSORAMA_USERNAME=...
export DATA_SOURCE_BOURSORAMA_PASSWORD=...
export EMAIL_SEND=false|true
export EMAIL_FROM=...
export EMAIL_TO=...
export EMAIL_SMTP_HOST=...
export EMAIL_SMTP_PORT=...
export EMAIL_SMTP_SSL_ENABLE=false|true
export EMAIL_SMTP_STARTTTLS_ENABLE=false|true
export EMAIL_SMTP_AUTH=false|true
export EMAIL_SMTP_AUTH_USERNAME=...
export EMAIL_SMTP_AUTH_PASSWORD=...
export EMAIL_SMTP_FROM=...
java -jar target/financereport-jar-with-dependencies.jar
```