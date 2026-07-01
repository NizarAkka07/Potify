<template>
  <q-page style="background: #F8F9FA; padding: 40px 0;">
    <div class="container q-mx-auto" style="max-width: 1100px; padding: 0 20px;">

      <!-- Header Dashboard -->
      <div class="row items-center q-mb-xl">
        <q-avatar size="80px" class="q-mr-lg shadow-2" style="background: #FFB300;">
          <img v-if="userAvatarUrl" :src="userAvatarUrl" style="width: 100%; height: 100%; object-fit: cover;">
          <q-icon v-else name="person" size="40px" color="white" />
        </q-avatar>
        <div>
          <h1 class="text-h4 text-weight-bold q-my-none" style="color: #0D1B2E;">{{ $t('dashboard.mySpace') }}</h1>
          <p class="text-subtitle1 text-grey-7">{{ $t('dashboard.welcome', { name: userName }) }}</p>
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
        <q-tab name="pools" :label="$t('dashboard.myPools')" />
        <q-tab name="invited" :label="$t('dashboard.sharedWithMe')">
          <q-badge
            v-if="invitedPools.length > 0"
            color="orange"
            floating
            rounded
            :label="invitedPools.length"
          />
        </q-tab>
        <q-tab name="contributions" :label="$t('dashboard.myContributions')" />
        <q-tab name="stats" :label="$t('dashboard.myStats')" />
        <q-tab name="notifications" :label="$t('dashboard.myNotifications')">
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
                  <div class="absolute-top-left q-ma-sm" style="background: transparent; padding: 0;">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-top-right q-ma-sm" style="background: transparent; padding: 0;">
                    <q-chip :color="getStatusColor(pool.status)" text-white size="sm" class="text-weight-bold">
                      {{ getStatusText(pool.status) }}
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
                      {{ getStatusText(pool.status) }}
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

                <q-separator />

                <q-card-actions align="right" class="q-py-xs">
                  <q-btn flat dense color="warning" icon="edit" :label="$t('dashboard.edit')" @click.stop="$router.push(`/pools/${pool.id}/edit`)" no-caps />
                  <q-btn flat dense color="primary" icon="visibility" :label="$t('dashboard.view')" @click.stop="$router.push(`/pools/${pool.id}`)" no-caps />
                </q-card-actions>
              </q-card>
            </div>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="add_circle_outline" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">{{ $t('dashboard.noPoolsCreated') }}</div>
            <q-btn :label="$t('dashboard.launchFirstPool')" color="primary" class="q-mt-lg" to="/pools/create" unelevated no-caps />
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
                  <div class="absolute-top-left q-ma-sm" style="background: transparent; padding: 0;">
                    <q-chip :color="getTypeColor(pool.type)" text-white size="sm" class="text-weight-bold">
                      {{ getTypeText(pool.type) }}
                    </q-chip>
                  </div>
                  <div class="absolute-bottom-right q-ma-sm" style="background: transparent; padding: 0;">
                    <q-chip color="orange-7" text-white size="sm" icon="group_add" class="text-weight-bold">
                      {{ $t('dashboard.invited') }}
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
                      {{ $t('dashboard.invited') }}
                    </q-chip>
                  </div>
                </div>

                <q-card-section class="q-pa-md">
                  <div class="text-subtitle1 text-weight-bold q-mb-xs line-clamp-1" style="color: #0D1B2E;">{{ pool.title }}</div>
                  <div class="text-caption text-grey-6 q-mb-sm">
                    <q-icon name="person" size="14px" /> {{ $t('dashboard.organizedBy') }} <strong>{{ pool.ownerName || 'un ami' }}</strong>
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
            <div class="text-h6 q-mt-md">{{ $t('dashboard.noPoolsShared') }}</div>
            <div class="text-body2 q-mt-sm text-grey-5">{{ $t('dashboard.sharedInvitationDesc') }}</div>
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
                      {{ $t('dashboard.donationOf', { amount: contrib.amount }) }} {{ $t('dashboard.for') }} <span class="text-primary">{{ getPoolTitle(contrib.poolId) }}</span>
                    </q-item-label>
                    <q-item-label caption>
                      {{ $t('dashboard.category') }} : <strong>{{ getPoolCategory(contrib.poolId) }}</strong> • {{ formatDate(contrib.createdAt) }} • {{ $t('dashboard.status') }} : <span :class="(contrib.status === 'REUSSIE' || contrib.status === 'CONFIRMED') ? 'text-green text-weight-bold' : 'text-orange'">{{ contrib.status === 'CONFIRMED' ? $t('dashboard.confirmed') : contrib.status }}</span>
                    </q-item-label>
                  </q-item-section>

                  <q-item-section side>
                    <div class="row q-gutter-xs">
                      <q-btn flat color="secondary" icon="picture_as_pdf" :label="$t('dashboard.pdfReceipt')" size="sm" @click="downloadReceipt(contrib)" no-caps />
                      <q-btn flat color="primary" :label="$t('dashboard.details')" size="sm" :to="`/pools/${contrib.poolId}`" no-caps />
                    </div>
                  </q-item-section>
                </q-item>
                <q-separator v-if="index < userContributions.length - 1" inset="item" />
              </div>
            </q-list>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="volunteer_activism" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">{{ $t('dashboard.noDonationsMade') }}</div>
            <q-btn :label="$t('dashboard.discoverPools')" color="primary" class="q-mt-lg" to="/pools" unelevated no-caps />
          </div>
        </q-tab-panel>

        <!-- PANNEAU : STATISTIQUES & GRAPHIPHES -->
        <q-tab-panel name="stats" class="q-pa-none">
          <div v-if="loadingStats || loadingPools || loadingContributions" class="flex flex-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <div v-else>
            <!-- KPI CARDS -->
            <div class="row q-col-gutter-md q-mb-xl">
              <!-- Organisateur KPIs -->
              <div class="col-12 col-sm-4">
                <q-card class="bg-white shadow-1 q-pa-md text-center" style="border-radius: 12px; border-left: 4px solid #0D1B2E;">
                  <div class="text-subtitle2 text-grey-6 uppercase">{{ $t('dashboard.totalCollected') }}</div>
                  <div class="text-h4 text-weight-bolder text-primary q-mt-sm">{{ organizerTotalAmount }} €</div>
                  <div class="text-caption text-grey-5 q-mt-xs">{{ userPools.length }} {{ $t('dashboard.poolsCreated') }}</div>
                </q-card>
              </div>
              <div class="col-12 col-sm-4">
                <q-card class="bg-white shadow-1 q-pa-md text-center" style="border-radius: 12px; border-left: 4px solid #FFA726;">
                  <div class="text-subtitle2 text-grey-6 uppercase">{{ $t('dashboard.uniqueContributors') }}</div>
                  <div class="text-h4 text-weight-bolder text-secondary q-mt-sm">{{ organizerUniqueContributors }}</div>
                  <div class="text-caption text-grey-5 q-mt-xs">{{ $t('dashboard.onActivePools') }}</div>
                </q-card>
              </div>
              <!-- Donateur KPI -->
              <div class="col-12 col-sm-4">
                <q-card class="bg-white shadow-1 q-pa-md text-center" style="border-radius: 12px; border-left: 4px solid #E12D3D;">
                  <div class="text-subtitle2 text-grey-6 uppercase">{{ $t('dashboard.myTotalDonations') }}</div>
                  <div class="text-h4 text-weight-bolder text-red q-mt-sm">{{ donorTotalAmount }} €</div>
                  <div class="text-caption text-grey-5 q-mt-xs">{{ userContributions.length }} {{ $t('dashboard.donationsMade') }}</div>
                </q-card>
              </div>
            </div>

            <!-- CHARTS SECTION -->
            <div class="row q-col-gutter-lg">
              
              <!-- Courbe de progression (Organisateur) -->
              <div class="col-12 col-md-8">
                <q-card class="bg-white shadow-1 q-pa-lg" style="border-radius: 12px;">
                  <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E;">
                    <q-icon name="trending_up" color="primary" size="24px" class="q-mr-sm" />
                    {{ $t('dashboard.cumulativeProgression') }}
                  </div>
                  <div v-if="organizerContributions.length === 0" class="flex flex-center q-py-xl text-grey-5">
                    {{ $t('dashboard.noDonationsToChart') }}
                  </div>
                  <div v-else>
                    <VueApexCharts type="area" height="300" :options="progressionChartOptions" :series="progressionChartSeries" />
                  </div>
                </q-card>
              </div>

              <!-- Répartition par catégorie (Donateur) -->
              <div class="col-12 col-md-4">
                <q-card class="bg-white shadow-1 q-pa-lg" style="border-radius: 12px; height: 100%;">
                  <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E;">
                    <q-icon name="pie_chart" color="red" size="24px" class="q-mr-sm" />
                    {{ $t('dashboard.myDonationsByCategory') }}
                  </div>
                  <div v-if="userContributions.length === 0" class="flex flex-center q-py-xl text-grey-5">
                    {{ $t('dashboard.makeDonationsToChart') }}
                  </div>
                  <div v-else class="flex flex-center">
                    <VueApexCharts type="donut" width="100%" max-width="320px" :options="categoryChartOptions" :series="categoryChartSeries" />
                  </div>
                </q-card>
              </div>

              <!-- Répartition des montants (Organisateur) -->
              <div class="col-12 col-md-12 q-mt-md">
                <q-card class="bg-white shadow-1 q-pa-lg" style="border-radius: 12px;">
                  <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E;">
                    <q-icon name="bar_chart" color="secondary" size="24px" class="q-mr-sm" />
                    {{ $t('dashboard.donationsByBracket') }}
                  </div>
                  <div v-if="organizerContributions.length === 0" class="flex flex-center q-py-xl text-grey-5">
                    {{ $t('dashboard.noDonationReceivedYet') }}
                  </div>
                  <div v-else>
                    <VueApexCharts type="bar" height="300" :options="bracketChartOptions" :series="bracketChartSeries" />
                  </div>
                </q-card>
              </div>

            </div>
          </div>
        </q-tab-panel>

        <!-- PANNEAU : NOTIFICATIONS -->
        <q-tab-panel name="notifications" class="q-pa-none">
          <div v-if="loadingNotifications" class="flex flex-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <div v-else-if="activeNotifications.length > 0">
            <div class="row justify-between items-center q-mb-md q-px-sm">
              <div class="text-subtitle2 text-grey-7">{{ $t('dashboard.latestNotifications') }}</div>
              <q-btn flat dense no-caps color="primary" :label="$t('dashboard.markAllAsRead')" v-if="unreadCount > 0" @click="markAllAsRead" />
            </div>

            <q-list class="bg-white shadow-1" style="border-radius: 12px; overflow: hidden;">
              <div v-for="(notif, index) in activeNotifications" :key="notif.id">
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
                <q-separator v-if="index < activeNotifications.length - 1" inset="item" />
              </div>
            </q-list>
          </div>

          <div v-else class="column items-center justify-center q-py-xl text-center text-grey-6 bg-white shadow-1" style="border-radius: 12px; border: 1px dashed #CCC;">
            <q-icon name="notifications_none" size="64px" color="grey-3" />
            <div class="text-h6 q-mt-md">{{ $t('dashboard.noNotificationsYet') }}</div>
            <div class="text-body2 q-mt-sm text-grey-5">{{ $t('dashboard.noNotificationsDesc') }}</div>
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
import VueApexCharts from 'vue3-apexcharts'
import { jsPDF } from 'jspdf'

