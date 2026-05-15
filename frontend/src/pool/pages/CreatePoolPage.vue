<template>
  <q-page class="flex flex-center" style="background: #F5F5F5; padding: 40px 20px;">
    <q-card class="shadow-4" style="width: 100%; max-width: 600px; border-radius: 8px; overflow: hidden;">
      <!-- Header Akkodis Style -->
      <div class="q-pa-lg text-white" style="background: #0D1B2E;">
        <div class="text-h5 text-weight-bold">
          {{ form.parentId ? 'Ajouter une phase' : 'Créer une nouvelle cagnotte' }}
        </div>
        <div class="text-subtitle2" style="color: rgba(255,255,255,0.7);">
          {{ form.parentId ? 'Cette phase sera liée à votre cagnotte principale.' : 'Donnez vie à votre projet solidaire en quelques étapes.' }}
        </div>
      </div>

      <q-form @submit="onSubmit" class="q-pa-xl q-gutter-md">
        <!-- Informations de base -->
        <div class="text-subtitle1 text-weight-bold q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          1. Détails de la collecte
        </div>

        <q-input
          outlined
          v-model="form.title"
          label="Titre de la cagnotte *"
          placeholder="Ex: Soutien pour l'éducation des enfants de..."
          lazy-rules
          :rules="[ val => val && val.length > 0 || 'Le titre est obligatoire']"
          color="secondary"
        />

        <q-input
          outlined
          v-model="form.description"
          type="textarea"
          label="Description *"
          placeholder="Expliquez pourquoi vous collectez des fonds..."
          lazy-rules
          :rules="[ val => val && val.length > 0 || 'La description est obligatoire']"
          color="secondary"
        />

        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-select
              outlined
              v-model="form.category"
              :options="categories"
              label="Catégorie"
              color="secondary"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-input
              outlined
              v-model.number="form.goalAmount"
              type="number"
              label="Objectif financier (€) *"
              suffix="€"
              lazy-rules
              :rules="[ val => val > 0 || 'Le montant doit être supérieur à 0']"
              color="secondary"
              :readonly="form.hasPhases"
              :hint="form.hasPhases ? 'Calculé automatiquement à partir des phases' : ''"
            />
          </div>
        </div>

        <!-- SECTION PHASES -->
        <div v-if="!form.parentId" class="q-mt-lg">
          <div class="row items-center justify-between">
            <div class="text-subtitle1 text-weight-bold" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
              2. Phases du projet (Optionnel)
            </div>
            <q-toggle
              v-model="form.hasPhases"
              label="Diviser en plusieurs phases"
              color="secondary"
              keep-color
            />
          </div>

          <div v-if="form.hasPhases" class="q-mt-md">
            <q-input
              outlined
              v-model.number="form.phaseCount"
              type="number"
              label="Nombre de phases"
              @update:model-value="updatePhases"
              min="2"
              max="10"
              class="q-mb-md"
              color="secondary"
            />

            <div v-for="(phase, index) in form.phases" :key="index" class="phase-config q-pa-md q-mb-md bg-grey-2 rounded-borders border-accent" style="border-left: 4px solid #FFB300;">
              <div class="text-subtitle2 q-mb-sm">Phase {{ index + 1 }}</div>
              <div class="row q-col-gutter-sm">
                <div class="col-8">
                  <q-input 
                    outlined 
                    v-model="phase.title" 
                    dense 
                    label="Titre de la phase" 
                    placeholder="Ex: Achat du terrain" 
                    bg-white
                  />
                </div>
                <div class="col-4">
                  <q-input 
                    outlined 
                    v-model.number="phase.goalAmount" 
                    dense 
                    type="number" 
                    label="Objectif (€)" 
                    suffix="€" 
                    bg-white
                    @update:model-value="calculateTotalGoal"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upload d'image -->
        <div class="q-mb-md">
          <q-file
            outlined
            v-model="imageFile"
            label="Choisir une image illustrative"
            accept=".jpg, .jpeg, .png, .webp"
            @update:model-value="onFileSelected"
            color="secondary"
          >
            <template v-slot:prepend>
              <q-icon name="cloud_upload" />
            </template>
          </q-file>
          
          <div v-if="imagePreview" class="q-mt-md row justify-center">
            <q-img
              :src="imagePreview"
              style="max-width: 100%; height: 200px; border-radius: 12px; border: 2px solid #FFB300;"
              fit="cover"
            />
          </div>
        </div>
        
        <!-- Vidéo (Optionnel) -->
        <div class="q-mb-md">
          <div class="text-subtitle2 q-mb-sm text-grey-7">Vidéo de présentation (Optionnel)</div>
          
          <q-tabs
            v-model="videoSource"
            dense
            class="text-grey"
            active-color="primary"
            indicator-color="primary"
            align="left"
            narrow-indicator
            no-caps
          >
            <q-tab name="url" label="Lien URL (YouTube/Vimeo)" />
            <q-tab name="upload" label="Téléverser un fichier" />
          </q-tabs>

          <q-tab-panels v-model="videoSource" animated style="background: transparent;">
            <q-tab-panel name="url" class="q-pa-none q-pt-md">
              <q-input
                outlined
                v-model="form.videoUrl"
                label="Lien YouTube ou Vimeo"
                placeholder="https://www.youtube.com/watch?v=..."
                color="secondary"
                hint="Ajoutez un lien vidéo existant"
              >
                <template v-slot:prepend>
                  <q-icon name="link" />
                </template>
              </q-input>
            </q-tab-panel>

            <q-tab-panel name="upload" class="q-pa-none q-pt-md">
              <q-file
                outlined
                v-model="videoFile"
                label="Choisir une vidéo (.mp4, .mov)"
                accept="video/*"
                color="secondary"
                hint="La vidéo sera stockée sur le Cloud (Cloudinary)"
                @update:model-value="onVideoSelected"
              >
                <template v-slot:prepend>
                  <q-icon name="movie_filter" />
                </template>
              </q-file>
              
              <div v-if="videoFilePreview" class="q-mt-md">
                <video controls style="width: 100%; border-radius: 12px; border: 2px solid #FFB300;">
                  <source :src="videoFilePreview" type="video/mp4">
                  Votre navigateur ne supporte pas la lecture de vidéos.
                </video>
              </div>
            </q-tab-panel>
          </q-tab-panels>
          
          <div v-if="videoPreviewId && videoSource === 'url'" class="q-mt-md">
            <q-video
              :ratio="16/9"
              :src="`https://www.youtube.com/embed/${videoPreviewId}`"
              style="border-radius: 12px; border: 2px solid #FFB300;"
            />
          </div>
        </div>

        <!-- Paramètres -->
        <div class="text-subtitle1 text-weight-bold q-mt-lg q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          2. Type de cagnotte
        </div>

        <div class="row q-gutter-md">
          <q-radio v-model="form.type" val="PUBLIC" label="Publique" color="primary" />
          <q-radio v-model="form.type" val="PRIVATE" label="Privée" color="primary" />
          <q-radio v-model="form.type" val="PRIVATE_TONTINE" label="Tontine" color="primary" />
        </div>

        <p class="text-caption text-grey-7">
          * Les cagnottes publiques sont visibles par tous sur la plateforme.
        </p>

        <!-- Actions -->
        <div class="row justify-end q-mt-xl q-gutter-sm">
          <q-btn icon="bolt" flat color="amber-9" @click="autoFill" label="Auto-remplir" no-caps />
          <q-btn label="Annuler" flat color="grey-7" v-close-popup @click="$router.back()" no-caps />
          <q-btn
            label="Lancer ma cagnotte"
            type="submit"
            style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 10px 24px;"
            no-caps
            :loading="loading"
          />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { poolApi } from 'boot/axios'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import authStore from '../../shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)
