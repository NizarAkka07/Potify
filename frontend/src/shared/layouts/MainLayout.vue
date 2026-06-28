<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated style="background: var(--akkodis-navy); border-bottom: 3px solid var(--akkodis-yellow);">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
          style="color: var(--akkodis-yellow);"
        />

        <q-toolbar-title class="row items-center cursor-pointer no-wrap" style="color: #FFFFFF; font-weight: 700;" @click="$router.push('/')">
          <img src="~assets/logo.png" style="height: 32px; margin-right: 10px; object-fit: contain;" alt="Potify Logo" />
          <span style="color: #FFFFFF; font-weight: 800; font-size: 1.2rem; letter-spacing: 1px;">POTIFY Admin</span>
        </q-toolbar-title>

        <div style="color: rgba(255,255,255,0.6);" class="gt-xs">Microservice User</div>

        <!-- Dark Mode Toggle -->
        <q-btn
          flat round dense
          :icon="$q.dark.isActive ? 'light_mode' : 'dark_mode'"
          style="color: rgba(255,255,255,0.85);"
          class="q-ml-md"
          @click="toggleDarkMode"
        >
          <q-tooltip>{{ $q.dark.isActive ? 'Mode clair' : 'Mode sombre' }}</q-tooltip>
        </q-btn>

        <!-- Language Selector -->
        <q-btn-dropdown
          :key="locale"
          flat no-caps
          class="text-white q-ml-md"
          :label="currentLangLabel"
          content-style="background: var(--akkodis-navy-mid); border: 1px solid rgba(255,255,255,0.15); border-radius: 8px;"
        >
          <q-list style="min-width: 150px; background: var(--akkodis-navy-mid); color: white;">
            <q-item
              v-for="lang in langs"
              :key="lang.value"
              clickable
              v-close-popup
              @click="changeLanguage(lang.value)"
              :active="locale === lang.value"
              active-class="bg-primary text-dark"
              style="border-radius: 4px;"
            >
              <q-item-section avatar style="min-width: auto; padding-right: 8px;">
                {{ lang.flag }}
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-weight-bold">{{ lang.label }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-btn-dropdown>

        <q-btn
          flat
          round
          dense
          icon="logout"
          class="q-ml-md"
          @click="onLogout"
        >
          <q-tooltip>{{ $t('nav.logout') }}</q-tooltip>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
    >
      <q-list>
        <q-item-label header>
          {{ $t('dashboardLayout.title') }}
        </q-item-label>

        <q-item clickable v-ripple to="/admin" exact v-if="authStore.hasAdminAccess.value">
          <q-item-section avatar>
            <q-icon name="dashboard" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('adminDashboard.title') }}</q-item-label>
            <q-item-section caption>{{ $t('dashboardLayout.generalStats') }}</q-item-section>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/admin/users" exact v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value">
          <q-item-section avatar>
            <q-icon name="people" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('dashboardLayout.users') }}</q-item-label>
            <q-item-section caption>{{ $t('dashboardLayout.accountMgmt') }}</q-item-section>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/admin/pools" exact v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPoolAdmin.value || authStore.isModerator.value">
          <q-item-section avatar>
            <q-icon :name="authStore.isModerator.value && !(authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPoolAdmin.value) ? 'chat' : 'account_balance_wallet'" />
          </q-item-section>
          <q-item-section>
            <q-item-label v-if="authStore.isModerator.value && !(authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPoolAdmin.value)">Messages</q-item-label>
            <q-item-label v-else>{{ $t('dashboardLayout.pools') }}</q-item-label>
            
            <q-item-section caption v-if="authStore.isModerator.value && !(authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPoolAdmin.value)">Gestion des messages</q-item-section>
            <q-item-section caption v-else>{{ $t('dashboardLayout.poolMgmt') }}</q-item-section>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/admin/roles" exact v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value">
          <q-item-section avatar>
            <q-icon name="security" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('dashboardLayout.roles') }}</q-item-label>
            <q-item-section caption>{{ $t('dashboardLayout.roleMgmt') }}</q-item-section>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/admin/permissions" exact v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value">
          <q-item-section avatar>
            <q-icon name="vpn_key" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('dashboardLayout.permissions') }}</q-item-label>
            <q-item-section caption>{{ $t('dashboardLayout.permMgmt') }}</q-item-section>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useQuasar } from 'quasar'
import authStore from 'src/shared/stores/auth'

const { locale } = useI18n()
const router = useRouter()
const $q = useQuasar()
const leftDrawerOpen = ref(false)

function toggleDarkMode() {
  $q.dark.toggle()
  localStorage.setItem('darkMode', $q.dark.isActive)
}

const langs = [
  { value: 'fr', label: 'Français', flag: '🇫🇷' },
  { value: 'en', label: 'English', flag: '🇬🇧' },
  { value: 'es', label: 'Español', flag: '🇪🇸' },
  { value: 'ar', label: 'العربية', flag: '🇸🇦' }
]

const currentLangLabel = computed(() => {
  const found = langs.find(l => l.value === locale.value)
  return found ? `${found.flag} ${found.label}` : locale.value.toUpperCase()
})

function changeLanguage(langCode) {
  locale.value = langCode
  localStorage.setItem('lang', langCode)
  if (langCode === 'ar') {
    document.documentElement.setAttribute('dir', 'rtl')
  } else {
    document.documentElement.setAttribute('dir', 'ltr')
  }
}

function toggleLeftDrawer () {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function onLogout () {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  const langCode = locale.value || 'fr'
  if (langCode === 'ar') {
    document.documentElement.setAttribute('dir', 'rtl')
  } else {
    document.documentElement.setAttribute('dir', 'ltr')
  }
})
</script>

