import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  Plus, 
  Search, 
  Filter, 
  Mail, 
  Phone, 
  Calendar,
  MessageSquare,
  Clock,
  CheckCircle
} from 'lucide-react';
import { api } from '../services/api';

const Activities = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  const { data: activities, isLoading, error } = useQuery(
    'recentActivities',
    () => api.get('/activities/recent').then(res => res.data)
  );

  const { data: overdueActivities } = useQuery(
    'overdueActivities',
    () => api.get('/activities/overdue').then(res => res.data)
  );

  const getActivityIcon = (activityType) => {
    switch (activityType) {
      case 'EMAIL':
        return Mail;
      case 'CALL':
        return Phone;
      case 'MEETING':
        return Calendar;
      case 'NOTE':
        return MessageSquare;
      case 'FOLLOW_UP':
        return Clock;
      default:
        return MessageSquare;
    }
  };

  const getActivityColor = (activityType) => {
    switch (activityType) {
      case 'EMAIL':
        return 'text-blue-600 bg-blue-100';
      case 'CALL':
        return 'text-green-600 bg-green-100';
      case 'MEETING':
        return 'text-purple-600 bg-purple-100';
      case 'NOTE':
        return 'text-gray-600 bg-gray-100';
      case 'FOLLOW_UP':
        return 'text-yellow-600 bg-yellow-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  const getStatusIcon = (activity) => {
    if (activity.completedDate) {
      return <CheckCircle className="h-4 w-4 text-green-600" />;
    }
    if (activity.scheduledDate && new Date(activity.scheduledDate) < new Date()) {
      return <Clock className="h-4 w-4 text-red-600" />;
    }
    return <Clock className="h-4 w-4 text-gray-400" />;
  };

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Error loading activities: {error.message}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Activities</h1>
          <p className="text-gray-600">Track all lead interactions and communications</p>
        </div>
        <button className="btn-primary flex items-center space-x-2">
          <Plus className="h-4 w-4" />
          <span>Add Activity</span>
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-3">
        <div className="card">
          <div className="flex items-center">
            <div className="p-3 rounded-lg bg-blue-100">
              <Mail className="h-6 w-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Activities</p>
              <p className="text-2xl font-bold text-gray-900">
                {isLoading ? '...' : activities?.length || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="p-3 rounded-lg bg-red-100">
              <Clock className="h-6 w-6 text-red-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Overdue</p>
              <p className="text-2xl font-bold text-gray-900">
                {isLoading ? '...' : overdueActivities?.length || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="p-3 rounded-lg bg-green-100">
              <CheckCircle className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Completed Today</p>
              <p className="text-2xl font-bold text-gray-900">
                {isLoading ? '...' : activities?.filter(a => 
                  a.completedDate && 
                  new Date(a.completedDate).toDateString() === new Date().toDateString()
                ).length || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="card">
        <div className="flex flex-col space-y-4 sm:flex-row sm:space-y-0 sm:space-x-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Search activities..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="input-field pl-10"
              />
            </div>
          </div>
          <div className="flex space-x-2">
            <select
              value={selectedType}
              onChange={(e) => setSelectedType(e.target.value)}
              className="input-field"
            >
              <option value="">All Types</option>
              <option value="EMAIL">Email</option>
              <option value="CALL">Call</option>
              <option value="MEETING">Meeting</option>
              <option value="NOTE">Note</option>
              <option value="FOLLOW_UP">Follow-up</option>
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

      {/* Overdue Activities */}
      {overdueActivities?.length > 0 && (
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900 text-red-600">Overdue Activities</h3>
          </div>
          <div className="space-y-4">
            {overdueActivities.map((activity) => {
              const Icon = getActivityIcon(activity.activityType);
              return (
                <div key={activity.id} className="flex items-center space-x-3 p-3 bg-red-50 rounded-lg">
                  <div className={`p-2 rounded-lg ${getActivityColor(activity.activityType)}`}>
                    <Icon className="h-4 w-4" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {activity.lead?.firstName} {activity.lead?.lastName}
                    </p>
                    <p className="text-xs text-gray-500 truncate">
                      {activity.description}
                    </p>
                  </div>
                  <div className="text-xs text-red-600">
                    Due: {activity.scheduledDate ? new Date(activity.scheduledDate).toLocaleDateString() : 'No date'}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Activities List */}
      <div className="card">
        <div className="card-header">
          <h3 className="text-lg font-medium text-gray-900">Recent Activities</h3>
        </div>
        <div className="space-y-4">
          {isLoading ? (
            <div className="text-center py-8">
              <div className="spinner mx-auto"></div>
              <p className="text-sm text-gray-500 mt-2">Loading activities...</p>
            </div>
          ) : activities?.length > 0 ? (
            activities
              .filter(activity => {
                if (searchTerm && !activity.description.toLowerCase().includes(searchTerm.toLowerCase())) {
                  return false;
                }
                if (selectedType && activity.activityType !== selectedType) {
                  return false;
                }
                return true;
              })
              .map((activity) => {
                const Icon = getActivityIcon(activity.activityType);
                return (
                  <div key={activity.id} className="flex items-start space-x-4 p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
                    <div className={`p-2 rounded-lg ${getActivityColor(activity.activityType)}`}>
                      <Icon className="h-5 w-5" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-2">
                          <span className="text-sm font-medium text-gray-900">
                            {activity.activityType}
                          </span>
                          {getStatusIcon(activity)}
                        </div>
                        <span className="text-xs text-gray-500">
                          {new Date(activity.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                      <p className="text-sm text-gray-700 mt-1">
                        {activity.description}
                      </p>
                      <div className="flex items-center space-x-4 mt-2 text-xs text-gray-500">
                        <span>
                          Lead: {activity.lead?.firstName} {activity.lead?.lastName}
                        </span>
                        {activity.outcome && (
                          <span>Outcome: {activity.outcome}</span>
                        )}
                        {activity.scheduledDate && (
                          <span>
                            Scheduled: {new Date(activity.scheduledDate).toLocaleDateString()}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-500">No activities found</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Activities;