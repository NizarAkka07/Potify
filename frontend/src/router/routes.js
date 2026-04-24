const routes = [
  {
    path: '/',
    component: () => import('src/shared/layouts/LandingLayout.vue'),
    children: [
      { path: '', component: () => import('src/landing/pages/IndexPage.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('src/shared/layouts/MainLayout.vue'),
    children: [
      { path: '/users', component: () => import('src/user/pages/UserListPage.vue') },
      { path: '/roles', component: () => import('src/user/pages/RoleListPage.vue') },
      { path: '/permissions', component: () => import('src/user/pages/PermissionListPage.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('src/pages/ErrorNotFound.vue')
  }
]

export default routes
