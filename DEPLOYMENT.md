# Deployment Guide

This guide covers deployment options for the Grok-powered SDR system, from local development to production environments.

## 🚀 Quick Start Deployment

### Prerequisites
- Docker and Docker Compose installed
- Grok API key from xAI
- 4GB RAM minimum
- 2GB disk space

### 1. Clone and Setup
```bash
git clone <repository-url>
cd grok-sdr-system
cp .env.example .env
```

### 2. Configure Environment
Edit `.env` file:
```bash
XAI_API_KEY=your_grok_api_key_here
SPRING_PROFILES_ACTIVE=prod
```

### 3. Deploy with Docker
```bash
docker-compose up --build -d
```

### 4. Verify Deployment
```bash
# Check health
curl http://localhost:8080/api/evaluation/health

# Access application
open http://localhost:8080
```

## 🐳 Docker Deployment

### Single Container Deployment
```bash
# Build and run
docker build -t grok-sdr-system .
docker run -d \
  --name grok-sdr \
  -p 8080:8080 \
  -e XAI_API_KEY=your_key_here \
  grok-sdr-system
```

### Multi-Container Deployment
```bash
# Full stack with nginx
docker-compose -f docker-compose.yml up -d

# Development mode
docker-compose -f docker-compose.dev.yml up -d
```

### Docker Compose Services

#### Main Application
```yaml
grok-sdr-system:
  build: .
  ports:
    - "8080:8080"
  environment:
    - XAI_API_KEY=${XAI_API_KEY}
    - SPRING_PROFILES_ACTIVE=prod
  volumes:
    - ./data:/app/data
    - ./logs:/app/logs
  restart: unless-stopped
```

#### Nginx Reverse Proxy
```yaml
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
    - "443:443"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
    - ./ssl:/etc/nginx/ssl
  depends_on:
    - grok-sdr-system
```

## ☁️ Cloud Deployment

### AWS Deployment

#### 1. EC2 Instance
```bash
# Launch EC2 instance (t3.medium or larger)
# Install Docker
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker

# Clone and deploy
git clone <repository-url>
cd grok-sdr-system
cp .env.example .env
# Edit .env with your API key
docker-compose up -d
```

#### 2. ECS with Fargate
```yaml
# ecs-task-definition.json
{
  "family": "grok-sdr-system",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "grok-sdr-system",
      "image": "your-account.dkr.ecr.region.amazonaws.com/grok-sdr-system:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "XAI_API_KEY",
          "value": "your-api-key"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/grok-sdr-system",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Google Cloud Platform

#### 1. Cloud Run
```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/PROJECT-ID/grok-sdr-system

# Deploy to Cloud Run
gcloud run deploy grok-sdr-system \
  --image gcr.io/PROJECT-ID/grok-sdr-system \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars XAI_API_KEY=your-key-here
```

#### 2. GKE (Google Kubernetes Engine)
```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grok-sdr-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: grok-sdr-system
  template:
    metadata:
      labels:
        app: grok-sdr-system
    spec:
      containers:
      - name: grok-sdr-system
        image: gcr.io/PROJECT-ID/grok-sdr-system:latest
        ports:
        - containerPort: 8080
        env:
        - name: XAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: grok-secrets
              key: api-key
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: grok-sdr-service
spec:
  selector:
    app: grok-sdr-system
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

### Azure Deployment

#### 1. Container Instances
```bash
# Deploy to Azure Container Instances
az container create \
  --resource-group myResourceGroup \
  --name grok-sdr-system \
  --image your-registry.azurecr.io/grok-sdr-system:latest \
  --dns-name-label grok-sdr-system \
  --ports 8080 \
  --environment-variables XAI_API_KEY=your-key-here
```

#### 2. App Service
```bash
# Deploy to Azure App Service
az webapp create \
  --resource-group myResourceGroup \
  --plan myAppServicePlan \
  --name grok-sdr-system \
  --deployment-container-image-name your-registry.azurecr.io/grok-sdr-system:latest

# Set environment variables
az webapp config appsettings set \
  --resource-group myResourceGroup \
  --name grok-sdr-system \
  --settings XAI_API_KEY=your-key-here
```

## 🗄️ Database Deployment

### Production Database Setup

#### PostgreSQL
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: grok_sdr
      POSTGRES_USER: grok_user
      POSTGRES_PASSWORD: secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  grok-sdr-system:
    build: .
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/grok_sdr
      - SPRING_DATASOURCE_USERNAME=grok_user
      - SPRING_DATASOURCE_PASSWORD=secure_password
      - XAI_API_KEY=${XAI_API_KEY}
    depends_on:
      - postgres

volumes:
  postgres_data:
```

#### MySQL
```yaml
mysql:
  image: mysql:8.0
  environment:
    MYSQL_DATABASE: grok_sdr
    MYSQL_USER: grok_user
    MYSQL_PASSWORD: secure_password
    MYSQL_ROOT_PASSWORD: root_password
  volumes:
    - mysql_data:/var/lib/mysql
  ports:
    - "3306:3306"
```

### Database Migration
```bash
# Run migrations
java -jar app.jar --spring.profiles.active=prod --spring.jpa.hibernate.ddl-auto=update

