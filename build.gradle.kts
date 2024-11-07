plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("jacoco") // Plugin de JaCoCo
}

group = "kj"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // H2
    implementation("com.h2database:h2") // base de datos a usar, puede ser otra
    // Swagger
    implementation("io.springfox:springfox-boot-starter:3.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Ejecuta el reporte de JaCoCo después de los tests
}

jacoco {
    toolVersion = "0.8.10" // Usa la última versión estable de JaCoCo
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Asegura que el reporte solo se ejecute tras los tests

    reports {
        xml.required = false  // Para reportes en XML (útil para CI/CD)
        csv.required = false // CSV es opcional
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml") // Reporte en HTML
    }
}

// Configuración opcional para verificación de cobertura mínima (por ejemplo, 80%)
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal() // Requiere al menos el 80% de cobertura total
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
