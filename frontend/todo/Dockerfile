# Use an official Node runtime as a base image
FROM node:15.1-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code to the container
COPY . .

# Build the Angular application
RUN npm run build --prod

# Expose the port that your Angular app will run on
EXPOSE 4200

# Define the command to run your Angular app
CMD ["npm", "start"]