plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    // jcenter() est obsolète, évite de l’utiliser sauf si nécessaire
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("org.postgresql:postgresql")
    implementation("io.jsonwebtoken:jjwt:0.12.6")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:3.0.1")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    ignoreFailures = true
}

tasks.named<Javadoc>("javadoc") {
    val mainSourceSet = project.extensions.getByName("sourceSets") as SourceSetContainer
    source = mainSourceSet.getByName("main").allJava
    classpath += mainSourceSet.getByName("main").compileClasspath

    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        addBooleanOption("author", true)
        addBooleanOption("version", true)
        links("https://docs.oracle.com/en/java/javase/17/docs/api/")
        if (JavaVersion.current().isJava9Compatible) {
            addBooleanOption("html5", true)
        }
    }
}
