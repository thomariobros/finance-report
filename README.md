# About

Java program to compute [Williams %R](https://en.wikipedia.org/wiki/Williams_%25R) of default Boursorama list, [SBF 120](https://fr.wikipedia.org/wiki/SBF_120) and UK markets during the last 14 weeks.
The program can send the report as email and by triggered by cron.

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
java -jar target/financereport-jar-with-dependencies.jar
```