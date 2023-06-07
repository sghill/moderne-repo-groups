plugins {
  kotlin("jvm") version "1.8.21"
  kotlin("plugin.serialization") version "1.8.21"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
  implementation("io.ktor:ktor-client-core:2.3.1")
  implementation("io.ktor:ktor-client-cio:2.3.1")
  implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")

  runtimeOnly("org.slf4j:slf4j-simple:1.7.36")
  
  testImplementation(kotlin("test"))
  testImplementation(platform("org.mockito:mockito-bom:5.3.1"))
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
  
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}
