<template>
  <q-page class="profile-page" style="background: #F8F9FA; padding: 40px 20px;">
    <div class="container q-mx-auto" style="max-width: 800px;">
      <!-- En-tête de profil Premium -->
      <q-card class="profile-header-card q-mb-lg no-shadow" style="border-radius: 20px; overflow: hidden; border: 1px solid #EEE; background: white;">
        <div class="header-banner" style="height: 120px; background: #0D1B2E;"></div>
        <q-card-section class="text-center" style="margin-top: -60px;">
          <q-avatar size="120px" class="profile-avatar shadow-5" style="border: 4px solid white; background: #FFB300;">
            <q-icon name="person" size="60px" color="white" />
          </q-avatar>
          <div class="text-h4 text-weight-bold q-mt-md" style="color: #0D1B2E;">{{ user.fullName }}</div>
          <div class="text-subtitle1 text-grey-7">{{ user.email }}</div>
          <div class="q-mt-sm">
            <q-chip outline color="secondary" text-color="secondary" dense class="text-weight-bold">
              Membre depuis {{ formatDate(user.createdAt) }}
            </q-chip>
          </div>
        </q-card-section>
      </q-card>

      <div class="row q-col-gutter-lg">
        <!-- Paramètres du profil -->
        <div class="col-12 col-md-8">
          <q-card class="form-card no-shadow" style="border-radius: 20px; border: 1px solid #EEE; background: white;">
            <q-card-section class="q-pa-xl">
              <div class="text-h5 text-weight-bold q-mb-xl" style="color: #0D1B2E;">
                <q-icon name="settings" color="primary" class="q-mr-sm" />
                Paramètres du compte
              </div>

              <q-form @submit="handleUpdateProfile" class="q-gutter-y-lg">
                <q-input
                  outlined
                  v-model="form.fullName"
                  label="Nom complet"
                  color="secondary"
                  bg-color="grey-1"
                  :rules="[val => !!val || 'Le nom est obligatoire']"
                >
                  <template v-slot:prepend>
                    <q-icon name="badge" color="grey-6" />
                  </template>
                </q-input>

                <q-input
                  outlined
                  v-model="form.email"
                  label="Adresse Email"
                  type="email"
                  color="grey-4"
                  bg-color="grey-2"
                  disable
                >
                  <template v-slot:prepend>
                    <q-icon name="lock" color="grey-6" />
                  </template>
                </q-input>

                <div class="row justify-end q-mt-xl">
                  <q-btn
                    label="Enregistrer les modifications"
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

        <!-- Informations supplémentaires / Stats -->
        <div class="col-12 col-md-4">
          <q-card class="info-card no-shadow q-mb-md" style="border-radius: 20px; border: 1px solid #EEE; background: white;">
            <q-card-section class="q-pa-lg text-center">
              <q-icon name="account_balance_wallet" size="48px" color="primary" class="q-mb-sm" />
              <div class="text-subtitle2 text-grey-7">Contributions totales</div>
              <div class="text-h5 text-weight-bold" style="color: #0D1B2E;">0.00 €</div>
            </q-card-section>
          </q-card>

          <q-card class="info-card no-shadow" style="border-radius: 20px; border: 1px solid #EEE; background: white;">
            <q-card-section class="q-pa-lg">
              <div class="text-subtitle2 text-weight-bold q-mb-md">Sécurité</div>
              <q-btn
                flat
                color="primary"
                icon="lock_reset"
                label="Changer le mot de passe"
                class="full-width"
                no-caps
                align="left"
                @click="showPasswordDialog = true"
              />
              <q-btn
                flat
                color="negative"
                icon="logout"
                label="Se déconnecter"
                class="full-width q-mt-sm"
                no-caps
                align="left"
                @click="logout"
              />
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
              :rules="[val => !!val || 'Requis']"
            />
            <q-input
              outlined
              v-model="passwordForm.newPassword"
              label="Nouveau mot de passe"
              type="password"
              :rules="[val => !!val || 'Requis', val => val.length >= 6 || 'Minimum 6 caractères']"
            />
            <q-input
              outlined
              v-model="passwordForm.confirmPassword"
              label="Confirmer le nouveau mot de passe"
              type="password"
              :rules="[
                val => !!val || 'Requis',
                val => val === passwordForm.newPassword || 'Les mots de passe ne correspondent pas'
              ]"
            />

            <div class="row justify-end q-mt-lg">
              <q-btn label="Annuler" flat v-close-popup no-caps />
              <q-btn
                label="Mettre à jour"
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
import { ref, onMounted, reactive } from 'vue'
import { useQuasar } from 'quasar'
import { getUserById, updateProfile, changePassword } from 'src/shared/services/api'
import authStore from 'src/shared/stores/auth'
import { useRouter } from 'vue-router'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)
const loadingPassword = ref(false)
const showPasswordDialog = ref(false)
const user = ref({})

const form = reactive({
  fullName: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchUserData = async () => {
  try {
    const userId = authStore.user.value?.id
    if (!userId) return
    
    const response = await getUserById(userId)
    user.value = response.data
    form.fullName = user.value.fullName
    form.email = user.value.email
  } catch (error) {
    console.error('Erreur chargement profil:', error)
  }
}

const handleUpdateProfile = async () => {
  loading.value = true
  try {
    const userId = authStore.user.value?.id
    const response = await updateProfile(userId, { fullName: form.fullName })
    user.value = response.data
    
    // Mise à jour du nom complet dans le store auth
    if (response.data.fullName !== authStore.user.value?.fullName) {
      const updatedUser = { ...authStore.user.value, fullName: response.data.fullName }
      authStore.setUser(updatedUser)
    }

    $q.notify({
      type: 'positive',
      message: 'Profil mis à jour avec succès !',
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
      message: 'Mot de passe modifié avec succès !',
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

const logout = () => {
  authStore.logout()
  router.push('/login')
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
</style>
