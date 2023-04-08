plugins {
	id("refinery-java-library")
}

configurations.testRuntimeClasspath {
	// VIATRA requires log4j 1.x, but we use log4j-over-slf4j instead
	exclude(group = "log4j", module = "log4j")
}

dependencies {
	implementation(libs.ecore)
	api(libs.viatra)
	api(project(":refinery-store-query"))
	testImplementation(libs.slf4j.simple)
	testImplementation(libs.slf4j.log4j)
}
