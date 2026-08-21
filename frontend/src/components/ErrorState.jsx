import { AlertTriangle } from 'lucide-react';

export default function ErrorState({ title, message, onRetry }) {
  return (
    <div className="card p-12 text-center border-error-200">
      <div className="w-16 h-16 rounded-full bg-error-50 flex items-center justify-center mx-auto mb-4">
        <AlertTriangle className="w-8 h-8 text-error-500" />
      </div>
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-sm text-gray-500 max-w-md mx-auto mb-6">{message}</p>
      {onRetry && (
        <button onClick={onRetry} className="btn-primary">
          Try Again
        </button>
      )}
    </div>
  );
}
