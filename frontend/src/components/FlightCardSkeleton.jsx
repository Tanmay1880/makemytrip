import { Plane } from 'lucide-react';

export default function FlightCardSkeleton() {
  return (
    <div className="card p-5 animate-pulse">
      <div className="flex flex-col lg:flex-row lg:items-center gap-4">
        <div className="flex items-center gap-3 lg:w-48">
          <div className="w-11 h-11 rounded-lg bg-gray-200" />
          <div className="space-y-2">
            <div className="h-4 w-24 bg-gray-200 rounded" />
            <div className="h-3 w-16 bg-gray-200 rounded" />
          </div>
        </div>
        <div className="flex items-center gap-3 flex-1">
          <div className="space-y-1">
            <div className="h-6 w-16 bg-gray-200 rounded" />
            <div className="h-3 w-12 bg-gray-200 rounded" />
          </div>
          <div className="flex-1 h-px bg-gray-200" />
          <div className="space-y-1">
            <div className="h-6 w-16 bg-gray-200 rounded" />
            <div className="h-3 w-12 bg-gray-200 rounded" />
          </div>
        </div>
        <div className="space-y-2 lg:w-32">
          <div className="h-6 w-20 bg-gray-200 rounded" />
          <div className="h-9 w-28 bg-gray-200 rounded-lg" />
        </div>
      </div>
      <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-3 gap-2">
        {[0, 1, 2].map((i) => (
          <div key={i} className="h-14 bg-gray-100 rounded-lg" />
        ))}
      </div>
    </div>
  );
}
