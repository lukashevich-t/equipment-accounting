const routes = [
  {
    path: '/',
    component: () => import('layouts/MyLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Search.vue') },
      { path: '/create', component: () => import('pages/Create.vue') },
      { path: '/test', component: () => import('pages/Test.vue') },
      {
        path: '/association',
        component: () => import('pages/association.vue'),
      },
    ],
  },
]

// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('pages/Error404.vue'),
  })
}

export default routes
