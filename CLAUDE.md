# CLAUDE.md — Netflix-BIU Codebase Guide

This file provides AI assistants with the context needed to work effectively in this repository.

---

## Project Overview

Netflix-BIU is a full-stack Netflix clone with four major components:

| Component | Tech | Location |
|-----------|------|----------|
| REST API backend | Node.js + Express + MongoDB | `MainApi/` |
| Web frontend | React 18 | `netflix-web/` |
| Android app | Java + MVVM + Room | `netflix-android/` |
| Recommendation engine | C++ (TCP socket server) | `RecommendationSystem/` |

---

## Repository Structure

```
Netflix-BIU/
├── MainApi/                    # Node.js Express backend
│   ├── app.js                  # Entry point: Express setup, MongoDB connection, route mounting
│   ├── routes/                 # Route definitions (user, token, movie, category, search)
│   ├── controllers/            # HTTP request handlers (thin layer, delegate to services)
│   ├── services/               # Business logic (validation, DB queries, socket calls)
│   ├── models/                 # Mongoose schemas (User, Movie, Category, Counter, Token)
│   ├── middleware/             # uploadMiddleware.js (Multer + Cloudinary)
│   ├── utils/                  # socketClient.js (C++ recommendation server comms)
│   ├── config/                 # Environment files (.env.prod, .env.dev)
│   ├── test/                   # Python integration test suite
│   ├── Dockerfile              # Multi-stage: builds React then serves from Node
│   └── vercel.json             # Vercel deployment + hourly cron config
├── netflix-web/                # React SPA
│   ├── src/
│   │   ├── App.js              # Router, dark/light mode toggle, route definitions
│   │   ├── components/
│   │   │   ├── Admin/          # Admin panel, movie/category CRUD forms
│   │   │   ├── Home/           # Category listings, movie cards, detail modals
│   │   │   ├── Login/          # Login and signup forms
│   │   │   ├── FirstPage/      # Landing page and hero section
│   │   │   └── Utils/          # TopMenu, SearchPage, VideoPlayer, helpers
│   │   └── styles/             # Component-scoped CSS files
│   └── .env                    # REACT_APP_API_URL (set before building)
├── netflix-android/            # Android MVVM app (Java)
├── RecommendationSystem/       # C++ TCP socket server
├── wiki/                       # Setup and usage documentation
│   ├── installation.md         # Complete setup guide
│   ├── web_example.md
│   └── android_example.md
├── docker-compose.yml          # Orchestrates all services
└── README.md
```

---

## Architecture Patterns

### Backend: MVC with Service Layer

```
HTTP Request → Route → Controller → Service → Model (MongoDB)
                                 ↓
                          socketClient → C++ Recommendation Server
```

- **Controllers** (`controllers/`) only handle HTTP: parse request, call service, return response.
- **Services** (`services/`) contain all business logic: validation, queries, recommendation calls.
- **Models** (`models/`) define Mongoose schemas; do not put logic here.
- **Routes** (`routes/`) mount controllers and apply the auth middleware where needed.

### Authentication

- JWT-based. Tokens generated at `POST /api/tokens/`.
- The `middleware/auth.js` verifies the Bearer token and attaches the user to `req.user`.
- All routes except `POST /api/users/` and `POST /api/tokens/` are protected.
- JWT secret comes from the `SECRET` environment variable.

### File Uploads

- Handled by `middleware/uploadMiddleware.js` using Multer + `multer-storage-cloudinary`.
- Images and videos are stored in Cloudinary, not locally.
- Max file size: 500 MB. Fields: `picture` (image) and `video` (video file).

### Recommendation System

- A C++ TCP socket server runs on `RECOMMENDATION_IP:RECOMMENDATION_PORT` (defaults: `recommendation_server:8080`).
- `utils/socketClient.js` opens a socket connection per request and communicates with newline-delimited text commands.
- `services/recommendation.js` wraps all recommendation operations: seed, fetch, add watch, delete watch.
- On Vercel, an hourly cron (`/api/cron/seed-recommendations`) re-seeds the recommendation server because it is stateless between cold starts.

### Frontend: Feature-Organized Components

- **`Admin/`** — full CRUD UI for admins (movies, categories).
- **`Home/`** — main viewing experience (browse by category, movie modals, video player).
- **`Login/`** — login/signup with JWT storage in `localStorage`.
- **`FirstPage/`** — unauthenticated landing page.
- **`Utils/`** — shared UI: navigation menu, search, video player.
- Dark/light mode state lives in `App.js` and is persisted to `localStorage`.

---

## Data Models

### User
```js
{ _id: Number (auto-increment), email, password, nickname, admin: Boolean,
  picture, createdAt, moviesList: [{ movieId, watchedAt }] }
```

### Movie
```js
{ _id: Number (auto-increment), name, picture, video, description, author,
  age, releaseDate, quality, time, categories: [ObjectId → Category],
  cast: [{ name, role }], properties: Map, timestamps }
```

### Category
```js
{ _id: ObjectId, name, promoted: Boolean }
```

Auto-incrementing IDs are managed by the `Counter` model — do not set `_id` manually on User or Movie.

---

## API Endpoints

