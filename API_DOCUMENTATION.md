# API Documentation

This document provides comprehensive documentation for the Grok-powered SDR system REST API.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API does not require authentication for development. In production, implement proper authentication mechanisms.

## Content Type
All requests and responses use `application/json` content type.

## Error Handling
The API returns standard HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `404` - Not Found
- `500` - Internal Server Error

Error responses include a message:
```json
{
  "error": "Error description",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Lead Management API

### Get All Leads
```http
GET /leads
```

**Query Parameters:**
- `firstName` (optional) - Filter by first name
- `lastName` (optional) - Filter by last name
- `companyName` (optional) - Filter by company name
- `industry` (optional) - Filter by industry
- `pipelineStage` (optional) - Filter by pipeline stage

**Response:**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "title": "VP of Engineering",
    "companyName": "Example Corp",
    "companySize": "1000+",
    "industry": "Technology",
    "location": "San Francisco, CA",
    "linkedinUrl": "https://linkedin.com/in/johndoe",
    "website": "https://example.com",
    "qualificationScore": 85,
    "qualificationReasoning": "Strong technical background and decision-making authority",
    "pipelineStage": "QUALIFIED",
    "lastContactDate": "2024-01-15T10:30:00Z",
    "nextFollowUpDate": "2024-01-22T10:30:00Z",
    "notes": "Interested in our enterprise solution",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

### Get Lead by ID
```http
GET /leads/{id}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "title": "VP of Engineering",
  "companyName": "Example Corp",
  "companySize": "1000+",
  "industry": "Technology",
  "location": "San Francisco, CA",
  "linkedinUrl": "https://linkedin.com/in/johndoe",
  "website": "https://example.com",
  "qualificationScore": 85,
  "qualificationReasoning": "Strong technical background and decision-making authority",
  "pipelineStage": "QUALIFIED",
  "lastContactDate": "2024-01-15T10:30:00Z",
  "nextFollowUpDate": "2024-01-22T10:30:00Z",
  "notes": "Interested in our enterprise solution",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Create Lead
```http
POST /leads
```

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@company.com",
  "phone": "+1-555-0456",
  "title": "CTO",
  "companyName": "Tech Startup",
  "companySize": "50-100",
  "industry": "SaaS",
  "location": "Austin, TX",
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "website": "https://techstartup.com",
  "notes": "Referred by existing customer"
}
```

**Response:**
```json
{
  "id": 2,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@company.com",
  "phone": "+1-555-0456",
  "title": "CTO",
  "companyName": "Tech Startup",
  "companySize": "50-100",
  "industry": "SaaS",
  "location": "Austin, TX",
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "website": "https://techstartup.com",
  "qualificationScore": null,
  "qualificationReasoning": null,
  "pipelineStage": "NEW",
  "lastContactDate": null,
  "nextFollowUpDate": null,
  "notes": "Referred by existing customer",
  "createdAt": "2024-01-16T00:00:00Z",
  "updatedAt": "2024-01-16T00:00:00Z"
}
```

### Update Lead
```http
PUT /leads/{id}
```

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@company.com",
  "phone": "+1-555-0456",
  "title": "CTO",
  "companyName": "Tech Startup",
  "companySize": "50-100",
  "industry": "SaaS",
  "location": "Austin, TX",
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "website": "https://techstartup.com",
  "notes": "Updated notes after initial contact",
  "pipelineStage": "CONTACTED"
}
```

### Delete Lead
```http
DELETE /leads/{id}
```

**Response:** `204 No Content`

### Qualify Lead
```http
POST /leads/{id}/qualify
```

**Response:**
```json
{
  "score": 85,
  "reasoning": "Strong technical background and decision-making authority. Company size and industry align well with our target market.",
  "recommendations": "Schedule a technical demo to showcase our enterprise features. Focus on ROI and scalability benefits."
}
```

### Re-qualify Lead with Custom Criteria
```http
POST /leads/{id}/requalify
```

**Request Body:**
```json
{
  "budget": "High",
  "timeline": "Q1 2024",
  "decision_maker": "Yes",
  "pain_points": "Scalability, Security"
}
```

**Response:**
```json
{
  "score": 92,
  "reasoning": "High budget, urgent timeline, and clear pain points that align with our solution. Decision maker identified.",
  "recommendations": "Prioritize this lead. Schedule executive meeting to discuss strategic implementation."
}
```

### Generate Email Message
```http
POST /leads/{id}/messages/email
```

**Request Body:**
```json
{
  "messageType": "initial outreach"
}
```

**Response:**
```json
{
  "message": "Hi Jane,\n\nI hope this message finds you well. I noticed your role as CTO at Tech Startup and was impressed by your company's growth in the SaaS space.\n\nI wanted to reach out because we've helped similar companies in your industry achieve significant improvements in their technical infrastructure and scalability.\n\nWould you be available for a brief 15-minute conversation this week to discuss how we might be able to help Tech Startup achieve its goals?\n\nBest regards,\nSales Team"
}
```

### Generate LinkedIn Message
```http
POST /leads/{id}/messages/linkedin
```

**Request Body:**
```json
{
  "messageType": "connection request"
}
```

