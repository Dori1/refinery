import tools.refinery.buildsrc.SonarPropertiesUtils

plugins {
	id("refinery-java-library")
	id("refinery-mwe2")
	id("refinery-sonarqube")
}

dependencies {
	api(libs.ecore)
	api(libs.ecore.xmi)
	mwe2(libs.ecore.codegen)
	mwe2(libs.mwe.utils)
	mwe2(libs.mwe2.lib)
	mwe2(libs.xtext.core)
	mwe2(libs.xtext.xbase)
}

sourceSets {
	main {
		java.srcDir("src/main/emf-gen")
	}
}

val generateEPackage by tasks.registering(JavaExec::class) {
	mainClass.set("org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher")
	classpath(configurations.mwe2)
	inputs.file("src/main/java/tools/refinery/language/model/GenerateProblemModel.mwe2")
	inputs.file("src/main/resources/model/problem.ecore")
	inputs.file("src/main/resources/model/problem.genmodel")
	outputs.dir("src/main/emf-gen")
	args("src/main/java/tools/refinery/language/model/GenerateProblemModel.mwe2", "-p", "rootPath=/$projectDir")
}

for (taskName in listOf("compileJava", "processResources", "generateEclipseSourceFolders")) {
	tasks.named(taskName) {
		dependsOn(generateEPackage)
	}
}

tasks.clean {
	delete("src/main/emf-gen")
}

sonarqube.properties {
	SonarPropertiesUtils.addToList(properties, "sonar.exclusions", "src/main/emf-gen/**")
}

eclipse.project.natures.plusAssign(listOf(
		"org.eclipse.sirius.nature.modelingproject",
		"org.eclipse.pde.PluginNature",
		"org.eclipse.xtext.ui.shared.xtextNature",
))
