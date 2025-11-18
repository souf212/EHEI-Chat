import axios from 'axios'

/**
 * Service API pour communiquer avec le backend Spring Boot
 * 
 * Utilise Axios pour faire des requêtes HTTP
 */

// URL de base de l'API (backend Spring Boot)
const API_BASE_URL = 'http://localhost:8080/api'

// Créer une instance Axios avec configuration par défaut
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * Service d'authentification
 */
export const authService = {
  /**
   * Inscription : Envoyer le numéro de téléphone
   * @param {string} phoneNumber - Numéro au format +212XXXXXXXXX
   * @returns {Promise} Réponse avec userId
   */
  register: async (phoneNumber) => {
    const response = await api.post('/auth/register', { phoneNumber })
    return response.data
  },

  /**
   * Vérification : Vérifier le code reçu
   * @param {string} phoneNumber - Numéro de téléphone
   * @param {string} code - Code de vérification à 6 chiffres
   * @returns {Promise} Réponse avec les données utilisateur
   */
  verify: async (phoneNumber, code) => {
    const response = await api.post('/auth/verify', { phoneNumber, code })
    return response.data
  },

  /**
   * Connexion : Se connecter avec un numéro vérifié
   * @param {string} phoneNumber - Numéro de téléphone
   * @returns {Promise} Réponse avec les données utilisateur
   */
  login: async (phoneNumber) => {
    const response = await api.post('/auth/login', { phoneNumber })
    return response.data
  },
}

/**
 * Service de chat
 */
export const chatService = {
  /**
   * Récupérer l'historique d'un chat individuel
   * @param {string} userId1 - ID du premier utilisateur
   * @param {string} userId2 - ID du second utilisateur
   * @returns {Promise} Liste des messages
   */
  getChatHistory: async (userId1, userId2) => {
    const response = await api.get('/chat/history', {
      params: { userId1, userId2 },
    })
    return response.data
  },

  /**
   * Récupérer l'historique d'un groupe
   * @param {string} groupId - ID du groupe
   * @returns {Promise} Liste des messages
   */
  getGroupHistory: async (groupId) => {
    const response = await api.get(`/chat/group/${groupId}/history`)
    return response.data
  },
}

export default api

