# Installation

## 1. Download the Repository

Clone or download the repository to your local machine.
```
git clone https://github.com/idorozin/Netflix-BIU.git
```
## 2. Open the Repo in Your Terminal

Navigate into the root directory of the project.

```
cd Netflix-BIU
```

## 3. Create the `.env.prod` File

Before running the services, you need to create a `.env.prod` file in the `config` directory within `./MainApi`.

### Commands to Create the File:

**Linux/Mac:**

```bash
mkdir -p ./MainApi/config
touch ./MainApi/config/.env.prod
```

**Windows (Command Prompt - CMD):**

```cmd
mkdir MainApi\config && type nul > MainApi\config\.env.prod
```

> Note: You can leave this file empty if you don't wish to change the default ports. Configuration details are described at the end of this document.

## 4. Run the Services

To build and run the recommendation system, web API, and serve the frontend:

```bash
docker compose up --build -d
```

This will:

- Build the recommendation system, web API, and MongoDB Docker images
- Run them in the background

### Default Ports

- Recommendation system: `8080`
- Web API: `3000`
- MongoDB: `27017`

## 5. *Recommended* - Run Test Script

A test script is provided to:

- Create 100 movies
- Create 100 users with usernames: user0@example.com, password: password123
- Recommend 1000 random movies to random users
- Create Admin User with username : admin@example.com , password : admin123
To run the test script:

```bash
docker compose run --rm test_case

```
To access the React frontend, open your browser and go to:

```
http://localhost:3000
```



## 6. Android Installation

### a. Get Your IPv4 Address (skip this if using emulator):

- **Windows:**
  ```cmd
  ipconfig
  ```
- **Linux/Mac:**
  ```bash
  ifconfig
  ```

### b. Open Android Project

Open the Android project in Android Studio.

### c. Update Constants.java

Navigate to:

```
java/com/example/netflix/Utils/Constants.java
```

Replace the IP address with your local IPv4 address.

### d. Build & Install the APK

Build the project and install the APK on your Android device.

- Use Android Studio Emulator or a physical device
- Ensure the device is connected to the same network as your machine

---

## Extras: Change Default Ports

To override default ports, update your `MainApi/config/.env.prod` file with custom values:

### Example `.env.prod` File:

```
PORT=4000
RECOMMENDATION_PORT=8080
RECOMMENDATION_IP=127.0.0.1
CONNECTION_STRING=mongodb://127.0.0.1/NetFlix
SECRET=sod
```

> Default Ports:
>
> - Recommendation Server: 8080
> - App: 3000
> - MongoDB: 27017




### Admin creation on MongoDb:

<img src="https://github.com/user-attachments/assets/bcd2d0fd-e47f-4ad5-a25d-2993c6e66cd9" height="400"/>
<img src="https://github.com/user-attachments/assets/542cc508-3324-4eef-bda6-3782dacfeca9" height="400"/>



