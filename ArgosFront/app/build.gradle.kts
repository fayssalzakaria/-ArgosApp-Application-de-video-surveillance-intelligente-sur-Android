plugins {
    alias(libs.plugins.android.application)
}

android {
    packagingOptions {
        exclude ("mockito-extensions/org.mockito.plugins.MockMaker")
    }
    namespace = "com.example.argosapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.argosapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


}

dependencies {
    // Libraries principales
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")


    // Retrofit pour les requêtes API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Converter Gson pour la gestion des données JSON
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Volley pour les requêtes HTTP
    implementation("com.android.volley:volley:1.2.1")

    // Authentification JWT
    implementation("com.auth0:java-jwt:4.4.0")
    implementation(libs.espresso.intents)

    // Dépendances de test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.2.0")
    // Mockito pour les tests unitaires
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.12.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // Pour les tests unitaires
    testImplementation ("org.mockito:mockito-core:4.8.0")
    // Pour les tests instrumentés (tests Android)
    androidTestImplementation ("org.mockito:mockito-android:4.8.0")
    // Autre dépendance pour les tests
    androidTestImplementation ("org.mockito:mockito-inline:4.8.0")
    testImplementation ("org.mockito:mockito-android:5.11.0")
    implementation ("org.postgresql:postgresql:42.2.5")
    // Tests Robolectr  ic
    testImplementation("org.robolectric:robolectric:4.10.3")
    implementation ("org.mindrot:jbcrypt:0.4")
    // WebSocket
    implementation(libs.javawebsocket)

    // RxJava et RxAndroid
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.okhttp)

    // Media3 (ExoPlayer nouvelle génération)
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")


    implementation(libs.tyrus.client)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(files("libs/libwebrtc.aar"))
    implementation (libs.okhttp.v493)
    // Gson Converter pour gérer JSON
    implementation(libs.converter.gson)

    implementation(libs.retrofit)






}
