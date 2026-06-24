#!/bin/bash

# Configuration
JVM_OPTS="-Xmx192m -XX:ActiveProcessorCount=1"
SPRING_OPTS="-Dspring.profiles.active=default -Dspring.datasource.username=postgres -Dspring.datasource.password=123"

# Format is service:port
SERVICES_LIST="security-service:8084 member-service:8081 expedition-service:8082 payment-service:8083 api-gateway:8080"

cleanup() {
  echo ""
  echo "=================================================="
  echo "Stopping all services..."
  echo "=================================================="

  # Kill backend services
  for item in $SERVICES_LIST; do
    local service="${item%%:*}"
    local port="${item##*:}"
    local pid=$(lsof -t -i:$port)
    if [ -n "$pid" ]; then
      echo "Stopping $service (PID $pid)..."
      kill "$pid" 2>/dev/null
    fi
  done

  # Kill frontend
  local frontend_pid=$(lsof -t -i:5173)
  if [ -n "$frontend_pid" ]; then
    echo "Stopping frontend (PID $frontend_pid)..."
    kill "$frontend_pid" 2>/dev/null
  fi

  # Stop postgres container
  echo "Stopping PostgreSQL database container..."
  docker compose stop postgres

  echo "Cleanup complete. Goodbye!"
  exit 0
}

# Trap Ctrl+C and termination signals
trap cleanup SIGINT SIGTERM

echo "=================================================="
echo "Starting RedBus Microservice Platform (Host Mode)"
echo "=================================================="

# 1. Start Docker Desktop if not running
if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running. Launching Docker Desktop..."
  open -a Docker
  echo "Waiting for Docker daemon to start (this might take a few moments)..."
  until docker info >/dev/null 2>&1; do
    sleep 2
  done
  echo "Docker is running."
fi

# 2. Kill any processes currently occupying the required ports
echo "Checking and freeing service ports..."
for item in $SERVICES_LIST; do
  service="${item%%:*}"
  port="${item##*:}"
  pid=$(lsof -t -i:$port)
  if [ -n "$pid" ]; then
    echo "Port $port is already in use by PID $pid. Killing it..."
    kill -9 "$pid" 2>/dev/null
    sleep 1
  fi
done

# Kill frontend if running
frontend_pid=$(lsof -t -i:5173)
if [ -n "$frontend_pid" ]; then
  echo "Frontend port 5173 is already in use. Killing PID $frontend_pid..."
  kill -9 "$frontend_pid" 2>/dev/null
  sleep 1
fi

# 3. Spin up PostgreSQL container only
echo "Starting PostgreSQL container..."
docker compose up -d postgres

# Wait for DB to be healthy
echo "Waiting for PostgreSQL database to be healthy..."
until [ "$(docker inspect -f '{{.State.Health.Status}}' redbus-postgres 2>/dev/null)" == "healthy" ]; do
  sleep 1
done
echo "PostgreSQL is healthy and database is ready."

# 4. Start Java Services
wait_for_port() {
  local port=$1
  local name=$2
  echo "Waiting for $name to be fully responsive on port $port..."
  while ! nc -z localhost "$port"; do
    sleep 1
  done
  echo "✓ $name is up!"
}

# Start Security Service
echo "Starting Security Service..."
java $JVM_OPTS $SPRING_OPTS -Dspring.datasource.url=jdbc:postgresql://localhost:5432/securityDB -jar backend/security-service/target/security-service-1.0.jar > logs_security.log 2>&1 &
wait_for_port 8084 "Security Service"

# Start Member Service
echo "Starting Member Service..."
java $JVM_OPTS $SPRING_OPTS -Dspring.datasource.url=jdbc:postgresql://localhost:5432/memberDB -jar backend/member-service/target/member-service-1.0.jar > logs_member.log 2>&1 &
wait_for_port 8081 "Member Service"

# Start Expedition Service
echo "Starting Expedition Service..."
java $JVM_OPTS $SPRING_OPTS -Dspring.datasource.url=jdbc:postgresql://localhost:5432/expeditionDB -jar backend/expedition-service/target/expedition-service-1.0.jar > logs_expedition.log 2>&1 &
wait_for_port 8082 "Expedition Service"

# Start Payment Service
echo "Starting Payment Service..."
java $JVM_OPTS $SPRING_OPTS -Dspring.datasource.url=jdbc:postgresql://localhost:5432/paymentDB -jar backend/payment-service/target/payment-service-1.0.jar > logs_payment.log 2>&1 &
wait_for_port 8083 "Payment Service"

# Start API Gateway
echo "Starting API Gateway..."
java $JVM_OPTS $SPRING_OPTS -jar backend/api-gateway/target/api-gateway-1.0.jar > logs_gateway.log 2>&1 &
wait_for_port 8080 "API Gateway"

# 5. Start React Frontend
echo "Starting React Frontend..."
npm --prefix frontend run dev &

echo "=================================================="
echo "All services are running!"
echo "- Frontend: http://localhost:5173"
echo "- API Gateway: http://localhost:8080"
echo "Press [Ctrl+C] to stop all services clean."
echo "=================================================="

# Keep script running to handle Ctrl+C cleanup
while true; do
  sleep 1
done