const tab = ref('pools')
const userName = computed(() => authStore.user.value?.fullName || authStore.user.value?.firstName || 'Utilisateur')
const userAvatarUrl = computed(() => authStore.user.value?.avatarUrl)
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
const activeNotifications = computed(() => {
  return userNotifications.value.filter(n => n.status === 'ACTIVE')
})

// Statistiques
const allPools = ref([])
const organizerContributions = ref([])
const loadingStats = ref(false)

const getPoolTitle = (poolId) => {
  const pool = allPools.value.find(p => p.id === poolId)
  return pool ? pool.title : 'Cagnotte #' + poolId.substring(0, 8)
}

const getPoolCategory = (poolId) => {
  const pool = allPools.value.find(p => p.id === poolId)
  return pool ? pool.category : 'Autre'
}

const fetchAllPools = async () => {
  try {
    const res = await poolService.getAllPools()
    allPools.value = res.data || []
  } catch (error) {
    console.error('Erreur chargement de la liste des cagnottes:', error)
  }
}

const fetchStatsData = async () => {
  if (!userId.value) return
  loadingStats.value = true
  try {
    if (allPools.value.length === 0) {
      await fetchAllPools()
    }
    
    const poolContributionsPromises = userPools.value.map(async (pool) => {
      try {
        const res = await poolService.getPoolContributions(pool.id)
        return (res.data || []).map(c => ({
          ...c,
          poolTitle: pool.title,
          poolCategory: pool.category
        }))
      } catch (err) {
        console.error(`Error fetching contributions for pool ${pool.id}:`, err)
        return []
      }
    })
    
    const resolvedContributions = await Promise.all(poolContributionsPromises)
    organizerContributions.value = resolvedContributions.flat().filter(c => c.status === 'CONFIRMED' || c.status === 'REUSSIE')
  } catch (error) {
    console.error('Error fetching statistics data:', error)
  } finally {
    loadingStats.value = false
  }
}

