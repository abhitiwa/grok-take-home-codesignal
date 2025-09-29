import React, { useState } from 'react';
import { useQuery, useMutation } from 'react-query';
import { 
  Play, 
  BarChart3, 
  Brain, 
  CheckCircle, 
  XCircle,
  Clock,
  TrendingUp,
  MessageSquare,
  Settings
} from 'lucide-react';
import { api } from '../services/api';
import toast from 'react-hot-toast';

const Evaluation = () => {
  const [selectedTest, setSelectedTest] = useState('');
  const [testResults, setTestResults] = useState(null);
  const [isRunning, setIsRunning] = useState(false);

  const { data: healthCheck, isLoading: healthLoading } = useQuery(
    'healthCheck',
    () => api.get('/evaluation/health').then(res => res.data)
  );

  const { data: evaluationHistory } = useQuery(
    'evaluationHistory',
    () => api.get('/evaluation/history').then(res => res.data)
  );

  const { data: evaluationMetrics } = useQuery(
    'evaluationMetrics',
    () => api.get('/evaluation/metrics').then(res => res.data)
  );

  const runEvaluationMutation = useMutation(
    (testType) => {
      switch (testType) {
        case 'qualification':
          return api.post('/evaluation/qualification', {});
        case 'messaging':
          return api.post('/evaluation/messaging', {});
        case 'prompts':
          return api.post('/evaluation/prompts', {});
        case 'comprehensive':
          return api.post('/evaluation/comprehensive', {});
        default:
          throw new Error('Invalid test type');
      }
    },
    {
      onSuccess: (response) => {
        setTestResults(response.data);
        toast.success('Evaluation completed successfully!');
        setIsRunning(false);
      },
      onError: (error) => {
        toast.error('Evaluation failed: ' + error.message);
        setIsRunning(false);
      },
    }
  );

  const handleRunTest = (testType) => {
    setSelectedTest(testType);
    setIsRunning(true);
    setTestResults(null);
    runEvaluationMutation.mutate(testType);
  };

  const testTypes = [
    {
      id: 'qualification',
      name: 'Lead Qualification',
      description: 'Test Grok\'s ability to score and qualify leads',
      icon: TrendingUp,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
    },
    {
      id: 'messaging',
      name: 'Message Generation',
      description: 'Test personalized message creation',
      icon: MessageSquare,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
    },
    {
      id: 'prompts',
      name: 'Prompt Variations',
      description: 'Test different prompt engineering approaches',
      icon: Settings,
      color: 'text-purple-600',
      bgColor: 'bg-purple-100',
    },
    {
      id: 'comprehensive',
      name: 'Comprehensive Suite',
      description: 'Run all evaluation tests',
      icon: BarChart3,
      color: 'text-orange-600',
      bgColor: 'bg-orange-100',
    },
  ];

  const getStatusIcon = (success) => {
    return success ? (
      <CheckCircle className="h-4 w-4 text-green-600" />
    ) : (
      <XCircle className="h-4 w-4 text-red-600" />
    );
  };

  const formatResults = (results) => {
    if (!results) return null;

    switch (selectedTest) {
      case 'qualification':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-gray-900">
                    {results.totalTests || 0}
                  </div>
                  <div className="text-sm text-gray-500">Total Tests</div>
                </div>
              </div>
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-green-600">
                    {results.successfulTests || 0}
                  </div>
                  <div className="text-sm text-gray-500">Successful</div>
                </div>
              </div>
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-600">
                    {results.averageScore?.toFixed(1) || 0}
                  </div>
                  <div className="text-sm text-gray-500">Avg Score</div>
                </div>
              </div>
            </div>
            <div className="card">
              <h4 className="text-md font-medium text-gray-900 mb-4">Test Results</h4>
              <div className="space-y-2">
                {results.testResults?.map((test, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div className="flex items-center space-x-3">
                      {getStatusIcon(test.success)}
                      <div>
                        <p className="text-sm font-medium text-gray-900">
                          {test.leadName} - {test.company}
                        </p>
                        <p className="text-xs text-gray-500">Score: {test.score}/100</p>
                      </div>
                    </div>
                    <div className="text-xs text-gray-500">
                      {test.responseTime}ms
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        );

      case 'messaging':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-gray-900">
                    {results.totalTests || 0}
                  </div>
                  <div className="text-sm text-gray-500">Total Tests</div>
                </div>
              </div>
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-green-600">
                    {results.successfulTests || 0}
                  </div>
                  <div className="text-sm text-gray-500">Successful</div>
                </div>
              </div>
              <div className="card">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-600">
                    {results.averageMessageLength?.toFixed(0) || 0}
                  </div>
                  <div className="text-sm text-gray-500">Avg Length</div>
                </div>
              </div>
            </div>
            <div className="card">
              <h4 className="text-md font-medium text-gray-900 mb-4">Sample Messages</h4>
              <div className="space-y-3">
                {results.testResults?.slice(0, 3).map((test, index) => (
                  <div key={index} className="p-3 bg-gray-50 rounded-lg">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-sm font-medium text-gray-900">
                        {test.messageType} - {test.leadId}
                      </span>
                      <span className="text-xs text-gray-500">
                        {test.messageLength} chars
                      </span>
                    </div>
                    <p className="text-xs text-gray-700 line-clamp-3">
                      {test.message}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        );

      case 'comprehensive':
        return (
          <div className="space-y-4">
            <div className="card">
              <h4 className="text-md font-medium text-gray-900 mb-4">System Health</h4>
              <div className="flex items-center space-x-4">
                <div className={`p-2 rounded-lg ${results.health?.apiConnection ? 'bg-green-100' : 'bg-red-100'}`}>
                  <Brain className={`h-5 w-5 ${results.health?.apiConnection ? 'text-green-600' : 'text-red-600'}`} />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900">
                    API Status: {results.health?.status || 'Unknown'}
                  </p>
                  {results.health?.responseTime && (
                    <p className="text-xs text-gray-500">
                      Response Time: {results.health.responseTime}ms
                    </p>
                  )}
                </div>
              </div>
            </div>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="card">
                <h5 className="text-sm font-medium text-gray-900 mb-2">Qualification Results</h5>
                <div className="text-2xl font-bold text-blue-600">
                  {results.qualification?.averageScore?.toFixed(1) || 0}
                </div>
                <div className="text-xs text-gray-500">Average Score</div>
              </div>
              <div className="card">
                <h5 className="text-sm font-medium text-gray-900 mb-2">Messaging Results</h5>
                <div className="text-2xl font-bold text-green-600">
                  {results.messaging?.successfulTests || 0}
                </div>
                <div className="text-xs text-gray-500">Successful Tests</div>
              </div>
            </div>
          </div>
        );

      default:
        return (
          <div className="card">
            <pre className="text-xs text-gray-700 whitespace-pre-wrap">
              {JSON.stringify(results, null, 2)}
            </pre>
          </div>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Model Evaluation</h1>
          <p className="text-gray-600">Test and evaluate Grok's performance across different scenarios</p>
        </div>
        <div className="flex items-center space-x-2">
          <Brain className="h-6 w-6 text-primary-600" />
          <span className="text-sm font-medium text-gray-700">Grok AI Testing</span>
        </div>
      </div>

      {/* Health Status */}
      <div className="card">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-medium text-gray-900">System Health</h3>
            <p className="text-sm text-gray-600">Current Grok API connection status</p>
          </div>
          <div className="flex items-center space-x-4">
            <div className={`p-2 rounded-lg ${healthCheck?.apiConnection ? 'bg-green-100' : 'bg-red-100'}`}>
              <Brain className={`h-5 w-5 ${healthCheck?.apiConnection ? 'text-green-600' : 'text-red-600'}`} />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-900">
                {healthLoading ? 'Checking...' : (healthCheck?.apiConnection ? 'Connected' : 'Disconnected')}
              </p>
              {healthCheck?.responseTime && (
                <p className="text-xs text-gray-500">
                  Response Time: {healthCheck.responseTime}ms
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Test Types */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
        {testTypes.map((test) => {
          const Icon = test.icon;
          return (
            <div key={test.id} className="card">
              <div className="flex items-start space-x-4">
                <div className={`p-3 rounded-lg ${test.bgColor}`}>
                  <Icon className={`h-6 w-6 ${test.color}`} />
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-medium text-gray-900">{test.name}</h3>
                  <p className="text-sm text-gray-600 mt-1">{test.description}</p>
                  <button
                    onClick={() => handleRunTest(test.id)}
                    disabled={isRunning}
                    className="btn-primary mt-4 flex items-center space-x-2"
                  >
                    {isRunning && selectedTest === test.id ? (
                      <Clock className="h-4 w-4 animate-spin" />
                    ) : (
                      <Play className="h-4 w-4" />
                    )}
                    <span>
                      {isRunning && selectedTest === test.id ? 'Running...' : 'Run Test'}
                    </span>
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Test Results */}
      {testResults && (
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">
              Test Results - {testTypes.find(t => t.id === selectedTest)?.name}
            </h3>
          </div>
          {formatResults(testResults)}
        </div>
      )}

      {/* Evaluation History */}
      {evaluationHistory?.length > 0 && (
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Evaluation History</h3>
          </div>
          <div className="space-y-3">
            {evaluationHistory.slice(0, 5).map((entry, index) => (
              <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-3">
                  <div className="p-2 rounded-lg bg-blue-100">
                    <BarChart3 className="h-4 w-4 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">
                      {entry.type?.replace('_', ' ').toUpperCase()}
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(entry.timestamp).toLocaleString()}
                    </p>
                  </div>
                </div>
                <div className="text-xs text-gray-500">
                  {entry.results?.totalTests || 0} tests
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Evaluation Metrics */}
      {evaluationMetrics && (
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Evaluation Metrics</h3>
          </div>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">
                {evaluationMetrics.totalEvaluations || 0}
              </div>
              <div className="text-sm text-gray-500">Total Evaluations</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-blue-600">
                {Object.keys(evaluationMetrics.evaluationsByType || {}).length}
              </div>
              <div className="text-sm text-gray-500">Test Types</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">
                {evaluationMetrics.lastEvaluation ? 'Active' : 'Inactive'}
              </div>
              <div className="text-sm text-gray-500">Status</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Evaluation;