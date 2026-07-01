<template>
  <!-- Page de connexion centrée avec un arrière-plan à gradient doux -->
  <q-page class="flex flex-center auth-page">
    <!-- Carte d'authentification avec bords arrondis et ombre subtile -->
    <q-card class="auth-card">
      <!-- Section du formulaire de connexion -->
      <q-card-section class="q-pa-xl">
        <div class="q-mb-lg">
          <div class="text-h5 text-weight-bolder text-secondary font-inter">{{ $t('auth.loginTitle') }}</div>
          <div class="text-caption text-grey-6 q-mt-xs">{{ $t('auth.loginSubtitle') }}</div>
        </div>

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
              <q-icon name="email" color="grey-5" />
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
              <q-icon name="lock" color="grey-5" />
            </template>
          </q-input>

          <!-- Actions : Bouton de connexion -->
          <div class="row justify-end q-mt-lg">
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
        <q-btn flat no-caps dense color="secondary" :label="$t('auth.signUp')" to="/register" class="text-weight-bold hover-underline" />
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

.forgot-btn {
  border-radius: 8px;
  color: #64748b;
  &:hover {
    background: rgba(0, 0, 0, 0.03);
    color: #0f172a;
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