// Computations for KPIs
const organizerTotalAmount = computed(() => {
  return organizerContributions.value.reduce((sum, c) => sum + Number(c.amount), 0)
})

const organizerUniqueContributors = computed(() => {
  const emails = new Set(organizerContributions.value.map(c => c.contributorEmail || c.contributorName).filter(Boolean))
  return emails.size
})

const donorTotalAmount = computed(() => {
  return userContributions.value.reduce((sum, c) => sum + Number(c.amount), 0)
})

// Chart 1: Progression Cumulative (Organisateur)
const progressionChartData = computed(() => {
  if (organizerContributions.value.length === 0) return { categories: [], data: [] }
  const sorted = [...organizerContributions.value].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  const groupedByDate = {}
  sorted.forEach(c => {
    const dateStr = new Date(c.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
    groupedByDate[dateStr] = (groupedByDate[dateStr] || 0) + Number(c.amount)
  })
  const categories = Object.keys(groupedByDate)
  const data = []
  let cumSum = 0
  categories.forEach(date => {
    cumSum += groupedByDate[date]
    data.push(cumSum)
  })
  return { categories, data }
})

const progressionChartSeries = computed(() => {
  return [{
    name: 'Fonds Cumulés',
    data: progressionChartData.value.data
  }]
})

const progressionChartOptions = computed(() => {
  return {
    chart: {
      id: 'progression-chart',
      toolbar: { show: false },
      fontFamily: 'Inter, sans-serif'
    },
    colors: ['#0D1B2E'],
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.45,
        opacityTo: 0.05,
        stops: [0, 100]
      }
    },
    stroke: { curve: 'smooth', width: 3 },
    xaxis: {
      categories: progressionChartData.value.categories,
      labels: { style: { colors: '#5A6A85', fontSize: '11px' } }
    },
    yaxis: {
      labels: {
        formatter: (val) => `${val} €`,
        style: { colors: '#5A6A85', fontSize: '11px' }
      }
    },
    dataLabels: { enabled: false },
    tooltip: { theme: 'light' }
  }
})

