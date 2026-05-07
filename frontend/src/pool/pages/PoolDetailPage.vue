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
              <span class="text-grey-7">Créé par <span class="text-weight-bold text-primary">{{ pool.ownerName || 'Anonyme' }}</span></span>
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
                <span>{{ contributions.length }} participants</span>
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

          <!-- Liste des derniers contributeurs (Sidebar) -->
          <q-card class="q-mt-lg q-pa-lg no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Derniers donateurs ({{ contributions.length }})</div>
            
            <q-list v-if="contributions.length > 0">
              <q-item v-for="contrib in contributions.slice(0, 5)" :key="contrib.id" class="q-px-none q-py-sm">
                <q-item-section avatar>
                  <q-avatar size="32px" color="blue-1" text-color="primary">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ contrib.contributorName.charAt(0) }}</span>
                      <span v-else>?</span>
                    </template>
                    <span v-else>{{ contrib.contributorName.charAt(0) }}</span>
                  </q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold" style="font-size: 0.9rem;">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ contrib.contributorName }} (Anonyme)</span>
                      <span v-else>Donateur anonyme</span>
                    </template>
                    <span v-else>{{ contrib.contributorName }}</span>
                  </q-item-label>
                  <q-item-label caption>{{ contrib.amount }} € • {{ formatDate(contrib.createdAt) }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
            
            <div v-else class="text-center q-py-md text-grey-6 text-italic">
              Soyez le premier à contribuer !
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
            <div class="text-body1 text-grey-8 q-mb-xl" style="white-space: pre-line; line-height: 1.6;">
              {{ pool.description }}
            </div>

            <!-- SECTION : COMMENTAIRES (Messages du Pool) -->
            <div class="text-h5 text-weight-bold q-mb-lg row items-center" style="color: #0D1B2E;">
              <q-icon name="forum" class="q-mr-sm" color="primary" />
              Commentaires et Questions ({{ messages.length }})
            </div>

            <!-- Champ de saisie pour nouveau commentaire -->
            <div v-if="authStore.isAuthenticated.value" class="q-mb-xl">
              <q-input
                filled
                v-model="newComment"
                type="textarea"
                placeholder="Posez une question ou laissez un message de soutien..."
                rows="3"
                color="secondary"
                bg-color="white"
                class="shadow-1"
                style="border-radius: 8px;"
              >
                <template v-slot:after>
                  <div class="column justify-end full-height q-pb-xs">
                    <q-btn round color="primary" icon="send" :loading="sendingComment" @click="postComment" />
                  </div>
                </template>
              </q-input>
            </div>
            <div v-else class="q-pa-md bg-blue-1 text-blue-9 text-weight-bold q-mb-xl" style="border-radius: 8px; border: 1px dashed #2196F3;">
              <q-icon name="info" class="q-mr-sm" />
              Veuillez vous connecter pour laisser un commentaire.
            </div>

            <!-- Liste des commentaires -->
            <div v-if="messages.length > 0" class="q-gutter-y-md">
              <div v-for="msg in messages" :key="msg.id" class="comment-item q-pa-md" style="background: white; border-radius: 12px; border: 1px solid #EEE;">
                <div class="row justify-between items-center q-mb-sm">
                  <div class="row items-center">
                    <q-avatar size="32px" color="primary" text-white class="q-mr-sm">
                      {{ msg.userName?.charAt(0) || '?' }}
                    </q-avatar>
                    <div>
                      <div class="text-weight-bold" style="color: #0D1B2E;">
                        {{ msg.userName || 'Utilisateur' }}
                      </div>
                      <div class="text-caption text-grey-6">{{ formatDate(msg.createdAt) }}</div>
                    </div>
                  </div>
                </div>
                <div class="text-body2 text-grey-9 q-pl-md border-left" style="border-left: 2px solid #EEE;">
                  {{ msg.content }}
                </div>
              </div>
            </div>
            <div v-else class="text-center q-py-xl text-grey-5">
              <q-icon name="chat_bubble_outline" size="48px" class="q-mb-sm" />
              <div>Aucun commentaire pour le moment. Soyez le premier !</div>
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

    <!-- Dialogue de Contribution -->
    <q-dialog v-model="contributionDialog" persistent>
      <q-card style="min-width: 400px; border-radius: 16px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold">Faire un don</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <div class="text-subtitle2 q-mb-md text-grey-7">
            Soutenez "{{ pool?.title }}" en choisissant un montant.
          </div>

          <q-form @submit="submitContribution" class="q-gutter-md">
            <q-input
              outlined
              v-model.number="contributionForm.amount"
              type="number"
              label="Montant du don (€)"
              suffix="€"
              :rules="[val => val > 0 || 'Le montant doit être supérieur à 0']"
              color="secondary"
              autofocus
            />

            <q-input
              v-if="!authStore.isAuthenticated.value"
              outlined
              v-model="contributionForm.contributorName"
              label="Votre nom (facultatif)"
              placeholder="Ex: Jean Dupont"
              color="secondary"
            />

            <q-input
              outlined
              v-model="contributionForm.message"
              type="textarea"
              label="Petit message de soutien (facultatif)"
              placeholder="Votre message sera affiché sur la page..."
              color="secondary"
              rows="3"
            />

            <q-checkbox
              v-model="contributionForm.anonymous"
              label="Faire ce don de manière anonyme"
              color="secondary"
            />

            <div class="q-mt-lg">
              <q-btn
                label="Confirmer le paiement"
                type="submit"
                class="full-width q-py-sm text-weight-bold"
                style="background: #FFB300; color: #1A1A2A;"
                :loading="submitting"
                no-caps
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'
import { poolApi } from 'boot/axios'
import { useQuasar } from 'quasar'
import authStore from 'src/shared/stores/auth'

const route = useRoute()
const $q = useQuasar()

const loading = ref(true)
const error = ref(null)
const pool = ref(null)
const contributions = ref([])
const messages = ref([])

const isOwner = computed(() => {
  return authStore.isAuthenticated.value && pool.value?.ownerId === authStore.user.value?.id
})

// Gestion des commentaires (Pool Messages)
const newComment = ref('')
const sendingComment = ref(false)

const postComment = async () => {
  if (!newComment.value.trim()) return
  
  sendingComment.value = true
  try {
    await poolApi.post('/messages', {
      poolId: pool.value.id,
      userId: authStore.user.value?.id,
      content: newComment.value,
      isPublic: true // Tous les messages sont désormais publics
    })
    
    newComment.value = ''
    $q.notify({
      type: 'positive',
      message: 'Commentaire publié !',
      position: 'bottom'
    })
    await fetchMessages()
  } catch (err) {
    console.error('Erreur publication commentaire:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de la publication.'
    })
  } finally {
    sendingComment.value = false
  }
}

