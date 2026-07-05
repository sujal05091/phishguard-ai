# PhishGuard AI - Android Phishing Protection App

## Overview
PhishGuard AI is an Android application that provides real-time phishing protection by intercepting URLs and analyzing them using Amazon Bedrock's Titan AI models.

## Features
- **URL Interception**: Monitors SMS, email, and messaging apps for URLs
- **Virtual Browser**: Secure WebView with disabled downloads and file access
- **AI-Powered Detection**: Uses Amazon Titan models for phishing analysis
- **Real-time Protection**: Blocks malicious sites before they load
- **Privacy-First**: Minimal data logging and secure processing

## Architecture
- **Minimum SDK**: Android 8.0 (API 26)
- **Language**: Kotlin
- **AI Service**: Amazon Bedrock (Titan Text Models)
- **Architecture**: MVVM with LiveData
- **Security**: Locked-down WebView, no file downloads

## Setup Instructions

### 1. AWS Configuration
1. Create an AWS account and set up Bedrock access
2. Configure IAM roles for mobile app access
3. Update `AWSConfig.kt` with your credentials (use secure storage)

### 2. Build Configuration
```bash
# Clone and open in Android Studio
cd PhishGuardAI
./gradlew build
```

### 3. Permissions
The app requires these permissions:
- `INTERNET` - For web browsing and API calls
- `ACCESS_NETWORK_STATE` - Network connectivity checks
- `RECEIVE_SMS` - SMS URL interception
- `READ_SMS` - SMS content analysis

### 4. Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Security Features
- WebView sandbox with disabled file access
- No download functionality
- Secure credential management
- Privacy-compliant data handling
- Real-time threat detection

## Deployment
1. Build signed APK/AAB
2. Test on internal track
3. Deploy to Google Play Store

## Important Notes
- Replace placeholder AWS credentials with secure implementation
- Test thoroughly with various URL types
- Monitor API usage and costs
- Ensure compliance with privacy regulations

## License
This project is for educational/demonstration purposes.# phishguard-ai
