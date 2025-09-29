import React from 'react';
import { useQuery } from 'react-query';
import { 
  Users, 
  Activity, 
  TrendingUp, 
  Clock,
  Brain,
  Mail,
  Phone
} from 'lucide-react';
import { api } from '../services/api';

const Dashboard = () => {
  const { data: pipelineStats, isLoading: statsLoading } = useQuery(
    'pipelineStats',
    () => api.get('/leads/stats/pipeline').then(res => res.data)
  );

  const { data: recentActivities, isLoading: activitiesLoading } = useQuery(
    'recentActivities',
    () => api.get('/activities/recent').then(res => res.data)
  );

  const { data: followUpLeads, isLoading: followUpLoading } = useQuery(
    'followUpLeads',
    () => api.get('/leads/follow-up').then(res => res.data)
  );

  const { data: healthCheck, isLoading: healthLoading } = useQuery(
    'healthCheck',
    () => api.get('/evaluation/health').then(res => res.data)
  );

  const stats = [
    {
      name: 'Total Leads',
      value: pipelineStats ? Object.values(pipelineStats).reduce((a, b) => a + b, 0) : 0,
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
    },
    {
      name: 'Qualified Leads',
      value: pipelineStats?.QUALIFIED || 0,
      icon: TrendingUp,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
    },
    {
      name: 'Meetings Scheduled',
      value: pipelineStats?.MEETING_SCHEDULED || 0,
      icon: Clock,
      color: 'text-purple-600',
      bgColor: 'bg-purple-100',
    },
    {
      name: 'Converted',
      value: pipelineStats?.CONVERTED || 0,
      icon: Activity,
      color: 'text-orange-600',
      bgColor: 'bg-orange-100',
    },
  ];

  const getActivityIcon = (activityType) => {
    switch (activityType) {
      case 'EMAIL':
        return Mail;
      case 'CALL':
        return Phone;
      default:
        return Activity;
    }
  };

  const getActivityColor = (activityType) => {
    switch (activityType) {
      case 'EMAIL':
        return 'text-blue-600';
      case 'CALL':
        return 'text-green-600';
      case 'MEETING':
        return 'text-purple-600';
      default:
        return 'text-gray-600';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">Overview of your sales pipeline and activities</p>
        </div>
        <div className="flex items-center space-x-2">
          <Brain className="h-6 w-6 text-primary-600" />
          <span className="text-sm font-medium text-gray-700">Grok AI Powered</span>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="card">
              <div className="flex items-center">
                <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                  <Icon className={`h-6 w-6 ${stat.color}`} />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {statsLoading ? '...' : stat.value}
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Health Status */}
      <div className="card">
        <div className="card-header">
          <h3 className="text-lg font-medium text-gray-900">System Health</h3>
        </div>
        <div className="flex items-center space-x-4">
          <div className={`p-2 rounded-lg ${healthCheck?.apiConnection ? 'bg-green-100' : 'bg-red-100'}`}>
            <Brain className={`h-5 w-5 ${healthCheck?.apiConnection ? 'text-green-600' : 'text-red-600'}`} />
          </div>
          <div>
            <p className="text-sm font-medium text-gray-900">
              Grok API Status: {healthLoading ? 'Checking...' : (healthCheck?.apiConnection ? 'Connected' : 'Disconnected')}
            </p>
            {healthCheck?.responseTime && (
              <p className="text-xs text-gray-500">
                Response Time: {healthCheck.responseTime}ms
              </p>
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Recent Activities */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Recent Activities</h3>
          </div>
          <div className="space-y-4">
            {activitiesLoading ? (
              <div className="text-center py-4">
                <div className="spinner mx-auto"></div>
                <p className="text-sm text-gray-500 mt-2">Loading activities...</p>
              </div>
            ) : recentActivities?.length > 0 ? (
              recentActivities.slice(0, 5).map((activity) => {
                const Icon = getActivityIcon(activity.activityType);
                return (
                  <div key={activity.id} className="flex items-center space-x-3">
                    <div className={`p-2 rounded-lg bg-gray-100`}>
                      <Icon className={`h-4 w-4 ${getActivityColor(activity.activityType)}`} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {activity.lead?.firstName} {activity.lead?.lastName}
                      </p>
                      <p className="text-xs text-gray-500 truncate">
                        {activity.description}
                      </p>
                    </div>
                    <div className="text-xs text-gray-400">
                      {new Date(activity.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                );
              })
            ) : (
              <p className="text-sm text-gray-500 text-center py-4">No recent activities</p>
            )}
          </div>
        </div>

        {/* Follow-up Required */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Follow-up Required</h3>
          </div>
          <div className="space-y-4">
            {followUpLoading ? (
              <div className="text-center py-4">
                <div className="spinner mx-auto"></div>
                <p className="text-sm text-gray-500 mt-2">Loading follow-ups...</p>
              </div>
            ) : followUpLeads?.length > 0 ? (
              followUpLeads.slice(0, 5).map((lead) => (
                <div key={lead.id} className="flex items-center space-x-3">
                  <div className="p-2 rounded-lg bg-yellow-100">
                    <Clock className="h-4 w-4 text-yellow-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {lead.firstName} {lead.lastName}
                    </p>
                    <p className="text-xs text-gray-500 truncate">
                      {lead.companyName}
                    </p>
                  </div>
                  <div className="text-xs text-gray-400">
                    {lead.nextFollowUpDate ? new Date(lead.nextFollowUpDate).toLocaleDateString() : 'No date set'}
                  </div>
                </div>
              ))
            ) : (
              <p className="text-sm text-gray-500 text-center py-4">No follow-ups required</p>
            )}
          </div>
        </div>
      </div>

      {/* Pipeline Overview */}
      <div className="card">
        <div className="card-header">
          <h3 className="text-lg font-medium text-gray-900">Pipeline Overview</h3>
        </div>
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-7">
          {pipelineStats && Object.entries(pipelineStats).map(([stage, count]) => (
            <div key={stage} className="text-center">
              <div className="text-2xl font-bold text-gray-900">{count}</div>
              <div className="text-xs text-gray-500 capitalize">
                {stage.toLowerCase().replace('_', ' ')}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;