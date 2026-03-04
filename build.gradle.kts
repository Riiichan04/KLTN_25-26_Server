plugins {
	java
	id("java-library")
	id("org.springframework.boot") version "4.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "vn.id.nonglam.kltn"
version = "0.0.1-SNAPSHOT"
description = "Graduation project server"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	api(libs.mysql.mysql.connector.java)
	api(libs.org.jdbi.jdbi3.core)
	api(libs.org.jdbi.jdbi3.sqlobject)

	api(libs.org.projectlombok.lombok)
	annotationProcessor(libs.org.projectlombok.lombok)

	api(libs.com.google.code.gson.gson)

	api(libs.org.mindrot.jbcrypt)

	//For Hibernate/JPA ORM
	api(libs.org.hibernate.orm)

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	implementation (libs.cloudinary)
	implementation (libs.cloudinary.http5)

	api(libs.spring.boot.starter.mail)
	implementation(libs.spring.boot.starter.thymeleaf)
	implementation(libs.springboot.starter)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.web)

	testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
	testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}


tasks.withType<Test> {
	useJUnitPlatform()
}
