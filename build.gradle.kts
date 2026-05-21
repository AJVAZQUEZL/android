//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    id("com.chaquo.python") version "15.0.1" apply false
//}
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // ¡La versión real que sí existe en los servidores!
    id("com.chaquo.python") version "17.0.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}