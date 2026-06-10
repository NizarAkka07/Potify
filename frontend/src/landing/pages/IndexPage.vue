<template>
  <q-page class="index-page bg-light-gray" style="background: #FAFAFB; color: #1A1A2A;">
    
    <!-- 1. HERO SECTION – Simple, clean and elegant -->
    <div class="hero-section" style="min-height: 70vh; display: flex; align-items: center; padding: 80px 0;">
      <div class="max-container q-px-xl">
        <div class="row items-center q-col-gutter-xl">
          
          <!-- Text & Search Column -->
          <div class="col-12 col-md-7">
            <!-- Simple Badge -->
            <div class="simple-badge q-mb-md">
              <span class="badge-dot"></span>
              <span class="badge-text">Financement Participatif Solidaire</span>
            </div>

            <!-- Title -->
            <h1 class="text-weight-bold q-mb-md hero-title" style="color: #0D1B2E; font-size: 2.8rem; line-height: 1.2; font-family: 'Inter', sans-serif;">
              Ensemble, donnons vie <br>
              <span style="color: #FFA726;">à ce qui compte.</span>
            </h1>

            <p class="q-mb-lg hero-subtitle" style="font-size: 1.1rem; color: #5A6A85; line-height: 1.6; max-width: 540px;">
              Créez une cagnotte en quelques clics, organisez-la par étapes transparentes avec PotiBuddy, et mobilisez votre entourage en toute confiance.
            </p>

            <!-- Simple Search Bar -->
            <div class="search-bar shadow-1 q-pa-sm q-mb-lg bg-white">
              <div class="row q-col-gutter-sm items-center">
                <div class="col-12 col-sm-6">
                  <q-input
                    borderless
                    v-model="searchKeyword"
                    placeholder="Rechercher une cause..."
                    dense
                    class="q-px-md search-input"
                    @keyup.enter="handleSearch"
                  >
                    <template v-slot:prepend>
                      <q-icon name="search" color="grey-6" />
                    </template>
                  </q-input>
                </div>

                <div class="col-12 col-sm-3">
                  <q-select
                    borderless
                    v-model="selectedCategory"
                    :options="categoryOptions"
                    :display-value="selectedCategory || 'Catégories'"
                    dense
                    class="q-px-sm select-input"
                    clearable
                  >
                    <template v-slot:prepend>
                      <q-icon name="sell" color="grey-6" size="18px" />
                    </template>
                  </q-select>
                </div>

                <div class="col-12 col-sm-3">
                  <q-btn
                    unelevated
                    no-caps
                    label="Rechercher"
                    class="full-width"
                    @click="handleSearch"
                    style="background: #FFA726; color: #1A1A2A; font-weight: 700; border-radius: 8px; height: 40px;"
                  />
                </div>
              </div>
            </div>

            <!-- CTAs -->
            <div class="row items-center q-gutter-md">
              <q-btn
                unelevated
                no-caps
                label="Créer une cagnotte"
                to="/pools/create"
                style="background: #0D1B2E; color: #FFFFFF; font-weight: 700; padding: 10px 24px; border-radius: 8px;"
              />
              <q-btn
                flat
                no-caps
                label="Comment ça marche ?"
                to="#how-it-works"
                style="color: #0D1B2E; font-weight: 600;"
              />
            </div>
          </div>

          <!-- Image Column -->
          <div class="col-12 col-md-5 gt-sm text-center">
            <img
              src="~assets/hero-solidarity.png"
              alt="Solidarité et Entraide"
              class="hero-image"
              style="width: 100%; max-width: 440px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.06); object-fit: cover;"
            />
          </div>

        </div>
      </div>
    </div>

    <!-- 2. STATS BAR – Flat & Clean -->
    <div class="q-py-lg" style="background: #0D1B2E;">
      <div class="row q-col-gutter-md q-px-xl justify-center text-center max-container text-white">
        <div class="col-12 col-sm-4">
          <div class="text-h4 text-weight-bold" style="color: #FFA726;">12M €</div>
          <div class="text-caption text-grey-4">Collectés au total</div>
        </div>
        <div class="col-12 col-sm-4">
          <div class="text-h4 text-weight-bold" style="color: #FFA726;">15 000+</div>
          <div class="text-caption text-grey-4">Projets financés</div>
        </div>
        <div class="col-12 col-sm-4">
          <div class="text-h4 text-weight-bold" style="color: #FFA726;">2.5M</div>
          <div class="text-caption text-grey-4">Membres actifs</div>
        </div>
      </div>
    </div>

    <!-- 3. HOW IT WORKS – Clean 3 columns -->
    <div id="how-it-works" class="q-py-xxl bg-white" style="padding: 80px 0;">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xl">
          <h2 class="text-h4 text-weight-bold text-dark q-mb-sm" style="font-family: 'Inter', sans-serif;">Comment ça marche ?</h2>
          <p class="text-grey-6" style="font-size: 1rem;">La simplicité au cœur de chaque collecte.</p>
        </div>

        <div class="row q-col-gutter-lg">
          <div class="col-12 col-md-4 text-center q-pa-md">
            <div class="step-icon-circle q-mx-auto q-mb-md">1</div>
            <h3 class="text-subtitle1 text-weight-bold text-dark q-mb-xs">Créez votre cagnotte</h3>
            <p class="text-grey-7" style="font-size: 0.9rem; line-height: 1.5;">
              Décrivez votre projet en quelques lignes et divisez-le en étapes claires grâce à l'aide de notre IA.
            </p>
          </div>
          <div class="col-12 col-md-4 text-center q-pa-md">
            <div class="step-icon-circle q-mx-auto q-mb-md">2</div>
            <h3 class="text-subtitle1 text-weight-bold text-dark q-mb-xs">Partagez le lien</h3>
            <p class="text-grey-7" style="font-size: 0.9rem; line-height: 1.5;">
              Invitez vos proches, collègues ou le public à contribuer en toute transparence et sécurité.
            </p>
          </div>
          <div class="col-12 col-md-4 text-center q-pa-md">
            <div class="step-icon-circle q-mx-auto q-mb-md">3</div>
            <h3 class="text-subtitle1 text-weight-bold text-dark q-mb-xs">Récupérez les fonds</h3>
            <p class="text-grey-7" style="font-size: 0.9rem; line-height: 1.5;">
              Suivez les virements sécurisés directement vers votre compte bancaire à chaque jalon validé.
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. FEATURED CAMPAIGNS – Flat Grid -->
    <div class="q-py-xxl" style="padding: 80px 0;">
      <div class="max-container q-px-xl">
        <div class="row items-center justify-between q-mb-xl">
          <div>
            <h2 class="text-h4 text-weight-bold text-dark q-mb-xs" style="margin-top:0; font-family: 'Inter', sans-serif;">Cagnottes à la une</h2>
            <p class="text-grey-6 q-my-none">Soutenez les projets en cours recommandés par notre communauté.</p>
          </div>
          <q-btn 
            flat 
            no-caps 
            color="orange-9"
            label="Voir tout" 
            to="/pools" 
            icon-right="chevron_right" 
          />
        </div>

        <div class="row q-col-gutter-lg">
          <!-- Real dynamic campaign cards -->
          <div class="col-12 col-sm-6 col-md-4" v-for="pool in pools" :key="pool.id">
            <q-card class="simple-pool-card flat shadow-0" style="height: 100%; border-radius: 12px; border: 1px solid rgba(0,0,0,0.08); background: white; overflow: hidden;">
              <!-- Image or generic background -->
              <div style="height: 180px; width: 100%; background: #ECEFF1;" class="relative-position">
                <img
                  v-if="pool.imageUrl"
                  :src="pool.imageUrl"
                  style="height: 180px; object-fit: cover; width: 100%;"
                />
                <div v-else class="fit flex flex-center text-grey-4" style="background: #0D1B2E;">
                  <q-icon name="payments" size="3.5rem" color="orange-3" />
                </div>
                <div class="absolute-top-left q-ma-sm">
                  <span class="simple-tag">{{ pool.category || 'Général' }}</span>
                </div>
              </div>

              <q-card-section class="q-pa-md">
                <h3 class="text-subtitle1 text-weight-bold text-dark q-mb-xs line-clamp-2" style="font-family: 'Inter', sans-serif; line-height: 1.3; height: 42px;">
                  {{ pool.title }}
                </h3>
                <p class="text-caption text-grey-7 line-clamp-3 q-mb-none" style="height: 50px; line-height: 1.4;">
                  {{ pool.description }}
                </p>
              </q-card-section>

              <q-card-section class="q-px-md q-pb-none q-pt-none">
                <q-linear-progress :value="getProgressValue(pool)" style="height: 5px; border-radius: 2px;" color="orange-8" class="q-mb-xs" />
                <div class="row justify-between items-center text-caption text-weight-bold">
                   <span style="color: #FFA726;">{{ getPercentageText(pool) }}</span>
                   <span class="text-dark">{{ pool.currentAmount }} € <span class="text-grey-6 text-weight-regular">/ {{ pool.goalAmount }} €</span></span>
                </div>
              </q-card-section>

              <q-card-actions class="q-pa-md">
                <q-btn
                  unelevated
                  no-caps
                  class="full-width"
                  label="Découvrir"
                  style="background: #FFA726; color: #1A1A2A; font-weight: 700; border-radius: 6px;"
                  @click="contribute(pool)"
                />
              </q-card-actions>
            </q-card>
          </div>

          <!-- Mock campaigns when API is empty -->
          <template v-if="pools.length === 0">
            <div class="col-12 col-sm-6 col-md-4" v-for="mock in mockPools" :key="mock.id">
              <q-card class="simple-pool-card flat shadow-0" style="height: 100%; border-radius: 12px; border: 1px solid rgba(0,0,0,0.08); background: white; overflow: hidden;">
                <div style="height: 180px; width: 100%; background: #0D1B2E;" class="relative-position flex flex-center">
                  <q-icon :name="mock.icon" size="3.5rem" color="orange-3" />
                  <div class="absolute-top-left q-ma-sm">
                    <span class="simple-tag">{{ mock.category }}</span>
                  </div>
                </div>

                <q-card-section class="q-pa-md">
                  <h3 class="text-subtitle1 text-weight-bold text-dark q-mb-xs line-clamp-2" style="font-family: 'Inter', sans-serif; line-height: 1.3; height: 42px;">
                    {{ mock.title }}
                  </h3>
                  <p class="text-caption text-grey-7 line-clamp-3 q-mb-none" style="height: 50px; line-height: 1.4;">
                    {{ mock.description }}
                  </p>
                </q-card-section>

                <q-card-section class="q-px-md q-pb-none q-pt-none">
                  <q-linear-progress :value="mock.currentAmount / mock.goalAmount" style="height: 5px; border-radius: 2px;" color="orange-8" class="q-mb-xs" />
                  <div class="row justify-between items-center text-caption text-weight-bold">
                     <span style="color: #FFA726;">{{ Math.round((mock.currentAmount / mock.goalAmount) * 100) }}%</span>
                     <span class="text-dark">{{ mock.currentAmount }} € <span class="text-grey-6 text-weight-regular">/ {{ mock.goalAmount }} €</span></span>
                  </div>
                </q-card-section>

                <q-card-actions class="q-pa-md">
                  <q-btn
                    unelevated
                    no-caps
                    class="full-width"
                    label="Découvrir"
                    style="background: #FFA726; color: #1A1A2A; font-weight: 700; border-radius: 6px;"
                    to="/pools"
                  />
                </q-card-actions>
              </q-card>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 5. CATEGORIES – Flat Chips -->
    <div class="q-py-xxl bg-white" style="padding: 80px 0;">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xl">
          <h2 class="text-h4 text-weight-bold text-dark q-mb-xs" style="font-family: 'Inter', sans-serif;">Causes populaires</h2>
          <p class="text-grey-6">Filtrez les projets selon vos thématiques favorites.</p>
        </div>

        <div class="row q-col-gutter-md justify-center">
          <div class="col-6 col-sm-4 col-md-2" v-for="cat in categories" :key="cat.name">
            <div 
              class="category-flat-card q-pa-md text-center cursor-pointer"
              @click="$router.push({ path: '/pools', query: { category: cat.name } })"
            >
              <q-icon :name="cat.icon" size="24px" color="orange-8" class="q-mb-sm" />
              <div class="text-weight-bold text-dark text-caption">{{ cat.name }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 6. FINAL CALL TO ACTION – Clean Minimal Banner -->
    <div class="q-py-xxl text-white text-center" style="background: #0D1B2E; padding: 80px 0;">
      <div class="max-container q-px-xl" style="max-width: 600px;">
        <h2 class="text-h4 text-weight-bold q-mb-sm" style="font-family: 'Inter', sans-serif;">Prêt à changer les choses ?</h2>
        <p class="text-body2 q-mb-lg text-grey-4" style="line-height: 1.5;">
          Créez votre première cagnotte dès maintenant et concrétisez vos projets.
        </p>
        <q-btn
          no-caps
          label="Lancer ma cagnotte"
          style="background: #FFA726; color: #1A1A2A; font-weight: 700; padding: 12px 36px; border-radius: 8px; font-size: 1rem;"
          to="/pools/create"
        />
      </div>
    </div>

  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { poolApi } from 'boot/axios'

const router = useRouter()

// Search state
const searchKeyword = ref('')
const selectedCategory = ref(null)

const categoryOptions = ['Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']

const pools = ref([])

const mockPools = ref([
  {
    id: 'mock-1',
    title: 'Achat de fournitures scolaires pour la rentrée',
    description: 'Une cagnotte solidaire pour offrir des cahiers et cartables aux élèves défavorisés.',
    category: 'Éducation',
    currentAmount: 850,
    goalAmount: 1200,
    icon: 'school'
  },
  {
    id: 'mock-2',
    title: 'Soins vétérinaires d\'urgence',
    description: 'Aidez notre refuge à financer les traitements indispensables pour les animaux blessés.',
    category: 'Animaux',
    currentAmount: 420,
    goalAmount: 1000,
    icon: 'pets'
  },
  {
    id: 'mock-3',
    title: 'Jardin partagé communautaire',
    description: 'Création d\'un espace vert participatif ouvert à tous les habitants de la commune.',
    category: 'Projets',
    currentAmount: 2300,
    goalAmount: 3000,
    icon: 'lightbulb'
  }
])

const categories = [
  { name: 'Santé', icon: 'local_hospital' },
  { name: 'Éducation', icon: 'school' },
  { name: 'Urgence', icon: 'warning' },
  { name: 'Animaux', icon: 'pets' },
  { name: 'Projets', icon: 'lightbulb' },
  { name: 'Sport', icon: 'sports_soccer' }
]

const fetchPools = async () => {
  try {
    const response = await poolApi.get('/pools')
    pools.value = (response.data || []).filter(p => !p.parentId).slice(0, 3)
  } catch (error) {
    console.error('Erreur lors du chargement des cagnottes:', error)
  }
}

const handleSearch = () => {
  const query = {}
  if (searchKeyword.value.trim()) {
    query.search = searchKeyword.value.trim()
  }
  if (selectedCategory.value) {
    query.category = selectedCategory.value
  }
  router.push({ path: '/pools', query })
}

const contribute = (pool) => {
  router.push(`/pools/${pool.id}`)
}

const getProgressValue = (pool) => {
  if (!pool.goalAmount) return 0
  return Math.min(pool.currentAmount / pool.goalAmount, 1)
}

const getPercentageText = (pool) => {
  if (!pool.goalAmount) return '0%'
  const percentage = Math.round((pool.currentAmount / pool.goalAmount) * 100)
  return `${percentage}%`
}

onMounted(() => {
  fetchPools()
})
</script>

<style scoped>
.max-container {
  max-width: 1100px;
  margin: 0 auto;
  width: 100%;
}

/* Simple Badge */
.simple-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(13, 27, 46, 0.04);
  border: 1px solid rgba(13, 27, 46, 0.08);
  padding: 6px 14px;
  border-radius: 6px;
}
.badge-dot {
  width: 7px;
  height: 7px;
  background: #FFA726;
  border-radius: 50%;
  margin-right: 8px;
}
.badge-text {
  font-size: 0.7rem;
  font-weight: 700;
  color: #0D1B2E;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Simple Search Bar */
.search-bar {
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}
.search-input, .select-input {
  font-size: 0.9rem;
}



/* How It Works Steps */
.step-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 167, 38, 0.1);
  color: #FFA726;
  font-weight: 700;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Simple Pool Card */
.simple-pool-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.simple-pool-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.06) !important;
}

.simple-tag {
  background: rgba(13, 27, 46, 0.9);
  color: #FFFFFF;
  font-size: 0.65rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 4px;
  text-transform: uppercase;
}

/* Category Flat Cards */
.category-flat-card {
  background: #FFFFFF;
  border: 1px solid rgba(0,0,0,0.08);
  border-radius: 10px;
  transition: all 0.2s ease;
}
.category-flat-card:hover {
  border-color: #FFA726;
  box-shadow: 0 4px 10px rgba(255, 167, 38, 0.08);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