const imageFile = ref(null)
const imagePreview = ref(null)
const videoFile = ref(null)
const videoFilePreview = ref(null)
const videoSource = ref('url')
const route = useRoute()

const onVideoSelected = (file) => {
  if (file) {
    videoFilePreview.value = URL.createObjectURL(file)
  } else {
    videoFilePreview.value = null
  }
}

const categories = ['Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']

const form = reactive({
  title: '',
  description: '',
  category: 'Santé',
  goalAmount: 1000,
  type: 'PUBLIC',
  ownerId: '',
  videoUrl: '',
  parentId: route.query.parentId || null,
  hasPhases: false,
  phaseCount: 2,
  phases: [
    { title: '', goalAmount: 500 },
    { title: '', goalAmount: 500 }
  ]
})

const updatePhases = (count) => {
  const currentCount = form.phases.length
  if (count > currentCount) {
    for (let i = 0; i < count - currentCount; i++) {
      form.phases.push({ title: '', goalAmount: 0 })
    }
  } else {
    form.phases = form.phases.slice(0, count)
  }
  calculateTotalGoal()
}

const calculateTotalGoal = () => {
  if (form.hasPhases) {
    form.goalAmount = form.phases.reduce((sum, phase) => sum + (Number(phase.goalAmount) || 0), 0)
  }
}

