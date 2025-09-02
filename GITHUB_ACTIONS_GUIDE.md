# GitHub Actions CI/CD Guide

## 🚀 Automated Testing & Building

I've set up GitHub Actions workflows that will automatically run tests and build your app whenever you push code.

## 📁 **Workflow Files Created**

### 1. **`.github/workflows/android-ci.yml`** - Main CI Pipeline
**Triggers on:**
- Push to `main`, `master`, or `develop` branches  
- Pull requests to these branches

**What it does:**
- ✅ **Runs unit tests** (`./gradlew test`)
- ✅ **Runs lint checks** (`./gradlew lint`) 
- ✅ **Builds debug APK** (`./gradlew assembleDebug`)
- ✅ **Uploads test results** and **APK artifacts**
- ✅ **Caches Gradle** for faster builds

### 2. **`.github/workflows/android-release.yml`** - Release Pipeline
**Triggers on:**
- Git tags starting with `v*` (e.g., `v1.0.0`)
- Manual workflow dispatch

**What it does:**
- ✅ **Runs all tests** first
- ✅ **Builds release APK**
- ✅ **Creates GitHub release** with APK attached

## 🎯 **How It Works**

### Every Push:
```
Push Code → GitHub Actions → Run Tests → Build APK → Report Results
```

### For Releases:
```
Create Tag → GitHub Actions → Test → Build Release → Create GitHub Release
```

## 🔧 **Setup Instructions**

### 1. **Push to GitHub**
```bash
git add .
git commit -m "Add GitHub Actions CI/CD"
git remote add origin https://github.com/yourusername/sticker-creator.git
git branch -M main
git push -u origin main
```

### 2. **Enable Actions** (if needed)
- Go to your GitHub repo
- Click **"Actions"** tab
- Enable GitHub Actions if prompted

### 3. **First Run**
- Actions will run automatically on your first push
- Check the **"Actions"** tab to see progress

## 📊 **What You'll See**

### ✅ **Successful Run:**
- **Green checkmark** next to commits
- **Test results** in artifacts
- **Debug APK** ready for download

### ❌ **Failed Run:**
- **Red X** next to commits  
- **Error logs** in the Actions tab
- **Email notification** (if enabled)

## 🎨 **Customization Options**

### Add More Branches:
```yaml
on:
  push:
    branches: [ "main", "master", "develop", "feature/*" ]
```

### Enable UI Tests:
Uncomment the `instrumented-test` job in `android-ci.yml`
- **Note**: UI tests are slower and use more resources
- **Cost**: May use more GitHub Actions minutes

### Add Code Coverage:
```yaml
- name: Generate coverage report
  run: ./gradlew jacocoTestReport

- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
```

## 🚨 **Troubleshooting**

### Common Issues:

1. **"Permission denied" for gradlew**
   - Fixed by: `chmod +x gradlew` (already included)

2. **Build fails but works locally**
   - Check Java version (using JDK 11)
   - Verify dependencies in `build.gradle`

3. **Tests fail on CI but pass locally**
   - Check for hardcoded paths or device-specific code
   - Ensure tests don't depend on external resources

### Monitoring:
- **Actions tab**: See all workflow runs
- **Email notifications**: Get alerts for failures
- **Status badges**: Add to README for public repos

## 🎉 **Benefits**

- ✅ **Catch bugs early** before they reach main branch
- ✅ **Consistent builds** across all environments  
- ✅ **Automated testing** - no manual intervention needed
- ✅ **Ready-to-install APKs** for every build
- ✅ **Professional workflow** for open source projects

## 📈 **Next Steps**

1. **Push your code** to trigger the first run
2. **Check Actions tab** to see results
3. **Download APK** from artifacts
4. **Create a release tag** to test release workflow:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

Your Android project now has professional-grade CI/CD! 🚀
