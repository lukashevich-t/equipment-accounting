plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.allopen") version "1.5.31"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-jdbc-mariadb")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-elytron-security-jdbc")
    implementation("io.quarkiverse.mybatis:quarkus-mybatis:1.0.4")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-agroal")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-cache")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("com.google.zxing:core:${project.property("zxingVersion")}")
    implementation("net.sf.jasperreports:jasperreports:${project.property("jasperReportsVersion")}") {
        exclude(group = "aopalliance", module = "aopalliance")
        exclude(group = "org.bouncycastle", module = "bcmail-jdk14")
        exclude(group = "org.bouncycastle", module = "bcprov-jdk14")
        exclude(group = "org.bouncycastle", module = "bctsp-jdk14")
        exclude(group = "org.codehaus.castor", module = "castor-core")
        exclude(group = "org.codehaus.castor", module = "castor-xml")
        exclude(group = "commons-beanutils", module = "commons-beanutils")
        exclude(group = "com.google.zxing", module = "core")
        exclude(group = "org.eclipse.jdt.core.compiler", module = "ecj")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
        exclude(group = "jakarta-regexp", module = "jakarta-regexp")
        exclude(group = "org.jfree", module = "jcommon")
        exclude(group = "org.jfree", module = "jfreechart")
        exclude(group = "org.apache.lucene", module = "lucene-analyzers-common")
        exclude(group = "org.apache.lucene", module = "lucene-core")
        exclude(group = "org.apache.lucene", module = "lucene-queries")
        exclude(group = "org.apache.lucene", module = "lucene-queryparser")
        exclude(group = "org.olap4j", module = "olap4j")
        exclude(group = "xml-apis", module = "xml-apis")
        exclude(group = "com.lowagie", module = "itext")
    }

    // Это вместо модифицированного com.lowagie: itext: 2.1.7js8, который хостился на jfrog.io, который в 2022 году закроется:
    // см. https://github.com/TIBCOSoftware/jasperreports/issues/148#issuecomment-700888163
    implementation("com.lowagie:itext:${project.property("iTextVersion")}") {
        exclude(group = "bouncycastle", module = "bcmail-jdk14")
        exclude(group = "bouncycastle", module = "bcprov-jdk14")
        exclude(group = "bouncycastle", module = "bctsp-jdk14")
    }

//    implementation("commons-dbutils:commons-dbutils:1.7")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:mariadb")
    testImplementation("io.quarkus:quarkus-flyway")
    testImplementation("org.flywaydb:flyway-mysql:9.1.2")
}

group = "by.gto.equipment"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}

tasks.withType<Test>() {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
