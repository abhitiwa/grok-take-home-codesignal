# Grok-Powered SDR System

A comprehensive AI-powered Sales Development Representative (SDR) system that leverages Grok's API to enhance and automate the sales prospecting process. This system provides intelligent lead qualification, personalized messaging, and comprehensive pipeline management with systematic AI evaluation capabilities.

## Overview

This system demonstrates the power of Grok AI for sales automation, featuring:
- AI-driven lead qualification with detailed scoring and reasoning
- Personalized message generation for multiple communication channels
- Systematic evaluation framework for prompt optimization
- Professional web interface for sales team productivity
- Production-ready architecture with comprehensive error handling

## Features

### Core AI Capabilities
- **Intelligent Lead Qualification**: Grok analyzes leads across multiple criteria (company size, industry, title, location) and provides 0-100 scores with detailed reasoning and actionable recommendations
- **Personalized Message Generation**: Creates customized outreach messages for emails, LinkedIn, follow-ups, and meeting requests based on lead profiles and context
- **Custom Qualification Criteria**: Allows users to define specific qualification parameters and re-score leads accordingly
- **Multi-Channel Messaging**: Supports different communication channels with appropriate tone and length adjustments

### Evaluation Framework
- **Systematic Testing**: Comprehensive evaluation of Grok's performance across different sales scenarios
- **Prompt Optimization**: Testing of various prompt engineering approaches to identify best practices
- **Performance Metrics**: Tracking of response times, success rates, and content quality
- **Continuous Improvement**: Historical tracking of evaluation results for prompt iteration

### Pipeline Management
- **Stage Tracking**: Defined pipeline stages from initial contact through conversion
- **Automated Progression**: Business logic for advancing leads through the sales process
- **Activity Logging**: Comprehensive tracking of all lead interactions and communications
- **Follow-up Management**: Automated identification of leads requiring attention

### User Interface
- **Modern Dashboard**: Real-time pipeline overview with statistics and health monitoring
- **Lead Management**: Comprehensive interface for viewing, editing, and managing prospects
- **Activity Tracking**: Detailed interaction history with searchable and filterable views
- **Evaluation Interface**: Interactive testing platform for AI performance validation

## Quick Start

### Prerequisites
- Java 8+ (tested with Java 17)
- Maven 3.6+
- Node.js 18+ and npm
- Grok API key from xAI

### Local Development Setup

```bash
# 1. Clone the repository
git clone <repository-url>
cd grok-sdr-system

# 2. Environment configuration
cp .env.example .env
# Edit .env and add your Grok API key:
# XAI_API_KEY=your_grok_api_key_here

# 3. Start the backend
source .env && mvn spring-boot:run

# 4. Start the frontend (in new terminal)
cd frontend
npm install
npm start

# 5. Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api
# H2 Database Console: http://localhost:8080/api/h2-console