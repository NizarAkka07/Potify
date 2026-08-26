<template>
  <q-page style="background: #FAFAFB; padding-bottom: 80px; font-family: 'Inter', sans-serif;">
    


    <!-- Filters & Main Content Area -->
    <div class="max-container q-px-md q-py-xl">
      <div class="row q-col-gutter-lg">
        
        <!-- Sidebar Categories (Desktop) -->
        <div class="col-12 col-md-3 gt-sm">
          <q-card flat class="bg-white q-pa-lg filter-sidebar" style="border-radius: 16px; border: 1px solid rgba(0,0,0,0.05); box-shadow: 0 4px 20px rgba(0,0,0,0.02); position: sticky; top: 100px;">
            <div class="text-subtitle1 text-weight-bold q-mb-md flex items-center" style="color: #0D1B2E;">
              <q-icon name="category" class="q-mr-sm" size="20px" color="primary" />
              {{ $t('publicPools.categories') }}
            </div>
            
            <q-list dense class="category-list">
              <q-item
                v-for="cat in categories"
                :key="cat"
                clickable
                v-ripple
                :active="selectedCategory === cat"
                active-class="active-category"
                @click="selectedCategory = cat"
                class="q-py-md q-mb-sm category-item"
                style="border-radius: 10px; transition: all 0.2s;"
              >
                <q-item-section avatar style="min-width: 36px;">
                  <q-avatar size="32px" :color="selectedCategory === cat ? 'amber-2' : 'grey-2'" :text-color="selectedCategory === cat ? 'primary' : 'grey-7'">
                    <q-icon :name="getCategoryIcon(cat)" size="18px" />
                  </q-avatar>
                </q-item-section>
                <q-item-section class="text-weight-bold text-grey-8" style="font-size: 0.95rem;">
                  {{ getCategoryLabel(cat) }}
                </q-item-section>
                <q-item-section side>
                  <q-badge color="grey-3" text-color="grey-8" class="text-weight-bold" style="border-radius: 8px; font-size: 0.75rem;">
                    {{ getCategoryCount(cat) }}
                  </q-badge>
                </q-item-section>
              </q-item>
            </q-list>

            <q-separator class="q-my-lg" />

            <!-- Reset Filter Button -->
            <q-btn
              flat
              color="primary"
              :label="$t('publicPools.resetFilters')"
              no-caps
              icon="refresh"
              class="full-width text-weight-bold"
              style="border-radius: 10px; background: rgba(255, 167, 38, 0.05);"
              @click="resetFilters"
            />
          </q-card>
        </div>

        <!-- Main Pools Grid Section -->
        <div class="col-12 col-md-9">
          
          <!-- Top Toolbar: Status Filters, Search, & Sorting -->
          <q-card flat class="bg-white q-pa-md q-mb-lg toolbar-card" style="border-radius: 16px; border: 1px solid rgba(0,0,0,0.05); box-shadow: 0 4px 20px rgba(0,0,0,0.02);">
            <div class="row items-center justify-between q-col-gutter-sm">
              <!-- Result count & Status Pills -->
              <div class="col-12 col-sm-auto row items-center q-gutter-md">
                <div class="text-subtitle2 text-grey-7 text-weight-bold">
                  {{ filteredPools.length }} {{ filteredPools.length > 1 ? $t('publicPools.poolsFoundMultiple') : $t('publicPools.poolsFoundSingle') }}
                </div>
                <!-- Status filter buttons -->
                <div class="row q-gutter-xs">
                  <q-btn
                    v-for="statusOpt in statusOptions"
                    :key="statusOpt.value"
                    flat
                    dense
                    no-caps
                    :label="statusOpt.label"
                    class="q-px-sm text-weight-bold status-pill-btn"
                    :class="selectedStatus === statusOpt.value ? 'bg-primary text-white shadow-1' : 'text-grey-7 bg-grey-2'"
                    style="border-radius: 8px; font-size: 0.8rem; height: 32px;"
                    @click="selectedStatus = statusOpt.value"
                  />
                </div>
              </div>

              <!-- Search Input (Middle) -->
              <div class="col-12 col-sm-grow col-md-5 q-px-md">
                <q-input
                  outlined
                  v-model="searchQuery"
                  :placeholder="$t('publicPools.searchPlaceholder')"
                  bg-color="grey-1"
                  color="primary"
                  dense
                  style="border-radius: 8px; max-width: 400px; margin: 0 auto; width: 100%;"
                  class="search-input-field"
                >
                  <template v-slot:prepend>
                    <q-icon name="search" color="grey-6" />
                  </template>
                  <template v-slot:append>
                    <q-spinner-dots v-if="loading" color="primary" size="20px" />
                    <q-btn
                      v-else-if="searchQuery"
                      flat
                      round
                      dense
                      icon="close"
                      color="grey-6"
                      @click="searchQuery = ''"
                    />
                  </template>
                </q-input>
              </div>
              
              <!-- Sorting dropdown -->
              <div class="col-12 col-sm-auto row items-center q-gutter-sm justify-end">
                <span class="text-caption text-grey-6 text-weight-bold">{{ $t('publicPools.sortByLabel') }} :</span>
                <q-select
                  v-model="sortBy"
                  :options="sortOptions"
                  dense
                  outlined
                  emit-value
                  map-options
                  options-dense
                  bg-color="grey-1"
                  color="primary"
                  class="text-weight-bold text-primary sort-select"
                  style="font-size: 0.85rem; width: 180px; border-radius: 8px;"
                  control-class="text-primary text-weight-bold"
                />
              </div>
            </div>
          </q-card>

          <!-- Mobile Categories Dropdown (Visible only on mobile/tablet) -->
          <div class="lt-md q-mb-lg">
            <q-select
              outlined
              v-model="selectedCategory"
              :options="categoryOptionsMobile"
              option-value="value"
              option-label="label"
              emit-value
              map-options
              :label="$t('publicPools.selectCategoryMobile')"
              :display-value="getCategoryLabel(selectedCategory)"
              bg-color="white"
              color="primary"
              style="border-radius: 12px;"
            >
              <template v-slot:prepend>
                <q-icon :name="getCategoryIcon(selectedCategory)" color="primary" />
              </template>
            </q-select>
          </div>

          <!-- Loader -->
          <div v-if="loading" class="row justify-center q-py-xl" style="min-height: 200px;">
            <q-spinner-dots color="primary" size="50px" />
          </div>

          <!-- Cards Grid -->
          <template v-else>
            <div v-if="filteredPools.length > 0" class="row q-col-gutter-lg">
              <div 
                v-for="(pool, index) in filteredPools" 
                :key="pool.id" 
                class="col-12 col-sm-6 animate-card"
                :style="{ 'animation-delay': index * 0.05 + 's' }"
              >
                <q-card class="pool-card cursor-pointer" @click="goToPool(pool.id)">
                  <!-- Card Header Image Area -->
                  <div class="pool-image relative-position" style="min-height: 200px; height: 200px; overflow: hidden;">
                    <!-- Fallback Gradient if Image is missing -->
                    <div 
                      v-if="!pool.imageUrl" 
                      class="full-height flex flex-center relative-position" 
                      :style="{ background: getCategoryGradient(pool.category) }"
                    >
                      <q-icon :name="getCategoryIcon(pool.category)" size="64px" color="white" style="opacity: 0.25;" />
                      <div class="absolute-center text-center">
                        <q-avatar size="50px" color="white" text-color="primary" class="shadow-2">
                          <q-icon :name="getCategoryIcon(pool.category)" size="26px" />
                        </q-avatar>
                      </div>
                    </div>
                    
                    <!-- Dynamic uploaded image -->
                    <img 
                      v-else 
                      :src="pool.imageUrl" 
                      alt="Pool Image" 
                      style="width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s;"
                      class="card-img-element"
                    />

                    <!-- Category Badge -->
                    <div class="category-glass-badge">{{ getCategoryLabel(pool.category) }}</div>

                    <!-- Status Badge -->
                    <div 
                      class="status-glass-badge" 
                      :class="getStatusClass(pool.status)"
                    >
                      <span v-if="pool.status === 'PUBLIEE'" class="status-pulse-dot"></span>
                      {{ getStatusLabel(pool.status) }}
                    </div>
                  </div>

                  <!-- Card Content Area -->
                  <q-card-section class="q-pa-lg">
                    <!-- Title & Type -->
                    <div class="row items-center justify-between q-mb-sm">
                      <div class="text-caption text-primary text-weight-bold text-uppercase tracking-wider">
                        {{ pool.type === 'PRIVATE_TONTINE' ? $t('publicPools.tontineType') : (pool.type === 'PRIVATE' ? $t('publicPools.privateType') : $t('publicPools.publicType')) }}
                      </div>
                      <div class="text-caption text-grey-5 flex items-center">
                        <q-icon name="calendar_today" size="12px" class="q-mr-xs" />
                        {{ formatDate(pool.createdAt) }}
                      </div>
                    </div>
                    
                    <div class="text-h6 text-weight-bold q-mb-sm text-dark line-clamp-1" style="color: #0D1B2E; font-size: 1.15rem;">
                      {{ pool.title }}
                    </div>
                    <p class="text-grey-6 line-clamp-2 q-mb-lg" style="font-size: 0.88rem; line-height: 1.5; min-height: 2.6rem;">
                      {{ pool.description }}
                    </p>

                    <!-- Financial progress bar -->
                    <div class="q-mb-md">
                      <div class="row justify-between items-center q-mb-xs">
                        <div>
                          <span class="text-h6 text-weight-bolder text-primary">{{ formatCurrency(pool.currentAmount) }}</span>
                          <span class="text-caption text-grey-6 q-ml-xs">{{ $t('publicPools.collected') }}</span>
                        </div>
                        <div class="text-caption text-grey-6 text-weight-bold">
                          {{ $t('publicPools.goal') }} : {{ formatCurrency(pool.goalAmount) }}
                        </div>
                      </div>
                      
                      <q-linear-progress
                        :value="getProgress(pool)"
                        size="10px"
                        style="border-radius: 6px;"
                        class="progress-bar-glowing"
                        :color="getProgressColor(pool)"
                        track-color="grey-2"
                      />
                    </div>

                    <!-- Meta details: Contributors count & Percent -->
                    <div class="row justify-between items-center q-mt-sm q-mb-lg text-caption text-grey-6 font-weight-medium">
                      <div class="row items-center q-gutter-xs">
                        <q-icon name="group" color="grey-6" size="16px" />
                        <span class="text-weight-bold text-dark">{{ contributorsMap[pool.id] || 0 }}</span> 
                        <span>{{ (contributorsMap[pool.id] || 0) > 1 ? $t('publicPools.contributorsMultiple') : $t('publicPools.contributorsSingle') }}</span>
                      </div>
                      <div class="row items-center q-gutter-xs text-weight-bold" :class="pool.currentAmount >= pool.goalAmount ? 'text-green' : 'text-primary'">
                        <q-icon :name="pool.currentAmount >= pool.goalAmount ? 'check_circle' : 'trending_up'" size="16px" />
                        <span>{{ getPercentage(pool) }}% {{ $t('publicPools.funded') }}</span>
                      </div>
                    </div>

                    <q-separator class="q-my-md" style="opacity: 0.5;" />

                    <!-- Action Buttons -->
                    <div class="row items-center justify-between no-wrap">
                      <q-btn 
                        flat 
                        no-caps 
                        color="grey-7" 
                        :label="$t('publicPools.detailsButton')" 
                        icon="info" 
                        dense 
                        class="q-px-sm border-radius-8"
                        style="border-radius: 8px;"
                        @click.stop="goToPool(pool.id)"
                      />
                      
                      <div class="row q-gutter-xs">
                        <!-- Share button -->
                        <q-btn 
                          flat 
                          round 
                          dense 
                          color="grey-7" 
                          icon="share" 
                          @click.stop="sharePool(pool.id)"
                        >
                          <q-tooltip>{{ $t('publicPools.shareTooltip') }}</q-tooltip>
                        </q-btn>
                        
                        <!-- Contribute button -->
                        <q-btn 
                          unelevated 
                          no-caps 
                          :color="(pool.status === 'CLOTUREE' || (pool.goalAmount > 0 && (pool.currentAmount || 0) >= pool.goalAmount)) ? 'positive' : 'primary'" 
                          :label="(pool.status === 'CLOTUREE' || (pool.goalAmount > 0 && (pool.currentAmount || 0) >= pool.goalAmount)) ? 'Objectif atteint' : $t('publicPools.contributeButton')" 
                          :icon-right="(pool.status === 'CLOTUREE' || (pool.goalAmount > 0 && (pool.currentAmount || 0) >= pool.goalAmount)) ? 'check_circle' : 'favorite'" 
                          class="q-px-md text-weight-bold contribute-btn" 
                          :style="(pool.status === 'CLOTUREE' || (pool.goalAmount > 0 && (pool.currentAmount || 0) >= pool.goalAmount)) ? 'border-radius: 8px; opacity: 0.85;' : 'border-radius: 8px; background: #FFA726 !important; color: #1A1A2A !important;'"
                          @click.stop="goToPoolDetailForDonation(pool.id)"
                          :disable="pool.status === 'CLOTUREE' || (pool.goalAmount > 0 && (pool.currentAmount || 0) >= pool.goalAmount)"
                        />
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <!-- Empty State -->
            <div v-else class="column items-center justify-center q-py-xl text-center empty-state-container bg-white shadow-1" style="border-radius: 20px; border: 1px dashed rgba(0,0,0,0.1); padding: 60px 20px;">
              <q-avatar size="80px" color="amber-1" text-color="primary" class="q-mb-md">
                <q-icon name="search_off" size="40px" />
              </q-avatar>
              <div class="text-h5 text-weight-bold text-dark q-mt-md">{{ $t('publicPools.noPoolsFound') }}</div>
              <p class="text-grey-6 q-mt-sm" style="max-width: 400px; margin: 8px auto 20px;">
                {{ $t('publicPools.emptyDesc') }}
              </p>
              <q-btn
                :label="$t('publicPools.emptyResetButton')"
                color="primary"
                unelevated
                no-caps
                @click="resetFilters"
                style="border-radius: 8px; font-weight: bold; background: #FFA726 !important; color: #1A1A2A !important;"
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
import { useRouter, useRoute } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'
import { useQuasar } from 'quasar'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const route = useRoute()
const $q = useQuasar()
const { t } = useI18n()

