import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { Link } from 'react-router-dom';
import { 
  Plus, 
  Search, 
  Filter, 
  Mail, 
  Phone,
  Star,
  Clock,
  CheckCircle
} from 'lucide-react';
import { api } from '../services/api';
import toast from 'react-hot-toast';

const Leads = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedStage, setSelectedStage] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const queryClient = useQueryClient();

  const { data: leads, isLoading, error } = useQuery(
    ['leads', searchTerm, selectedStage],
    () => {
      const params = new URLSearchParams();
      if (searchTerm) {
        const [firstName, lastName] = searchTerm.split(' ');
        if (firstName) params.append('firstName', firstName);
        if (lastName) params.append('lastName', lastName);
      }
      if (selectedStage) params.append('pipelineStage', selectedStage);
      
      return api.get(`/leads?${params.toString()}`).then(res => res.data);
    }
  );

  const qualifyLeadMutation = useMutation(
    (leadId) => api.post(`/leads/${leadId}/qualify`),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('leads');
        toast.success('Lead qualified successfully!');
      },
      onError: (error) => {
        toast.error('Failed to qualify lead: ' + error.message);
      },
    }
  );

  const generateMessageMutation = useMutation(
    ({ leadId, messageType }) => api.post(`/leads/${leadId}/messages/email`, { messageType }),
    {
      onSuccess: (response) => {
        toast.success('Message generated successfully!');
        console.log('Generated message:', response.data.message);
      },
      onError: (error) => {
        toast.error('Failed to generate message: ' + error.message);
      },
    }
  );

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

  const getStageIcon = (stage) => {
    switch (stage) {
      case 'NEW':
        return <Clock className="h-4 w-4" />;
      case 'CONTACTED':
        return <Mail className="h-4 w-4" />;
      case 'QUALIFIED':
        return <Star className="h-4 w-4" />;
      case 'ENGAGED':
        return <Phone className="h-4 w-4" />;
      case 'MEETING_SCHEDULED':
        return <CheckCircle className="h-4 w-4" />;
      case 'CONVERTED':
        return <CheckCircle className="h-4 w-4" />;
      default:
        return <Clock className="h-4 w-4" />;
    }
  };

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Error loading leads: {error.message}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Leads</h1>
          <p className="text-gray-600">Manage your sales prospects and pipeline</p>
        </div>
        <button className="btn-primary flex items-center space-x-2">
          <Plus className="h-4 w-4" />
          <span>Add Lead</span>
        </button>
      </div>

      {/* Search and Filters */}
      <div className="card">
        <div className="flex flex-col space-y-4 sm:flex-row sm:space-y-0 sm:space-x-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Search leads by name or company..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="input-field pl-10"
              />
            </div>
          </div>
          <div className="flex space-x-2">
            <select
              value={selectedStage}
              onChange={(e) => setSelectedStage(e.target.value)}
              className="input-field"
            >
              <option value="">All Stages</option>
              <option value="NEW">New</option>
              <option value="CONTACTED">Contacted</option>
              <option value="QUALIFIED">Qualified</option>
              <option value="ENGAGED">Engaged</option>
              <option value="MEETING_SCHEDULED">Meeting Scheduled</option>
              <option value="CONVERTED">Converted</option>
              <option value="CLOSED_LOST">Closed Lost</option>
            </select>
            <button
              onClick={() => setShowFilters(!showFilters)}
              className="btn-secondary flex items-center space-x-2"
            >
              <Filter className="h-4 w-4" />
              <span>Filters</span>
            </button>
          </div>
        </div>
      </div>

      {/* Leads Table */}
      <div className="card">
        {isLoading ? (
          <div className="text-center py-12">
            <div className="spinner mx-auto"></div>
            <p className="text-sm text-gray-500 mt-2">Loading leads...</p>
          </div>
        ) : leads?.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Lead
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Company
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Stage
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Score
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Last Contact
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {leads.map((lead) => (
                  <tr key={lead.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="flex-shrink-0 h-10 w-10">
                          <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                            <span className="text-sm font-medium text-primary-600">
                              {lead.firstName?.[0]}{lead.lastName?.[0]}
                            </span>
                          </div>
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {lead.firstName} {lead.lastName}
                          </div>
                          <div className="text-sm text-gray-500">{lead.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{lead.companyName}</div>
                      <div className="text-sm text-gray-500">{lead.industry}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`badge ${getStageColor(lead.pipelineStage)} flex items-center space-x-1`}>
                        {getStageIcon(lead.pipelineStage)}
                        <span>{lead.pipelineStage?.replace('_', ' ')}</span>
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {lead.qualificationScore ? (
                        <span className={`badge ${getScoreColor(lead.qualificationScore)}`}>
                          {lead.qualificationScore}/100
                        </span>
                      ) : (
                        <span className="text-sm text-gray-400">Not scored</span>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {lead.lastContactDate 
                        ? new Date(lead.lastContactDate).toLocaleDateString()
                        : 'Never'
                      }
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <Link
                          to={`/leads/${lead.id}`}
                          className="text-primary-600 hover:text-primary-900"
                        >
                          View
                        </Link>
                        <button
                          onClick={() => qualifyLeadMutation.mutate(lead.id)}
                          disabled={qualifyLeadMutation.isLoading}
                          className="text-green-600 hover:text-green-900 disabled:opacity-50"
                        >
                          {qualifyLeadMutation.isLoading ? '...' : 'Qualify'}
                        </button>
                        <button
                          onClick={() => generateMessageMutation.mutate({ 
                            leadId: lead.id, 
                            messageType: 'initial outreach' 
                          })}
                          disabled={generateMessageMutation.isLoading}
                          className="text-blue-600 hover:text-blue-900 disabled:opacity-50"
                        >
                          {generateMessageMutation.isLoading ? '...' : 'Message'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-gray-500">No leads found</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Leads;