**Response:**
```json
{
  "message": "Hi Jane, I noticed your role at Tech Startup. I'd love to connect and share some insights about scaling SaaS infrastructure. Would you be interested in a brief conversation?"
}
```

### Update Pipeline Stage
```http
PUT /leads/{id}/pipeline-stage
```

**Request Body:**
```json
{
  "pipelineStage": "ENGAGED"
}
```

### Get Leads by Pipeline Stage
```http
GET /leads/pipeline/{stage}
```

**Path Parameters:**
- `stage` - Pipeline stage (NEW, CONTACTED, QUALIFIED, ENGAGED, MEETING_SCHEDULED, CONVERTED, CLOSED_LOST)

### Get Leads Needing Follow-up
```http
GET /leads/follow-up
```

### Get Pipeline Statistics
```http
GET /leads/stats/pipeline
```

**Response:**
```json
{
  "NEW": 15,
  "CONTACTED": 8,
  "QUALIFIED": 12,
  "ENGAGED": 6,
  "MEETING_SCHEDULED": 4,
  "CONVERTED": 3,
  "CLOSED_LOST": 2
}
```

## Activity Management API

### Get All Activities
```http
GET /activities
```

**Response:**
```json
[
  {
    "id": 1,
    "lead": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe"
    },
    "activityType": "EMAIL",
    "description": "Sent initial outreach email",
    "outcome": "No response yet",
    "nextSteps": "Follow up in 3 days",
    "scheduledDate": null,
    "completedDate": "2024-01-15T10:30:00Z",
    "createdAt": "2024-01-15T10:30:00Z",
    "createdBy": "Sales Rep"
  }
]
```

### Get Activity by ID
```http
GET /activities/{id}
```

### Get Activities by Lead
```http
GET /activities/lead/{leadId}
```

### Create Activity
```http
POST /activities
```

**Request Body:**
```json
{
  "lead": {
    "id": 1
  },
  "activityType": "CALL",
  "description": "Initial discovery call",
  "outcome": "Interested in learning more",
  "nextSteps": "Send technical documentation",
  "scheduledDate": "2024-01-20T14:00:00Z",
  "createdBy": "Sales Rep"
}
```

### Update Activity
```http
PUT /activities/{id}
```

### Delete Activity
```http
DELETE /activities/{id}
```

### Get Recent Activities
```http
GET /activities/recent
```

### Get Activities by Type
```http
GET /activities/type/{activityType}
```

**Path Parameters:**
- `activityType` - Activity type (EMAIL, CALL, MEETING, NOTE, FOLLOW_UP, etc.)

### Get Overdue Activities
```http
GET /activities/overdue
```

### Complete Activity
```http
PUT /activities/{id}/complete
```

### Get Activity Statistics
```http
GET /activities/lead/{leadId}/stats
```

**Response:**
```json
{
  "totalActivities": 5,
  "activitiesByType": {
    "EMAIL": 2,
    "CALL": 2,
    "MEETING": 1
  },
  "lastActivityDate": "2024-01-15T10:30:00Z",
  "lastActivityType": "CALL",
  "completedActivities": 4,
  "pendingActivities": 1,
  "activitiesThisMonth": 3
}
```

## Evaluation API

### Test System Health
```http
GET /evaluation/health
```

**Response:**
```json
{
  "apiConnection": true,
  "status": "Healthy",
  "responseTime": 245,
  "responseReceived": true
}
```

### Run Qualification Evaluation
```http
POST /evaluation/qualification
```

**Request Body:**
```json
{}
```

**Response:**
```json
{
  "testResults": [
    {
      "leadId": 1,
      "leadName": "John Doe",
      "company": "Example Corp",
      "score": 85,
      "reasoning": "Strong technical background and decision-making authority",
      "responseTime": 1234,
      "success": true
    }
  ],
  "totalTests": 3,
  "successfulTests": 3,
  "averageScore": 82.3,
  "averageResponseTime": 1156
}
```

### Run Messaging Evaluation
```http
POST /evaluation/messaging
```

**Request Body:**
```json
{}
```

**Response:**
```json
{
  "testResults": [
    {
      "leadId": 1,
      "messageType": "initial outreach",
      "message": "Generated message content...",
      "messageLength": 156,
      "responseTime": 892,
      "success": true
    }
  ],
  "totalTests": 12,
  "successfulTests": 12,
  "averageMessageLength": 142.5,
  "averageResponseTime": 945
}
```

### Test Prompt Variations
```http
POST /evaluation/prompts
```

**Request Body:**
```json
{}
```

**Response:**
```json
{
  "testResults": [
    {
      "promptVariation": 1,
      "prompt": "You are an expert sales development representative...",
      "response": "AI response content...",
      "responseLength": 234,
      "responseTime": 1567,
      "success": true
    }
  ],
  "totalTests": 4,
  "successfulTests": 4,
  "averageResponseLength": 198.5,
  "averageResponseTime": 1423
}
```

### Run Comprehensive Evaluation
```http
POST /evaluation/comprehensive
```

