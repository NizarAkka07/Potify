<template>
  <!-- Page de connexion centrée avec un arrière-plan gris clair -->
  <q-page class="flex flex-center bg-grey-2">
    <!-- Carte d'authentification avec une ombre prononcée -->
    <q-card class="auth-card shadow-24">
      <!-- Section d'en-tête de la carte avec couleur primaire -->
      <q-card-section class="bg-primary text-white q-pa-lg">
        <div class="text-h5 text-weight-bold">Connexion</div>
        <div class="text-subtitle2">Accédez à votre plateforme solidaire</div>
      </q-card-section>

      <!-- Section du formulaire de connexion -->
      <q-card-section class="q-pa-xl">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <!-- Champ de saisie pour l'email -->
          <q-input
            v-model="loginForm.email"
            label="Email"
            type="email"
            filled
            lazy-rules
            :rules="[val => !!val || 'L\'email est requis']"
          >
            <!-- Icône d'enveloppe au début du champ -->
            <template v-slot:prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <!-- Champ de saisie pour le mot de passe -->
          <q-input
            v-model="loginForm.password"
            label="Mot de passe"
            type="password"
            filled
            lazy-rules
            :rules="[val => !!val || 'Le mot de passe est requis']"
          >
            <!-- Icône de cadenas au début du champ -->
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
          </q-input>

          <!-- Actions du formulaire : lien mot de passe oublié et bouton de soumission -->
          <div class="row justify-between items-center q-mt-md">
            <q-btn flat color="primary" label="Mot de passe oublié ?" size="sm" />
            <q-btn
              label="Se connecter"
              type="submit"
              color="primary"
              padding="sm xl"
              :loading="loading"
            />
          </div>
        </q-form>
      </q-card-section>

      <!-- Section de redirection pour les nouveaux utilisateurs -->
      <q-card-section class="text-center q-pa-md bg-grey-1">
        <span>Pas encore de compte ? </span>
        <q-btn flat color="primary" label="Inscrivez-vous" to="/register" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
// Importations des utilitaires Vue et Quasar
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
// Importation de l'instance API (Axios)
import { api } from 'src/boot/axios'
// Importation du store d'authentification
import authStore from 'src/shared/stores/auth'

// Initialisation des utilitaires Quasar et du Router
const $q = useQuasar()
const router = useRouter()
// État de chargement pour le bouton de soumission
const loading = ref(false)

// Modèle réactif pour les données du formulaire
const loginForm = ref({
  email: '',
  password: ''
})

/**
 * Fonction de soumission du formulaire.
 * Envoie les identifiants au backend et gère la réponse.
 */
const onSubmit = async () => {
  loading.value = true // Active l'état de chargement
  try {
    // Appel POST vers le endpoint d'authentification
    const response = await api.post('/auth/signin', loginForm.value)
    const { accessToken } = response.data

    // Stockage du token dans le store global
    authStore.setToken(accessToken)

    // Notification de succès
    $q.notify({
      color: 'positive',
      message: 'Connexion réussie !',
      icon: 'check_circle'
    })

    // Redirection vers la page d'accueil
    router.push('/')
  } catch (error) {
    // Gestion et notification d'erreur
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || 'Erreur lors de la connexion',
      icon: 'report_problem'
    })
  } finally {
    loading.value = false // Désactive l'état de chargement
  }
}
</script>

<style lang="scss" scoped>
/* Styles CSS spécifiques à la carte d'authentification */
.auth-card {
  width: 100%;
  max-width: 450px;
  border-radius: 12px;
}
</style>
