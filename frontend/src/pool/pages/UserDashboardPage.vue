<template>
  <q-page style="background: #F8F9FA; padding: 40px 0;">
    <div class="container q-mx-auto" style="max-width: 1100px; padding: 0 20px;">

      <!-- Header Dashboard -->
      <div class="row items-center q-mb-xl">
        <q-avatar size="80px" class="q-mr-lg shadow-2">
          <img src="https://cdn.quasar.dev/img/avatar.png">
        </q-avatar>
        <div>
          <h1 class="text-h4 text-weight-bold q-my-none" style="color: #0D1B2E;">Mon Espace</h1>
          <p class="text-subtitle1 text-grey-7">Bienvenue, {{ userName }}. Gérez vos cagnottes et suivez vos contributions.</p>
        </div>
      </div>

      <!-- Onglets -->
      <q-tabs
        v-model="tab"
        dense
        class="text-grey-7 q-mb-lg"
        active-color="primary"
        indicator-color="primary"
        align="left"
        narrow-indicator
        no-caps
        style="font-weight: 600;"
      >
        <q-tab name="pools" label="Mes Cagnottes" />
        <q-tab name="invited" label="Partagées avec moi">
          <q-badge
            v-if="invitedPools.length > 0"
            color="orange"
            floating
            rounded
            :label="invitedPools.length"
          />
        </q-tab>
        <q-tab name="contributions" label="Mes Contributions" />
        <q-tab name="notifications" label="Mes Notifications">
          <q-badge
            v-if="unreadCount > 0"
            color="red"
            floating
            rounded
            :label="unreadCount"
          />
        </q-tab>
      </q-tabs>

      <q-separator class="q-mb-xl" style="background: rgba(0,0,0,0.05);" />

      <!-- Contenu des Onglets -->
      <q-tab-panels v-model="tab" animated style="background: transparent;">

        <!-- PANNEAU : MES CAGNOTTES -->
        <q-tab-panel name="pools" class="q-pa-none">
          <div v-if="loadingPools" class="flex flex-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <div v-else-if="userPools.length > 0" class="row q-col-gutter-lg">
            <div v-for="pool in userPools" :key="pool.id" class="col-12 col-sm-6 col-md-4">
              <q-card class="pool-card shadow-1" @click="$router.push(`/pools/${pool.id}`)">
                <q-img v-if="pool.imageUrl" :src="pool.imageUrl" style="height: 180px;" fit="cover">
                  <div class="absolute-top-left q-ma-sm">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-top-right q-ma-sm">
                    <q-chip :color="getStatusColor(pool.status)" text-white size="sm" class="text-weight-bold">
                      {{ pool.status }}
                    </q-chip>
                  </div>
                </q-img>
                <div v-else class="bg-grey-3 relative-position" style="height: 180px;">
                  <div class="absolute-top-left q-ma-sm">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-top-right q-ma-sm">
                    <q-chip :color="getStatusColor(pool.status)" text-white size="sm" class="text-weight-bold">
                      {{ pool.status }}
                    </q-chip>
                  </div>
                </div>

                <q-card-section class="q-pa-md">
                  <div class="text-subtitle1 text-weight-bold q-mb-xs line-clamp-1" style="color: #0D1B2E;">{{ pool.title }}</div>
                  <div class="row justify-between items-center q-mt-sm">
                    <div class="text-primary text-weight-bolder">{{ pool.currentAmount }} €</div>
                    <div class="text-grey-6 text-caption">Objectif: {{ pool.goalAmount }} €</div>
                  </div>
                  <q-linear-progress :value="pool.currentAmount / pool.goalAmount" color="secondary" class="q-mt-xs" style="height: 6px; border-radius: 3px;" />
                </q-card-section>
              </q-card>
            </div>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="add_circle_outline" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">Vous n'avez pas encore créé de cagnotte</div>
            <q-btn label="Lancer ma première cagnotte" color="primary" class="q-mt-lg" to="/pools/create" unelevated no-caps />
          </div>
        </q-tab-panel>

        <!-- PANNEAU : PARTAGÉES AVEC MOI -->
        <q-tab-panel name="invited" class="q-pa-none">
          <div v-if="loadingInvited" class="flex flex-center q-py-xl">
            <q-spinner-dots color="orange" size="40px" />
          </div>

          <div v-else-if="invitedPools.length > 0" class="row q-col-gutter-lg">
            <div v-for="pool in invitedPools" :key="pool.id" class="col-12 col-sm-6 col-md-4">
              <q-card class="pool-card shadow-1" @click="$router.push(`/pools/${pool.id}`)">
                <q-img v-if="pool.imageUrl" :src="pool.imageUrl" style="height: 180px;" fit="cover">
                  <div class="absolute-top-left q-ma-sm">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-bottom-right q-ma-sm">
                    <q-chip color="orange-7" text-white size="sm" icon="group_add" class="text-weight-bold">
                      Invité
                    </q-chip>
                  </div>
                </q-img>
                <div v-else class="bg-grey-3 relative-position" style="height: 180px;">
                  <div class="absolute-top-left q-ma-sm">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-bottom-right q-ma-sm">
                    <q-chip color="orange-7" text-white size="sm" icon="group_add" class="text-weight-bold">
                      Invité
                    </q-chip>
                  </div>
                </div>

                <q-card-section class="q-pa-md">
                  <div class="text-subtitle1 text-weight-bold q-mb-xs line-clamp-1" style="color: #0D1B2E;">{{ pool.title }}</div>
                  <div class="text-caption text-grey-6 q-mb-sm">
                    <q-icon name="person" size="14px" /> Organisé par <strong>{{ pool.ownerName || 'un ami' }}</strong>
                  </div>
                  <div class="row justify-between items-center q-mt-sm">
                    <div class="text-primary text-weight-bolder">{{ pool.currentAmount }} €</div>
                    <div class="text-grey-6 text-caption">Objectif: {{ pool.goalAmount }} €</div>
                  </div>
                  <q-linear-progress :value="pool.currentAmount / pool.goalAmount" color="orange" class="q-mt-xs" style="height: 6px; border-radius: 3px;" />
                </q-card-section>
              </q-card>
            </div>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="group_add" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">Aucune cagnotte partagée avec vous</div>
            <div class="text-body2 q-mt-sm text-grey-5">Lorsqu'un ami vous invite à sa cagnotte, elle apparaîtra ici.</div>
          </div>
        </q-tab-panel>

        <!-- PANNEAU : MES CONTRIBUTIONS -->
        <q-tab-panel name="contributions" class="q-pa-none">
          <div v-if="loadingContributions" class="flex flex-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <div v-else-if="userContributions.length > 0">
            <q-list class="bg-white shadow-1" style="border-radius: 12px; overflow: hidden;">
              <div v-for="(contrib, index) in userContributions" :key="contrib.id">
                <q-item class="q-py-md">
                  <q-item-section avatar>
                    <q-icon name="favorite" color="red-5" size="24px" />
                  </q-item-section>

                  <q-item-section>
                    <q-item-label class="text-weight-bold" style="color: #0D1B2E;">
                      Don de {{ contrib.amount }} €
                    </q-item-label>
                    <q-item-label caption>
                      {{ formatDate(contrib.createdAt) }} • Statut : <span :class="contrib.status === 'REUSSIE' ? 'text-green' : 'text-orange'">{{ contrib.status }}</span>
                    </q-item-label>
                  </q-item-section>

                  <q-item-section side>
                    <q-btn flat color="primary" label="Détails" size="sm" :to="`/pools/${contrib.poolId}`" no-caps />
                  </q-item-section>
                </q-item>
                <q-separator v-if="index < userContributions.length - 1" inset="item" />
              </div>
            </q-list>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="volunteer_activism" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">Vous n'avez pas encore fait de don</div>
            <q-btn label="Découvrir les cagnottes" color="primary" class="q-mt-lg" to="/pools" unelevated no-caps />
          </div>
        </q-tab-panel>

        <!-- PANNEAU : NOTIFICATIONS -->
        <q-tab-panel name="notifications" class="q-pa-none">
          <div v-if="loadingNotifications" class="flex flex-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <div v-else-if="userNotifications.length > 0">
            <div class="row justify-between items-center q-mb-md q-px-sm">
              <div class="text-subtitle2 text-grey-7">Dernières notifications reçues</div>
              <q-btn flat dense no-caps color="primary" label="Tout marquer comme lu" v-if="unreadCount > 0" @click="markAllAsRead" />
            </div>

            <q-list class="bg-white shadow-1" style="border-radius: 12px; overflow: hidden;">
              <div v-for="(notif, index) in userNotifications" :key="notif.id">
                <q-item class="q-py-md" :class="{'bg-blue-1': notif.status === 'ACTIVE'}">
                  <q-item-section avatar>
                    <q-avatar :color="notif.status === 'ACTIVE' ? 'blue-2' : 'grey-2'" size="40px">
                      <q-icon 
                        :name="notif.type === 'CONTRIBUTION' ? 'monetization_on' : (notif.type === 'MESSAGE' ? 'chat' : (notif.type === 'INVITATION' ? 'person_add' : (notif.type === 'REACTION' ? 'favorite' : 'notifications')))" 
                        :color="notif.type === 'CONTRIBUTION' ? 'green' : (notif.type === 'MESSAGE' ? 'blue' : (notif.type === 'INVITATION' ? 'purple' : (notif.type === 'REACTION' ? 'pink' : 'orange')))" 
                      />
                    </q-avatar>
                  </q-item-section>

                  <q-item-section>
                    <q-item-label class="text-weight-bold" style="color: #0D1B2E;">
                      {{ notif.title }}
                    </q-item-label>
                    <q-item-label class="text-grey-8 q-mt-xs">{{ notif.content }}</q-item-label>
                    <q-item-label caption class="text-grey-5 q-mt-xs">
                      {{ formatNotificationDate(notif.createdAt) }}
                    </q-item-label>
                  </q-item-section>

                  <q-item-section side v-if="notif.status === 'ACTIVE'">
                    <q-btn flat round dense size="sm" icon="check" color="green" @click.stop="markNotificationAsRead(notif.id)">
                      <q-tooltip>Marquer comme lu</q-tooltip>
                    </q-btn>
                  </q-item-section>
                </q-item>
                <q-separator v-if="index < userNotifications.length - 1" inset="item" />
              </div>
            </q-list>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="notifications_none" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">Aucune notification pour le moment</div>
            <div class="text-body2 q-mt-sm text-grey-5">Vous recevrez des alertes ici lors de nouvelles activités.</div>
          </div>
        </q-tab-panel>

      </q-tab-panels>

    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { poolApi } from 'boot/axios'