**Response:**
```json
{
  "health": {
    "apiConnection": true,
    "status": "Healthy",
    "responseTime": 245
  },
  "qualification": {
    "averageScore": 82.3,
    "successfulTests": 3,
    "totalTests": 3
  },
  "messaging": {
    "successfulTests": 12,
    "totalTests": 12,
    "averageMessageLength": 142.5
  },
  "promptVariations": {
    "successfulTests": 4,
    "totalTests": 4,
    "averageResponseTime": 1423
  },
  "overall": {
    "totalEvaluations": 1,
    "lastEvaluation": "2024-01-16T00:00:00Z",
    "systemHealth": "Good"
  }
}
```

### Get Evaluation History
```http
GET /evaluation/history
```

**Response:**
```json
[
  {
    "type": "qualification",
    "timestamp": "2024-01-16T00:00:00Z",
    "results": {
      "totalTests": 3,
      "successfulTests": 3,
      "averageScore": 82.3
    }
  }
]
```

### Get Evaluation Metrics
```http
GET /evaluation/metrics
```

**Response:**
```json
{
  "totalEvaluations": 5,
  "lastEvaluation": "2024-01-16T00:00:00Z",
  "evaluationsByType": {
    "qualification": 2,
    "messaging": 2,
    "prompt_variations": 1
  }
}
```

## Data Models

### Lead Model
```json
{
  "id": "Long",
  "firstName": "String (required)",
  "lastName": "String (required)",
  "email": "String (required, unique)",
  "phone": "String (optional)",
  "title": "String (optional)",
  "companyName": "String (optional)",
  "companySize": "String (optional)",
  "industry": "String (optional)",
  "location": "String (optional)",
  "linkedinUrl": "String (optional)",
  "website": "String (optional)",
  "qualificationScore": "Integer (0-100, optional)",
  "qualificationReasoning": "String (optional)",
  "pipelineStage": "Enum (NEW, CONTACTED, QUALIFIED, ENGAGED, MEETING_SCHEDULED, CONVERTED, CLOSED_LOST)",
  "lastContactDate": "DateTime (optional)",
  "nextFollowUpDate": "DateTime (optional)",
  "notes": "String (optional)",
  "createdAt": "DateTime (auto-generated)",
  "updatedAt": "DateTime (auto-generated)"
}
```

### Activity Model
```json
{
  "id": "Long",
  "lead": "Lead (required)",
  "activityType": "Enum (EMAIL, CALL, MEETING, NOTE, FOLLOW_UP, etc.)",
  "description": "String (required)",
  "outcome": "String (optional)",
  "nextSteps": "String (optional)",
  "scheduledDate": "DateTime (optional)",
  "completedDate": "DateTime (optional)",
  "createdAt": "DateTime (auto-generated)",
  "createdBy": "String (optional)"
}
```

### Pipeline Stages
- `NEW` - Newly created lead
- `CONTACTED` - Initial contact made
- `QUALIFIED` - Lead qualified and interested
- `ENGAGED` - Actively engaged in sales process
- `MEETING_SCHEDULED` - Meeting or demo scheduled
- `CONVERTED` - Successfully converted to customer
- `CLOSED_LOST` - Lead is no longer active

### Activity Types
- `EMAIL` - Email communication
- `CALL` - Phone call
- `MEETING` - In-person or virtual meeting
- `DEMO` - Product demonstration
- `FOLLOW_UP` - Follow-up communication
- `PROPOSAL` - Proposal or quote sent
- `NEGOTIATION` - Contract negotiation
- `NOTE` - General note or observation
- `LINKEDIN` - LinkedIn message or connection

## Rate Limiting
Currently, no rate limiting is implemented. In production, implement appropriate rate limiting based on your Grok API limits.

## CORS
CORS is enabled for all origins in development. Configure appropriately for production.

## Examples

### Complete Lead Workflow
```bash
# 1. Create a new lead
curl -X POST http://localhost:8080/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "companyName": "Example Corp",
    "title": "VP of Engineering"
  }'

# 2. Qualify the lead
curl -X POST http://localhost:8080/api/leads/1/qualify

# 3. Generate a personalized message
curl -X POST http://localhost:8080/api/leads/1/messages/email \
  -H "Content-Type: application/json" \
  -d '{"messageType": "initial outreach"}'

# 4. Log the activity
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -d '{
    "lead": {"id": 1},
    "activityType": "EMAIL",
    "description": "Sent initial outreach email",
    "outcome": "No response yet"
  }'

# 5. Update pipeline stage
curl -X PUT http://localhost:8080/api/leads/1/pipeline-stage \
  -H "Content-Type: application/json" \
  -d '{"pipelineStage": "CONTACTED"}'
```

### Evaluation Workflow
```bash
# 1. Check system health
curl http://localhost:8080/api/evaluation/health

# 2. Run qualification evaluation
curl -X POST http://localhost:8080/api/evaluation/qualification

# 3. Run messaging evaluation
curl -X POST http://localhost:8080/api/evaluation/messaging

# 4. Run comprehensive evaluation
curl -X POST http://localhost:8080/api/evaluation/comprehensive

# 5. Check evaluation history
curl http://localhost:8080/api/evaluation/history
```

---

For additional information, refer to the main README.md or create an issue in the repository.