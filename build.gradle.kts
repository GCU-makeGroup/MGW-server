plugins {
	java
	id("org.springframework.boot") version "3.5.13"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.awp"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.16")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // Q-Class
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // Jakarta
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j")
}

// Q-Class가 생성될 경로 지정 (기본적으로 build 폴더 아래에 생깁니다)
val querydslDir = layout.buildDirectory.dir("generated/querydsl").get().asFile

sourceSets {
    main {
        java.srcDirs(querydslDir)
    }
}

// 컴파일 태스크 설정
tasks.named<JavaCompile>("compileJava") {
    options.generatedSourceOutputDirectory.set(querydslDir)
}

// clean 태스크 실행 시 생성되었던 Q-Class 폴더도 함께 지우도록 설정
tasks.named<Delete>("clean") {
    delete(querydslDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}