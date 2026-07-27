<template>
  <q-layout view="hHh lpR fFf" :style="$q.dark.isActive ? 'background: #0A111E; color: #FFFFFF;' : 'background: #FFFFFF; color: #1A1A2A;'">

    <!-- NAVBAR – Potify Vivid (Glassmorphic & Responsive) -->
    <q-header 
      :style="{ 
        background: $q.dark.isActive ? 'var(--akkodis-navy)' : 'rgba(255, 255, 255, 0.85)',
        backdropFilter: 'blur(12px)',
        webkitBackdropFilter: 'blur(12px)',
        borderBottom: $q.dark.isActive ? '1px solid rgba(255,255,255,0.08)' : '1px solid rgba(0,0,0,0.06)'
      }" 
      style="box-shadow: 0 4px 20px rgba(0,0,0,0.02);"
    >
      <q-toolbar class="q-px-xl" style="min-height: 70px;">

        <!-- Logo : pure green serif text -->
        <q-toolbar-title
          class="cursor-pointer no-wrap"
          style="max-width: fit-content;"
          @click="$router.push('/')"
        >
          <span :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#054f31' }" style="font-family: 'Playfair Display', serif; font-style: italic; font-weight: 700; font-size: 1.8rem; letter-spacing: -0.5px;">Potify</span>
        </q-toolbar-title>

        <q-space />

        <!-- Liens de navigation desktop centered -->
        <div class="gt-sm row items-center q-gutter-xl">
          <span class="nav-link cursor-pointer" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#1A1A2A' }" style="font-size: 0.95rem; font-weight: 500;" @click="$router.push('/pools')">{{ $t('nav.explore') }}</span>
          <span class="nav-link cursor-pointer" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#1A1A2A' }" style="font-size: 0.95rem; font-weight: 500;" @click="scrollToHowItWorks">{{ $t('nav.howItWorks') }}</span>
          <span class="nav-link cursor-pointer" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#1A1A2A' }" style="font-size: 0.95rem; font-weight: 500;" @click="$router.push('/dashboard')">{{ $t('nav.mySpace') }}</span>
          <span v-if="authStore.hasAdminAccess.value" class="nav-link cursor-pointer" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#1A1A2A' }" style="font-size: 0.95rem; font-weight: 500;" @click="$router.push('/admin')">{{ $t('nav.admin') }}</span>
        </div>

        <q-space />

        <!-- Actions / Bouton Créer une cagnotte -->
        <div class="row items-center q-gutter-md">
          <!-- Connexion / Inscription if not logged in -->
          <template v-if="!authStore.isAuthenticated.value">
            <q-btn
              flat no-caps
              :label="$t('auth.loginTitle')"
              class="btn-nav-flat"
              :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#166534' }"
              style="font-weight: 550; font-size: 0.9rem;"
              to="/login"
            />
            <q-btn
              outline no-caps
              :label="$t('auth.registerTitle')"
              class="btn-nav-outline"
              :style="{
                color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#166534',
                borderColor: $q.dark.isActive ? 'rgba(255,255,255,0.3)' : 'rgba(22,163,74,0.3)'
              }"
              style="font-weight: 550; font-size: 0.9rem; border-radius: 8px;"
              to="/register"
            />
          </template>



          <!-- Notification service for logged-in users -->
          <template v-if="authStore.isAuthenticated.value">
            <q-btn flat round dense icon="notifications" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#166534' }" @click="fetchNotifications">
              <q-badge v-if="unreadCount > 0" color="red" floating>{{ unreadCount }}</q-badge>
              <q-menu :dark="$q.dark.isActive" style="min-width: 320px; max-height: 400px; border-radius: 12px; box-shadow: 0 8px 30px rgba(0,0,0,0.15);" class="q-pa-none">
                <div class="row items-center justify-between q-pa-md" :style="{ background: $q.dark.isActive ? '#162540' : '#f5f5f5', borderBottom: $q.dark.isActive ? '1px solid rgba(255,255,255,0.08)' : '1px solid #e0e0e0' }">
                  <span class="text-weight-bold text-subtitle1">{{ $t('nav.notifications') }}</span>
                  <span class="text-caption text-grey-7" v-if="unreadCount > 0">{{ unreadCount }} {{ $t('nav.noNotifications') }}</span>
                </div>
                
                <q-list style="max-height: 300px; overflow-y: auto;">
                   <q-item v-if="notifications.length === 0" class="q-py-md text-center text-grey-6">
                     <q-item-section>{{ $t('nav.noNotifications') }}</q-item-section>
                   </q-item>
                   <q-item 
                     v-for="notif in notifications" 
                     :key="notif.id" 
                     :class="{'notif-unread': notif.status === 'ACTIVE'}" 
                     class="q-py-md" 
                     :style="{ borderBottom: $q.dark.isActive ? '1px solid rgba(255,255,255,0.08)' : '1px solid #f0f0f0' }"
                   >
                     <q-item-section avatar>
                       <q-icon 
                         :name="notif.type === 'CONTRIBUTION' ? 'monetization_on' : (notif.type === 'MESSAGE' ? 'chat' : (notif.type === 'INVITATION' ? 'person_add' : (notif.type === 'REACTION' ? 'favorite' : 'notifications')))" 
                         :color="notif.type === 'CONTRIBUTION' ? 'green' : (notif.type === 'MESSAGE' ? 'blue' : (notif.type === 'INVITATION' ? 'purple' : (notif.type === 'REACTION' ? 'pink' : 'orange')))" 
                       />
                     </q-item-section>
                     <q-item-section>
                       <q-item-label class="text-weight-bold">{{ notif.title }}</q-item-label>
                       <q-item-label caption :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-9'">{{ notif.content }}</q-item-label>
                       <q-item-label caption class="text-grey-5">{{ formatDate(notif.createdAt) }}</q-item-label>
                     </q-item-section>
                     <q-item-section side v-if="notif.status === 'ACTIVE'">
                       <q-btn flat round dense size="sm" icon="check" color="green" @click.stop="markAsRead(notif.id)">
                         <q-tooltip>{{ $t('nav.markRead') }}</q-tooltip>
                       </q-btn>
                     </q-item-section>
                   </q-item>
                </q-list>
              </q-menu>
            </q-btn>

            <!-- User Menu -->
            <q-btn flat round dense>
              <q-avatar size="32px" style="background: #E8F5E9;">
                <q-img v-if="authStore.user.value?.avatarUrl" :src="authStore.user.value.avatarUrl" style="width: 100%; height: 100%; object-fit: cover;" />
                <span v-else class="text-green-9 text-weight-bold" style="font-size: 0.95rem;">{{ authStore.user.value?.fullName?.charAt(0).toUpperCase() || 'U' }}</span>
              </q-avatar>
              <q-tooltip>{{ $t('nav.profile') }}</q-tooltip>
              <q-menu
                :dark="$q.dark.isActive"
                style="min-width: 250px; border-radius: 12px; box-shadow: 0 8px 30px rgba(0,0,0,0.15);"
                class="q-pa-none"
              >
                <div class="row items-center q-pa-md" :style="{ background: $q.dark.isActive ? '#162540' : '#f5f5f5', borderBottom: $q.dark.isActive ? '1px solid rgba(255,255,255,0.08)' : '1px solid #e0e0e0' }">
                  <q-avatar class="q-mr-sm" size="40px" style="background: #E8F5E9;">
                    <q-img v-if="authStore.user.value?.avatarUrl" :src="authStore.user.value.avatarUrl" style="width: 100%; height: 100%; object-fit: cover;" />
                    <span v-else class="text-green-9 text-weight-bold">{{ authStore.user.value?.fullName?.charAt(0).toUpperCase() || 'U' }}</span>
                  </q-avatar>
                  <div class="column">
                    <span class="text-weight-bold text-subtitle2" :style="{ color: $q.dark.isActive ? '#ffffff' : '#1a1a2a' }">{{ authStore.user.value?.fullName }}</span>
                    <span class="text-caption text-grey-6">{{ authStore.user.value?.email }}</span>
                  </div>
                </div>

                <q-list class="q-py-xs">
                  <q-item clickable v-close-popup to="/profile" class="q-py-md">
                    <q-item-section avatar>
                      <q-icon name="person" color="green" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold">{{ $t('nav.profile') }}</q-item-label>
                      <q-item-label caption>{{ $t('profile.accountSettings') }}</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item clickable v-close-popup to="/dashboard" class="q-py-md">
                    <q-item-section avatar>
                      <q-icon name="dashboard" color="blue" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold">{{ $t('nav.mySpace') }}</q-item-label>
                      <q-item-label caption>Gérer mes cagnottes & dons</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item v-if="authStore.isSupportAgent.value || authStore.hasAdminAccess.value" clickable v-close-popup to="/admin/support" class="q-py-md">
                    <q-item-section avatar>
                      <q-icon name="support_agent" color="teal" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold">Desk Support Client</q-item-label>
                      <q-item-label caption>Gérer les demandes et le chat</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item v-if="authStore.hasAdminAccess.value" clickable v-close-popup to="/admin" class="q-py-md">
                    <q-item-section avatar>
                      <q-icon name="admin_panel_settings" color="amber" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold">{{ $t('nav.admin') }}</q-item-label>
                      <q-item-label caption>Administration du site</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-separator class="q-my-xs" />

                  <q-item clickable v-close-popup @click="onLogout" class="q-py-md text-red">
                    <q-item-section avatar>
                      <q-icon name="logout" color="negative" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold text-negative">{{ $t('nav.logout') }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </q-btn>
          </template>

          <!-- Dark Mode Toggle -->
          <q-btn
            flat round dense
            :icon="$q.dark.isActive ? 'light_mode' : 'dark_mode'"
            :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#054f31' }"
            @click="toggleDarkMode"
          >
            <q-tooltip>{{ $q.dark.isActive ? 'Mode clair' : 'Mode sombre' }}</q-tooltip>
          </q-btn>

          <!-- Language Selector -->
          <q-btn-dropdown
            :key="locale"
            flat no-caps
            :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#054f31' }"
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
                active-class="bg-primary text-white"
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
        </div>

      </q-toolbar>
    </q-header>

    <!-- CONTENU -->
    <q-page-container>
      <router-view />

      <!-- FOOTER – deep violet compact -->
      <div style="background: var(--akkodis-navy); border-top: 1px solid rgba(255, 255, 255, 0.08);">
        <div class="q-px-xl q-py-md" style="max-width: 1200px; margin: 0 auto;">
          <div class="row items-center justify-between q-col-gutter-md">
            <!-- Left: Logo & Copyright -->
            <div class="row items-center q-gutter-x-md">
              <span style="color: #FFFFFF; font-family: 'Playfair Display', serif; font-style: italic; font-weight: 700; font-size: 1.1rem; letter-spacing: -0.5px;">Potify</span>
              <span style="color: rgba(255,255,255,0.4); font-size: 0.8rem;">
                © 2026. {{ $t('footer.rights') }}
              </span>
            </div>

            <!-- Right: Links -->
            <div class="row items-center q-gutter-x-lg text-caption">
              <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.85rem;">{{ $t('footer.ourMission') }}</a>
              <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.85rem;">{{ $t('footer.contact') }}</a>
              <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.85rem;">{{ $t('footer.terms') }}</a>
              <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.85rem;">{{ $t('footer.privacy') }}</a>
            </div>
          </div>
        </div>
      </div>
    </q-page-container>

    <!-- Widget Chat Support & Chatbot IA -->
    <UserSupportWidget />

  </q-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useQuasar } from 'quasar'
