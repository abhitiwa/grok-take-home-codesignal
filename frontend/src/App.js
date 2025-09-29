import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from 'react-hot-toast';
import './App.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// Temporary placeholder components for Commit 6
const Dashboard = () => (
  <div className="p-8">
    <h1 className="text-3xl font-bold text-gray-900 mb-4">Grok SDR Dashboard</h1>
    <p className="text-gray-600">AI-powered Sales Development Representative System</p>
    <div className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div className="card">
        <h3 className="text-lg font-medium text-gray-900">Total Leads</h3>
        <p className="text-3xl font-bold text-primary-600">0</p>
      </div>
      <div className="card">
        <h3 className="text-lg font-medium text-gray-900">Qualified</h3>
        <p className="text-3xl font-bold text-green-600">0</p>
      </div>
      <div className="card">
        <h3 className="text-lg font-medium text-gray-900">Converted</h3>
        <p className="text-3xl font-bold text-blue-600">0</p>
      </div>
      <div className="card">
        <h3 className="text-lg font-medium text-gray-900">Pipeline Value</h3>
        <p className="text-3xl font-bold text-orange-600">$0</p>
      </div>
    </div>
  </div>
);

const Leads = () => (
  <div className="p-8">
    <h1 className="text-3xl font-bold text-gray-900 mb-4">Leads Management</h1>
    <p className="text-gray-600">Lead management and qualification interface coming soon</p>
  </div>
);

const Activities = () => (
  <div className="p-8">
    <h1 className="text-3xl font-bold text-gray-900 mb-4">Activities</h1>
    <p className="text-gray-600">Activity tracking interface coming soon</p>
  </div>
);

const Evaluation = () => (
  <div className="p-8">
    <h1 className="text-3xl font-bold text-gray-900 mb-4">Model Evaluation</h1>
    <p className="text-gray-600">AI model testing and evaluation interface coming soon</p>
  </div>
);

// Simple layout component
const Layout = ({ children }) => (
  <div className="min-h-screen bg-gray-50">
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <h1 className="text-xl font-bold text-gray-900">Grok SDR System</h1>
          </div>
          <div className="flex items-center space-x-8">
            <a href="/" className="text-gray-600 hover:text-gray-900">Dashboard</a>
            <a href="/leads" className="text-gray-600 hover:text-gray-900">Leads</a>
            <a href="/activities" className="text-gray-600 hover:text-gray-900">Activities</a>
            <a href="/evaluation" className="text-gray-600 hover:text-gray-900">Evaluation</a>
          </div>
        </div>
      </div>
    </nav>
    <main className="max-w-7xl mx-auto">
      {children}
    </main>
  </div>
);

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="App">
          <Layout>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/leads" element={<Leads />} />
              <Route path="/activities" element={<Activities />} />
              <Route path="/evaluation" element={<Evaluation />} />
            </Routes>
          </Layout>
          <Toaster 
            position="top-right"
            toastOptions={{
              duration: 4000,
              style: {
                background: '#363636',
                color: '#fff',
              },
            }}
          />
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;