/**
 * Calculates exact age in years based on a date of birth string.
 * Supports YYYY-MM-DD, DD/MM/YYYY, or ISO date strings.
 */
export function calculateAge(dobString) {
  if (!dobString) return null;
  
  let birthDate;
  if (dobString.includes('/')) {
    const parts = dobString.split('/');
    if (parts[0].length === 4) {
      // YYYY/MM/DD
      birthDate = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
    } else {
      // DD/MM/YYYY
      birthDate = new Date(parseInt(parts[2], 10), parseInt(parts[1], 10) - 1, parseInt(parts[0], 10));
    }
  } else {
    // YYYY-MM-DD
    birthDate = new Date(dobString);
  }

  if (isNaN(birthDate.getTime())) return null;

  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age >= 0 ? age : null;
}

/**
 * Formats a Date of Birth string into standard dd/MM/yyyy format.
 */
export function formatDateOfBirth(dobString) {
  if (!dobString) return 'Not specified';

  let dateObj;
  if (dobString.includes('/')) {
    const parts = dobString.split('/');
    if (parts[0].length === 4) {
      dateObj = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
    } else {
      dateObj = new Date(parseInt(parts[2], 10), parseInt(parts[1], 10) - 1, parseInt(parts[0], 10));
    }
  } else {
    dateObj = new Date(dobString);
  }

  if (isNaN(dateObj.getTime())) return dobString;

  const day = String(dateObj.getDate()).padStart(2, '0');
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const year = dateObj.getFullYear();

  return `${day}/${month}/${year}`;
}