// Gestion des contributions
const contributionDialog = ref(false)
const submitting = ref(false)
const contributionForm = reactive({
  amount: 20,
  contributorName: '',
  message: '',
  anonymous: false
})

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

const fetchContributions = async () => {
  try {
    const id = route.params.id
    const response = await poolService.getPoolContributions(id)
    contributions.value = response.data
  } catch (err) {
    console.error('Erreur chargement contributions:', err)
  }
}

const fetchMessages = async () => {
  try {
    const id = route.params.id
    const response = await poolApi.get(`/messages/pool/${id}`)
    messages.value = response.data
  } catch (err) {
    console.error('Erreur chargement messages:', err)
  }
}

const fetchPool = async () => {
  loading.value = true
  error.value = null
  try {
    const id = route.params.id
    const response = await poolService.getPoolById(id)
    pool.value = response.data
    await fetchContributions()
    await fetchMessages()
  } catch (err) {
    console.error('Erreur chargement cagnotte:', err)
    error.value = 'Cagnotte introuvable.'
  } finally {
    loading.value = false
  }
}

const contribute = () => {
  contributionDialog.value = true
}

const submitContribution = async () => {
  submitting.value = true
  try {
    const isAuth = authStore.isAuthenticated.value
    const user = authStore.user.value
    
    // Détermination du nom du contributeur
    let name = contributionForm.contributorName || 'Donateur anonyme'
    if (isAuth && user) {
      name = `${user.firstName} ${user.lastName}`
    }

    const payload = {
      poolId: pool.value.id,
      userId: user?.id || null,
      amount: contributionForm.amount,
      contributorName: name,
      message: contributionForm.message,
      anonymous: contributionForm.anonymous,
      paymentMethod: 'CARD' // Simulé pour l'instant
    }

    await poolService.contributeToPool(payload)

    $q.notify({
      type: 'positive',
      message: 'Merci pour votre contribution !',
      position: 'top'
    })

    contributionDialog.value = false
    // Recharger la cagnotte et les contributions
    await fetchPool()
  } catch (err) {
    console.error('Erreur contribution:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du traitement du don.'
    })
  } finally {
    submitting.value = false
  }
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
