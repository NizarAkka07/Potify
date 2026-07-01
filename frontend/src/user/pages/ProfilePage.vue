<template>
  <q-page class="profile-page" style="background: #F8F9FA; padding: 40px 20px;">
    <div class="container q-mx-auto" style="max-width: 1100px;">
      <div class="row q-col-gutter-lg items-stretch">
        <!-- Colonne gauche: Photo de profil (Avatar) -->
        <div class="col-12 col-md-4 flex">
          <q-card class="profile-left-card text-center no-shadow q-py-xl q-px-md full-width" style="border-radius: 20px; border: 1px solid #EEE; background: white;">
            <div class="row justify-center q-mb-lg">
              <q-avatar size="140px" class="profile-avatar shadow-5 cursor-pointer hover-avatar" style="border: 4px solid white; background: #FFB300;" @click="triggerFileInput">
                <q-img v-if="form.avatarUrl" :src="form.avatarUrl" style="width: 100%; height: 100%; object-fit: cover;" />
                <q-icon v-else name="person" size="70px" color="white" />
                <div class="avatar-overlay text-white row items-center justify-center">
                  <q-spinner-oval v-if="uploading" size="30px" color="white" />
                  <q-icon v-else name="photo_camera" size="30px" />
                </div>
              </q-avatar>
              <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="onFileSelected" />
            </div>
            <div class="text-h5 text-weight-bold q-mt-md" style="color: #0D1B2E;">{{ user.fullName }}</div>
            <div class="text-subtitle1 text-grey-7 q-mb-md" style="word-break: break-all;">{{ user.email }}</div>
            <q-chip outline color="secondary" text-color="secondary" dense class="text-weight-bold q-px-sm q-mb-md">
              {{ $t('profile.memberSince') }} {{ formatDate(user.createdAt) }}
            </q-chip>

            <q-separator class="q-my-md" style="background: rgba(0,0,0,0.05);" />

            <!-- Section contributions en bas de la même carte -->
            <div class="column items-center q-mt-lg">
              <q-avatar color="green-1" text-color="green" size="48px" class="q-mb-xs">
                <q-icon name="account_balance_wallet" size="24px" />
              </q-avatar>
              <div class="text-subtitle2 text-grey-7">{{ $t('profile.totalContributions') }}</div>
              <div class="text-h5 text-weight-bold text-primary">0.00 €</div>
            </div>
          </q-card>
        </div>

        <!-- Colonne droite: Paramètres modifiables (Nom complet, email, etc.) -->
        <div class="col-12 col-md-8 flex">
          <q-card class="form-card no-shadow full-width" style="border-radius: 20px; border: 1px solid #EEE; background: white;">
            <q-card-section class="q-pa-xl">
              <div class="text-h5 text-weight-bold q-mb-xl" style="color: #0D1B2E;">
                <q-icon name="settings" color="primary" class="q-mr-sm" />
                {{ $t('profile.accountSettings') }}
              </div>

              <q-form @submit="handleUpdateProfile" class="q-gutter-y-lg">
                <q-input
                  outlined
                  v-model="form.fullName"
                  label="Nom complet"
                  color="secondary"
                  bg-color="grey-1"
                  :rules="[val => !!val || $t('profile.nameRequired')]"
                >
                  <template v-slot:prepend>
                    <q-icon name="badge" color="grey-6" />
                  </template>
                </q-input>

                <q-input
                  outlined
                  v-model="form.email"
                  :label="$t('profile.emailAddress')"
                  type="email"
                  color="grey-4"
                  bg-color="grey-2"
                  disable
                >
                  <template v-slot:prepend>
                    <q-icon name="lock" color="grey-6" />
                  </template>
                </q-input>

                <div class="row justify-between items-center q-mt-xl">
                  <q-btn
                    flat
                    color="primary"
                    icon="lock_reset"
                    :label="$t('profile.changePassword')"
                    no-caps
                    @click="showPasswordDialog = true"
                  />
                  
                  <q-btn
                    :label="$t('profile.saveChanges')"
                    type="submit"
                    unelevated
                    class="q-px-xl q-py-md text-weight-bold"
                    style="background: #FFB300; color: #1A1A2A; border-radius: 12px;"
                    :loading="loading"
                    no-caps
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>

    <!-- Dialogue de changement de mot de passe -->
    <q-dialog v-model="showPasswordDialog" persistent>
      <q-card style="min-width: 400px; border-radius: 15px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold">Changer le mot de passe</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <q-form @submit="handleChangePassword" class="q-gutter-md">
            <q-input
              outlined
              v-model="passwordForm.oldPassword"
              label="Ancien mot de passe"
              type="password"
              :rules="[val => !!val || $t('profile.required')]"
            />
            <q-input
              outlined
              v-model="passwordForm.newPassword"
              label="Nouveau mot de passe"
              type="password"
              :rules="[val => !!val || $t('profile.required'), val => val.length >= 6 || $t('profile.minPasswordLength')]"
            />
            <q-input
              outlined
              v-model="passwordForm.confirmPassword"
              label="Confirmer le nouveau mot de passe"
              type="password"
              :rules="[
                val => !!val || $t('profile.required'),
                val => val === passwordForm.newPassword || $t('profile.passwordsMismatch')
              ]"
            />

            <div class="row justify-end q-mt-lg">
              <q-btn :label="$t('profile.cancel')" flat v-close-popup no-caps />
              <q-btn
                :label="$t('profile.update')"
                type="submit"
                unelevated
                style="background: #FFB300; color: #1A1A2A;"
                :loading="loadingPassword"
                no-caps
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, onMounted, reactive } from 'vue'
import { useQuasar } from 'quasar'
import { getUserById, updateProfile, changePassword } from 'src/shared/services/api'
import { poolService } from 'src/shared/services/poolService'
import authStore from 'src/shared/stores/auth'
const $q = useQuasar()
const loading = ref(false)
const loadingPassword = ref(false)
const showPasswordDialog = ref(false)
const user = ref({})
const fileInput = ref(null)
const uploading = ref(false)

