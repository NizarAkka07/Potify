<template>
  <q-page class="flex flex-center" style="background: #F5F5F5; padding: 40px 20px;">
    <q-card class="shadow-4" style="width: 100%; max-width: 700px; border-radius: 12px; overflow: hidden;">
      <!-- Header Akkodis Style -->
      <div class="q-pa-lg text-white" style="background: #0D1B2E; border-bottom: 4px solid #FFB300;">
        <div class="row items-center justify-between">
          <div>
            <div class="text-h5 text-weight-bold">
              {{ $t('editPool.title') }}
            </div>
            <div class="text-subtitle2 text-grey-4">
              {{ $t('editPool.subtitle') }}
            </div>
          </div>
          <q-btn flat round icon="arrow_back" color="white" @click="$router.back()">
            <q-tooltip>Retour</q-tooltip>
          </q-btn>
        </div>
      </div>

      <div v-if="fetching" class="q-pa-xl text-center">
        <q-spinner-dots size="40px" color="primary" />
        <div class="text-subtitle2 text-grey-7 q-mt-md">{{ $t('editPool.loading') }}</div>
      </div>

      <q-form v-else @submit="onSubmit" class="q-pa-xl q-gutter-md">
        <!-- Informations de base -->
        <div class="text-subtitle1 text-weight-bold q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('editPool.generalDetails') }}
        </div>

        <q-input
          outlined
          v-model="form.title"
          :label="$t('editPool.titleLabel')"
          lazy-rules
          :rules="[ val => val && val.length > 0 || 'Le titre est obligatoire']"
          color="secondary"
        />

        <q-input
          outlined
          v-model="form.description"
          type="textarea"
          label="Description *"
          lazy-rules
          :rules="[ val => val && val.length > 0 || 'La description est obligatoire']"
          color="secondary"
        />

        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-input
              outlined
              v-model.number="form.goalAmount"
              type="number"
              :label="$t('editPool.goalLabel')"
              suffix="€"
              lazy-rules
              :rules="[ val => val > 0 || 'Le montant doit être supérieur à 0']"
              color="secondary"
              :readonly="hasChildren"
              :hint="hasChildren ? 'Calculé automatiquement à partir des sous-cagnottes' : ''"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-select
              outlined
              v-model="form.status"
              :options="statusOptions"
              emit-value
              map-options
              :label="$t('editPool.statusLabel')"
              color="secondary"
            />
          </div>
        </div>

        <!-- Média de présentation -->
        <div class="text-subtitle1 text-weight-bold q-mt-lg q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('editPool.mediaHeader') }}
        </div>

        <div class="q-mb-md">
          <q-file
            outlined
            v-model="imageFile"
            :label="$t('editPool.changeImageLabel')"
            accept=".jpg, .jpeg, .png, .webp"
            @update:model-value="onFileSelected"
            color="secondary"
          >
            <template v-slot:prepend>
              <q-icon name="cloud_upload" />
            </template>
          </q-file>
          
          <div v-if="imagePreview || currentImageUrl" class="q-mt-md row justify-center">
            <q-img
              :src="imagePreview || currentImageUrl"
              style="max-width: 100%; height: 200px; border-radius: 12px; border: 2px solid #FFB300;"
              fit="cover"
            />
          </div>
        </div>

        <q-input
          outlined
          v-model="form.videoUrl"
          :label="$t('editPool.videoUrlLabel')"
          placeholder="https://www.youtube.com/watch?v=..."
          color="secondary"
        >
          <template v-slot:prepend>
            <q-icon name="movie" />
          </template>
        </q-input>

        <!-- Actions -->
        <div class="row justify-end q-mt-xl q-gutter-sm">
          <q-btn :label="$t('editPool.cancel')" flat color="grey-7" @click="$router.back()" no-caps />
          <q-btn
            :label="$t('editPool.saveChanges')"
            type="submit"
            style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 10px 24px;"
            no-caps
            :loading="submitting"
          />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import poolService from 'src/shared/services/poolService'
import authStore from 'src/shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const route = useRoute()

const fetching = ref(true)
const submitting = ref(false)
const hasChildren = ref(false)

const imageFile = ref(null)
const imagePreview = ref(null)
const currentImageUrl = ref(null)

const statusOptions = [
  { label: 'Active (En cours)', value: 'PUBLIEE' },
  { label: 'Terminée (Succès)', value: 'COMPLETED' },
  { label: 'Clôturée', value: 'CLOTUREE' },
  { label: 'Suspendue', value: 'SUSPENDUE' },
  { label: 'Brouillon', value: 'BROUILLON' },
  { label: 'En revue', value: 'EN_REVUE' }
]

const form = reactive({
  title: '',
  description: '',
  goalAmount: 0,
  status: 'PUBLIEE',
  videoUrl: ''
})

const fetchPoolDetails = async () => {
  try {
    const id = route.params.id
    const response = await poolService.getPoolById(id)
    const pool = response.data

    // Check ownership
    const currentUserId = authStore.user.value?.id
    const isAdmin = authStore.isAdmin.value
    
    if (pool.ownerId && currentUserId && String(pool.ownerId) !== String(currentUserId) && !isAdmin) {
      $q.notify({
        type: 'negative',
        message: t('editPool.errorUnauthorized')
      })
      router.push(`/pools/${id}`)
      return
    }

    form.title = pool.title
    form.description = pool.description
    form.goalAmount = pool.goalAmount
    form.status = pool.status
    form.videoUrl = pool.videoUrl || ''
    currentImageUrl.value = pool.imageUrl
    hasChildren.value = pool.children && pool.children.length > 0
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: t('editPool.errorFetch')
    })
    router.back()
  } finally {
    fetching.value = false
  }
}

const onFileSelected = (file) => {
  if (file) {
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imagePreview.value = null
  }
}

const onSubmit = async () => {
  submitting.value = true
  try {
    const id = route.params.id
    
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

    const payload = {
      title: form.title,
      description: form.description,
      goalAmount: form.goalAmount,
      status: form.status,
      videoUrl: form.videoUrl,
      imageData: imageData,
      imageContentType: imageContentType
    }

    await poolService.updatePool(id, payload)
    
    $q.notify({
      type: 'positive',
      message: t('editPool.success'),
      position: 'top'
    })
    router.push(`/pools/${id}`)
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de la mise à jour : ' + (error.response?.data?.message || 'Serveur injoignable'),
      position: 'top'
    })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchPoolDetails()
})
</script>

<style scoped>
.q-card {
  transition: all 0.3s ease;
}
</style>