const loading = ref(false)
const pools = ref([])
const searchQuery = ref('')
const selectedCategory = ref('Toutes')
const selectedStatus = ref('all')
const sortBy = ref('recent')

const categories = ['Toutes', 'Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']
const contributorsMap = ref({})

const getCategoryLabel = (catName) => {
  if (!catName || catName === 'Toutes') return t('publicPools.allCategories')
  const keyMap = {
    'Santé': 'catHealth',
    'Éducation': 'catEducation',
    'Urgence': 'catEmergency',
    'Animaux': 'catAnimals',
    'Projets': 'catProjects',
    'Sport': 'catSport'
  }
  const key = keyMap[catName]
  return key ? t(`home.${key}`) : catName
}

const sortOptions = computed(() => [
  { label: t('publicPools.sortRecent'), value: 'recent' },
  { label: t('publicPools.sortOldest'), value: 'oldest' },
  { label: t('publicPools.sortProgress'), value: 'progress' },
  { label: t('publicPools.sortPopular'), value: 'popular' }
])

const statusOptions = computed(() => [
  { label: t('publicPools.statusAll'), value: 'all' },
  { label: t('publicPools.statusActiveFilter'), value: 'active' },
  { label: t('publicPools.statusCompletedFilter'), value: 'completed' }
])

const categoryIcons = {
  'Toutes': 'grid_view',
  'Santé': 'local_hospital',
  'Éducation': 'school',
  'Urgence': 'warning',
  'Animaux': 'pets',
  'Projets': 'lightbulb',
  'Sport': 'sports_soccer'
}

