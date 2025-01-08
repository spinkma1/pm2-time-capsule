FROM node:18-alpine

# Set working directory
WORKDIR /app

# Install curl
RUN apk add --no-cache curl

# Copy everything from the frontend directory to /app inside the container
COPY . .

# Install dependencies
RUN npm install

# Expose the port your app runs on
EXPOSE 3000

# Start the application
CMD ["npm", "run", "dev"]