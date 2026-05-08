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

      <!-- Onglets Premium -->
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
        <q-tab name="contributions" label="Mes Contributions" />
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
                <q-img :src="pool.imageUrl || getPlaceholder()" style="height: 180px;" fit="cover">
                  <div class="absolute-top-right q-ma-sm">
                    <q-chip :color="getStatusColor(pool.status)" text-white size="sm" class="text-weight-bold">
                      {{ pool.status }}
                    </q-chip>
                  </div>
                </q-img>
                
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

      </q-tab-panels>

    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { poolApi } from 'boot/axios'
import authStore from 'src/shared/stores/auth'

const tab = ref('pools')
const userName = authStore.user.value?.firstName || 'Utilisateur'
const userId = authStore.user.value?.id

const userPools = ref([])
const userContributions = ref([])
const loadingPools = ref(false)
const loadingContributions = ref(false)

const fetchUserPools = async () => {
  if (!userId) return
  loadingPools.value = true
  try {
    const response = await poolApi.get(`/pools/user/${userId}`)
    userPools.value = response.data
  } catch (error) {
    console.error(error)
  } finally {
    loadingPools.value = false
  }
}

const fetchUserContributions = async () => {
  if (!userId) return
  loadingContributions.value = true
  try {
    const response = await poolApi.get(`/contributions/user/${userId}`)
    userContributions.value = response.data
  } catch (error) {
    console.error(error)
  } finally {
    loadingContributions.value = false
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 'PUBLIEE': return 'green-7'
    case 'EN_COURS': return 'blue-7'
    case 'TERMINEE': return 'grey-7'
    default: return 'orange-7'
  }
}

const getPlaceholder = () => {
  return 'https://images.unsplash.com/photo-1469571483350-f18203005d1d?auto=format&fit=crop&q=80&w=800'
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

onMounted(() => {
  fetchUserPools()
  fetchUserContributions()
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
