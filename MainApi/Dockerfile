# First Stage: Build the React app
FROM node:18 AS react-builder

WORKDIR /app
COPY ./netflix-web ./netflix-web
RUN cd netflix-web && npm install && npm run build

# Second Stage: Build the Main API Server
FROM node:18

WORKDIR /app

# Copy package.json and install dependencies first
COPY MainApi/package*.json ./
RUN npm install

# Copy the Main API source code
COPY MainApi .

# Copy built React app from first stage to `public/` in MainApi
COPY --from=react-builder /app/netflix-web/build ./public

# Expose the port
EXPOSE ${PORT}

# Start the server
CMD ["npm", "start"]
