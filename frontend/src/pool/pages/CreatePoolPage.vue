<template>
  <q-page class="flex flex-center" style="background: #F5F5F5; padding: 40px 20px;">
    <q-card class="shadow-4" style="width: 100%; max-width: 600px; border-radius: 8px; overflow: hidden;">
      <!-- Header Akkodis Style -->
      <div class="q-pa-lg text-white" style="background: #0D1B2E;">
        <div class="text-h5 text-weight-bold">Créer une nouvelle cagnotte</div>
        <div class="text-subtitle2" style="color: rgba(255,255,255,0.7);">Donnez vie à votre projet solidaire en quelques étapes.</div>
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
            />
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
import { ref, reactive } from 'vue'
import { poolApi } from 'boot/axios'
import { poolService } from 'src/shared/services/poolService'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import authStore from '../../shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)
const imageFile = ref(null)
const imagePreview = ref(null)

const categories = ['Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']

const form = reactive({
  title: '',
  description: '',
  category: 'Santé',
  goalAmount: 1000,
  type: 'PUBLIC',
  imageUrl: '',
  ownerId: ''
})

const onFileSelected = (file) => {
  if (file) {
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imagePreview.value = null
  }
}

const autoFill = () => {
  form.title = 'Soutien pour le refuge animalier de la région'
  form.description = 'Nous collectons des fonds pour aider les animaux abandonnés et assurer leur soins vétérinaires pendant l\'hiver. Votre aide est précieuse pour leur offrir un toit et de la nourriture.'
  form.category = 'Animaux'
  form.goalAmount = 2500
  form.type = 'PUBLIC'
  
  $q.notify({
    icon: 'bolt',
    message: 'Formulaire auto-rempli !',
    color: 'amber-9',
    position: 'top-right',
    timeout: 1000
  })
}

const onSubmit = async () => {
  loading.value = true
  try {
    // 1. Upload de l'image si présente
    if (imageFile.value) {
      try {
        const uploadRes = await poolService.uploadImage(imageFile.value)
        form.imageUrl = uploadRes.data.url
      } catch (uploadError) {
        console.error('Erreur upload:', uploadError)
        $q.notify({
          type: 'negative',
          message: 'Erreur lors de l\'upload de l\'image'
        })
        loading.value = false
        return
      }
    }

    // 2. Création de la cagnotte
    const userId = authStore.user.value?.id
    
    if (!userId) {
      $q.notify({
        type: 'negative',
        message: 'Vous devez être connecté pour créer une cagnotte'
      })
      loading.value = false
      return
    }

    const payload = {
      ...form,
      ownerId: userId
    }

    await poolApi.post('/pools', payload)
    
    $q.notify({
      type: 'positive',
      message: 'Cagnotte créée avec succès !',
      position: 'top'
    })
    
    router.push('/')
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
