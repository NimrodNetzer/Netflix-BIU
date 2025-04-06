import requests
import random
import string
from datetime import datetime, timedelta
from pymongo import MongoClient
import os
from urllib.parse import urljoin
import glob
import json

# MongoDB Connection
BASE_URL = os.getenv("BASE_URL", "http://localhost:4000/api/")
LOGIN_URL = urljoin(BASE_URL, "tokens/")
USER_URL = urljoin(BASE_URL, "users/")
CATEGORY_URL = urljoin(BASE_URL, "categories/")
MOVIE_URL = urljoin(BASE_URL, "movies/")
RECOMMEND_URL = urljoin(MOVIE_URL, "{movie_id}/recommend")

# MongoDB Configuration
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = os.getenv("DBNAME", "NetFlix")

def login(email, password):
    """Logs in a user and returns the token."""
    response = requests.post(LOGIN_URL, json={"email": email, "password": password})
    if response.status_code == 200:
        return response.json().get("token")
    else:
        print(f"Failed to log in with email {email}: {response.text}")
        return None

def clear_database():
    """Clears all collections in the MongoDB database."""
    client = MongoClient(MONGO_URI)
    db = client[DB_NAME]
    
    collections_to_clear = ["categories", "counters", "movies", "users"]
    for collection in collections_to_clear:
        db[collection].delete_many({})
        print(f"Cleared collection: {collection}")
    
    client.close()

def random_string(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

def random_date(start_date, end_date):
    delta = end_date - start_date
    random_days = random.randint(0, delta.days)
    return start_date + timedelta(days=random_days)

# ✅ Create Users (Including One Admin)
def create_users():
    client = MongoClient(MONGO_URI)  # Initialize MongoDB client
    db = client[DB_NAME]  # Select the database

    users = []

    # **Create Admin User**
    admin_user = {
        "email": "admin@example.com",
        "password": "admin123",
        "nickname": "AdminUser",
        "picture": "default",
    }

    response = requests.post(USER_URL, json=admin_user)
    if response.status_code == 201:
        # **Force Update the Admin User in MongoDB**
        db.users.update_one({"email": "admin@example.com"}, {"$set": {"admin": True}})
        users.append(admin_user)
        print("✅ Admin user created and set to admin: true in MongoDB.")
    else:
        print(f"❌ Failed to create admin user: {response.text}")

    # Create **Regular Users**
    for i in range(99):
        user = {
            "email": f"user{i}@example.com",
            "password": "password123",
            "nickname": f"User{i}",
            "picture": f"https://example.com/pictures/user{i}.jpg"
        }
        response = requests.post(USER_URL, json=user)
        if response.status_code == 201:
            users.append(user)
        else:
            print(f"❌ Failed to create user {i}: {response.text}")

    client.close()  # Close MongoDB connection
    return users


# Create Categories
def create_categories(user_tokens):
    category_ids = []
    for i in range(10):
        category = {
            "name": f"Category {i}",
            "promoted": random.choice([True, False])
        }
        token = random.choice(user_tokens)
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.post(CATEGORY_URL, json=category, headers=headers)
        if response.status_code == 201:
            category_id = response.headers.get("Location").split("/")[-1]
            if category_id:
                category_ids.append(category_id)
        else:
            print(f"Failed to create category {i}: {response.text}")
    return category_ids

# Create Movies
def create_movies(category_ids, user_tokens):
    movies = []
    image_files = glob.glob("./images/*.*")
    video_files = glob.glob("./videos/*.*")

    for i in range(100):
        selected_categories = random.sample(category_ids, random.randint(1, 3))
        movie = {
            "name": f"Movie {i}",
            "picture": f"https://example.com/movies/movie{i}.jpg",
            "description": f"This is the description of Movie {i}",
            "age": random.randint(1, 18),
            "time": f"{random.randint(1, 3)}h {random.randint(0, 59)}m",
            "releaseDate": random_date(datetime(2000, 1, 1), datetime(2025, 1, 1)).isoformat(),
            "quality": random.choice(["HD", "SD", "4K"]),
            "categories": selected_categories,
            "cast": [
                {"name": random_string(8), "role": random.choice(["Lead", "Supporting"])}
                for _ in range(random.randint(2, 5))
            ],
            "properties": {
                "genre": random.choice(["Action", "Drama", "Comedy", "Horror"]),
                "language": random.choice(["English", "Spanish", "French", "German"])
            },
            "author": f"Author {i}"
        }
        token = random.choice(user_tokens)
        headers = {"Authorization": f"Bearer {token}"}
        image_file = random.choice(image_files)
        video_file = random.choice(video_files)

        files = {
            "image": open(image_file, "rb"),
            "video": open(video_file, "rb")
        }
        try:
            response = requests.post(MOVIE_URL, files=files, data={"data": json.dumps(movie)}, headers=headers)
            if response.status_code == 201:
                movies.append(movie)
            else:
                print(f"Failed to create movie {i}: {response.text}")
        finally:
            for file in files.values():
                file.close()
    return movies

# Add Movie to Watched List
def add_movie_to_watched(user_tokens):
    user_id = random.randint(1, 90)
    movie_id = random.randint(1, 90)
    token = random.choice(user_tokens)
    headers = {"Authorization": f"Bearer {token}"}
    url = RECOMMEND_URL.format(movie_id=movie_id)
    response = requests.post(url, headers=headers)
    if response.status_code == 201:
        print(f"Movie {movie_id} successfully added to watched list for user {user_id}.")
    else:
        print(f"Failed to add movie {movie_id} to watched list for user {user_id}: {response.text}")

# Main Execution
def main():
    print("Clearing database...")
    clear_database()  # Clears all existing data

    print("Creating users...")
    users = create_users()
    print(f"Created {len(users)} users (including 1 admin).")

    print("Logging in users...")
    user_tokens = [login(user["email"], user["password"]) for user in users if login(user["email"], user["password"])]
    print(f"Logged in {len(user_tokens)} users.")

    print("Creating categories...")
    category_ids = create_categories(user_tokens)
    print(f"Created {len(category_ids)} categories.")

    print("Creating movies...")
    movies = create_movies(category_ids, user_tokens)
    print(f"Created {len(movies)} movies.")

    for i in range(1, 1000):
        add_movie_to_watched(user_tokens)

if __name__ == "__main__":
    main()
