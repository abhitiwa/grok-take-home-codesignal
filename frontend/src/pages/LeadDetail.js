import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  ArrowLeft, 
  Mail, 
  Phone, 
  Calendar, 
  Star,
  MessageSquare,
  Plus,
  Edit,
  Trash2
} from 'lucide-react';
import { api } from '../services/api';
import toast from 'react-hot-toast';

const LeadDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState('overview');
  const [showQualification, setShowQualification] = useState(false);

  const { data: lead, isLoading, error } = useQuery(
    ['lead', id],
    () => api.get(`/leads/${id}`).then(res => res.data),
    { enabled: !!id }
  );

  const { data: activities } = useQuery(
    ['activities', id],
    () => api.get(`/activities/lead/${id}`).then(res => res.data),
    { enabled: !!id }
  );

  const qualifyLeadMutation = useMutation(
    () => api.post(`/leads/${id}/qualify`),
    {
      onSuccess: (response) => {
        queryClient.invalidateQueries(['lead', id]);
        toast.success('Lead qualified successfully!');
        setShowQualification(false);
      },
      onError: (error) => {
        toast.error('Failed to qualify lead: ' + error.message);
      },
    }
  );

  const generateMessageMutation = useMutation(
    ({ messageType }) => api.post(`/leads/${id}/messages/email`, { messageType }),
    {
      onSuccess: (response) => {
        toast.success('Message generated successfully!');
        setGeneratedMessage(response.data.message);
      },
      onError: (error) => {
        toast.error('Failed to generate message: ' + error.message);
      },
    }
  );

  const [generatedMessage, setGeneratedMessage] = useState('');

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600 bg-green-100';
    if (score >= 60) return 'text-yellow-600 bg-yellow-100';
    return 'text-red-600 bg-red-100';
  };

  const getStageColor = (stage) => {
    switch (stage) {
      case 'NEW':
        return 'bg-gray-100 text-gray-800';
      case 'CONTACTED':
        return 'bg-blue-100 text-blue-800';
      case 'QUALIFIED':
        return 'bg-green-100 text-green-800';
      case 'ENGAGED':
        return 'bg-purple-100 text-purple-800';
      case 'MEETING_SCHEDULED':
        return 'bg-orange-100 text-orange-800';
      case 'CONVERTED':
        return 'bg-green-100 text-green-800';
      case 'CLOSED_LOST':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (isLoading) {
    return (
      <div className="text-center py-12">
        <div className="spinner mx-auto"></div>
        <p className="text-sm text-gray-500 mt-2">Loading lead details...</p>
      </div>
    );
  }

  if (error || !lead) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Error loading lead: {error?.message}</p>
        <button
          onClick={() => navigate('/leads')}
          className="btn-primary mt-4"
        >
          Back to Leads
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/leads')}
            className="text-gray-400 hover:text-gray-600"
          >
            <ArrowLeft className="h-6 w-6" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              {lead.firstName} {lead.lastName}
            </h1>
            <p className="text-gray-600">{lead.companyName}</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={() => setShowQualification(true)}
            className="btn-secondary flex items-center space-x-2"
          >
            <Star className="h-4 w-4" />
            <span>Qualify</span>
          </button>
          <button className="btn-primary flex items-center space-x-2">
            <Edit className="h-4 w-4" />
            <span>Edit</span>
          </button>
        </div>
      </div>

      {/* Lead Info Card */}
      <div className="card">
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <label className="text-sm font-medium text-gray-500">Email</label>
                <p className="text-sm text-gray-900">{lead.email}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">Phone</label>
                <p className="text-sm text-gray-900">{lead.phone || 'Not provided'}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">Title</label>
                <p className="text-sm text-gray-900">{lead.title || 'Not provided'}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">Company Size</label>
                <p className="text-sm text-gray-900">{lead.companySize || 'Not provided'}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">Industry</label>
                <p className="text-sm text-gray-900">{lead.industry || 'Not provided'}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">Location</label>
                <p className="text-sm text-gray-900">{lead.location || 'Not provided'}</p>
              </div>
            </div>
          </div>
          <div className="space-y-4">
            <div>
              <label className="text-sm font-medium text-gray-500">Pipeline Stage</label>
              <div className="mt-1">
                <span className={`badge ${getStageColor(lead.pipelineStage)}`}>
                  {lead.pipelineStage?.replace('_', ' ')}
                </span>
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">Qualification Score</label>
              <div className="mt-1">
                {lead.qualificationScore ? (
                  <span className={`badge ${getScoreColor(lead.qualificationScore)}`}>
                    {lead.qualificationScore}/100
                  </span>
                ) : (
                  <span className="text-sm text-gray-400">Not scored</span>
                )}
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">Last Contact</label>
              <p className="text-sm text-gray-900">
                {lead.lastContactDate 
                  ? new Date(lead.lastContactDate).toLocaleDateString()
                  : 'Never'
                }
              </p>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-500">Next Follow-up</label>
              <p className="text-sm text-gray-900">
                {lead.nextFollowUpDate 
                  ? new Date(lead.nextFollowUpDate).toLocaleDateString()
                  : 'Not scheduled'
                }
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {[
            { id: 'overview', name: 'Overview' },
            { id: 'activities', name: 'Activities' },
            { id: 'messages', name: 'Messages' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === tab.id
                  ? 'border-primary-500 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              {tab.name}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Qualification Reasoning */}
          {lead.qualificationReasoning && (
            <div className="card">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Qualification Analysis</h3>
              <p className="text-sm text-gray-700 whitespace-pre-wrap">
                {lead.qualificationReasoning}
              </p>
            </div>
          )}

          {/* Notes */}
          {lead.notes && (
            <div className="card">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Notes</h3>
              <p className="text-sm text-gray-700 whitespace-pre-wrap">
                {lead.notes}
              </p>
            </div>
          )}
        </div>
      )}

      {activeTab === 'activities' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-medium text-gray-900">Activities</h3>
            <button className="btn-primary flex items-center space-x-2">
              <Plus className="h-4 w-4" />
              <span>Add Activity</span>
            </button>
          </div>

          <div className="space-y-4">
            {activities?.length > 0 ? (
              activities.map((activity) => (
                <div key={activity.id} className="card">
                  <div className="flex items-start justify-between">
                    <div className="flex items-start space-x-3">
                      <div className="p-2 rounded-lg bg-gray-100">
                        {activity.activityType === 'EMAIL' && <Mail className="h-4 w-4 text-blue-600" />}
                        {activity.activityType === 'CALL' && <Phone className="h-4 w-4 text-green-600" />}
                        {activity.activityType === 'MEETING' && <Calendar className="h-4 w-4 text-purple-600" />}
                        {activity.activityType === 'NOTE' && <MessageSquare className="h-4 w-4 text-gray-600" />}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center space-x-2">
                          <span className="text-sm font-medium text-gray-900">
                            {activity.activityType}
                          </span>
                          <span className="text-xs text-gray-500">
                            {new Date(activity.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="text-sm text-gray-700 mt-1">
                          {activity.description}
                        </p>
                        {activity.outcome && (
                          <p className="text-xs text-gray-500 mt-2">
                            Outcome: {activity.outcome}
                          </p>
                        )}
                      </div>
                    </div>
                    <button className="text-gray-400 hover:text-gray-600">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-500">No activities recorded</p>
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'messages' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-medium text-gray-900">Generated Messages</h3>
            <div className="flex space-x-2">
              <button
                onClick={() => generateMessageMutation.mutate({ messageType: 'initial outreach' })}
                disabled={generateMessageMutation.isLoading}
                className="btn-secondary"
              >
                {generateMessageMutation.isLoading ? 'Generating...' : 'Generate Email'}
              </button>
              <button
                onClick={() => generateMessageMutation.mutate({ messageType: 'follow-up' })}
                disabled={generateMessageMutation.isLoading}
                className="btn-secondary"
              >
                {generateMessageMutation.isLoading ? 'Generating...' : 'Generate Follow-up'}
              </button>
            </div>
          </div>

          {generatedMessage && (
            <div className="card">
              <h4 className="text-md font-medium text-gray-900 mb-4">Generated Message</h4>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-700 whitespace-pre-wrap">
                  {generatedMessage}
                </p>
              </div>
              <div className="mt-4 flex space-x-2">
                <button className="btn-primary">Copy to Clipboard</button>
                <button className="btn-secondary">Edit Message</button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Qualification Modal */}
      {showQualification && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Qualify Lead</h3>
            <p className="text-sm text-gray-600 mb-6">
              This will use Grok AI to analyze the lead and assign a qualification score.
            </p>
            <div className="flex space-x-3">
              <button
                onClick={() => qualifyLeadMutation.mutate()}
                disabled={qualifyLeadMutation.isLoading}
                className="btn-primary flex-1"
              >
                {qualifyLeadMutation.isLoading ? 'Qualifying...' : 'Qualify Lead'}
              </button>
              <button
                onClick={() => setShowQualification(false)}
                className="btn-secondary flex-1"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default LeadDetail;