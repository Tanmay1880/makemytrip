import { useNavigate } from 'react-router-dom';
import {
  User,
  Mail,
  Phone,
  Shield,
  LogOut,
  BadgeCheck,
} from 'lucide-react';
import { useAuth } from '@/context/AuthContext';

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) {
    navigate('/login');
    return null;
  }

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const fields = [
    {
      icon: User,
      label: 'Full Name',
      value: `${user.firstName} ${user.lastName}`,
    },
    {
      icon: Mail,
      label: 'Email',
      value: user.email,
    },
    {
      icon: Phone,
      label: 'Phone',
      value: user.phoneNumber || 'N/A',
    },
    {
      icon: Shield,
      label: 'Role',
      value: user.role,
    },
  ];

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      {/* Page title */}
      <h1 className="text-2xl font-bold text-gray-900 mb-6">
        My Profile
      </h1>

      {/* Profile card */}
      <div className="card overflow-hidden mb-6">

        {/* Header */}
        <div className="bg-gradient-to-r from-primary-600 to-primary-800 px-6 py-8">

          <div className="flex items-center gap-4">

            <div className="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center">
              <User className="w-8 h-8 text-white" />
            </div>

            <div>
              <h2 className="text-xl font-bold text-white">
                {user.firstName} {user.lastName}
              </h2>

              <p className="text-primary-200 text-sm">
                {user.email}
              </p>
            </div>

          </div>

        </div>

        {/* Profile information */}
        <div className="p-6">

          {/* Account status */}
          <div className="flex items-center gap-2 mb-4">

            <BadgeCheck className="w-5 h-5 text-success-500" />

            <span className="text-sm font-medium text-gray-900">
              Account Status: Active
            </span>

          </div>

          {/* Fields */}
          <div className="space-y-4">

            {fields.map((field) => {
              const Icon = field.icon;

              return (
                <div
                  key={field.label}
                  className="flex items-center gap-3 pb-4 border-b border-gray-100 last:border-0"
                >

                  <div className="w-10 h-10 rounded-lg bg-gray-50 flex items-center justify-center flex-shrink-0">

                    <Icon className="w-4 h-4 text-gray-500" />

                  </div>

                  <div className="flex-1">

                    <p className="text-xs text-gray-400">
                      {field.label}
                    </p>

                    <p className="text-sm font-medium text-gray-900">
                      {field.value}
                    </p>

                  </div>

                </div>
              );
            })}

          </div>

        </div>

      </div>

      {/* Actions */}
      <div className="flex gap-3">

        <button
          onClick={handleLogout}
          className="btn-danger flex-1 py-3"
        >
          <LogOut className="w-4 h-4" />
          Logout
        </button>

      </div>

    </div>
  );
}