# Use AdoptOpenJDK base image
FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /app

# Copy application files
COPY Client.java .

# Install required packages
RUN apt-get update && apt-get install -y \
    default-jdk \
    && rm -rf /var/lib/apt/lists/*

# Compile application
RUN javac Client.java

# Run the application
CMD ["java", "Client"]


