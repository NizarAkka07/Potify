<template>
  <!-- Page de connexion centrée avec un arrière-plan à gradient doux -->
  <q-page class="flex flex-center auth-page">
    <!-- Carte d'authentification avec bords arrondis et ombre subtile -->
    <q-card class="auth-card">
      <!-- Section d'en-tête de la carte en bleu marine Akkodis -->
      <q-card-section class="auth-header text-center q-py-xl">
        <div class="logo-container q-mb-md">
          <q-icon name="payments" size="3rem" color="primary" />
        </div>
        <div class="text-h5 text-weight-bold text-white font-inter">{{ $t('auth.loginTitle') }}</div>
        <div class="text-caption text-grey-4 q-mt-xs">{{ $t('auth.loginSubtitle') }}</div>
      </q-card-section>

      <!-- Section du formulaire de connexion -->
      <q-card-section class="q-pa-xl">
        <q-form @submit="onSubmit" class="q-gutter-y-md">
          <!-- Champ de saisie pour l'email (Outlined) -->
          <q-input
            v-model="loginForm.email"
            :label="$t('auth.email')"
            type="email"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || $t('auth.emailRequired')]"
          >
            <!-- Icône d'enveloppe -->
            <template v-slot:prepend>
              <q-icon name="email" color="grey-6" />
            </template>
          </q-input>

          <!-- Champ de saisie pour le mot de passe (Outlined) -->
          <q-input
            v-model="loginForm.password"
            :label="$t('auth.password')"
            type="password"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || $t('auth.passwordRequired')]"
          >
            <!-- Icône de cadenas -->
            <template v-slot:prepend>
              <q-icon name="lock" color="grey-6" />
            </template>
          </q-input>

          <!-- Actions : Mot de passe oublié et Bouton de connexion -->
          <div class="row justify-between items-center q-mt-lg">
            <q-btn flat no-caps color="grey-7" :label="$t('auth.forgotPassword')" size="sm" class="forgot-btn" />
            <q-btn
              unelevated
              no-caps
              :label="$t('auth.loginButton')"
              type="submit"
              class="submit-btn text-weight-bold"
              :loading="loading"
            />
          </div>
        </q-form>
      </q-card-section>

      <!-- Section de redirection pour les nouveaux utilisateurs -->
      <q-card-section class="text-center q-py-lg register-section">
        <span class="text-grey-7">{{ $t('auth.noAccount') }} </span>
        <q-btn flat no-caps dense color="primary" :label="$t('auth.signUp')" to="/register" class="text-weight-bold" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'
import authStore from 'src/shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)

const loginForm = ref({
  email: '',
  password: ''
})

const onSubmit = async () => {
  loading.value = true
  try {
    const response = await api.post('/auth/signin', loginForm.value)
    const { accessToken } = response.data

    authStore.setToken(accessToken)
    authStore.setUser({ 
      id: response.data.id,
      email: response.data.email, 
      roles: response.data.roles,
      fullName: response.data.fullName,
      avatarUrl: response.data.avatarUrl
    })

    $q.notify({
      color: 'positive',
      message: t('auth.success'),
      icon: 'check_circle',
      position: 'top'
    })

    router.push('/')
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || t('auth.error'),
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
  background: radial-gradient(circle at 50% 50%, rgba(139, 92, 246, 0.05) 0%, transparent 60%),
              radial-gradient(circle at 10% 20%, rgba(30, 27, 75, 0.03) 0%, transparent 50%),
              var(--akkodis-bg);
}

.auth-card {
  width: 100%;
  max-width: 440px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(30, 27, 75, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  background: var(--akkodis-white);
  overflow: hidden;
}

.auth-header {
  background: var(--akkodis-navy);
  color: #FFFFFF;
  border-bottom: 3px solid var(--akkodis-yellow);
}

.logo-container {
  display: inline-flex;
  padding: 12px;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 50%;
}

.font-inter {
  font-family: 'Inter', sans-serif;
}

.submit-btn {
  background: var(--akkodis-yellow);
  color: #FFFFFF;
  border-radius: 8px;
  padding: 8px 24px;
  transition: transform 0.2s ease, filter 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    filter: brightness(1.05);
  }
}

.forgot-btn {
  border-radius: 6px;
  &:hover {
    background: rgba(0, 0, 0, 0.03);
  }
}

.register-section {
  background: var(--akkodis-grey-light);
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}
</style>
