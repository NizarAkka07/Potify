/**
 * Définition des routes de l'application.
 * Ce fichier configure le mapping entre les URLs et les composants Vue.
 */
const routes = [
  {
    path: '/', // Route racine
    component: () => import('src/shared/layouts/LandingLayout.vue'), // Layout pour les pages publiques
    children: [
      // Page d'accueil (Landing Page)
      { path: '', component: () => import('src/landing/pages/IndexPage.vue') },
      // Page de connexion
      { path: 'login', component: () => import('src/user/pages/auth/LoginPage.vue') },
      // Page d'inscription
      { path: 'register', component: () => import('src/user/pages/auth/RegisterPage.vue') },
      // Verification d'email
      { path: 'verify-email', component: () => import('src/user/pages/auth/VerifyEmailPage.vue') },
      // Creation de cagnotte
      { path: 'pools/create', component: () => import('src/pool/pages/CreatePoolPage.vue') },
      // Liste des cagnottes publiques
      { path: 'pools', component: () => import('src/pool/pages/PublicPoolsPage.vue') },
      // Détail d'une cagnotte
      { path: 'pools/:id', component: () => import('src/pool/pages/PoolDetailPage.vue') },
      // Acceptation d'invitation
      { path: 'pool/invitation', component: () => import('src/pool/pages/AcceptInvitationPage.vue') },
      // Dashboard utilisateur
      { path: 'dashboard', component: () => import('src/pool/pages/UserDashboardPage.vue'), meta: { requiresAuth: true } }
    ]
  },
  {
    path: '/admin', // Préfixe pour la partie administration
    meta: { requiresAuth: true }, // Méta-donnée pour indiquer que l'authentification est requise
    component: () => import('src/shared/layouts/MainLayout.vue'), // Layout principal pour les utilisateurs connectés
    children: [
      // Gestion des utilisateurs
      { path: '/users', component: () => import('src/user/pages/UserListPage.vue') },
      // Gestion des rôles
      { path: '/roles', component: () => import('src/user/pages/RoleListPage.vue') },
      // Gestion des permissions
      { path: '/permissions', component: () => import('src/user/pages/PermissionListPage.vue') }
    ]
  },

  // Route de capture pour gérer les pages non trouvées (404)
  // Doit toujours être en dernière position
  {
    path: '/:catchAll(.*)*',
    component: () => import('src/pages/ErrorNotFound.vue')
  }
]

// Exportation de la configuration des routes pour être utilisée par le routeur
export default routes
