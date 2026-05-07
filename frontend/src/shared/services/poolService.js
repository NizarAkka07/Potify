import { poolApi } from 'boot/axios'

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
   * Effectue une contribution vers une cagnotte.
   * @param {Object} data - Données de la contribution (poolId, amount, etc.).
   * @returns {Promise}
   */
  contributeToPool(data) {
    return poolApi.post('/contributions', data)
  },

  /**
   * Récupère la liste des contributions pour une cagnotte.
   * @param {string} poolId - ID de la cagnotte.
   * @returns {Promise}
   */
  getPoolContributions(poolId) {
    return poolApi.get(`/contributions/pool/${poolId}`)
  }
}

export default poolService
