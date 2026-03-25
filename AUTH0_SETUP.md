# Complete Auth0 Setup Guide

This guide details exactly how to configure the Auth0 values necessary to allow the Flutter app to authenticate and the Spring Boot backend to validate tokens and sync the user profiles.

## Step 1: Create an Auth0 Tenant
1. Go to [Auth0 Dashboard](https://manage.auth0.com/).
2. Create or select a Tenant.

## Step 2: Set up the API (Backend)
1. On the left sidebar, go to **Applications > APIs**.
2. Click **Create API**.
3. Fill in:
   - **Name**: `aiGym Backend API`
   - **Identifier (Audience)**: `https://api.aigym.com` (This can be any URL format string, it acts as an ID).
   - **Signing Algorithm**: `RS256`
4. Click **Create**.
5. Once created, go to the **Settings** tab. Keep this page open to grab the values.

### Configure `application.properties` (Spring Boot)
Open `/aiGym/src/main/resources/application.properties` and replace:
- `YOUR_AUTH0_DOMAIN`: Find this in Auth0 under **Applications > Default App > Settings** -> **Domain**. It looks like `dev-123.auth0.com`. Your `issuer-uri` would be `https://dev-123.auth0.com/`. Make sure there is a trailing slash `/`.
- `YOUR_AUTH0_AUDIENCE`: This is the exact Identifier you typed in Step 2.3 (`https://api.aigym.com`).

## Step 3: Set up the Application (Frontend)
1. On the left sidebar, go to **Applications > Applications**.
2. Click **Create Application**.
3. Choose **Native** (for Flutter).
4. Name it `aiGym Mobile App` and click Create.
5. In the **Settings** tab, configure the following:
   - **Allowed Callback URLs**: `aigym://YOUR_AUTH0_DOMAIN/android/com.example.ai_gym_mobile_app/callback, aigym://YOUR_AUTH0_DOMAIN/ios/com.example.aiGymMobileApp/callback`
   - **Allowed Logout URLs**: `aigym://YOUR_AUTH0_DOMAIN/android/com.example.ai_gym_mobile_app/callback, aigym://YOUR_AUTH0_DOMAIN/ios/com.example.aiGymMobileApp/callback`
   *(Replace `YOUR_AUTH0_DOMAIN` with your actual domain, like `dev-123.auth0.com`)*
6. Scroll down and **Save Changes**.

### Configure Auth0 to return User Email in Access Token (Optional but Recommended)
By default, Auth0 might not put the `email` inside the Access Token. To add it, use an Action:
1. Go to **Actions > Flows** and click **Login**.
2. Create a new custom Action (Build from scratch). Name: `Add Email to Access Token`.
3. Add this code:
```javascript
exports.onExecutePostLogin = async (event, api) => {
  if (event.user.email) {
    api.accessToken.setCustomClaim('email', event.user.email);
  }
};
```
4. Deploy the script.
5. Drag your new Action block into the Login flow between Start and Complete and apply.

## Step 4: Configure Flutter App variables
1. Open `/ai_gym_mobile_app/lib/main.dart`
2. Search for the `Auth0(...)` initialization block.
3. Replace the placeholder Domain and Client ID with the **Domain** and **Client ID** from your `aiGym Mobile App` Native Application settings dashboard in Auth0.

## Step 5: Test the Integration
1. Review the Flutter Android `build.gradle` file (`/android/app/build.gradle`) to ensure `auth0Domain` and `auth0Scheme` (`aigym`) match what's in the Auth0 dashboard.
2. Start the Spring Boot Application.
3. Start the Flutter App. Click Login.
4. Check the Database. An H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:aiGymDB`, Username: `sa`, Password: `password`). Check if the user record with the `auth0Id` and `email` exists in the `users` table after a successful login in Flutter!
