plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //id("org.openapi.generator")
}

android {
    namespace = "br.com.andreg.mobile.vortex"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "br.com.andreg.mobile.vortex"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Define a URL base para todos os builds
        buildConfigField("String", "BASE_URL", "\"https://api.vortex-dev.andreg.com.br\"")
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
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true // Habilita o buildConfig
    }

    /*// Point the generated classes
    sourceSets {
        getByName("main") {
            java.srcDirs("$projectDir/build/src/main/kotlin")
        }
    }*/
}
/*
openApiGenerate {
    generatorName = "kotlin"
    inputSpec = "$rootDir/app/src/main/api/openapi.json" // Path to your OpenAPI spec file
    outputDir = "$projectDir/build"           // Directory to store generated code
    packageName = "br.com.andreg.mobile.vortex"       // Replace with your desired package name
    configOptions.put("serializableModel","false")
    configOptions.put("serializationLibrary","gson")
    generateApiTests.set(false)
    generateApiDocumentation.set(false)

    // LINHA ADICIONAL PARA IGNORAR ERROS:
    validateSpec.set(false)
}*/

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.play.services.cast.framework)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.fragment:fragment-compose:1.7.1")
    
    // Fuel e Gson
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.kittinunf.fuel:fuel-android:2.3.1")
    implementation("com.android.volley:volley:1.2.1")

    // Jetpack DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Adicione esta linha para habilitar temas e componentes XML modernos
    implementation("com.google.android.material:material:1.12.0")

    // Certifique-se também de ter o appcompat (geralmente já vem, mas garanta)
    implementation("androidx.appcompat:appcompat:1.7.0")
}
/*
// Invoca o openApiGenerator para ser executado ANTES da compilação do Kotlin
afterEvaluate {
    tasks.findByName("compileDebugKotlin")?.dependsOn("openApiGenerate")
    tasks.findByName("compileReleaseKotlin")?.dependsOn("openApiGenerate")
}*/