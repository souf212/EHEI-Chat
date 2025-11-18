/**
 * Utilitaire pour valider les numéros de téléphone marocains
 */

/**
 * Valide un numéro de téléphone marocain
 * Format attendu : +212XXXXXXXXX
 * @param {string} phoneNumber - Numéro à valider
 * @returns {boolean} true si valide
 */
export const isValidMoroccanPhone = (phoneNumber) => {
  if (!phoneNumber) return false
  
  // Pattern : +212 suivi de 9 chiffres (commençant par 6 ou 7)
  const pattern = /^\+212[67]\d{8}$/
  return pattern.test(phoneNumber.trim())
}

/**
 * Normalise un numéro (supprime les espaces)
 * @param {string} phoneNumber - Numéro à normaliser
 * @returns {string} Numéro normalisé
 */
export const normalizePhone = (phoneNumber) => {
  if (!phoneNumber) return ''
  return phoneNumber.trim().replace(/\s+/g, '')
}

/**
 * Formate un numéro pour l'affichage
 * @param {string} phoneNumber - Numéro à formater
 * @returns {string} Numéro formaté (ex: +212 6 12 34 56 78)
 */
export const formatPhone = (phoneNumber) => {
  if (!phoneNumber) return ''
  const normalized = normalizePhone(phoneNumber)
  if (normalized.startsWith('+212')) {
    const number = normalized.substring(4)
    return `+212 ${number.substring(0, 1)} ${number.substring(1, 3)} ${number.substring(3, 5)} ${number.substring(5, 7)} ${number.substring(7)}`
  }
  return normalized
}

