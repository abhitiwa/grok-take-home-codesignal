# Grok-Powered SDR System

A comprehensive AI-powered Sales Development Representative (SDR) system that leverages Grok's API to enhance and automate the sales prospecting process. This system provides intelligent lead qualification, personalized messaging, and comprehensive pipeline management.

## 🚀 Features

### Core Functionality
- **AI-Powered Lead Qualification**: Uses Grok to analyze and score leads based on multiple criteria
- **Personalized Messaging**: Generates customized outreach messages for emails and LinkedIn
- **Pipeline Management**: Tracks leads through defined sales stages with automated progression
- **Activity Tracking**: Comprehensive logging of all lead interactions and communications
- **Model Evaluation Framework**: Systematic testing and evaluation of Grok's performance

### Technical Features
- **RESTful API**: Complete backend API with Spring Boot
- **Modern Frontend**: React-based UI with Tailwind CSS
- **Database Integration**: H2 database with JPA/Hibernate
- **Docker Support**: Containerized deployment with docker-compose
- **Health Monitoring**: System health checks and API status monitoring

## 🏗️ Architecture

### Backend (Spring Boot)
- **Java 8+** with Spring Boot 2.7.18
- **JPA/Hibernate** for database operations
- **WebFlux** for HTTP client operations
- **H2 Database** for development and testing
- **Maven** for dependency management

### Frontend (React)
- **React 18** with functional components and hooks
- **React Query** for data fetching and caching
- **React Router** for navigation
- **Tailwind CSS** for styling
- **Lucide React** for icons

### AI Integration
- **Grok API** integration with comprehensive error handling
- **Prompt Engineering** for optimal AI responses
- **Response Validation** and parsing
- **Evaluation Framework** for performance testing

## 📋 Prerequisites

- **Java 8+** (JDK 8 or higher)
- **Node.js 18+** and npm
- **Docker** and Docker Compose (for containerized deployment)
- **Grok API Key** from xAI

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd grok-sdr-system
```

### 2. Environment Configuration
```bash
# Copy the example environment file
cp .env.example .env

# Edit .env and add your Grok API key
XAI_API_KEY=your_grok_api_key_here
```

### 3. Backend Setup
```bash
# Build the backend
mvn clean package -DskipTests

# Run the backend
java -jar target/grok-sdr-system-1.0.0.jar
```

### 4. Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

### 5. Docker Deployment (Recommended)
```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build
```

## 🐳 Docker Deployment

### Quick Start
```bash
# Clone and setup
git clone <repository-url>
cd grok-sdr-system
cp .env.example .env

# Add your Grok API key to .env
echo "XAI_API_KEY=your_key_here" >> .env

# Start the system
docker-compose up --build
```

### Access Points
- **Application**: http://localhost:8080
- **API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/evaluation/health
- **H2 Console**: http://localhost:8080/h2-console (dev only)

## 📚 API Documentation

### Lead Management
```bash
# Get all leads
GET /api/leads

# Get lead by ID
GET /api/leads/{id}

# Create new lead
POST /api/leads
Content-Type: application/json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "companyName": "Example Corp",
  "title": "VP of Engineering"
}

# Qualify a lead
POST /api/leads/{id}/qualify

# Generate personalized message
POST /api/leads/{id}/messages/email
Content-Type: application/json
{
  "messageType": "initial outreach"
}
```

### Activity Management
```bash
# Get activities for a lead
GET /api/activities/lead/{leadId}

# Create new activity
POST /api/activities
Content-Type: application/json
{
  "lead": {"id": 1},
  "activityType": "EMAIL",
  "description": "Sent initial outreach email",
  "outcome": "No response yet"
}
```

### Evaluation Framework
```bash
# Test system health
GET /api/evaluation/health

# Run qualification evaluation
POST /api/evaluation/qualification

# Run messaging evaluation
POST /api/evaluation/messaging

# Run comprehensive evaluation
POST /api/evaluation/comprehensive
```

## 🎯 Usage Guide

### 1. Lead Management
1. **Add Leads**: Use the "Add Lead" button to create new prospects
2. **Qualify Leads**: Click "Qualify" to use Grok AI for scoring
3. **Pipeline Management**: Update lead stages as they progress
4. **Search & Filter**: Use search and filters to find specific leads

### 2. AI-Powered Features
1. **Lead Qualification**: 
   - Analyzes company size, industry, title, and other factors
   - Provides 0-100 score with detailed reasoning
   - Supports custom qualification criteria

2. **Personalized Messaging**:
   - Generates email and LinkedIn messages
   - Customizes based on lead data and message type
   - Supports follow-up and meeting request messages

### 3. Activity Tracking
1. **Log Interactions**: Record all communications with leads
2. **Schedule Follow-ups**: Set reminders for future activities
3. **Track Outcomes**: Monitor response rates and engagement

### 4. Evaluation & Testing
1. **Health Monitoring**: Check Grok API connection status
2. **Performance Testing**: Run evaluation suites to test AI performance
3. **Prompt Optimization**: Test different prompt variations

## 🔧 Configuration

### Application Properties
```yaml
# Grok API Configuration
grok:
  api:
    base-url: https://api.x.ai/v1
    model: grok-4
    temperature: 0.7
    max-tokens: 1000
    timeout: 30000