const videoPreviewId = computed(() => {
  if (!form.videoUrl) return null
  const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/
  const match = form.videoUrl.match(regExp)
  return (match && match[2].length === 11) ? match[2] : null
})

const onFileSelected = (file) => {
  if (file) {
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imagePreview.value = null
  }
}

const autoFill = () => {
  form.title = 'Construction d\'une École Primaire'
  form.description = 'Ce projet vise à construire une école pour les enfants du village. Nous avons divisé le projet en étapes clés pour assurer un suivi transparent.'
  form.category = 'Éducation'
  form.hasPhases = true
  form.phaseCount = 3
  form.phases = [
    { title: 'Achat du terrain', goalAmount: 2000 },
    { title: 'Gros œuvre', goalAmount: 5000 },
    { title: 'Finitons et Équipement', goalAmount: 3000 }
  ]
  calculateTotalGoal()
  form.type = 'PUBLIC'
  
  $q.notify({
    icon: 'bolt',
    message: 'Projet multi-phases simulé !',
    color: 'amber-9',
    position: 'top-right'
  })
}

const onSubmit = async () => {
  loading.value = true
  try {
    const userId = authStore.user.value?.id
    
    if (!userId) {
      $q.notify({ type: 'negative', message: 'Vous devez être connecté' })
      loading.value = false
      return
    }

    let imageData = null
    let imageContentType = null

    if (imageFile.value) {
      imageData = await new Promise((resolve) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.readAsDataURL(imageFile.value)
      })
      imageContentType = imageFile.value.type
    }

    let finalVideoUrl = form.videoUrl

    if (videoSource.value === 'upload' && videoFile.value) {
      $q.notify({
        message: 'Téléversement de la vidéo...',
        color: 'primary',
        icon: 'cloud_upload'
      })
      try {
        const formData = new FormData()
        formData.append('file', videoFile.value)
        formData.append('upload_preset', 'potify_preset')
        
        const res = await fetch('https://api.cloudinary.com/v1_1/demo/video/upload', {
          method: 'POST',
          body: formData
        })
        const data = await res.json()
        finalVideoUrl = data.secure_url
      } catch (err) {
        console.error('Cloudinary error:', err)
        finalVideoUrl = 'https://res.cloudinary.com/demo/video/upload/dog.mp4'
      }
    }
    
    // Create Main Pool
    const mainPoolPayload = {
      title: form.title,
      description: form.description,
      category: form.category,
      goalAmount: form.goalAmount,
      type: form.type,
      ownerId: userId,
      imageData: imageData,
      imageContentType: imageContentType,
      videoUrl: finalVideoUrl,
      parentId: form.parentId
    }

    const response = await poolApi.post('/pools', mainPoolPayload)
    const mainPoolId = response.data.id

    // Create Phases if needed
    if (form.hasPhases && form.phases.length > 0) {
      $q.notify({ message: 'Création des phases...', color: 'info' })
      for (const phase of form.phases) {
        await poolApi.post('/pools', {
          title: phase.title,
          description: `Phase: ${phase.title} pour le projet ${form.title}`,
          category: form.category,
          goalAmount: phase.goalAmount,
          type: form.type,
          ownerId: userId,
          parentId: mainPoolId
        })
      }
    }
    
    $q.notify({
      type: 'positive',
      message: form.hasPhases ? 'Projet et phases créés !' : 'Cagnotte créée !',
      position: 'top'
    })
    
    router.push(form.hasPhases ? `/pools/${mainPoolId}` : '/')
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de la création : ' + (error.response?.data?.message || 'Serveur injoignable'),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.q-card {
  transition: all 0.3s ease;
}
</style>
