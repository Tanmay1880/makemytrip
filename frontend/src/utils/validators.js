// ============================================================
// VALIDATORS
// ============================================================

export function isEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export function isPhone(phone) {
  return /^[\d\s\-\+\(\)]{7,}$/.test(phone);
}

export function isStrongPassword(password) {
  return password && password.length >= 6;
}

export function validateLogin({ email, password }) {
  const errors = {};
  if (!email) errors.email = 'Email is required';
  else if (!isEmail(email)) errors.email = 'Invalid email address';
  if (!password) errors.password = 'Password is required';
  return errors;
}

export function validateRegister({ firstName, lastName, email, phone, password, confirmPassword }) {
  const errors = {};
  if (!firstName) errors.firstName = 'First name is required';
  if (!lastName) errors.lastName = 'Last name is required';
  if (!email) errors.email = 'Email is required';
  else if (!isEmail(email)) errors.email = 'Invalid email address';
  if (!phone) errors.phone = 'Phone is required';
  else if (!isPhone(phone)) errors.phone = 'Invalid phone number';
  if (!password) errors.password = 'Password is required';
  else if (!isStrongPassword(password)) errors.password = 'Password must be at least 6 characters';
  if (!confirmPassword) errors.confirmPassword = 'Please confirm your password';
  else if (password !== confirmPassword) errors.confirmPassword = 'Passwords do not match';
  return errors;
}

export function validatePassenger(p, index) {
  const errors = {};
  if (!p.firstName) errors[`passenger_${index}_firstName`] = 'Required';
  if (!p.lastName) errors[`passenger_${index}_lastName`] = 'Required';
  if (!p.gender) errors[`passenger_${index}_gender`] = 'Required';
  return errors;
}
