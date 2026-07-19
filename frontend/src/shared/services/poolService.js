import { poolApi, paymentApi } from 'boot/axios'

/**
 * Service pour interagir avec l'API des cagnottes (Pools).
 */
export const poolService = {
  /**
   * Récupère la liste des cagnottes publiques avec un filtre de recherche optionnel.
   * @param {string} search - Terme de recherche par titre.
   * @returns {Promise}
   */
  getPublicPools(search = '') {
    const params = {}
    if (search) {
      params.search = search
    }
    return poolApi.get('/pools/public', { params })
  },

  /**
   * Récupère la liste complète de toutes les cagnottes (pour l'admin).
   */
  getAllPools() {
    return poolApi.get('/pools')
  },

  /**
   * Récupère les détails d'une cagnotte par son ID.
   * @param {string} id - ID de la cagnotte.
   * @returns {Promise}
   */
  getPoolById(id) {
    return poolApi.get(`/pools/${id}`)
  },

  /**
   * Crée une nouvelle cagnotte.
   * @param {Object} data - Données de la cagnotte.
   * @returns {Promise}
   */
  createPool(data) {
    return poolApi.post('/pools', data)
  },

  /**
   * Met à jour une cagnotte existante.
   * @param {string} id - ID de la cagnotte.
   * @param {Object} data - Données de la cagnotte à modifier.
   * @returns {Promise}
   */
  updatePool(id, data) {
    return poolApi.put(`/pools/${id}`, data)
  },

  /**
   * Upload une image vers le serveur.
   * @param {File} file - Le fichier image à uploader.
   * @returns {Promise}
   */
  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return poolApi.post('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * Initie un paiement Stripe.
   */
  initiateStripeCheckout(data) {
    return paymentApi.post('/payments/checkout/stripe', data)
  },

  /**
   * Confirme un paiement Stripe.
   */
  confirmStripePayment(sessionId) {
    return paymentApi.get('/payments/confirm/stripe', { params: { session_id: sessionId } })
  },

  /**
   * Initie un paiement PayPal.
   */
  initiatePayPalCheckout(data) {
    return paymentApi.post('/payments/checkout/paypal', data)
  },

  /**
   * Confirme un paiement PayPal.
   */
  confirmPayPalPayment(orderId) {
    return paymentApi.get('/payments/confirm/paypal', { params: { token: orderId } })
  },

  /**
   * Récupère la liste des contributions pour une cagnotte (via le service paiement).
   * @param {string} poolId - ID de la cagnotte.
   * @returns {Promise}
   */
  getPoolContributions(poolId) {
    return paymentApi.get(`/contributions/pool/${poolId}`)
  },

  /**
   * Récupère la liste des contributions d'un utilisateur.
   */
  getUserContributions(userId) {
    return paymentApi.get(`/contributions/user/${userId}`)
  },

  /**
   * Effectue un retrait depuis le portefeuille d'une cagnotte.
   * @param {Object} data - Contient poolId, userId, amount, iban, accountHolderName, bankName.
   */
  withdrawFunds(data) {
    return paymentApi.post('/payments/withdraw', data)
  },

  /**
   * Récupère la liste complète des transactions.
   */
  getTransactions() {
    return paymentApi.get('/payments/transactions')
  },

  /**
   * Confirme un retrait en attente.
   * @param {string} transactionId - ID de la transaction à confirmer.
   */
  confirmWithdrawal(transactionId) {
    return paymentApi.post(`/payments/withdraw/${transactionId}/confirm`)
  },

  /**
   * Suspendre une cagnotte (Modération).
   */
  suspendPool(poolId, reason = 'Signalement de contenu non conforme') {
    return poolApi.post(`/pools/${poolId}/suspend`, { reason })
  },

  /**
   * Approuver/publier une cagnotte (Modération/Administration).
   */
  approvePool(poolId) {
    return poolApi.post(`/pools/${poolId}/approve`)
  },

  /**
   * Récupère la liste des commentaires signalés (Modération).
   */
  getReportedMessages() {
    return poolApi.get('/messages/reported')
  },

  /**
   * Rejette le signalement d'un commentaire.
   */
  dismissReport(messageId) {
    return poolApi.post(`/messages/${messageId}/dismiss`)
  },

  /**
   * Supprime un commentaire.
   */
  deleteMessage(messageId) {
    return poolApi.delete(`/messages/${messageId}`)
  },

  /**
   * Récupère la liste des cagnottes signalées.
   */
  getReportedPools() {
    return poolApi.get('/pools/reported')
  },

  /**
   * Rejette les signalements d'une cagnotte.
   */
  dismissPoolReport(poolId) {
    return poolApi.post(`/pools/${poolId}/dismiss-reports`)
  },

  /**
   * Supprime une cagnotte.
   */
  deletePool(poolId) {
    return poolApi.delete(`/pools/${poolId}`)
  },

  /**
   * Récupère la liste des actualités / mises à jour d'une cagnotte.
   * @param {string} poolId
   * @returns {Promise}
   */
  getPoolUpdates(poolId) {
    return poolApi.get(`/pools/${poolId}/updates`)
  },

  /**
   * Publie une nouvelle actualité sur une cagnotte.
   * @param {string} poolId
   * @param {Object} data - { title, content, imageUrl, videoUrl }
   * @returns {Promise}
   */
  createPoolUpdate(poolId, data) {
    return poolApi.post(`/pools/${poolId}/updates`, data)
  }
}

export default poolService