const form = reactive({
  fullName: '',
  email: '',
  avatarUrl: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
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
    form.avatarUrl = response.data.url
    $q.notify({
      type: 'positive',
      message: 'Image importée avec succès ! N\'oubliez pas de sauvegarder.',
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

const fetchUserData = async () => {
  try {
    const userId = authStore.user.value?.id
    if (!userId) return
    
    const response = await getUserById(userId)
    user.value = response.data
    form.fullName = user.value.fullName
    form.email = user.value.email
    form.avatarUrl = user.value.avatarUrl || ''
  } catch (error) {
    console.error('Erreur chargement profil:', error)
  }
}

const handleUpdateProfile = async () => {
  loading.value = true
  try {
    const userId = authStore.user.value?.id
    const response = await updateProfile(userId, { 
      fullName: form.fullName,
      avatarUrl: form.avatarUrl
    })
    user.value = response.data
    
    // Mise à jour du nom complet et de l'avatar dans le store auth
    const updatedUser = { 
      ...authStore.user.value, 
      fullName: response.data.fullName,
      avatarUrl: response.data.avatarUrl
    }
    authStore.setUser(updatedUser)

    $q.notify({
      type: 'positive',
      message: t('profile.successUpdate'),
      position: 'top',
      timeout: 2000
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de la mise à jour : ' + (error.response?.data?.message || 'Erreur inconnue'),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  loadingPassword.value = true
  try {
    const userId = authStore.user.value?.id
    await changePassword(userId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    $q.notify({
      type: 'positive',
      message: t('profile.successPassword'),
      position: 'top'
    })
    
    showPasswordDialog.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'L\'ancien mot de passe est incorrect',
      position: 'top'
    })
  } finally {
    loadingPassword.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    month: 'long',
    year: 'numeric'
  })
}

onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.profile-page {
  font-family: 'Inter', sans-serif;
}
.profile-header-card {
  transition: transform 0.3s ease;
}
.form-card, .info-card {
  transition: all 0.3s ease;
}
.form-card:hover, .info-card:hover {
  box-shadow: 0 10px 30px rgba(0,0,0,0.05) !important;
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
