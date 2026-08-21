import { Plane, Mail, Phone, Globe } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-300 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <div className="flex items-center gap-2 mb-4">
              <div className="w-9 h-9 rounded-lg bg-primary-600 flex items-center justify-center">
                <Plane className="w-5 h-5 text-white -rotate-45" />
              </div>
              <span className="text-lg font-bold text-white">
                Make<span className="text-primary-400">MyTrip</span>
              </span>
            </div>
            <p className="text-sm text-gray-400 leading-relaxed">
              Your trusted partner for seamless flight bookings across the globe.
            </p>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-white mb-4">Quick Links</h3>
            <ul className="space-y-2 text-sm">
              <li><Link to="/" className="hover:text-primary-400 transition-colors">Search Flights</Link></li>
              <li><Link to="/bookings" className="hover:text-primary-400 transition-colors">My Bookings</Link></li>
              <li><Link to="/profile" className="hover:text-primary-400 transition-colors">Profile</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-white mb-4">Support</h3>
            <ul className="space-y-2 text-sm">
              <li className="flex items-center gap-2"><Mail className="w-4 h-4" /> support@makemytrip.com</li>
              <li className="flex items-center gap-2"><Phone className="w-4 h-4" /> +1-800-FLY-NOW</li>
              <li className="flex items-center gap-2"><Globe className="w-4 h-4" /> Available 24/7</li>
            </ul>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-white mb-4">Company</h3>
            <ul className="space-y-2 text-sm">
              <li><span className="hover:text-primary-400 cursor-pointer transition-colors">About Us</span></li>
              <li><span className="hover:text-primary-400 cursor-pointer transition-colors">Careers</span></li>
              <li><span className="hover:text-primary-400 cursor-pointer transition-colors">Privacy Policy</span></li>
            </ul>
          </div>
        </div>

        <div className="mt-10 pt-8 border-t border-gray-800 text-center text-sm text-gray-500">
          &copy; {new Date().getFullYear()} MakeMyTrip. All rights reserved.
        </div>
      </div>
    </footer>
  );
}