import { poolService } from 'src/shared/services/poolService'
import authStore from 'src/shared/stores/auth'

const tab = ref('pools')
const userName = computed(() => authStore.user.value?.firstName || 'Utilisateur')
const userId = computed(() => authStore.user.value?.id)
const userEmail = computed(() => authStore.user.value?.email || '')

const userPools = ref([])
const invitedPools = ref([])
const userContributions = ref([])
const loadingPools = ref(false)
const loadingInvited = ref(false)
const loadingContributions = ref(false)

// Notifications state
import notificationService from 'src/shared/services/notificationService'
const userNotifications = ref([])
const loadingNotifications = ref(false)
const unreadCount = computed(() => {
  return userNotifications.value.filter(n => n.status === 'ACTIVE').length
})

// Récupère les cagnottes créées par l'utilisateur (dont il est propriétaire)
const fetchUserPools = async () => {
  if (!userId.value) return
  loadingPools.value = true
  try {
    const response = await poolApi.get(`/pools/user/${userId.value}`, {
      params: { email: userEmail.value }
    })
    // On garde uniquement les cagnottes principales dont il est propriétaire
    userPools.value = response.data.filter(p => p.ownerId === userId.value && !p.parentId)
  } catch (error) {
    console.error('Erreur chargement mes cagnottes:', error)
  } finally {
    loadingPools.value = false
  }
}

