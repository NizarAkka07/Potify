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

          <!-- Lien mot de passe oublié -->
          <div class="row justify-end">
            <q-btn flat no-caps dense color="grey-7" label="Mot de passe oublié ?" to="/forgot-password" class="forgot-btn text-caption" />
          </div>

          <!-- Actions : Bouton de connexion -->
          <div class="row justify-end q-mt-md">
            <q-btn
              unelevated
              no-caps
              :label="$t('auth.loginButton')"
              type="submit"
              class="submit-btn text-weight-bold full-width"
              :loading="loading"
            />
          </div>
        </q-form>

        <!-- Séparateur social -->
        <div class="row items-center q-my-lg">
          <q-separator class="col" />
          <span class="q-px-sm text-caption text-grey-6 text-weight-bold font-inter">OU</span>
          <q-separator class="col" />
        </div>

        <!-- Bouton Google Login -->
        <div class="row justify-center">
          <q-btn
            outline
            no-caps
            class="full-width google-btn text-weight-bold"
            @click="loginWithGoogle"
            :loading="googleLoading"
          >
            <template v-slot:default>
              <svg class="q-mr-sm" width="18" height="18" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
              </svg>
              Continuer avec Google
            </template>
          </q-btn>
        </div>
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
const googleLoading = ref(false)

const loginForm = ref({
  email: '',
  password: ''
})

const handleGoogleToken = async (idToken) => {
  googleLoading.value = true
  try {
    const response = await api.post('/auth/google', { idToken })
    const { accessToken, refreshToken } = response.data

    authStore.setToken(accessToken)
    authStore.setRefreshToken(refreshToken)
    authStore.setUser({ 
      id: response.data.id,
      email: response.data.email, 
      roles: response.data.roles,
      fullName: response.data.fullName,
      avatarUrl: response.data.avatarUrl
    })

    $q.notify({
      color: 'positive',
      message: 'Connexion Google réussie !',
      icon: 'check_circle',
      position: 'top'
    })

    router.push('/')
  } catch (error) {
    console.error('Erreur Google Auth:', error)
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || 'Erreur lors de la connexion Google.',
      icon: 'report_problem',
      position: 'top'
    })
  } finally {
    googleLoading.value = false
  }
}

const GOOGLE_CLIENT_ID = '1077945200760-tbqpb374p6uciqsubi9v1l2tsbv8drd7.apps.googleusercontent.com'

const triggerGooglePopup = () => {
  if (window.google?.accounts?.oauth2) {
    const tokenClient = window.google.accounts.oauth2.initTokenClient({
      client_id: GOOGLE_CLIENT_ID,
      scope: 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid',
      callback: (response) => {
        if (response.access_token) {
          handleGoogleToken(response.access_token)
        }
      }
    })
    tokenClient.requestAccessToken()
  }
}

const loginWithGoogle = () => {
  if (window.google?.accounts?.oauth2) {
    triggerGooglePopup()
  } else if (window.google?.accounts?.id) {
    try {
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: (res) => {
          if (res.credential) {
            handleGoogleToken(res.credential)
          }
        },
        use_fedcm_for_prompt: false
      })
      window.google.accounts.id.prompt((notification) => {
        if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
          triggerGooglePopup()
        }
      })
    } catch (e) {
      console.warn('Google OneTap error:', e)
      triggerGooglePopup()
    }
  } else {
    loadGoogleScriptAndPrompt()
  }
}

const loadGoogleScriptAndPrompt = () => {
  if (document.getElementById('google-jssdk')) {
    triggerGooglePopup()
    return
  }
  const script = document.createElement('script')
  script.id = 'google-jssdk'
  script.src = 'https://accounts.google.com/gsi/client'
  script.onload = () => {
    loginWithGoogle()
  }
  document.head.appendChild(script)
}

const onSubmit = async () => {
  loading.value = true
  try {
    const response = await api.post('/auth/signin', loginForm.value)
    const { accessToken, refreshToken } = response.data

    authStore.setToken(accessToken)
    authStore.setRefreshToken(refreshToken)
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

.google-btn {
  border-radius: 12px;
  border: 1px solid #e2e8f0 !important;
  color: #334155 !important;
  padding: 8px 16px;
  transition: all 0.2s ease;

  &:hover {
    background: #f8fafc !important;
    border-color: #cbd5e1 !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }
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