// Chart 2: Répartition par Tranches de Montants (Organisateur)
const bracketChartData = computed(() => {
  const counts = { '0-20€': 0, '21-50€': 0, '51-100€': 0, '101-250€': 0, '250€+': 0 }
  organizerContributions.value.forEach(c => {
    const amt = Number(c.amount)
    if (amt <= 20) counts['0-20€']++
    else if (amt <= 50) counts['21-50€']++
    else if (amt <= 100) counts['51-100€']++
    else if (amt <= 250) counts['101-250€']++
    else counts['250€+']++
  })
  return counts
})

const bracketChartSeries = computed(() => {
  return [{
    name: 'Nombre de dons',
    data: Object.values(bracketChartData.value)
  }]
})

const bracketChartOptions = computed(() => {
  return {
    chart: {
      id: 'bracket-chart',
      toolbar: { show: false },
      fontFamily: 'Inter, sans-serif'
    },
    colors: ['#FFA726'],
    plotOptions: {
      bar: {
        borderRadius: 4,
        columnWidth: '50%'
      }
    },
    xaxis: {
      categories: Object.keys(bracketChartData.value),
      labels: { style: { colors: '#5A6A85', fontSize: '11px' } }
    },
    yaxis: {
      labels: {
        formatter: (val) => Math.round(val),
        style: { colors: '#5A6A85', fontSize: '11px' }
      }
    },
    dataLabels: { enabled: false }
  }
})

// Chart 3: Répartition par Catégorie (Donateur)
const categoryChartData = computed(() => {
  const catTotals = {}
  userContributions.value.forEach(c => {
    const cat = getPoolCategory(c.poolId)
    catTotals[cat] = (catTotals[cat] || 0) + Number(c.amount)
  })
  return catTotals
})

const categoryChartSeries = computed(() => {
  return Object.values(categoryChartData.value)
})

const categoryChartOptions = computed(() => {
  return {
    chart: {
      id: 'category-chart',
      fontFamily: 'Inter, sans-serif'
    },
    labels: Object.keys(categoryChartData.value),
    colors: ['#0D1B2E', '#FFA726', '#E12D3D', '#2EC4B6', '#7209B7'],
    legend: {
      position: 'bottom',
      labels: { colors: '#5A6A85' }
    },
    dataLabels: {
      formatter: (val) => `${Math.round(val)}%`
    },
    tooltip: {
      y: { formatter: (val) => `${val} €` }
    }
  }
})

