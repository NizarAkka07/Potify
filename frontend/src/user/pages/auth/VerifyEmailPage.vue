<template>
  <q-page class="flex flex-center bg-grey-1">
    <q-card class="q-pa-xl shadow-2" style="width: 450px; border-radius: 16px;">
      <q-card-section class="text-center">
        <div v-if="loading">
          <q-spinner-oval color="primary" size="4em" />
          <div class="text-h6 q-mt-md">Vérification de votre email...</div>
        </div>

        <div v-else-if="success">
          <q-icon name="check_circle" color="positive" size="5em" />
          <div class="text-h5 text-weight-bold q-mt-md" style="color: #0D1B2E;">Félicitations !</div>
          <div class="text-subtitle1 q-mt-sm text-grey-7">
            Votre email a été vérifié avec succès.
          </div>
          <q-btn
            label="Se connecter"
            color="primary"
            class="full-width q-mt-xl text-weight-bold"
            size="lg"
            to="/login"
            no-caps
          />
        </div>

        <div v-else>
          <q-icon name="error" color="negative" size="5em" />
          <div class="text-h5 text-weight-bold q-mt-md" style="color: #0D1B2E;">Oups !</div>
          <div class="text-subtitle1 q-mt-sm text-grey-7">
            {{ errorMessage || 'Le lien de vérification est invalide ou a expiré.' }}
          </div>
          <q-btn
            label="Retour à l'accueil"
            flat
            color="primary"
            class="full-width q-mt-xl"
            to="/"
            no-caps
          />
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from 'boot/axios'

const route = useRoute()
const loading = ref(true)
const success = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  const token = route.query.token
  
  if (!token) {
    loading.value = false
    success.value = false
    errorMessage.value = 'Jeton de vérification manquant.'
    return
  }

  try {
    await api.get(`/auth/verify-email?token=${token}`)
    success.value = true
  } catch (error) {
    success.value = false
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.q-card {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05) !important;
}
</style>
