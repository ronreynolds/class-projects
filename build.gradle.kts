plugins {
	id("java")
}

group 	= "com.ronreynolds"
version = "0.0.1-SNAPSHOT"

val mainClass = "com.ronreynolds.games.Main"

// library versions
val assertJVersion      = "3.24.2"
val guavaVersion        = "33.2.1-jre"
val jUnitJupiterVersion	= "5.10.2"  // from build/generated/api/build.gradle
val lombokVersion       = "1.18.32"
val picoCliVersion 		= "4.7.6"
val slf4jVersion        = "1.7.25"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenLocal()	// so we can use some of our local libs
	mavenCentral()
}

dependencies {
	implementation  ("info.picocli:picocli:$picoCliVersion")
	implementation	("org.slf4j:slf4j-api:$slf4jVersion")
	runtimeOnly		("org.slf4j:slf4j-simple:$slf4jVersion")

	// Lombok dependencies
	compileOnly				("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor		("org.projectlombok:lombok:$lombokVersion")
	testCompileOnly			("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor	("org.projectlombok:lombok:$lombokVersion")

	// test dependencies
	testImplementation	("org.assertj:assertj-core:$assertJVersion")
	testImplementation	("org.junit.jupiter:junit-jupiter:$jUnitJupiterVersion")
	testRuntimeOnly		("org.junit.platform:junit-platform-launcher")
}

tasks.compileJava {
	options.encoding = "UTF-8"
}

tasks.create("uberjar", Jar::class) {
	group = "build"
	description = "Creates a jar containing classes and all runtime dependencies"
	manifest.attributes["Main-Class"] = mainClass
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	val dependencies = configurations
		.runtimeClasspath
		.get()
		.map(::zipTree)
	from(dependencies)
	with(tasks.jar.get())
}

tasks.compileTestJava {
	options.encoding = "UTF-8"
}

tasks.withType<Test> {
	useJUnitPlatform()
}