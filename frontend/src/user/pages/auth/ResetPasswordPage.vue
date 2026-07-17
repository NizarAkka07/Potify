<template>
  <!-- Page de réinitialisation du mot de passe -->
  <q-page class="flex flex-center auth-page">
    <q-card class="auth-card">
      <q-card-section class="q-pa-xl">
        <div class="q-mb-lg">
          <div class="text-h5 text-weight-bolder text-secondary font-inter">Nouveau mot de passe</div>
          <div class="text-caption text-grey-6 q-mt-xs">Définissez votre nouveau mot de passe</div>
        </div>

        <!-- Message de succès -->
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template v-slot:avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          Mot de passe réinitialisé avec succès !
        </q-banner>

        <!-- Message d'erreur -->
        <q-banner v-if="errorMsg" class="bg-negative text-white q-mb-md" rounded>
          <template v-slot:avatar>
            <q-icon name="error" color="white" />
          </template>
          {{ errorMsg }}
        </q-banner>

        <q-form v-if="!success" @submit="onSubmit" class="q-gutter-y-md">
          <!-- Nouveau mot de passe -->
          <q-input
            v-model="form.newPassword"
            label="Nouveau mot de passe"
            :type="showPassword ? 'text' : 'password'"
            outlined
            color="secondary"
            lazy-rules
            :rules="[
              val => !!val || 'Le mot de passe est obligatoire',
              val => val.length >= 8 || 'Minimum 8 caractères',
              val => /[A-Z]/.test(val) || 'Au moins une majuscule',
              val => /[0-9]/.test(val) || 'Au moins un chiffre'
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" color="grey-5" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="showPassword ? 'visibility' : 'visibility_off'"
                class="cursor-pointer"
                color="grey-5"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <!-- Confirmation du mot de passe -->
          <q-input
            v-model="form.confirmPassword"
            label="Confirmer le mot de passe"
            :type="showConfirm ? 'text' : 'password'"
            outlined
            color="secondary"
            lazy-rules
            :rules="[
              val => !!val || 'La confirmation est obligatoire',
              val => val === form.newPassword || 'Les mots de passe ne correspondent pas'
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock_outline" color="grey-5" />
            </template>
            <template v-slot:append>
              <q-icon
                :name="showConfirm ? 'visibility' : 'visibility_off'"
                class="cursor-pointer"
                color="grey-5"
                @click="showConfirm = !showConfirm"
              />
            </template>
          </q-input>

          <!-- Bouton de soumission -->
          <div class="row justify-end q-mt-lg">
            <q-btn
              unelevated
              no-caps
              label="Réinitialiser"
              type="submit"
              class="submit-btn text-weight-bold"
              :loading="loading"
              icon-right="lock_reset"
            />
          </div>
        </q-form>

        <!-- Bouton retour si succès -->
        <div v-if="success" class="row justify-center q-mt-lg">
          <q-btn
            unelevated
            no-caps
            color="secondary"
            label="Se connecter"
            to="/login"
            icon="login"
            class="text-weight-bold"
            style="border-radius: 12px; padding: 10px 28px;"
          />
        </div>
      </q-card-section>

      <!-- Section de retour vers la connexion -->
      <q-card-section v-if="!success" class="text-center q-py-lg register-section">
        <span class="text-grey-7">Vous vous souvenez ? </span>
        <q-btn flat no-caps dense color="secondary" label="Se connecter" to="/login" class="text-weight-bold hover-underline" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const $q = useQuasar()
const route = useRoute()
const loading = ref(false)
const success = ref(false)
const errorMsg = ref('')
const showPassword = ref(false)
const showConfirm = ref(false)

const form = ref({
  newPassword: '',
  confirmPassword: ''
})

const token = ref('')

onMounted(() => {
  token.value = route.query.token || ''
  if (!token.value) {
    errorMsg.value = 'Lien de réinitialisation invalide. Veuillez refaire une demande.'
  }
})

const onSubmit = async () => {
  if (!token.value) {
    errorMsg.value = 'Token manquant. Veuillez refaire une demande de réinitialisation.'
    return
  }

  loading.value = true
  errorMsg.value = ''
  try {
    await api.post('/auth/reset-password', {
      token: token.value,
      newPassword: form.value.newPassword
    })
    success.value = true
    $q.notify({
      color: 'positive',
      message: 'Mot de passe réinitialisé avec succès !',
      icon: 'check_circle',
      position: 'top'
    })
  } catch (error) {
    errorMsg.value = error.response?.data || 'Lien de réinitialisation invalide ou expiré.'
    $q.notify({
      color: 'negative',
      message: errorMsg.value,
      icon: 'report_problem',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  background: #F8F9FA;
  background-image: radial-gradient(circle at 10% 20%, rgba(0, 230, 118, 0.04) 0%, transparent 40%),
                    radial-gradient(circle at 90% 80%, rgba(6, 47, 37, 0.03) 0%, transparent 45%);
}

.auth-card {
  width: 100%;
  max-width: 440px;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(6, 47, 37, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: #FFFFFF;
  overflow: hidden;
}

.font-inter {
  font-family: 'Inter', sans-serif;
}

.submit-btn {
  background: #00e676 !important;
  color: #062f25 !important;
  font-weight: 700;
  border-radius: 12px;
  padding: 10px 28px;
  box-shadow: 0 4px 12px rgba(0, 230, 118, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease, filter 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 18px rgba(0, 230, 118, 0.3);
    filter: brightness(1.05);
  }
}

.register-section {
  background: #F8F9FA;
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}

.hover-underline:hover {
  text-decoration: underline;
}

:deep(.q-field--outlined .q-field__control) {
  border-radius: 12px !important;
}
:deep(.q-field--outlined .q-field__control::before) {
  border-color: rgba(0, 0, 0, 0.08) !important;
}
:deep(.q-field--focused .q-field__control::after) {
  border-width: 2px !important;
}
</style>
