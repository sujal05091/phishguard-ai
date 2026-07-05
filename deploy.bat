@echo off
echo Building PhishGuard AI Android App...

echo.
echo Step 1: Clean previous builds
call gradlew clean

echo.
echo Step 2: Run unit tests
call gradlew test

echo.
echo Step 3: Build debug APK
call gradlew assembleDebug

echo.
echo Step 4: Build release AAB (requires signing configuration)
REM call gradlew bundleRelease

echo.
echo Build complete! 
echo Debug APK location: app\build\outputs\apk\debug\app-debug.apk
echo.
echo Next steps:
echo 1. Configure AWS credentials in AWSConfig.kt
echo 2. Test on physical device with SMS permissions
echo 3. Set up signing configuration for release build
echo 4. Deploy to Google Play internal track

pause