import { Link } from 'react-router-dom';

export default function StatusBadge({ status }) {
  const styles = {
    PENDING: 'bg-warning-100 text-warning-700',
    CONFIRMED: 'bg-success-100 text-success-700',
    CANCELLED: 'bg-error-100 text-error-700',
    COMPLETED: 'bg-primary-100 text-primary-700',
    PAID: 'bg-success-100 text-success-700',
    FAILED: 'bg-error-100 text-error-700',
    REFUNDED: 'bg-gray-100 text-gray-700',
    SCHEDULED: 'bg-primary-100 text-primary-700',
    ACTIVE: 'bg-success-100 text-success-700',
    SUCCEEDED: 'bg-success-100 text-success-700',
  };

  const labels = {
    PENDING: 'Pending',
    CONFIRMED: 'Confirmed',
    CANCELLED: 'Cancelled',
    COMPLETED: 'Completed',
    PAID: 'Paid',
    FAILED: 'Failed',
    REFUNDED: 'Refunded',
    SCHEDULED: 'Scheduled',
    ACTIVE: 'Active',
    SUCCEEDED: 'Succeeded',
  };

  return (
    <span className={`badge ${styles[status] || 'bg-gray-100 text-gray-700'}`}>
      {labels[status] || status}
    </span>
  );
}
