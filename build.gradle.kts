plugins {
	// Same Loom flavor and version as bca-crests: consumers include this project
	// as a composite build, and mixed Loom plugins in one build do not coexist well.
	id("dev.architectury.loom") version "1.11-SNAPSHOT"
	id("architectury-plugin") version "3.4-SNAPSHOT"
	id("maven-publish")
}

group = property("maven_group") as String
version = property("mod_version") as String
base.archivesName.set(property("archives_name") as String)

architectury {
	platformSetupLoomIde()
	fabric()
}

loom {
	silentMojangMappingsLicense()
	splitEnvironmentSourceSets()

	mods {
		create("rhettl-owo-dark-ui") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

repositories {
	mavenCentral()
	maven("https://maven.wispforest.io/releases/")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

	// Consumers provide owo-lib; it is declared as a dependency, never bundled.
	modImplementation("io.wispforest:owo-lib:${property("owo_version")}")

	testImplementation("org.junit.jupiter:junit-jupiter:${property("junit_version")}")
	testImplementation("org.assertj:assertj-core:${property("assertj_version")}")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

afterEvaluate {
	dependencies {
		"clientImplementation"(sourceSets["main"].output)
		// Theme values are client-side; test them like the client source set they live in.
		"testImplementation"(sourceSets["client"].output)
	}
}

tasks {
	processResources {
		inputs.property("version", project.version)
		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to inputs.properties["version"]))
		}
	}

	withType<JavaCompile>().configureEach {
		options.release = 21
	}

	test {
		useJUnitPlatform()
	}

	jar {
		inputs.property("archivesName", project.base.archivesName)
		from("LICENSE") {
			rename { "${it}_${inputs.properties["archivesName"]}" }
		}
	}

	withType<JavaExec>().configureEach {
		jvmArgs("-Dmixin.env.compatLevel=JAVA_21")
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.base.archivesName.get()
			from(components["java"])
		}
	}

	repositories {
		mavenLocal()

		// GitHub Packages: SNAPSHOTs from main and releases from v* tags (see .github/workflows/publish.yml).
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/${findProperty("gpr.owner") ?: System.getenv("GITHUB_REPOSITORY_OWNER") ?: "rhettl"}/owo-ui-components")
			credentials {
				username = (findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR") ?: "") as String
				password = (findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN") ?: "") as String
			}
		}
	}
}
