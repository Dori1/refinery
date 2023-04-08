plugins {
	id("refinery-java-library")
}

dependencies {
	implementation(libs.eclipseCollections)
	implementation(libs.eclipseCollections.api)
	api(project(":refinery-language"))
	api(project(":refinery-store"))
	testImplementation(testFixtures(project(":refinery-language")))
}
