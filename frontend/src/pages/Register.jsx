import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plane, Mail, Lock, User, Phone, Eye, EyeOff, AlertCircle } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useToast } from '@/context/ToastContext';
import { validateRegister } from '@/utils/validators';

export default function Register() {
  const { register } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: undefined });
    setServerError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validateRegister(form);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    setServerError('');
    try {
      const result = await register(form);
      toast.success(`Account created. Welcome, ${result.user.firstName}!`);
      navigate('/');
    } catch (err) {
      const msg = err?.response?.data?.message || 'Registration failed. Please try again.';
      setServerError(msg);
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12 bg-gradient-to-br from-primary-50 via-white to-gray-50">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-xl bg-primary-600 flex items-center justify-center mx-auto mb-4">
            <Plane className="w-7 h-7 text-white -rotate-45" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Create Account</h1>
          <p className="text-sm text-gray-500 mt-1">Join MakeMyTrip to start booking flights</p>
        </div>

        <div className="card p-8">
          {serverError && (
            <div className="mb-4 flex items-center gap-2 px-4 py-3 rounded-lg bg-error-50 border border-error-200 text-error-700 text-sm">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              {serverError}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="label">First Name</label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                  <input
                    type="text"
                    name="firstName"
                    value={form.firstName}
                    onChange={handleChange}
                    placeholder="John"
                    className={`input pl-10 ${errors.firstName ? 'border-error-400' : ''}`}
                  />
                </div>
                {errors.firstName && <p className="text-xs text-error-500 mt-1">{errors.firstName}</p>}
              </div>
              <div>
                <label className="label">Last Name</label>
                <input
                  type="text"
                  name="lastName"
                  value={form.lastName}
                  onChange={handleChange}
                  placeholder="Doe"
                  className={`input ${errors.lastName ? 'border-error-400' : ''}`}
                />
                {errors.lastName && <p className="text-xs text-error-500 mt-1">{errors.lastName}</p>}
              </div>
            </div>

            <div>
              <label className="label">Email</label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="you@example.com"
                  className={`input pl-10 ${errors.email ? 'border-error-400' : ''}`}
                />
              </div>
              {errors.email && <p className="text-xs text-error-500 mt-1">{errors.email}</p>}
            </div>

            <div>
              <label className="label">Phone</label>
              <div className="relative">
                <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type="tel"
                  name="phone"
                  value={form.phone}
                  onChange={handleChange}
                  placeholder="+1 555 000 0000"
                  className={`input pl-10 ${errors.phone ? 'border-error-400' : ''}`}
                />
              </div>
              {errors.phone && <p className="text-xs text-error-500 mt-1">{errors.phone}</p>}
            </div>

            <div>
              <label className="label">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="At least 6 characters"
                  className={`input pl-10 pr-10 ${errors.password ? 'border-error-400' : ''}`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-error-500 mt-1">{errors.password}</p>}
            </div>

            <div>
              <label className="label">Confirm Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  placeholder="Re-enter password"
                  className={`input pl-10 ${errors.confirmPassword ? 'border-error-400' : ''}`}
                />
              </div>
              {errors.confirmPassword && <p className="text-xs text-error-500 mt-1">{errors.confirmPassword}</p>}
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="btn-primary w-full py-3"
            >
              {submitting ? (
                <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                'Create Account'
              )}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-primary-600 font-medium hover:text-primary-700">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
