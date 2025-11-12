import com.google.protobuf.gradle.*

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.protobuf") version "0.9.5"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"// 引入ksp插件
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.hghuangggeng.app_client"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.2"
    }
    plugins {
        id("java") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.47.0"
        }
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.47.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("java") {
                    option("lite")
                }
                id("grpc") {
                    option("lite")
                }
                id("grpckt") {
                    option("lite")
                }
            }
            it.builtins {
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("com.google.protobuf:protobuf-kotlin:3.21.2")
    implementation("io.grpc:grpc-stub:1.52.0")
    implementation("io.grpc:grpc-protobuf:1.52.0")
    implementation("io.grpc:grpc-okhttp:1.52.0")

    implementation("com.google.protobuf:protobuf-java-util:3.21.7")
    implementation("com.google.protobuf:protobuf-kotlin:3.21.2")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")

    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    // 如果需要运行时反射支持
    implementation("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation(project(":easyipc_annotations"))//引入刚才新建的ksp model

    // hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
}