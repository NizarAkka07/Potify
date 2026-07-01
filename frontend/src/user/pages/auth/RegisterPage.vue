<template>
  <!-- Page d'inscription centrée avec un arrière-plan à gradient doux -->
  <q-page class="flex flex-center auth-page">
    <!-- Carte d'authentification avec bords arrondis et ombre subtile -->
    <q-card class="auth-card">
      <!-- Section d'en-tête de la carte en bleu marine Akkodis -->
      <q-card-section class="auth-header text-center q-py-xl">
        <div class="logo-container q-mb-md">
          <q-icon name="person_add" size="3rem" color="primary" />
        </div>
        <div class="text-h5 text-weight-bold text-white font-inter">{{ $t('auth.registerTitle') }}</div>
        <div class="text-subtitle2 text-grey-4 q-mt-xs">{{ $t('auth.registerSubtitle') }}</div>
      </q-card-section>

      <!-- Section du formulaire d'inscription -->
      <q-card-section class="q-pa-xl">
        <q-form @submit="onSubmit" class="q-gutter-y-md">
          <!-- Image de Profil (Optionnelle) -->
          <div class="row justify-center q-mb-lg">
            <q-avatar size="100px" class="profile-avatar shadow-2 cursor-pointer hover-avatar" style="border: 3px solid white; background: #FFB300;" @click="triggerFileInput">
              <q-img v-if="registerForm.avatarUrl" :src="registerForm.avatarUrl" style="width: 100%; height: 100%; object-fit: cover;" />
              <q-icon v-else name="person" size="50px" color="white" />
              <div class="avatar-overlay text-white row items-center justify-center">
                <q-spinner-oval v-if="uploading" size="24px" color="white" />
                <q-icon v-else name="photo_camera" size="24px" />
              </div>
            </q-avatar>
            <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="onFileSelected" />
          </div>
          <div class="text-caption text-center text-grey-6 q-mt-xs q-mb-md" style="margin-top: -10px;">
            Photo de profil (optionnelle)
          </div>

          <!-- Nom complet (Outlined) -->
          <q-input
            v-model="registerForm.fullName"
            :label="$t('auth.fullName')"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || $t('auth.fullNameRequired')]"
          >
            <template v-slot:prepend>
              <q-icon name="person" color="grey-6" />
            </template>
          </q-input>

          <!-- Email (Outlined) -->
          <q-input
            v-model="registerForm.email"
            :label="$t('auth.email')"
            type="email"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || $t('auth.emailRequired')]"
          >
            <template v-slot:prepend>
              <q-icon name="email" color="grey-6" />
            </template>
          </q-input>

          <!-- Mot de passe (Outlined) -->
          <q-input
            v-model="registerForm.password"
            :label="$t('auth.password')"
            type="password"
            outlined
            color="secondary"
            lazy-rules
            :rules="[
              val => !!val || $t('auth.passwordRequired'),
              val => val.length >= 8 || $t('auth.minPassword')
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" color="grey-6" />
            </template>
          </q-input>

          <!-- Bouton de soumission -->
          <div class="row justify-end q-mt-lg">
            <q-btn
              unelevated
              no-caps
              :label="$t('auth.registerButton')"
              type="submit"
              class="submit-btn text-weight-bold"
              :loading="loading"
            />
          </div>
        </q-form>
      </q-card-section>

      <!-- Section de redirection pour les utilisateurs existants -->
      <q-card-section class="text-center q-py-lg register-section">
        <span class="text-grey-7">{{ $t('auth.hasAccount') }} </span>
        <q-btn flat no-caps dense color="primary" :label="$t('auth.signIn')" to="/login" class="text-weight-bold" />
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
import { poolService } from 'src/shared/services/poolService'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)
const fileInput = ref(null)
const uploading = ref(false)

const registerForm = ref({
  fullName: '',
  email: '',
  password: '',
  avatarUrl: ''
})

const triggerFileInput = () => {
  fileInput.value.click()
}

const onFileSelected = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  uploading.value = true
  try {
    const response = await poolService.uploadImage(file)
    registerForm.value.avatarUrl = response.data.url
    $q.notify({
      type: 'positive',
      message: 'Photo importée avec succès !',
      position: 'top',
      timeout: 2000
    })
  } catch (error) {
    console.error('Erreur upload avatar:', error)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de l\'upload de l\'image.',
      position: 'top'
    })
  } finally {
    uploading.value = false
  }
}

const onSubmit = async () => {
  loading.value = true
  try {
    await api.post('/auth/signup', registerForm.value)

    $q.notify({
      color: 'positive',
      message: t('auth.regSuccess'),
      icon: 'email',
      position: 'top',
      timeout: 10000
    })

    router.push('/login')
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || t('auth.regError'),
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

.register-section {
  background: var(--akkodis-grey-light);
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}

.profile-avatar {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}
.profile-avatar:hover .avatar-overlay {
  opacity: 1;
}
.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hidden {
  display: none !important;
}
</style>
