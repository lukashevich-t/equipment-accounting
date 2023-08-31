import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
//    id ("org.jetbrains.kotlin.jvm") version "1.3.31"
    war
}

group = "by.gto.equipment"
version = "3.0.0"
extra["commons_io_version"] = "2.5"
extra["resteasy_version"] = "3.6.3.Final"
extra["slf4j_version"] = "1.7.22.jbossorg-1"

java {
    sourceCompatibility = JavaVersion.VERSION_12
}

repositories {
    mavenCentral()
    maven("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/")
    maven("file://d:/timo_personal/MegaSync/repo")
    maven("http://repository.jboss.org/nexus/content/repositories/releases")
//    maven("http://jasperreports.sourceforge.net/maven2")
    maven("http://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts")
    maven("https://repository.primefaces.org")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.zxing", "core", "3.3.3")
    implementation("org.primefaces.themes", "blitzer", "1.0.10")
//    implementation("org.primefaces.themes", "aristo", "1.0.1")
//    implementation("org.primefaces.themes", "afternoon", "1.0.10")
//    implementation("org.primefaces.themes", "afterdark", "1.0.10")
    implementation("net.sf.jasperreports", "jasperreports", "6.8.0") {
        exclude("aopalliance", "aopalliance")
        exclude("bouncycastle", "bcmail-jdk14")
        exclude("org.bouncycastle", "bcmail-jdk14")
        exclude("bouncycastle", "bcprov-jdk14")
        exclude("org.bouncycastle", "bcprov-jdk14")
        exclude("org.bouncycastle", "bctsp-jdk14")
        exclude("org.codehaus.castor", "castor-core")
        exclude("org.codehaus.castor", "castor-xml")
        exclude("commons-beanutils", "commons-beanutils")
        exclude("com.google.zxing", "core")
        exclude("org.eclipse.jdt.core.compiler", "ecj")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("com.fasterxml.jackson.core", "jackson-databind")
        exclude("jakarta-regexp", "jakarta-regexp")
        exclude("org.jfree", "jcommon")
        exclude("org.jfree", "jfreechart")
        exclude("org.apache.lucene", "lucene-analyzers-common")
        exclude("org.apache.lucene", "lucene-core")
        exclude("org.apache.lucene", "lucene-queries")
        exclude("org.apache.lucene", "lucene-queryparser")
        exclude("org.olap4j", "olap4j")
        exclude("xml-apis", "xml-apis")
    }
    providedCompile("javax.servlet", "javax.servlet-api", "3.1.0")
    providedCompile("javax", "javaee-api", "7.0")
    providedCompile("commons-io", "commons-io", extra["commons_io_version"] as String)
    providedCompile("org.jboss.resteasy", "resteasy-jaxrs", extra["resteasy_version"] as String)
    providedCompile("org.jboss.resteasy", "resteasy-jackson2-provider", extra["resteasy_version"] as String)
    providedCompile("org.slf4j", "slf4j-api", extra["slf4j_version"] as String)

    implementation("by.gto.library", "bto-library-common", "1.1.15") {
        exclude("log4j", "apache-log4j-extras")
        exclude("mysql", "mysql-connector-java")
    }
    implementation("by.gto.fonts", "by.gto.fonts", "0.1")
    implementation("commons-dbutils", "commons-dbutils", "1.7")
    implementation(group = "org.primefaces", name = "primefaces", version = "7.0")
    implementation(group = "org.primefaces.extensions", name = "primefaces-extensions", version = "7.0")

    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}