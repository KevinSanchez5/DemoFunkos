plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
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
    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    //SpringBoot test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    //JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //H2
    implementation("com.h2database:h2") // base de datos a usar, puede ser otra
    //Swagger
    implementation("io.springfox:springfox-boot-starter:3.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