import authStore from 'src/shared/stores/auth'
import notificationService from 'src/shared/services/notificationService'
import UserSupportWidget from 'src/support/components/UserSupportWidget.vue'
import { date } from 'quasar'

const { locale } = useI18n()
const router = useRouter()
const $q = useQuasar()

function toggleDarkMode() {
  $q.dark.toggle()
  localStorage.setItem('darkMode', $q.dark.isActive)
}

function scrollToHowItWorks() {
  if (router.currentRoute.value.path !== '/') {
    router.push('/').then(() => {
      setTimeout(() => {
        const el = document.getElementById('how-it-works')
        if (el) el.scrollIntoView({ behavior: 'smooth' })
      }, 100)
    })
  } else {
    const el = document.getElementById('how-it-works')
    if (el) el.scrollIntoView({ behavior: 'smooth' })
  }
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

const notifications = ref([])
const unreadCount = computed(() => {
  return notifications.value.filter(n => n.status === 'ACTIVE').length
})

let intervalId = null

async function fetchNotifications() {
  if (!authStore.isAuthenticated.value || !authStore.user.value?.id) return
  try {
    const data = await notificationService.getUserNotifications(authStore.user.value.id)
    notifications.value = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error('Erreur de chargement des notifications:', error)
  }
}

async function markAsRead(id) {
  try {
    await notificationService.markNotificationRead(id)
    const notif = notifications.value.find(n => n.id === id)
    if (notif) {
      notif.status = 'COMPLETED'
    }
  } catch (error) {
    console.error('Erreur lors du marquage comme lu:', error)
  }
}

function formatDate(isoString) {
  if (!isoString) return ''
  return date.formatDate(new Date(isoString), 'DD/MM/YYYY HH:mm')
}

function onLogout () {
  authStore.logout()
  router.push('/login')
}

watch(
  () => authStore.isAuthenticated.value,
  (isAuth) => {
    if (isAuth) {
      fetchNotifications()
      startPolling()
    } else {
      stopPolling()
      notifications.value = []
    }
  },
  { immediate: true }
)

function startPolling() {
  stopPolling()
  intervalId = setInterval(fetchNotifications, 10000)
}

function stopPolling() {
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
}

onMounted(() => {
  const langCode = locale.value || 'fr'
  if (langCode === 'ar') {
    document.documentElement.setAttribute('dir', 'rtl')
  } else {
    document.documentElement.setAttribute('dir', 'ltr')
  }
  if (authStore.isAuthenticated.value) {
    fetchNotifications()
    startPolling()
  }
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
/* Navigation link animations */
.nav-link {
  position: relative;
  padding: 6px 0;
  transition: color 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #16A34A;
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.nav-link:hover {
  color: #16A34A !important;
}

.nav-link:hover::after {
  width: 100%;
}

/* Premium Navbar Buttons */
.btn-premium-nav {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

.btn-premium-nav:hover {
  transform: translateY(-1px) scale(1.02);
  box-shadow: 0 4px 14px rgba(22, 163, 74, 0.15) !important;
  border-color: #16A34A !important;
}

.btn-nav-flat {
  transition: all 0.2s ease !important;
}

.btn-nav-flat:hover {
  color: #16A34A !important;
  transform: translateY(-1px);
}

.btn-nav-outline {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

.btn-nav-outline:hover {
  background: rgba(22, 163, 74, 0.04) !important;
  border-color: #16A34A !important;
  color: #16A34A !important;
  transform: translateY(-1px);
}
</style>