### Auth
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/tokens/` | No | Login → returns JWT |
| POST | `/api/users/` | No | Register new user |
| GET | `/api/users/:id` | Yes | Get user by ID |

### Movies
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/movies/` | Yes | Movies grouped by promoted categories |
| POST | `/api/movies/` | Yes | Create movie (multipart/form-data) |
| GET | `/api/movies/:id` | Yes | Get movie details |
| PUT | `/api/movies/:id` | Yes | Update movie (multipart/form-data) |
| DELETE | `/api/movies/:id` | Yes | Delete movie |
| GET | `/api/movies/:id/recommend` | Yes | Get recommendations for a movie |
| POST | `/api/movies/:id/recommend` | Yes | Mark movie as watched |
| POST | `/api/movies/seed-recommendations` | Yes | Seed recommendation server |

### Categories
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/categories/` | Yes | List all categories (filter: `?promoted=true`) |
| POST | `/api/categories/` | Yes | Create category |
| GET | `/api/categories/:id` | Yes | Get category |
| PATCH | `/api/categories/:id` | Yes | Update category |
| DELETE | `/api/categories/:id` | Yes | Delete category |

### Search
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/movies/search?query=…` | Yes | Search by name, description, author, cast |

---

## Environment Variables

### MainApi (`config/.env.prod` or `config/.env.dev`)

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `3000` | Server listen port |
| `RECOMMENDATION_PORT` | `8080` | C++ server port |
| `RECOMMENDATION_IP` | `recommendation_server` | C++ server host |
| `CONNECTION_STRING` | — | MongoDB URI |
| `SECRET` | `sod` | JWT signing secret (change in production) |
| `CLOUDINARY_CLOUD_NAME` | — | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | — | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | — | Cloudinary API secret |
| `CRON_SECRET` | — | Secret header for Vercel cron validation |

### Frontend (`netflix-web/.env`)

| Variable | Description |
|----------|-------------|
| `REACT_APP_API_URL` | Backend API base URL (empty = same-origin proxy) |

---

## Development Workflow

### Docker (Recommended)

```bash
# Start all services (API, MongoDB, C++ recommendation server)
docker compose up --build -d

# Run integration test suite
docker compose run --rm test_case

# View logs
docker compose logs -f app
```

### Local Development

```bash
# Backend
cd MainApi
npm install
npm start                    # Runs on port 3000

# Frontend (separate terminal)
cd netflix-web
npm install
npm start                    # Dev server on port 3000 (proxies API calls)

# Run React tests
npm test
```

### Creating an Admin User

The first admin must be created directly in MongoDB (set `admin: true`). Subsequent admins can be promoted via the admin UI.

---

## Testing

### Integration Tests (Python)

Located at `MainApi/test/test.py`. Runs against a live API + MongoDB instance.

```bash
# Via Docker
docker compose run --rm test_case

# Directly (requires running API and MongoDB)
cd MainApi/test
pip install -r requirements.txt
BASE_URL=http://localhost:3000/api/ MONGO_URI=mongodb://localhost:27017 DBNAME=netflix python test.py
```

The test script:
1. Creates 100 users (user 1 is admin)
2. Creates 10 categories
3. Creates 100 movies with random media
4. Adds 1000 random watch events across users

### Frontend Tests

```bash
cd netflix-web
npm test    # Jest + React Testing Library
```

---

## Deployment

### Vercel

`MainApi/vercel.json` configures:
- All requests routed to `app.js`
- CORS headers on all responses
- Hourly cron job (`0 * * * *`) to `/api/cron/seed-recommendations` with `CRON_SECRET` header

### Docker Compose (Self-Hosted)

```
Services:
  recommendation_server  → C++ binary on port 8080
  app                    → Node.js + bundled React on port 3000
  mongo                  → MongoDB on port 27017
  test_case              → Python test runner (profile: test)
```

The `Dockerfile` in `MainApi/`:
1. Builds the React app (`npm run build`)
2. Copies the build output to `public/` in the Node.js image
3. Serves static files from Express

---

## Key Conventions

### Adding a New API Route

1. Create schema in `models/` if needed (use auto-increment pattern from existing models).
2. Add business logic to `services/`.
3. Add HTTP handler to `controllers/` (keep it thin — call service, return result).
4. Add route file in `routes/` and apply `auth` middleware where required.
5. Mount the route in `app.js`.

### Adding a New Frontend Component

1. Create a folder under `src/components/<Feature>/`.
2. Add a matching CSS file for styles (no global style pollution).
3. Use functional components with React hooks.
4. Access the API via `REACT_APP_API_URL` from `process.env` (or relative paths if proxied).
5. Store auth token from `localStorage` and include it as `Authorization: Bearer <token>` in API calls.

### File Uploads in Forms

Use `multipart/form-data` when creating or updating movies. The backend expects `picture` (image) and `video` (video) fields. Do not send JSON for these endpoints.

### Recommendation System Integration

When adding features that interact with the recommendation system, use `services/recommendation.js` functions. Do not open raw sockets from controllers. Handle connection errors gracefully — the C++ server may be unavailable during cold starts.

---

## Known Issues & Notes

- **Passwords are stored in plaintext** in MongoDB. Adding bcrypt hashing is a security improvement to consider.
- **CORS is wide open** (`origin: '*'`) in `app.js`. Restrict this in production deployments.
- **JWT secret default** (`sod`) must be overridden via the `SECRET` environment variable in any non-development environment.
- The recommendation server is **stateful in-memory** — it loses all data on restart. The hourly cron and `populateAndSeed` function handle recovery.
- The `Counter` model manages auto-increment IDs. If you delete documents and need to reset sequences, update the counter collection directly in MongoDB.
