import { Plane, Search } from 'lucide-react';

export default function EmptyState({ title, message, action }) {
  return (
    <div className="card p-12 text-center">
      <div className="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <Search className="w-8 h-8 text-gray-400" />
      </div>
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-sm text-gray-500 max-w-md mx-auto mb-6">{message}</p>
      {action}
    </div>
  );
}