# Database Configuration
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### Environment Variables
```bash
# Required
XAI_API_KEY=your_grok_api_key

# Optional
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
GROK_API_TEMPERATURE=0.7
GROK_API_MAX_TOKENS=1000
```

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GrokApiServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend Tests
```bash
cd frontend
npm test
npm run test:coverage
```

### API Testing
```bash
# Test health endpoint
curl http://localhost:8080/api/evaluation/health

# Test lead qualification
curl -X POST http://localhost:8080/api/leads/1/qualify

# Test message generation
curl -X POST http://localhost:8080/api/leads/1/messages/email \
  -H "Content-Type: application/json" \
  -d '{"messageType": "initial outreach"}'
```

## 📊 Performance & Monitoring

### Health Checks
- **API Health**: `/api/evaluation/health`
- **Database Health**: Automatic connection testing
- **Grok API Health**: Connection and response time monitoring

### Metrics
- **Response Times**: Tracked for all API calls
- **Success Rates**: Monitored for AI operations
- **Error Rates**: Logged and tracked
- **Usage Statistics**: Lead and activity metrics

### Logging
- **Structured Logging**: JSON format for production
- **Log Levels**: Configurable per package
- **Error Tracking**: Comprehensive error logging
- **Performance Logging**: Response time tracking

## 🚨 Troubleshooting

### Common Issues

#### 1. Grok API Connection Issues
```bash
# Check API key
echo $XAI_API_KEY

# Test connection
curl -H "Authorization: Bearer $XAI_API_KEY" \
     -H "Content-Type: application/json" \
     -d '{"messages":[{"role":"user","content":"test"}],"model":"grok-4"}' \
     https://api.x.ai/v1/chat/completions
```

#### 2. Database Connection Issues
```bash
# Check H2 console
http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: password
```

#### 3. Frontend Build Issues
```bash
# Clear node modules
rm -rf node_modules package-lock.json
npm install

# Clear build cache
npm run build -- --clear-cache
```

#### 4. Docker Issues
```bash
# Rebuild containers
docker-compose down
docker-compose up --build

# Check logs
docker-compose logs -f grok-sdr-system

# Clean up
docker system prune -a
```

### Performance Optimization

#### 1. API Response Times
- Monitor Grok API response times
- Implement caching for frequent requests
- Use connection pooling for database

#### 2. Frontend Performance
- Enable React Query caching
- Implement lazy loading for large datasets
- Optimize bundle size with code splitting

#### 3. Database Performance
- Add indexes for frequently queried fields
- Implement pagination for large datasets
- Use connection pooling

## 🔒 Security Considerations

### API Security
- **Input Validation**: All inputs are validated
- **SQL Injection Prevention**: Using JPA/Hibernate
- **CORS Configuration**: Properly configured for frontend
- **Error Handling**: No sensitive data in error messages

### Data Protection
- **Environment Variables**: Sensitive data in environment variables
- **Database Security**: H2 for development, PostgreSQL for production
- **API Key Management**: Secure storage and rotation

### Production Deployment
- **HTTPS**: Use SSL/TLS in production
- **Firewall**: Restrict access to necessary ports
- **Monitoring**: Implement security monitoring
- **Backups**: Regular database backups

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

### Code Standards
- **Java**: Follow Google Java Style Guide
- **JavaScript**: Use ESLint and Prettier
- **Commits**: Use conventional commit messages
- **Documentation**: Update README for new features

### Testing Requirements
- **Unit Tests**: Required for new features
- **Integration Tests**: For API endpoints
- **Frontend Tests**: For React components
- **E2E Tests**: For critical user flows

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **xAI** for providing the Grok API
- **Spring Boot** team for the excellent framework
- **React** team for the frontend library
- **Tailwind CSS** for the styling framework

## 📞 Support

For support and questions:
- **Issues**: Create an issue in the repository
- **Documentation**: Check the README and code comments
- **API**: Use the evaluation endpoints to test functionality

---

**Built with ❤️ using Grok AI**