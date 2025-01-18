import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    id ("java")
    id ("io.freefair.lombok") version "8.11"
    id ("com.gradleup.shadow") version "9.0.0-beta4"
}

repositories {
    mavenCentral()
}

group = "cn.nukkit"
version = "1.0-SNAPSHOT"

dependencies {
    implementation ("org.fusesource.jansi:jansi:2.4.1")
    implementation ("com.google.guava:guava:33.4.0-jre")
    implementation ("com.github.oshi:oshi-core:6.6.5")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("org.yaml:snakeyaml:2.3")

    implementation("net.minecrell:terminalconsoleappender:1.3.0") {
        exclude(group = "org.jline", module = "jline-reader")
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
    }

    implementation ("org.jline:jline:3.28.0")

    implementation ("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation ("org.apache.logging.log4j:log4j-api:2.22.3")

    annotationProcessor("org.apache.logging.log4j:log4j-core:2.24.3")

    implementation ("net.sf.jopt-simple:jopt-simple:6.0-alpha-3")
    implementation ("org.iq80.leveldb:leveldb:0.12")
    implementation ("io.netty:netty-all:4.1.117.Final")
    implementation ("org.apache.commons:commons-lang3:3.17.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    transform(Log4j2PluginsCacheFileTransformer())
    manifest {
        attributes(
            "Main-Class" to "cn.nukkit.Nukkit",
            "Multi-Release" to "true",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}