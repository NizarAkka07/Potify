<template>
  <!-- Page d'inscription centrée avec un arrière-plan à gradient doux -->
  <q-page class="flex flex-center auth-page">
    <!-- Carte d'authentification avec bords arrondis et ombre subtile -->
    <q-card class="auth-card">
      <!-- Section du formulaire d'inscription -->
      <q-card-section class="q-pa-xl">
        <div class="q-mb-lg">
          <div class="text-h5 text-weight-bolder text-secondary font-inter">{{ $t('auth.registerTitle') }}</div>
          <div class="text-caption text-grey-6 q-mt-xs">{{ $t('auth.registerSubtitle') }}</div>
        </div>

        <q-form @submit="onSubmit" class="q-gutter-y-md">
          <!-- Image de Profil (Optionnelle) -->
          <div class="row justify-center q-mb-md">
            <q-avatar size="100px" class="profile-avatar shadow-2 cursor-pointer hover-avatar" style="border: 3px solid white; background: #062f25;" @click="triggerFileInput">
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
              <q-icon name="person" color="grey-5" />
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
              <q-icon name="email" color="grey-5" />
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
              <q-icon name="lock" color="grey-5" />
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
        <q-btn flat no-caps dense color="secondary" :label="$t('auth.signIn')" to="/login" class="text-weight-bold hover-underline" />
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
