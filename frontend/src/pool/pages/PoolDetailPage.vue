<template>
  <q-page class="pool-detail-page" style="background: #F8F9FA; padding-bottom: 60px;">
    <!-- État de chargement -->
    <div v-if="loading" class="flex flex-center" style="height: 60vh;">
      <q-spinner-dots color="primary" size="40px" />
    </div>

    <!-- État d'erreur -->
    <div v-else-if="error" class="flex flex-center" style="height: 60vh;">
      <div class="text-center">
        <q-icon name="error_outline" color="negative" size="64px" />
        <div class="text-h6 q-mt-md">{{ error }}</div>
        <q-btn flat color="primary" label="Retour à l'accueil" class="q-mt-md" @click="$router.push('/')" />
      </div>
    </div>

    <!-- Contenu -->
    <div v-else-if="pool" class="container q-mx-auto" style="max-width: 1000px; padding: 0 20px;">
      <!-- Section Hero avec Image -->
      <div class="row q-col-gutter-lg q-pt-lg">
        <div class="col-12 col-md-7">
          <q-card class="image-card no-shadow" style="border-radius: 16px; overflow: hidden;">
            <q-img
              :src="pool.imageUrl || getPoolImage(pool.category)"
              style="height: 450px;"
              fit="cover"
            >
              <div class="absolute-top-left q-ma-md">
                <q-chip color="secondary" text-white class="text-weight-bold shadow-2">
                  {{ pool.category }}
                </q-chip>
              </div>
            </q-img>
          </q-card>
        </div>

        <!-- Sidebar / Actions -->
        <div class="col-12 col-md-5">
          <q-card class="action-card q-pa-lg no-shadow shadow-1" style="border-radius: 16px; border: 1px solid #EEE; background: white;">
            <div class="text-h4 text-weight-bold q-mb-md" style="color: #0D1B2E; line-height: 1.2;">
              {{ pool.title }}
            </div>

            <div class="flex items-center q-mb-lg">
              <q-avatar size="32px" class="q-mr-sm">
                <img src="https://cdn.quasar.dev/img/avatar.png">
              </q-avatar>
              <span class="text-grey-7">Créé par <span class="text-weight-bold text-primary">Utilisateur</span></span>
            </div>

            <!-- Progression -->
            <div class="q-mb-xl">
              <div class="row items-end justify-between q-mb-sm">
                <div class="text-h4 text-weight-bolder text-primary">
                  {{ pool.currentAmount || 0 }} €
                </div>
                <div class="text-grey-7">
                  sur {{ pool.goalAmount }} €
                </div>
              </div>
              
              <q-linear-progress 
                :value="(pool.currentAmount || 0) / pool.goalAmount" 
                color="secondary" 
                size="12px" 
                rounded 
                class="q-mb-sm"
              />
              
              <div class="row justify-between text-caption text-grey-6 text-weight-medium">
                <span>{{ Math.round(((pool.currentAmount || 0) / pool.goalAmount) * 100) }}% complété</span>
                <span>0 participants</span>
              </div>
            </div>

            <!-- Bouton principal -->
            <q-btn
              label="Contribuer maintenant"
              unelevated
              class="full-width q-py-md text-weight-bold"
              style="background: #FFB300; color: #1A1A2A; border-radius: 12px; font-size: 1.1rem;"
              no-caps
              @click="contribute"
            />
            
            <div class="row q-gutter-sm q-mt-md">
              <q-btn outline color="primary" icon="share" label="Partager" class="col" no-caps style="border-radius: 10px;" />
              <q-btn outline color="grey-7" icon="favorite_border" class="col-auto" style="border-radius: 10px;" />
            </div>
          </q-card>
        </div>
      </div>

      <!-- Section Détails -->
      <div class="row q-col-gutter-lg q-mt-lg">
        <div class="col-12 col-md-7">
          <q-card class="details-card q-pa-xl no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="text-h5 text-weight-bold q-mb-lg" style="color: #0D1B2E;">
              À propos de cette cagnotte
            </div>
            <div class="text-body1 text-grey-8" style="white-space: pre-line; line-height: 1.6;">
              {{ pool.description }}
            </div>

            <q-separator class="q-my-xl" />

            <div class="text-h6 text-weight-bold q-mb-md">Comment ça marche ?</div>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="security" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">Sécurisé</div>
                <div class="text-caption text-grey-7">Paiements protégés</div>
              </div>
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="history" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">Transparent</div>
                <div class="text-caption text-grey-7">Suivi des fonds</div>
              </div>
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="group" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">Collectif</div>
                <div class="text-caption text-grey-7">Ensemble on va plus loin</div>
              </div>
            </div>
          </q-card>
        </div>

        <!-- Sidebar Secondaire -->
        <div class="col-12 col-md-5">
          <q-card class="info-card q-pa-lg no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Informations</div>
            <q-list dense>
              <q-item>
                <q-item-section avatar>
                  <q-icon name="calendar_today" color="grey-6" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-grey-7">Date de creation</q-item-label>
                  <q-item-label class="text-weight-bold">{{ formatDate(pool.createdAt) }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item class="q-mt-sm">
                <q-item-section avatar>
                  <q-icon name="public" color="grey-6" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-grey-7">Visibilite</q-item-label>
                  <q-item-label class="text-weight-bold">Publique</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'
import { useQuasar } from 'quasar'

const route = useRoute()
const $q = useQuasar()

const loading = ref(true)
const error = ref(null)
const pool = ref(null)

const getPoolImage = (category) => {
  const images = {
    'Santé': 'https://images.unsplash.com/photo-1505751172107-5739a00774ad?q=80&w=1000&auto=format&fit=crop',
    'Éducation': 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?q=80&w=1000&auto=format&fit=crop',
    'Urgence': 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=1000&auto=format&fit=crop',
    'Animaux': 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?q=80&w=1000&auto=format&fit=crop',
    'Projets': 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?q=80&w=1000&auto=format&fit=crop',
    'Sport': 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=1000&auto=format&fit=crop'
  }
  return images[category] || 'https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?q=80&w=1000&auto=format&fit=crop'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

const fetchPool = async () => {
  loading.value = true
  error.value = null
  try {
    const id = route.params.id
    const response = await poolService.getPoolById(id)
    pool.value = response.data
  } catch (err) {
    console.error('Erreur chargement cagnotte:', err)
    error.value = 'Cagnotte introuvable.'
  } finally {
    loading.value = false
  }
}

const contribute = () => {
  $q.notify({
    message: 'Le module de paiement sera bientôt disponible !',
    color: 'amber-9',
    icon: 'payment'
  })
}

onMounted(fetchPool)
</script>

<style scoped>
.image-card {
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.action-card {
  position: sticky;
  top: 100px;
}
@media (max-width: 991px) {
  .action-card {
    position: static;
  }
}
</style>
