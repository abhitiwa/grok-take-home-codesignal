# Setup Instructions for Payton

## Method 1: Local Development (Fastest and Most Reliable)

### Step 1: Backend Setup
```bash
# Set up environment with a real Grok API key
echo "XAI_API_KEY=your_actual_grok_api_key_here" > .env

# Start backend
source .env && mvn spring-boot:run
```

You should see:
Started GrokSdrApplication in X.X seconds
Tomcat started on port(s): 8080 (http) with context path '/api'

### Step 2: Frontend Setup (New Terminal)
```bash
# Navigate to frontend directory from project root
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Browser should open at http://localhost:3000

### Step 3: Test Everything Works
```bash
# Test backend API
curl http://localhost:8080/api/test/grok-connection
# Expected: "Grok API: Connected"

# Test Grok integration
curl -X POST http://localhost:8080/api/test/grok-message \
-H "Content-Type: application/json" \
-d '"What is 2+2?"'
# Expected: "Grok Response: 4"
```

## Method 2: Docker (Backend) + Local Frontend

### Step 1: Docker Backend
```bash
# Set up environment
echo "XAI_API_KEY=your_actual_grok_api_key_here" > .env

# Build and run backend in Docker
docker-compose up --build -d

# Check it's running
docker ps
# Should show: "Up X seconds" (not unhealthy)
```

### Step 2: Local Frontend
```bash
# In new terminal
cd frontend
npm install
npm start
```

### Step 3: Test
```bash
# Test Docker backend
curl http://localhost:8081/api/test/grok-connection
# Expected: "Grok API: Connected"

# Frontend should be at http://localhost:3000
```

## Core Features to Test

### 1. Basic API Test
```bash
curl http://localhost:8080/api/test/grok-connection
```

### 2. Lead Management
```bash
# Create a lead
curl -X POST http://localhost:8080/api/leads \
-H "Content-Type: application/json" \
-d '{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@company.com",
  "title": "VP of Engineering",
  "companyName": "TechCorp",
  "companySize": "1000+",
  "industry": "Technology"
}'

# Qualify the lead (AI scoring)
curl -X POST http://localhost:8080/api/leads/1/qualify

# Generate personalized message
curl -X POST http://localhost:8080/api/leads/1/messages/email \
-H "Content-Type: application/json" \
-d '{"messageType": "initial outreach"}'
```

### 3. AI Evaluation Framework
```bash
# Test qualification performance
curl -X POST http://localhost:8080/api/evaluation/qualification \
-H "Content-Type: application/json" \
-d '{}'

# Run comprehensive evaluation
curl -X POST http://localhost:8080/api/evaluation/comprehensive \
-H "Content-Type: application/json" \
-d '{}'
```

## Access Points

- Frontend UI: http://localhost:3000 (if running locally)
- Backend API: http://localhost:8080/api (local) or http://localhost:8081/api (Docker)
- Health Check: /api/test/grok-connection
- Database Console: http://localhost:8080/api/h2-console

## Expected Behavior

### AI Lead Qualification
Should return JSON with score 0-100, detailed reasoning, and recommendations

### Message Generation  
Should return personalized content referencing lead details and company information

### Evaluation Framework
Should return test results with success rates, response times, and performance metrics

## Troubleshooting

### Frontend Issues
Use Method 1 (local development) - most reliable for frontend

### Docker Health Check
May show "unhealthy" due to slow Grok API calls - this doesn't affect functionality

### API Key Errors
Ensure you're using a real Grok API key in the .env file

The core AI capabilities work through both API endpoints and React frontend interface.
