<template>
  <!-- Layout principal pour les pages publiques (Landing, Login, Register) -->
  <q-layout view="hHh lpR fFf" class="bg-grey-1">
    
    <!-- BARRE DE NAVIGATION (HEADER) -->
    <q-header elevated class="bg-white text-grey-9 q-py-xs">
      <q-toolbar class="q-px-lg">
        <!-- Logo de l'application avec redirection vers l'accueil au clic -->
        <q-toolbar-title class="row items-center cursor-pointer no-wrap" @click="$router.push('/')">
          <q-icon name="favorite" color="primary" size="md" class="q-mr-sm" />
          <span class="text-h5 text-weight-bolder text-primary">Potify</span>
        </q-toolbar-title>

        <q-space />

        <!-- Liens pour la navigation Desktop (cachés sur mobile) -->
        <div class="gt-sm row items-center q-gutter-md">
          <q-btn flat no-caps label="Accueil" class="text-weight-bold" />
          <q-btn flat no-caps label="Explorer" class="text-weight-bold" />
          <q-btn flat no-caps label="Comment ça marche" class="text-weight-bold" />
          <q-btn flat no-caps label="Créer une cagnotte" color="primary" class="text-weight-bold" />
        </div>

        <q-space class="gt-sm" />

        <!-- Boutons d'action (Connexion/Inscription ou Déconnexion) -->
        <div class="row items-center q-gutter-sm">
          <!-- Affichage si l'utilisateur n'est pas authentifié -->
          <template v-if="!authStore.isAuthenticated.value">
            <q-btn flat no-caps label="Se connecter" class="gt-xs text-weight-bold" to="/login" />
            <q-btn outline no-caps color="primary" label="S'inscrire" class="text-weight-bold" to="/register" />
          </template>
          <!-- Affichage si l'utilisateur est authentifié -->
          <template v-else>
            <q-btn flat no-caps label="Tableau de bord" color="primary" class="text-weight-bold" to="/admin" />
            <q-btn flat round dense icon="logout" @click="onLogout">
              <q-tooltip>Se déconnecter</q-tooltip>
            </q-btn>
          </template>
        </div>
      </q-toolbar>
    </q-header>

    <!-- CONTENU DE LA PAGE -->
    <q-page-container>
      <!-- Zone d'affichage des composants injectés par le router -->
      <router-view />

      <!-- PIED DE PAGE STATIQUE (FOOTER) -->
      <div class="bg-dark text-white q-py-xl q-mt-xl">
        <div class="q-px-lg" style="max-width: 1200px; margin: 0 auto;">
          <div class="row q-col-gutter-lg">
            <!-- Section Description -->
            <div class="col-12 col-md-4">
              <div class="text-h5 text-weight-bolder q-mb-md flex items-center">
                <q-icon name="favorite" color="primary" class="q-mr-sm" />
                Potify
              </div>
              <p class="text-grey-4">Ensemble, donnons vie à ce qui compte. La plateforme de solidarité transparente et facile à utiliser.</p>
            </div>
            <!-- Section Liens Utiles -->
            <div class="col-12 col-md-2">
              <div class="text-h6 q-mb-md">À propos</div>
              <div class="column q-gutter-y-sm">
                <a href="#" class="text-grey-4" style="text-decoration: none;">Notre mission</a>
                <a href="#" class="text-grey-4" style="text-decoration: none;">Contact</a>
              </div>
            </div>
            <!-- Section Légal -->
            <div class="col-12 col-md-2">
              <div class="text-h6 q-mb-md">Légal</div>
              <div class="column q-gutter-y-sm">
                <a href="#" class="text-grey-4" style="text-decoration: none;">Conditions</a>
                <a href="#" class="text-grey-4" style="text-decoration: none;">Confidentialité</a>
              </div>
            </div>
            <!-- Section Réseaux Sociaux -->
            <div class="col-12 col-md-4 text-right">
              <q-btn round flat icon="fab fa-facebook" color="white" />
              <q-btn round flat icon="fab fa-twitter" color="white" />
              <q-btn round flat icon="fab fa-instagram" color="white" />
            </div>
          </div>
          <!-- Séparateur et Copyright -->
          <q-separator color="grey-8" class="q-my-md" />
          <div class="text-center text-grey-5">
            © 2026 Potify. Tous droits réservés.
          </div>
        </div>
      </div>
    </q-page-container>

  </q-layout>
</template>

<script setup>
// Importations des dépendances Vue et du store d'authentification
import { useRouter } from 'vue-router'
import authStore from 'src/shared/stores/auth'

// Initialisation du router pour la navigation programmatique
const router = useRouter()

/**
 * Fonction de déconnexion.
 * Vide les données du store et redirige vers la page de connexion.
 */
function onLogout () {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* Styles spécifiques au composant (aucun style personnalisé nécessaire ici) */
</style>
