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
      // Mot de passe oublié
      { path: 'forgot-password', component: () => import('src/user/pages/auth/ForgotPasswordPage.vue') },
      // Réinitialisation du mot de passe
      { path: 'reset-password', component: () => import('src/user/pages/auth/ResetPasswordPage.vue') },
      // Creation de cagnotte
      { path: 'pools/create', component: () => import('src/pool/pages/CreatePoolPage.vue') },
      // Liste des cagnottes publiques
      { path: 'pools', component: () => import('src/pool/pages/PublicPoolsPage.vue') },
      // Détail d'une cagnotte
      { path: 'pools/:id', component: () => import('src/pool/pages/PoolDetailPage.vue') },
      // Édition d'une cagnotte
      { path: 'pools/:id/edit', component: () => import('src/pool/pages/EditPoolPage.vue'), meta: { requiresAuth: true } },
      // Acceptation d'invitation
      { path: 'pool/invitation', component: () => import('src/pool/pages/AcceptInvitationPage.vue') },
      // Dashboard utilisateur
      { path: 'dashboard', component: () => import('src/pool/pages/UserDashboardPage.vue'), meta: { requiresAuth: true } },
      // Profil utilisateur
      { path: 'profile', component: () => import('src/user/pages/ProfilePage.vue'), meta: { requiresAuth: true } },
      // Redirection de succès de paiement Stripe / PayPal
      { path: 'payment/success', component: () => import('src/pool/pages/PaymentSuccessPage.vue') }
    ]
  },
  {
    path: '/admin', // Préfixe pour la partie administration
    meta: { requiresAuth: true }, // Méta-donnée pour indiquer que l'authentification est requise
    component: () => import('src/shared/layouts/MainLayout.vue'), // Layout principal pour les utilisateurs connectés
    children: [
      // Dashboard principal
      { path: '', component: () => import('src/user/pages/AdminDashboardPage.vue') },
      // Gestion des utilisateurs
      { path: 'users', component: () => import('src/user/pages/UserListPage.vue') },
      // Gestion des rôles et permissions unifiée
      { path: 'roles', component: () => import('src/user/pages/RoleListPage.vue') },
      { path: 'permissions', component: () => import('src/user/pages/RoleListPage.vue') },
      // Gestion totale des cagnottes
      { path: 'pools', component: () => import('src/pool/pages/AdminPoolsPage.vue') },
      // Centre de Support Administrateur (Chat & Tickets)
      { path: 'support', component: () => import('src/support/pages/AdminSupportDesk.vue') }
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