const categoryOptionsMobile = computed(() => categories.map(cat => ({
  value: cat,
  label: getCategoryLabel(cat)
})))

const getCategoryIcon = (category) => {
  return categoryIcons[category] || 'folder'
}

const getCategoryGradient = (category) => {
  switch (category) {
    case 'Santé':
      return 'linear-gradient(135deg, #059669 0%, #10b981 100%)'
    case 'Éducation':
      return 'linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)'
    case 'Urgence':
      return 'linear-gradient(135deg, #dc2626 0%, #f87171 100%)'
    case 'Animaux':
      return 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)'
    case 'Projets':
      return 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)'
    case 'Sport':
      return 'linear-gradient(135deg, #0891b2 0%, #06b6d4 100%)'
    default:
      return 'linear-gradient(135deg, #4b5563 0%, #6b7280 100%)'
  }
}

const getStatusLabel = (status) => {
  switch (status) {
    case 'PUBLIEE': return t('publicPools.statusActive')
    case 'CLOTUREE': return t('publicPools.statusClosed')
    case 'SUSPENDUE': return 'Suspendue'
    case 'ARCHIVEE': return 'Archivée'
    default: return status || t('publicPools.statusActive')
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 'PUBLIEE': return 'badge-active'
    case 'CLOTUREE': return 'badge-closed'
    case 'SUSPENDUE': return 'badge-suspended'
    case 'ARCHIVEE': return 'badge-archived'
    default: return 'badge-default'
  }
}

