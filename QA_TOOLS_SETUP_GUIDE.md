# QA Tools Setup Guide for Sticker Creator App

## 🎯 **Recommended QA Stack**

### **1. Static Code Analysis**

#### **Android Lint (Already Available)**
```gradle
android {
    lintOptions {
        abortOnError true
        warningsAsErrors true
        checkReleaseBuilds true
        checkDependencies true
    }
}
```

#### **Detekt for Kotlin**
```gradle
// In app/build.gradle
plugins {
    id "io.gitlab.arturbosch.detekt" version "1.23.4"
}

detekt {
    toolVersion = "1.23.4"
    config = files("$projectDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4"
}
```

#### **SonarQube Integration**
```gradle
// In build.gradle (project level)
plugins {
    id "org.sonarqube" version "4.4.1.3373"
}

sonarqube {
    properties {
        property "sonar.projectKey", "sticker-creator-app"
        property "sonar.organization", "your-org"
        property "sonar.host.url", "https://sonarcloud.io"
        property "sonar.sources", "src/main/java,src/main/kotlin"
        property "sonar.tests", "src/test/java,src/androidTest/java"
        property "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml"
    }
}
```

### **2. Security Analysis**

#### **OWASP Dependency Check**
```gradle
// In build.gradle (project level)
plugins {
    id 'org.owasp.dependencycheck' version '8.4.3'
}

dependencyCheck {
    format = 'ALL'
    suppressionFile = 'config/dependency-check-suppressions.xml'
    failBuildOnCVSS = 7
}
```

#### **MobSF Integration (CI/CD)**
```yaml
# In .github/workflows/security-scan.yml
name: Security Scan
on: [push, pull_request]

jobs:
  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Run MobSF Scan
        uses: fundacaocerti/mobsf-action@v1.7.1
        with:
          input-file-path: app/build/outputs/apk/debug/app-debug.apk
          mobsf-url: ${{ secrets.MOBSF_URL }}
          mobsf-api-key: ${{ secrets.MOBSF_API_KEY }}
```

### **3. Code Coverage**

#### **JaCoCo Setup**
```gradle
// In app/build.gradle
android {
    buildTypes {
        debug {
            testCoverageEnabled true
        }
    }
}

// Add JaCoCo plugin
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.8"
}

tasks.register('jacocoTestReport', JacocoReport) {
    dependsOn "testDebugUnitTest"
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required = true
        html.required = true
    }

    def fileFilter = [
        '**/R.class', '**/R$*.class', '**/BuildConfig.*', '**/Manifest*.*',
        '**/*Test*.*', 'android/**/*.*', '**/*$WhenMappings.*'
    ]
    
    def debugTree = fileTree(dir: "build/intermediates/javac/debug", excludes: fileFilter)
    def kotlinDebugTree = fileTree(dir: "build/tmp/kotlin-classes/debug", excludes: fileFilter)
    
    classDirectories.from = files([debugTree, kotlinDebugTree])
    sourceDirectories.from = files(["src/main/java", "src/main/kotlin"])
    executionData.from = files("build/jacoco/testDebugUnitTest.exec")
}
```

### **4. Performance Monitoring**

#### **LeakCanary (Already in Debug)**
```gradle
dependencies {
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
}
```

#### **Stetho for Debugging**
```gradle
dependencies {
    debugImplementation 'com.facebook.stetho:stetho:1.6.0'
    debugImplementation 'com.facebook.stetho:stetho-okhttp3:1.6.0'
}
```

### **5. CI/CD Quality Gates**

#### **GitHub Actions Workflow**
```yaml
# .github/workflows/qa-checks.yml
name: QA Checks

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  code-quality:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    
    - name: Run Android Lint
      run: ./gradlew lintDebug
    
    - name: Run Detekt
      run: ./gradlew detekt
    
    - name: Run Unit Tests
      run: ./gradlew testDebugUnitTest
    
    - name: Generate Code Coverage Report
      run: ./gradlew jacocoTestReport
    
    - name: Run Dependency Check
      run: ./gradlew dependencyCheckAnalyze
    
    - name: Upload Coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        files: ./app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
    
    - name: Upload Lint Results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: lint-results
        path: app/build/reports/lint-results-debug.html
```

## 🛠️ **Implementation Priority**

### **Phase 1: Essential Tools (Week 1)**
1. ✅ Android Lint (already configured)
2. 🔄 Detekt for Kotlin code quality
3. 🔄 JaCoCo for code coverage
4. 🔄 GitHub Actions for CI/CD

### **Phase 2: Security Focus (Week 2)**
1. 🔄 OWASP Dependency Check
2. 🔄 MobSF for security scanning
3. 🔄 Snyk for vulnerability monitoring

### **Phase 3: Advanced Analysis (Week 3)**
1. 🔄 SonarQube integration
2. 🔄 Performance monitoring tools
3. 🔄 Advanced security testing

## 📈 **Quality Metrics to Track**

- **Code Coverage**: Target >80%
- **Lint Issues**: Zero critical issues
- **Security Vulnerabilities**: Zero high/critical CVEs
- **Technical Debt**: Monitor and reduce over time
- **Test Success Rate**: >95%
- **Build Success Rate**: >98%

## 🚨 **Quality Gates**

### **Pull Request Requirements**
- All tests must pass
- Code coverage must not decrease
- No new critical lint issues
- No new security vulnerabilities
- Detekt checks must pass

### **Release Requirements**
- Full security scan completed
- Performance benchmarks met
- All quality gates passed
- Manual testing completed

## 📚 **Resources**

- [Android Testing Guide](https://developer.android.com/training/testing)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security-testing-guide/)
- [SonarQube for Android](https://docs.sonarqube.org/latest/analysis/languages/java/)
- [Detekt Documentation](https://detekt.dev/)