// Download Receipt PDF Function
const downloadReceipt = (contrib) => {
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4'
  })

  const primaryColor = [13, 27, 46]
  const secondaryColor = [255, 167, 38]

  // Header Band
  doc.setFillColor(primaryColor[0], primaryColor[1], primaryColor[2])
  doc.rect(0, 0, 210, 40, 'F')

  doc.setFillColor(secondaryColor[0], secondaryColor[1], secondaryColor[2])
  doc.rect(0, 40, 210, 3, 'F')

  doc.setTextColor(255, 255, 255)
  doc.setFont('helvetica', 'bold')
  doc.setFontSize(28)
  doc.text('POTIFY', 20, 22)
  
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(10)
  doc.text('Financement Participatif Solidaire', 20, 30)

  // Document Title
  doc.setTextColor(primaryColor[0], primaryColor[1], primaryColor[2])
  doc.setFont('helvetica', 'bold')
  doc.setFontSize(20)
  doc.text('RECU DE CONTRIBUTION FINANCIERE', 20, 65)

  doc.setFont('helvetica', 'normal')
  doc.setFontSize(10)
  doc.setTextColor(100, 100, 100)
  doc.text(`Reference du don : ${contrib.id}`, 20, 72)
  doc.text(`Date de transaction : ${formatDate(contrib.createdAt)}`, 20, 77)

  doc.setDrawColor(220, 220, 220)
  doc.line(20, 85, 190, 85)

  // Donor Info
  doc.setTextColor(primaryColor[0], primaryColor[1], primaryColor[2])
  doc.setFont('helvetica', 'bold')
  doc.setFontSize(12)
  doc.text('INFORMATIONS DU DONATEUR', 20, 95)

  doc.setFont('helvetica', 'normal')
  doc.setFontSize(11)
  doc.text(`Nom : ${contrib.contributorName || userName.value}`, 20, 103)
  doc.text(`Email : ${contrib.contributorEmail || userEmail.value}`, 20, 110)

  // Project Info
  doc.setFont('helvetica', 'bold')
  doc.text('DETAILS DU PROJET SOUTENU', 20, 125)

  doc.setFont('helvetica', 'normal')
  doc.text(`Nom du projet : ${getPoolTitle(contrib.poolId)}`, 20, 133)
  doc.text(`Categorie : ${getPoolCategory(contrib.poolId)}`, 20, 140)

  // Amount Block
  doc.setFillColor(245, 247, 250)
  doc.rect(20, 150, 170, 25, 'F')

  doc.setFont('helvetica', 'bold')
  doc.setFontSize(14)
  doc.setTextColor(primaryColor[0], primaryColor[1], primaryColor[2])
  doc.text('MONTANT TOTAL DU DON', 30, 166)

  doc.setTextColor(secondaryColor[0], secondaryColor[1], secondaryColor[2])
  doc.setFontSize(18)
  doc.text(`${contrib.amount} EUR`, 130, 166)

  // Legal Notice
  doc.setTextColor(120, 120, 120)
  doc.setFont('helvetica', 'italic')
  doc.setFontSize(9)
  doc.text("Ce document atteste que le donateur mentionne ci-dessus a effectue une contribution financiere", 20, 195)
  doc.text("au projet designe via la plateforme securisee Potify. Ce recu est emis a titre d'information.", 20, 200)

  // Footer Banner
  doc.setFillColor(240, 240, 240)
  doc.rect(0, 270, 210, 27, 'F')

  doc.setTextColor(100, 100, 100)
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(8)
  doc.text('Potify SAS - Plateforme solidaire enregistree', 20, 280)
  doc.text('Pour toute question : support@potify.com', 20, 285)

  doc.save(`recu-potify-${contrib.id.substring(0, 8)}.pdf`)
}

// Récupère les {{ $t('dashboard.poolsCreated') }} par l'utilisateur (dont il est propriétaire)
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
    case 'EN_REVUE': return 'amber-7'
    case 'CLOTUREE': case 'COMPLETED': return 'grey-7'
    case 'SUSPENDUE': return 'red-7'
    case 'BROUILLON': return 'orange-7'
    default: return 'orange-7'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'BROUILLON': return 'Brouillon'
    case 'EN_REVUE': return 'En revue'
    case 'PUBLIEE': return 'Publiée'
    case 'SUSPENDUE': return 'Suspendue'
    case 'CLOTUREE': return 'Clôturée'
    case 'ARCHIVEE': return 'Archivée'
    case 'COMPLETED': return 'Terminée'
    default: return status
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
    fetchAllPools()
    fetchUserPools()
    fetchInvitedPools()
    fetchUserContributions()
    fetchUserNotifications()
  }
})

watch(userId, (newId) => {
  if (newId) {
    fetchAllPools()
    fetchUserPools()
    fetchInvitedPools()
    fetchUserContributions()
    fetchUserNotifications()
  }
}, { immediate: true })

watch(tab, (newTab) => {
  if (newTab === 'stats') {
    fetchStatsData()
  }
})
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
