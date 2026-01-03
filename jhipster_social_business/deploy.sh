#!/bin/bash

# XaaS Deployment Script
# Usage: ./deploy.sh
# Prerequisites: Docker, .env file

echo "🚀 Starting Deployment..."

# 1. Check if .env exists
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    echo "   Please copy .env.example to .env and fill in your secrets."
    exit 1
fi

# 2. Load Environment Variables (Securely)
echo "🔑 Loading environment variables..."
export $(grep -v '^#' .env | xargs)

# 3. Check for Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker is not installed."
    exit 1
fi

# 4. Pull Latest Image
IMAGE_NAME="whatsappproductservicepro:latest"
echo "⬇️ Pulling latest image: $IMAGE_NAME..."
# Note: In a real scenario, you would pull from a registry (e.g., docker pull ghcr.io/myorg/$IMAGE_NAME)
# For local dev/manual build, we assume the image exists or is built via JHipster
# docker pull your-registry/$IMAGE_NAME

# 5. Stop and Remove Existing Container
CONTAINER_NAME="whatsapp-pro-app"
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "🛑 Stopping existing container..."
    docker stop $CONTAINER_NAME
    echo "🗑️ Removing existing container..."
    docker rm $CONTAINER_NAME
fi

# 6. Run New Container
echo "▶️ Starting new container..."
docker run -d \
    --name $CONTAINER_NAME \
    --env-file .env \
    -p 8081:8081 \
    -e SPRING_PROFILES_ACTIVE=prod \
    --restart unless-stopped \
    $IMAGE_NAME

echo "✅ Deployment Complete! App running on port 8081."
docker logs -f $CONTAINER_NAME --tail 50