const getProgressColor = (pool) => {
  const pct = getPercentage(pool)
  if (pct >= 100) return 'green'
  if (pct >= 75) return 'teal'
  if (pct >= 50) return 'primary'
  return 'amber-8'
}

const getCategoryCount = (cat) => {
  if (!pools.value) return 0
  const activePools = pools.value.filter(p => !p.parentId && p.status !== 'SUSPENDUE' && p.status !== 'SUSPENDED' && p.status !== 'ARCHIVEE')
  if (cat === 'Toutes') return activePools.length
  return activePools.filter(p => p.category === cat).length
}

let searchTimer = null

const fetchPools = async () => {
  loading.value = true
  try {
    const response = await poolService.getPublicPools(searchQuery.value)
    pools.value = response.data || []
    
    // Fetch unique contributor count for each pool
    const counts = {}
    await Promise.all(
      pools.value.map(async (pool) => {
        try {
          const res = await poolService.getPoolContributions(pool.id)
          const contribs = res.data || []
          const uniqueContributors = new Set(
            contribs
              .filter(c => c.status === 'CONFIRMED' || c.status === 'REUSSIE' || c.status === 'SUCCEEDED')
              .map(c => c.contributorEmail || c.contributorName || c.id)
          )
          counts[pool.id] = uniqueContributors.size
        } catch {
          counts[pool.id] = 0
        }
      })
    )
    contributorsMap.value = counts
  } catch (error) {
    console.error('Erreur lors du chargement des cagnottes:', error)
  } finally {
    loading.value = false
  }
}

