<template>
  <q-layout view="hHh lpR fFf" style="background: #FFFFFF;">

    <!-- NAVBAR – Dark navy Akkodis exact -->
    <q-header style="background: #0D1B2E; border-bottom: none; box-shadow: 0 2px 8px rgba(0,0,0,0.25);">
      <q-toolbar class="q-px-xl" style="min-height: 64px;">

        <!-- Logo : texte blanc + barre jaune -->
        <q-toolbar-title
          class="row items-center cursor-pointer no-wrap"
          style="max-width: fit-content;"
          @click="$router.push('/')"
        >
          <!-- Barre verticale jaune à gauche du logo (style Akkodis) -->
          <div style="width: 4px; height: 28px; background: #FFB300; margin-right: 10px; border-radius: 2px;"></div>
          <span style="color: #FFFFFF; font-weight: 800; font-size: 1.4rem; letter-spacing: 1px;">POTIFY</span>
        </q-toolbar-title>

        <q-space />

        <!-- Liens de navigation desktop – style Akkodis (blanc, uppercase) -->
        <div class="gt-sm row items-center q-gutter-lg">
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;" @click="$router.push('/')">Accueil</span>
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;" @click="$router.push('/pools')">Explorer</span>
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;">Comment ça marche</span>
        </div>

        <q-space class="gt-sm" />

        <!-- Auth buttons -->
        <div class="row items-center q-gutter-sm">
          <template v-if="!authStore.isAuthenticated.value">
            <q-btn
              flat no-caps
              label="Se connecter"
              class="gt-xs"
              style="color: rgba(255,255,255,0.85); font-weight: 500;"
              to="/login"
            />
            <!-- Bouton S'inscrire = CTA jaune Akkodis -->
            <q-btn
              no-caps
              label="S'inscrire"
              style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 8px 20px;"
              to="/register"
            />
          </template>
          <template v-else>
            <q-btn flat no-caps label="Mon Espace" icon="dashboard" style="color: rgba(255,255,255,0.85); font-weight: 500;" to="/dashboard" />
            <q-btn flat no-caps label="Profil" icon="person" style="color: rgba(255,255,255,0.85); font-weight: 500;" to="/profile" class="gt-sm" />
            <q-btn v-if="authStore.isAdmin.value" flat no-caps label="Administration" style="color: rgba(255,255,255,0.85);" to="/admin" />
            
            <!-- Cloche de Notifications -->
            <q-btn flat round dense icon="notifications" style="color: rgba(255,255,255,0.85); margin-right: 8px;" @click="fetchNotifications">
              <q-badge v-if="unreadCount > 0" color="red" floating>{{ unreadCount }}</q-badge>
              <q-menu style="min-width: 320px; max-height: 400px; border-radius: 8px;" class="q-pa-none">
                <div class="row items-center justify-between q-pa-md bg-grey-2" style="border-bottom: 1px solid #e0e0e0;">
                  <span class="text-weight-bold text-subtitle1">Notifications</span>
                  <span class="text-caption text-grey-7" v-if="unreadCount > 0">{{ unreadCount }} non lue(s)</span>
                </div>
                
                <q-list style="max-height: 300px; overflow-y: auto;">
                  <q-item v-if="notifications.length === 0" class="q-py-md text-center text-grey-6">
                    <q-item-section>Aucune notification</q-item-section>
                  </q-item>
                  <q-item v-for="notif in notifications" :key="notif.id" :class="{'bg-yellow-1': notif.status === 'ACTIVE'}" class="q-py-md" style="border-bottom: 1px solid #f0f0f0;">
                    <q-item-section avatar>
                      <q-icon 
                        :name="notif.type === 'CONTRIBUTION' ? 'monetization_on' : (notif.type === 'MESSAGE' ? 'chat' : (notif.type === 'INVITATION' ? 'person_add' : (notif.type === 'REACTION' ? 'favorite' : 'notifications')))" 
                        :color="notif.type === 'CONTRIBUTION' ? 'green' : (notif.type === 'MESSAGE' ? 'blue' : (notif.type === 'INVITATION' ? 'purple' : (notif.type === 'REACTION' ? 'pink' : 'orange')))" 
                      />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-bold">{{ notif.title }}</q-item-label>
                      <q-item-label caption class="text-grey-9">{{ notif.content }}</q-item-label>
                      <q-item-label caption class="text-grey-5">{{ formatDate(notif.createdAt) }}</q-item-label>
                    </q-item-section>
                    <q-item-section side v-if="notif.status === 'ACTIVE'">
                      <q-btn flat round dense size="sm" icon="check" color="green" @click.stop="markAsRead(notif.id)">
                        <q-tooltip>Marquer comme lu</q-tooltip>
                      </q-btn>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </q-btn>

            <q-btn flat round dense icon="logout" style="color: rgba(255,255,255,0.75);" @click="onLogout">
              <q-tooltip>Se déconnecter</q-tooltip>
            </q-btn>
          </template>
        </div>

      </q-toolbar>
    </q-header>

    <!-- CONTENU -->
    <q-page-container>
      <router-view />

      <!-- FOOTER – dark navy Akkodis avec top-border jaune -->
      <div style="background: #0D1B2E; border-top: 3px solid #FFB300;">
        <div class="q-px-xl q-py-xl" style="max-width: 1200px; margin: 0 auto;">
          <div class="row q-col-gutter-xl">

            <!-- Brand -->
            <div class="col-12 col-md-4">
              <div class="row items-center q-mb-md">
                <div style="width: 4px; height: 24px; background: #FFB300; margin-right: 10px; border-radius: 2px;"></div>
                <span style="color: #FFFFFF; font-weight: 800; font-size: 1.2rem; letter-spacing: 1px;">POTIFY</span>
              </div>
              <p style="color: rgba(255,255,255,0.6); line-height: 1.7; font-size: 0.9rem;">
                Ensemble, donnons vie à ce qui compte. La plateforme de solidarité transparente et facile à utiliser.
              </p>
            </div>

            <!-- À propos -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">À propos</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Notre mission</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Contact</a>
              </div>
            </div>

            <!-- Légal -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">Légal</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Conditions</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Confidentialité</a>
              </div>
            </div>

            <!-- Réseaux sociaux -->
            <div class="col-12 col-md-4 text-right">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">Suivez-nous</div>
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
            © 2026 Potify. Tous droits réservés.
          </div>
        </div>
      </div>
    </q-page-container>

  </q-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import authStore from 'src/shared/stores/auth'
import notificationService from 'src/shared/services/notificationService'
import { date } from 'quasar'

const router = useRouter()

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
/* Hover sur liens navbar */
.nav-link:hover {
  color: #FFB300 !important;
  transition: color 0.2s ease;
}
</style>
