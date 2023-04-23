import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    ios()
    iosSimulatorArm64()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    configure(targets){
        if (this is KotlinNativeTarget && konanTarget.family.isAppleFamily){
            compilations.getByName("main").cinterops.create("kvo"){
                packageName("io.kicks")
            }
        }
    }

//    configure(targets) {
//        if (this is KotlinNativeTarget && konanTarget.family.let { it == IOS || it == OSX }) {
//            compilations.getByName("main").cinterops.create("kvo") {
//                packageName("io.github.kicks.kvo")
//            }
//        }
//    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }
    
    sourceSets {
        val commonMain by getting{
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

            }
        }
        val androidMain by getting{
            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation("androidx.media3:media3-exoplayer:1.0.1")
            }
        }
        val androidUnitTest by getting
        val iosMain by getting {
            dependsOn(commonMain)
        }
        val iosSimulatorArm64Main by getting{
            dependsOn(iosMain)
        }
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by getting {
            dependsOn(commonTest)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "io.github.kicks"
    compileSdk = 33
    defaultConfig {
        minSdk = 25
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}