watch(searchQuery, () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchPools()
  }, 300)
})

const filteredPools = computed(() => {
  let result = (pools.value || []).filter(p => p.status !== 'SUSPENDUE' && p.status !== 'SUSPENDED' && p.status !== 'ARCHIVEE')
  
  // Category filter
  if (selectedCategory.value !== 'Toutes') {
    result = result.filter(p => p.category === selectedCategory.value)
  }

  // Status filter
  if (selectedStatus.value === 'active') {
    result = result.filter(p => p.status === 'PUBLIEE')
  } else if (selectedStatus.value === 'completed') {
    result = result.filter(p => p.status === 'CLOTUREE')
  }
  
  // Exclude sub-pools from public view
  result = result.filter(p => !p.parentId)

  // Sort
  const sorted = [...result]
  if (sortBy.value === 'recent') {
    sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } else if (sortBy.value === 'oldest') {
    sorted.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  } else if (sortBy.value === 'progress') {
    sorted.sort((a, b) => {
      const progA = a.goalAmount ? (a.currentAmount / a.goalAmount) : 0
      const progB = b.goalAmount ? (b.currentAmount / b.goalAmount) : 0
      return progB - progA
    })
  } else if (sortBy.value === 'popular') {
    sorted.sort((a, b) => {
      const cntA = contributorsMap.value[a.id] || 0
      const cntB = contributorsMap.value[b.id] || 0
      return cntB - cntA
    })
  }
  
  return sorted
})

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = 'Toutes'
  selectedStatus.value = 'all'
  sortBy.value = 'recent'
  fetchPools()
}

const goToPool = (id) => {
  router.push(`/pools/${id}`)
}

const goToPoolDetailForDonation = (id) => {
  // Direct details redirect where they can choose to donate
  router.push(`/pools/${id}`)
}

const sharePool = (poolId) => {
  const url = `${window.location.origin}/#/pools/${poolId}`
  navigator.clipboard.writeText(url)
    .then(() => {
      $q.notify({
        message: t('publicPools.copySuccess'),
        color: 'positive',
        icon: 'share',
        position: 'bottom-right',
        timeout: 2000
      })
    })
    .catch(() => {
      $q.notify({
        message: t('publicPools.copyError'),
        color: 'negative',
        icon: 'error',
        position: 'bottom-right',
        timeout: 2000
      })
    })
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

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  })
}