# Or use Flyway
mvn flyway:migrate
```

## 🔒 Security Configuration

### SSL/TLS Setup
```nginx
# nginx-ssl.conf
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
    
    location / {
        proxy_pass http://grok-sdr-system:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Environment Security
```bash
# Use secrets management
# AWS Secrets Manager
aws secretsmanager create-secret \
  --name grok-sdr/api-key \
  --secret-string "your-api-key-here"

# HashiCorp Vault
vault kv put secret/grok-sdr api_key="your-api-key-here"

# Kubernetes Secrets
kubectl create secret generic grok-secrets \
  --from-literal=api-key=your-api-key-here
```

## 📊 Monitoring & Logging

### Application Monitoring
```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana

  grok-sdr-system:
    build: .
    environment:
      - MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics,prometheus
      - MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
```

### Log Aggregation
```yaml
# ELK Stack
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:7.15.0
  environment:
    - discovery.type=single-node
  ports:
    - "9200:9200"

kibana:
  image: docker.elastic.co/kibana/kibana:7.15.0
  ports:
    - "5601:5601"
  environment:
    - ELASTICSEARCH_HOSTS=http://elasticsearch:9200

logstash:
  image: docker.elastic.co/logstash/logstash:7.15.0
  volumes:
    - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
```

## 🔄 CI/CD Pipeline

### GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Build Docker image
      run: docker build -t grok-sdr-system .
    
    - name: Push to registry
      run: |
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker tag grok-sdr-system ${{ secrets.REGISTRY }}/grok-sdr-system:latest
        docker push ${{ secrets.REGISTRY }}/grok-sdr-system:latest
    
    - name: Deploy to production
      run: |
        ssh ${{ secrets.PROD_HOST }} "docker pull ${{ secrets.REGISTRY }}/grok-sdr-system:latest"
        ssh ${{ secrets.PROD_HOST }} "docker-compose up -d"
```

### GitLab CI
```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - deploy

build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

test:
  stage: test
  script:
    - docker run --rm $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA mvn test

deploy:
  stage: deploy
  script:
    - docker pull $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
    - docker tag $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA $CI_REGISTRY_IMAGE:latest
    - docker push $CI_REGISTRY_IMAGE:latest
  only:
    - main
```

## 🚨 Troubleshooting

### Common Deployment Issues

#### 1. Container Won't Start
```bash
# Check logs
docker logs grok-sdr-system

# Check resource usage
docker stats grok-sdr-system

# Check environment variables
docker exec grok-sdr-system env
```

#### 2. Database Connection Issues
```bash
# Test database connection
docker exec -it postgres psql -U grok_user -d grok_sdr -c "SELECT 1;"

# Check network connectivity
docker network ls
docker network inspect grok-sdr-network
```

#### 3. API Key Issues
```bash
# Verify API key is set
docker exec grok-sdr-system printenv XAI_API_KEY

# Test API connection
curl -H "Authorization: Bearer $XAI_API_KEY" \
     -H "Content-Type: application/json" \
     -d '{"messages":[{"role":"user","content":"test"}],"model":"grok-4"}' \
     https://api.x.ai/v1/chat/completions
```

#### 4. Performance Issues
```bash
# Monitor resource usage
docker stats

# Check application metrics
curl http://localhost:8080/actuator/metrics

# Analyze logs
docker logs grok-sdr-system | grep ERROR
```

### Scaling Considerations

#### Horizontal Scaling
```yaml
# docker-compose.scale.yml
version: '3.8'
services:
  grok-sdr-system:
    build: .
    deploy:
      replicas: 3
    environment:
      - XAI_API_KEY=${XAI_API_KEY}
    ports:
      - "8080-8082:8080"
```

#### Load Balancer Configuration
```nginx
upstream grok-sdr-backend {
    server grok-sdr-system_1:8080;
    server grok-sdr-system_2:8080;
    server grok-sdr-system_3:8080;
}
```

## 📋 Deployment Checklist

### Pre-Deployment
- [ ] API key configured and tested
- [ ] Database backup completed
- [ ] Environment variables set
- [ ] SSL certificates configured
- [ ] Monitoring setup verified

### Deployment
- [ ] Application built and tested
- [ ] Containers deployed successfully
- [ ] Health checks passing
- [ ] Database migrations completed
- [ ] Load balancer configured

### Post-Deployment
- [ ] Application accessible
- [ ] API endpoints responding
- [ ] Monitoring alerts configured
- [ ] Backup procedures tested
- [ ] Documentation updated

## 🔧 Maintenance

### Regular Tasks
- **Weekly**: Check logs for errors
- **Monthly**: Update dependencies
- **Quarterly**: Security audit
- **Annually**: Performance review

### Backup Procedures
```bash
# Database backup
docker exec postgres pg_dump -U grok_user grok_sdr > backup_$(date +%Y%m%d).sql

# Application backup
docker save grok-sdr-system:latest | gzip > grok-sdr-system_$(date +%Y%m%d).tar.gz
```

### Update Procedures
```bash
# Update application
git pull origin main
docker-compose down
docker-compose up --build -d

# Verify update
curl http://localhost:8080/api/evaluation/health
```

---

For additional support, refer to the main README.md or create an issue in the repository.