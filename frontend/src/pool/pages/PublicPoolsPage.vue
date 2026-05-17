<template>
  <q-page style="background: #F8F9FA;">
    <!-- Hero Section -->
    <div class="hero-section text-white q-pa-xl" style="background: linear-gradient(135deg, #0D1B2E 0%, #1A2E44 100%);">
      <div class="max-container">
        <h1 class="text-h3 text-weight-bold q-mb-md">Explorer les cagnottes</h1>
        <p class="text-subtitle1 q-mb-xl" style="color: rgba(255,255,255,0.7); max-width: 600px;">
          Découvrez les projets qui vous tiennent à cœur et contribuez à faire la différence.
          Chaque don compte, chaque geste est un pas vers le changement.
        </p>

        <!-- Barre de recherche premium -->
        <div class="search-container q-pa-sm bg-white shadow-2" style="border-radius: 12px; max-width: 700px;">
          <div class="row items-center no-wrap">
            <q-input
              borderless
              v-model="searchQuery"
              placeholder="Rechercher une cagnotte par titre..."
              class="col q-px-md"
              color="primary"
            >
              <template v-slot:append>
                <q-spinner-dots v-if="loading" color="primary" size="20px" />
                <q-icon v-else name="search" size="24px" color="grey-6" />
              </template>
            </q-input>
          </div>
        </div>
      </div>
    </div>

    <!-- Filtres et Contenu -->
    <div class="max-container q-py-xl">
      <div class="row q-col-gutter-lg">
        <!-- Sidebar Filtres (Desktop) -->
        <div class="col-12 col-md-3 gt-sm">
          <q-card flat class="bg-white q-pa-lg" style="border-radius: 12px; border: 1px solid rgba(0,0,0,0.05);">
            <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E;">Catégories</div>
            <q-list dense padding>
              <q-item
                v-for="cat in categories"
                :key="cat"
                clickable
                v-ripple
                :active="selectedCategory === cat"
                active-class="active-category"
                @click="selectedCategory = cat"
                class="q-py-sm"
                style="border-radius: 8px;"
              >
                <q-item-section>{{ cat }}</q-item-section>
                <q-item-section side v-if="selectedCategory === cat">
                  <q-icon name="chevron_right" color="primary" />
                </q-item-section>
              </q-item>
            </q-list>

            <q-separator class="q-my-lg" />

            <q-btn
              outline
              color="grey-7"
              label="Réinitialiser"
              no-caps
              class="full-width"
              @click="resetFilters"
            />
          </q-card>
        </div>

        <!-- Grille de cagnottes -->
        <div class="col-12 col-md-9">
          <!-- Filtres Mobile -->
          <div class="lt-md q-mb-lg">
            <q-select
              outlined
              v-model="selectedCategory"
              :options="categories"
              label="Filtrer par catégorie"
              bg-color="white"
              color="primary"
              style="border-radius: 12px;"
            />
          </div>

          <div v-if="loading" class="row justify-center q-py-xl">
            <q-spinner-dots color="primary" size="40px" />
          </div>

          <template v-else>
            <div v-if="filteredPools.length > 0" class="row q-col-gutter-lg">
              <div v-for="pool in filteredPools" :key="pool.id" class="col-12 col-sm-6">
                <q-card class="pool-card cursor-pointer" @click="goToPool(pool.id)">
                  <div class="pool-image relative-position" style="background: #E0E0E0; min-height: 200px;">
                    <img v-if="pool.imageUrl" :src="pool.imageUrl" alt="Pool Image" style="width: 100%; height: 200px; object-fit: cover;">
                    <div class="category-badge">{{ pool.category }}</div>
                  </div>

                  <q-card-section class="q-pa-lg">
                    <div class="text-h6 text-weight-bold q-mb-sm text-dark line-clamp-1">{{ pool.title }}</div>
                    <p class="text-grey-7 line-clamp-2 q-mb-lg" style="font-size: 0.9rem;">{{ pool.description }}</p>

                    <div class="q-mb-md">
                      <div class="row justify-between items-center q-mb-xs">
                        <span class="text-weight-bold" style="color: #0D1B2E;">{{ formatCurrency(pool.currentAmount) }}</span>
                        <span class="text-grey-6" style="font-size: 0.85rem;">Objectif : {{ formatCurrency(pool.goalAmount) }}</span>
                      </div>
                      <q-linear-progress
                        :value="getProgress(pool)"
                        size="8px"
                        style="border-radius: 4px;"
                        color="amber-8"
                        track-color="grey-2"
                      />
                    </div>

                    <div class="row justify-between items-center">
                      <div class="row items-center q-gutter-xs">
                        <q-icon name="trending_up" color="green" size="18px" />
                        <span class="text-green text-weight-medium">{{ getPercentage(pool) }}%</span>
                      </div>
                      <q-btn flat no-caps color="primary" label="Voir détails" icon-right="arrow_forward" dense />
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <!-- État vide -->
            <div v-else class="column items-center justify-center q-py-xl text-center">
              <q-icon name="sentiment_dissatisfied" size="64px" color="grey-4" />
              <div class="text-h5 text-grey-5 q-mt-md">Aucune cagnotte trouvée</div>
              <p class="text-grey-6">Essayez de modifier vos critères de recherche ou de filtrage.</p>
              <q-btn
                label="Tout afficher"
                color="primary"
                unelevated
                no-caps
                @click="resetFilters"
                class="q-mt-md"
              />
            </div>
          </template>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'

const router = useRouter()
const loading = ref(false)
const pools = ref([])
const searchQuery = ref('')
const selectedCategory = ref('Toutes')
const categories = ['Toutes', 'Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']

// Timer pour le debounce
let searchTimer = null

const fetchPools = async () => {
  loading.value = true
  try {
    const response = await poolService.getPublicPools(searchQuery.value)
    pools.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des cagnottes:', error)
  } finally {
    loading.value = false
  }
}

// Watcher pour la recherche en temps réel
watch(searchQuery, () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchPools()
  }, 300) // Attendre 300ms après la dernière frappe
})

const filteredPools = computed(() => {
  if (selectedCategory.value === 'Toutes') {
    return pools.value
  }
  return pools.value.filter(p => p.category === selectedCategory.value)
})

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = 'Toutes'
  fetchPools()
}

const goToPool = (id) => {
  router.push(`/pools/${id}`)
}

const formatCurrency = (val) => {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(val || 0)
}

const getProgress = (pool) => {
  if (!pool.goalAmount) return 0
  return Math.min(pool.currentAmount / pool.goalAmount, 1)
}

const getPercentage = (pool) => {
  if (!pool.goalAmount) return 0
  return Math.round((pool.currentAmount / pool.goalAmount) * 100)
}



onMounted(() => {
  fetchPools()
})
</script>

<style scoped>
.max-container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pool-card {
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s ease;
  border: 1px solid rgba(0,0,0,0.05);
  background: white;
}

.pool-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(13, 27, 46, 0.1) !important;
}

.category-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: #FFB300;
  color: #0D1B2E;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.75rem;
  text-transform: uppercase;
}

.active-category {
  background: rgba(255, 179, 0, 0.1) !important;
  color: #0D1B2E !important;
  font-weight: 700 !important;
}

.hero-section {
  padding-top: 80px;
  padding-bottom: 100px;
}

@media (max-width: 599px) {
  .hero-section {
    padding-top: 40px;
    padding-bottom: 60px;
    text-align: center;
  }
  .search-container {
    margin: 0 auto;
  }
}
</style>
