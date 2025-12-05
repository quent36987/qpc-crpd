# Stage 1: Build Angular App
FROM node:20 as build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install
RUN npm install -g @angular/cli

# Copy the rest of the application code
COPY . .

# Build the Angular app based on the configuration passed from the workflow
ARG CONFIGURATION=production
RUN ng build --configuration=$CONFIGURATION

# Stage 2: Nginx Setup
FROM nginx:latest

# Copy the Angular build files to the nginx directory
COPY --from=build /app/dist/aerolight-front/browser/ /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

# Expose the port and start Nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
