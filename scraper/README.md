# Proyecto Parking: Scraper

* **Tech**: Java JDK + Jsoup.
* **Source**: [Búsqueda Directorio](http://online.ucn.cl/directoriotelefonicoemail/Default.aspx)
* **Source**: [Nombre Rut y Firma - Rutificador](https://www.nombrerutyfirma.com/)

## Java Libraries

* SLF4J: [org.slf4j:slf4j-api](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
* Logback: [ch.qos.logback:logback-classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic)
* Jsoup: [org.jsoup:jsoup](https://mvnrepository.com/artifact/org.jsoup/jsoup)
* Gson: [com.google.code.gson:gson](https://mvnrepository.com/artifact/com.google.code.gson/gson)
* Lombok: [org.projectlombok:lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* Commons-lang: [org.apache.commons:commons-lang3](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3)
* JPA: [javax.persistence:javax.persistence-api](https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api)

## Domain Model

<div hidden>
```
@startuml

class Main {
    {static} + main(String[])
    {static} - sleep()
}

class DirectorioUCN {
    {static} - URL: String
    {static} + scrape(Integer): Ficha
    {static} - getText(Document, String): String
}

class Ficha <<Builder>> {
    - nombre: String
    - cargo: String
    - unidad: String
    - email: String
    - telefono: String
    - oficina: String
    - direccion: String
}

class NombreRutFirma {
    {static} - URL: String
    {static} + scrape(String): List<Rutificador>
}

class Rutificador <<Builder>> {
    - nombre: String
    - rut: String
    - sexo: String
    - direccion: String
    - comuna: String
}

class Persona <<Entity>> {
    - id: Long
    - codigo: Integer
    - rut: String
    - nombre: String
    - email: String
    - cargo: String
    - unidad: String
    - oficina: String
    - direccionOficina: String
    - sexo: Sexo
    - direccion: String
    - comuna: String
    - telefonoFijo: String
    - telefonoMovil: String
}

enum Sexo {
    MASCULINO,
    FEMENINO
}

Main ..> DirectorioUCN: <<use>>
Main ..> NombreRutFirma: <<use>>

Persona --> Sexo

NombreRutFirma +-- Rutificador
DirectorioUCN +-- Ficha

@enduml
```
</div>
