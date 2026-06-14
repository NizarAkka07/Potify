<template>
  <q-page class="flex flex-center bg-grey-1">
    <q-card style="width: 400px; max-width: 90vw;" class="q-pa-lg text-center">
      <div v-if="loading">
        <q-spinner-dots color="primary" size="40px" />
        <div class="q-mt-md text-h6">{{ $t('acceptInvitation.loading') }}</div>
      </div>

      <div v-else-if="success">
        <q-icon name="check_circle" color="positive" size="60px" />
        <div class="q-mt-md text-h5 text-positive">{{ $t('acceptInvitation.successTitle') }}</div>
        <p class="q-mt-md text-body1">
          {{ $t('acceptInvitation.successDesc') }}
        </p>
        <q-btn
          color="primary"
          :label="$t('acceptInvitation.goToPoolButton')"
          class="q-mt-lg full-width"
          @click="goToPool"
        />
      </div>

      <div v-else>
        <q-icon name="error" color="negative" size="60px" />
        <div class="q-mt-md text-h5 text-negative">{{ $t('acceptInvitation.errorTitle') }}</div>
        <p class="q-mt-md text-body1">
          {{ errorMessage }}
        </p>
        <q-btn
          outline
          color="primary"
          :label="$t('acceptInvitation.backHome')"
          class="q-mt-lg full-width"
          to="/"
        />
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { poolApi } from 'boot/axios'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const loading = ref(true)
const success = ref(false)
const errorMessage = ref('')
const poolId = ref(null)

onMounted(async () => {
  const token = route.query.token
  if (!token) {
    loading.value = false
    errorMessage.value = 'Jeton d\'invitation manquant.'
    return
  }

  try {
    const response = await poolApi.post('/invitations/accept', null, {
      params: { token }
    })
    success.value = true
    poolId.value = response.data.poolId
    $q.notify({
      type: 'positive',
      message: t('acceptInvitation.successToast')
    })
  } catch (err) {
    console.error('Erreur acceptation:', err)
    errorMessage.value = err.response?.data?.message || t('verifyEmail.errorDesc')
  } finally {
    loading.value = false
  }
})

const goToPool = () => {
  if (poolId.value) {
    router.push(`/pools/${poolId.value}`)
  } else {
    router.push('/')
  }
}
</script>
