# Use Ubuntu as base image
FROM ubuntu:22.04

# Avoid timezone prompt during installation
ENV DEBIAN_FRONTEND=noninteractive

# Install required packages
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    x11-apps \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libxcursor1 \
    libxfixes3 \
    libgtk-3-0 \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy your application files
COPY *.java ./
COPY icon.png ./
COPY MANIFEST.MF ./

# Compile the Java files
RUN javac *.java

# Create the JAR file
RUN jar cvfm DuplicateRemover.jar MANIFEST.MF *.class icon.png

# Set display environment variable
ENV DISPLAY=:0

# Command to run your application
CMD ["java", "-jar", "DuplicateRemover.jar"]
