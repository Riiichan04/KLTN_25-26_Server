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
	//For jwt
	implementation(libs.jjwt.api)
	runtimeOnly(libs.jjwt.impl)
	runtimeOnly(libs.jjwt.jackson)
	//For cloudinary
	implementation (libs.cloudinary)
	implementation (libs.cloudinary.http5)

	implementation(libs.opencsv)

	api(libs.spring.boot.starter.mail)
	implementation(libs.spring.boot.starter.thymeleaf)
	implementation(libs.springboot.starter)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.security)

	testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
	testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}


tasks.withType<Test> {
	useJUnitPlatform()
}
