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

            <q-btn flat round dense icon="logout" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.85)' : '#166534' }" @click="onLogout">
              <q-tooltip>{{ $t('nav.logout') }}</q-tooltip>
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

      <!-- FOOTER – deep violet avec top-border violet -->
      <div style="background: var(--akkodis-navy); border-top: 3px solid var(--akkodis-yellow);">
        <div class="q-px-xl q-py-xl" style="max-width: 1200px; margin: 0 auto;">
          <div class="row q-col-gutter-xl">

            <!-- Brand -->
            <div class="col-12 col-md-4">
              <div class="row items-center q-mb-md">
                <img src="~assets/logo.png" style="height: 32px; margin-right: 10px; object-fit: contain;" alt="Potify Logo" />
                <span style="color: #FFFFFF; font-family: 'Playfair Display', serif; font-style: italic; font-weight: 700; font-size: 1.3rem; letter-spacing: -0.5px;">Potify</span>
              </div>
              <p style="color: rgba(255,255,255,0.6); line-height: 1.7; font-size: 0.9rem;">
                {{ $t('home.subtitle') }}
              </p>
            </div>

            <!-- À propos -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: var(--akkodis-yellow); font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">{{ $t('footer.about') }}</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">{{ $t('footer.ourMission') }}</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">{{ $t('footer.contact') }}</a>
              </div>
            </div>

            <!-- Légal -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: var(--akkodis-yellow); font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">{{ $t('footer.legal') }}</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">{{ $t('footer.terms') }}</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">{{ $t('footer.privacy') }}</a>
              </div>
            </div>

            <!-- Réseaux sociaux -->
            <div class="col-12 col-md-4 text-right">
              <div class="text-weight-bold q-mb-md" style="color: var(--akkodis-yellow); font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">{{ $t('footer.followUs') }}</div>
              <div class="row justify-end q-gutter-sm">
                <q-btn round flat icon="fab fa-linkedin" style="color: rgba(255,255,255,0.7);" />
                <q-btn round flat icon="fab fa-twitter" style="color: rgba(255,255,255,0.7);" />
                <q-btn round flat icon="fab fa-instagram" style="color: rgba(255,255,255,0.7);" />
              </div>
            </div>
          </div>

          <!-- Séparateur + copyright -->
          <q-separator style="background: rgba(255,255,255,0.1); margin: 24px 0;" />
          <div class="text-center" style="color: rgba(255,255,255,0.4); font-size: 0.85rem;">
            © 2026 Potify. {{ $t('footer.rights') }}
          </div>
        </div>
      </div>
    </q-page-container>

  </q-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useQuasar } from 'quasar'
import authStore from 'src/shared/stores/auth'
import notificationService from 'src/shared/services/notificationService'
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
