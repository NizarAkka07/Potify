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
        <div class="text-h5 text-weight-bold text-white font-inter">Inscription</div>
        <div class="text-subtitle2 text-grey-4 q-mt-xs">Rejoignez notre communauté solidaire</div>
      </q-card-section>

      <!-- Section du formulaire d'inscription -->
      <q-card-section class="q-pa-xl">
        <q-form @submit="onSubmit" class="q-gutter-y-md">
          <!-- Nom complet (Outlined) -->
          <q-input
            v-model="registerForm.fullName"
            label="Nom complet"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || 'Le nom est requis']"
          >
            <template v-slot:prepend>
              <q-icon name="person" color="grey-6" />
            </template>
          </q-input>

          <!-- Email (Outlined) -->
          <q-input
            v-model="registerForm.email"
            label="Email"
            type="email"
            outlined
            color="secondary"
            lazy-rules
            :rules="[val => !!val || 'L\'email est requis']"
          >
            <template v-slot:prepend>
              <q-icon name="email" color="grey-6" />
            </template>
          </q-input>

          <!-- Mot de passe (Outlined) -->
          <q-input
            v-model="registerForm.password"
            label="Mot de passe"
            type="password"
            outlined
            color="secondary"
            lazy-rules
            :rules="[
              val => !!val || 'Le mot de passe est requis',
              val => val.length >= 8 || 'Minimum 8 caractères'
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
              label="Créer mon compte"
              type="submit"
              class="submit-btn text-weight-bold"
              :loading="loading"
            />
          </div>
        </q-form>
      </q-card-section>

      <!-- Section de redirection pour les utilisateurs existants -->
      <q-card-section class="text-center q-py-lg register-section">
        <span class="text-grey-7">Vous avez déjà un compte ? </span>
        <q-btn flat no-caps dense color="orange-9" label="Se connecter" to="/login" class="text-weight-bold" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const $q = useQuasar()
const router = useRouter()
const loading = ref(false)

const registerForm = ref({
  fullName: '',
  email: '',
  password: ''
})

const onSubmit = async () => {
  loading.value = true
  try {
    await api.post('/auth/signup', registerForm.value)

    $q.notify({
      color: 'positive',
      message: 'Inscription réussie ! Veuillez vérifier votre email pour activer votre compte.',
      icon: 'email',
      position: 'top',
      timeout: 10000
    })

    router.push('/login')
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || 'Erreur lors de l\'inscription',
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
  background: radial-gradient(circle at 50% 50%, rgba(255, 167, 38, 0.02) 0%, transparent 60%),
              radial-gradient(circle at 10% 20%, rgba(13, 27, 46, 0.03) 0%, transparent 50%),
              #FAFAFB;
}

.auth-card {
  width: 100%;
  max-width: 440px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(13, 27, 46, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  background: #FFFFFF;
  overflow: hidden;
}

.auth-header {
  background: #0D1B2E;
  color: #FFFFFF;
  border-bottom: 3px solid #FFA726;
}

.logo-container {
  display: inline-flex;
  padding: 12px;
  background: rgba(255, 167, 38, 0.1);
  border-radius: 50%;
}

.font-inter {
  font-family: 'Inter', sans-serif;
}

.submit-btn {
  background: #FFA726;
  color: #0D1B2E;
  border-radius: 8px;
  padding: 8px 24px;
  transition: transform 0.2s ease, filter 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    filter: brightness(1.05);
  }
}

.register-section {
  background: #FAFAFB;
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}
</style>

