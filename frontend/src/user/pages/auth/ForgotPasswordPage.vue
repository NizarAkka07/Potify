<template>
  <!-- Page de mot de passe oublié -->
  <q-page class="flex flex-center auth-page">
    <q-card class="auth-card">
      <q-card-section class="q-pa-xl">
        <div class="q-mb-lg">
          <div class="text-h5 text-weight-bolder text-secondary font-inter">Mot de passe oublié</div>
          <div class="text-caption text-grey-6 q-mt-xs">Entrez votre email pour recevoir un lien de réinitialisation</div>
        </div>

        <!-- Message de succès -->
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template v-slot:avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          {{ successMessage }}
        </q-banner>

        <q-form v-if="!success" @submit="onSubmit" class="q-gutter-y-md">
          <!-- Champ email -->
          <q-input
            v-model="email"
            label="Adresse email"
            type="email"
            outlined
            color="secondary"
            lazy-rules
            :rules="[
              val => !!val || 'L\'email est obligatoire',
              val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Email invalide'
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="email" color="grey-5" />
            </template>
          </q-input>

          <!-- Bouton de soumission -->
          <div class="row justify-end q-mt-lg">
            <q-btn
              unelevated
              no-caps
              label="Envoyer le lien"
              type="submit"
              class="submit-btn text-weight-bold"
              :loading="loading"
              icon-right="send"
            />
          </div>
        </q-form>

        <!-- Bouton retour si succès -->
        <div v-if="success" class="row justify-center q-mt-lg">
          <q-btn
            flat
            no-caps
            color="secondary"
            label="Retour à la connexion"
            to="/login"
            icon="arrow_back"
            class="text-weight-bold"
          />
        </div>
      </q-card-section>

      <!-- Section de retour vers la connexion -->
      <q-card-section class="text-center q-py-lg register-section">
        <span class="text-grey-7">Vous vous souvenez ? </span>
        <q-btn flat no-caps dense color="secondary" label="Se connecter" to="/login" class="text-weight-bold hover-underline" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { api } from 'src/boot/axios'

const loading = ref(false)
const success = ref(false)
const successMessage = ref('')
const email = ref('')

const onSubmit = async () => {
  loading.value = true
  try {
    const response = await api.post('/auth/forgot-password', { email: email.value })
    success.value = true
    successMessage.value = response.data || 'Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.'
  } catch {
    // Même en cas d'erreur, on affiche un succès pour ne pas révéler si l'email existe
    success.value = true
    successMessage.value = 'Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.'
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