// Récupère les cagnottes auxquelles l'utilisateur a été invité
const fetchInvitedPools = async () => {
  if (!userId.value) return
  loadingInvited.value = true
  try {
    const response = await poolApi.get(`/pools/user/${userId.value}/invited`, {
      params: { email: userEmail.value }
    })
    invitedPools.value = response.data.filter(p => !p.parentId)
  } catch (error) {
    console.error('Erreur chargement cagnottes partagées:', error)
  } finally {
    loadingInvited.value = false
  }
}

const fetchUserContributions = async () => {
  if (!userId.value) return
  loadingContributions.value = true
  try {
    const response = await poolService.getUserContributions(userId.value)
    userContributions.value = response.data
  } catch (error) {
    console.error("Erreur chargement contributions:", error)
  } finally {
    loadingContributions.value = false
  }
}

const fetchUserNotifications = async () => {
  if (!userId.value) return
  loadingNotifications.value = true
  try {
    const data = await notificationService.getUserNotifications(userId.value)
    userNotifications.value = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error('Erreur chargement notifications:', error)
  } finally {
    loadingNotifications.value = false
  }
}

const markNotificationAsRead = async (id) => {
  try {
    await notificationService.markNotificationRead(id)
    const notif = userNotifications.value.find(n => n.id === id)
    if (notif) {
      notif.status = 'COMPLETED'
    }
  } catch (error) {
    console.error('Erreur marquage notification:', error)
  }
}

const markAllAsRead = async () => {
  const activeNotifs = userNotifications.value.filter(n => n.status === 'ACTIVE')
  for (const notif of activeNotifs) {
    await markNotificationAsRead(notif.id)
  }
}

const formatNotificationDate = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleString('fr-FR', {
    day: 'numeric',
    month: 'long',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusColor = (status) => {
  switch (status) {
    case 'PUBLIEE': return 'green-7'
    case 'EN_COURS': return 'blue-7'
    case 'TERMINEE': return 'grey-7'
    default: return 'orange-7'
  }
}

const getTypeColor = (type) => {
  switch (type) {
    case 'PUBLIC': return 'blue-6'
    case 'PRIVATE': return 'purple-6'
    case 'PRIVATE_TONTINE': return 'orange-8'
    default: return 'grey-6'
  }
}

const getTypeText = (type) => {
  switch (type) {
    case 'PUBLIC': return 'Publique'
    case 'PRIVATE': return 'Privée'
    case 'PRIVATE_TONTINE': return 'Tontine'
    default: return type
  }
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

onMounted(() => {
  if (userId.value) {
    fetchUserPools()
    fetchInvitedPools()
    fetchUserContributions()
    fetchUserNotifications()
  }
})

watch(userId, (newId) => {
  if (newId) {
    fetchUserPools()
    fetchInvitedPools()
    fetchUserContributions()
    fetchUserNotifications()
  }
}, { immediate: true })
</script>

<style scoped>
.pool-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid rgba(0,0,0,0.05);
}
.pool-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12) !important;
}
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
