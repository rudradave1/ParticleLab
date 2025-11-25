// Root build.gradle.kts - keep this minimal: only plugin alias declarations
plugins {
    // these aliases come from gradle/libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