watch(() => route.query.category, (newCat) => {
  if (newCat) {
    selectedCategory.value = newCat
  } else {
    selectedCategory.value = 'Toutes'
  }
})

onMounted(() => {
  if (route.query.category) {
    selectedCategory.value = route.query.category
  }
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

/* Hero Section */
.hero-section {
  padding-top: 60px;
  padding-bottom: 70px;
  border-radius: 0 0 24px 24px;
}

.glow-decoration-1 {
  position: absolute;
  top: -50px;
  left: -50px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 167, 38, 0.15) 0%, transparent 70%);
  filter: blur(40px);
}

.glow-decoration-2 {
  position: absolute;
  bottom: -80px;
  right: -50px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.15) 0%, transparent 70%);
  filter: blur(50px);
}

.simple-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 6px 14px;
  border-radius: 30px;
  backdrop-filter: blur(10px);
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: #FFA726;
  border-radius: 50%;
  margin-right: 8px;
  box-shadow: 0 0 10px #FFA726;
}

.badge-text {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: rgba(255, 255, 255, 0.9);
}

/* Sidebar Categories */
.filter-sidebar {
  border-radius: 16px;
}

.category-item:hover {
  background: rgba(13, 27, 46, 0.03);
}

.active-category {
  background: rgba(255, 167, 38, 0.08) !important;
}

.active-category .text-grey-8 {
  color: #0D1B2E !important;
}

/* Toolbar Card */
.toolbar-card {
  border-radius: 16px;
}

/* Pool Cards styling */
.pool-card {
  border-radius: 20px;
  overflow: hidden;
  transition: transform 0.4s cubic-bezier(0.165, 0.84, 0.44, 1), box-shadow 0.4s ease;
  border: 1px solid rgba(0, 0, 0, 0.04);
  background: white;
  box-shadow: 0 4px 25px rgba(0, 0, 0, 0.02);
}

.pool-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 35px rgba(13, 27, 46, 0.08) !important;
}

.pool-card:hover .card-img-element {
  transform: scale(1.06);
}

/* Badges on Image */
.category-glass-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  background: rgba(13, 27, 46, 0.7);
  backdrop-filter: blur(8px);
  color: white;
  padding: 4px 12px;
  border-radius: 30px;
  font-weight: 700;
  font-size: 0.75rem;
  letter-spacing: 0.03em;
  border: 1px solid rgba(255, 255, 255, 0.1);
  text-transform: uppercase;
  z-index: 2;
}

.status-glass-badge {
  position: absolute;
  top: 14px;
  right: 14px;
  backdrop-filter: blur(8px);
  padding: 4px 12px;
  border-radius: 30px;
  font-weight: 700;
  font-size: 0.75rem;
  display: inline-flex;
  align-items: center;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.badge-active {
  background: rgba(16, 185, 129, 0.85);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.badge-completed {
  background: rgba(37, 99, 235, 0.85);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.badge-closed {
  background: rgba(107, 114, 128, 0.85);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.badge-review {
  background: rgba(245, 158, 11, 0.85);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.status-pulse-dot {
  width: 6px;
  height: 6px;
  background-color: #ffffff;
  border-radius: 50%;
  margin-right: 6px;
  display: inline-block;
  animation: pulse-dot-anim 1.5s infinite ease-in-out;
}

@keyframes pulse-dot-anim {
  0% {
    transform: scale(0.85);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(0.85);
    opacity: 0.5;
  }
}

/* Staggered Card Entrance Animation */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-card {
  animation: fadeInUp 0.6s cubic-bezier(0.165, 0.84, 0.44, 1) both;
}

.progress-bar-glowing {
  box-shadow: 0 0 10px rgba(255, 167, 38, 0.05);
}

.contribute-btn {
  transition: all 0.3s ease;
}

.contribute-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 167, 38, 0.35);
  background: #ffb300 !important;
}

@media (max-width: 599px) {
  .hero-section {
    padding-top: 30px;
    padding-bottom: 40px;
    text-align: center;
  }
  .simple-badge {
    margin: 0 auto 16px;
  }
  .search-box-card {
    margin-top: 10px;
  }
}
</style>
