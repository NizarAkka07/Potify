<template>
  <q-layout view="hHh lpR fFf" style="background: #FFFFFF;">

    <!-- NAVBAR – Dark navy Akkodis exact -->
    <q-header style="background: #0D1B2E; border-bottom: none; box-shadow: 0 2px 8px rgba(0,0,0,0.25);">
      <q-toolbar class="q-px-xl" style="min-height: 64px;">

        <!-- Logo : texte blanc + barre jaune -->
        <q-toolbar-title
          class="row items-center cursor-pointer no-wrap"
          style="max-width: fit-content;"
          @click="$router.push('/')"
        >
          <!-- Barre verticale jaune à gauche du logo (style Akkodis) -->
          <div style="width: 4px; height: 28px; background: #FFB300; margin-right: 10px; border-radius: 2px;"></div>
          <span style="color: #FFFFFF; font-weight: 800; font-size: 1.4rem; letter-spacing: 1px;">POTIFY</span>
        </q-toolbar-title>

        <q-space />

        <!-- Liens de navigation desktop – style Akkodis (blanc, uppercase) -->
        <div class="gt-sm row items-center q-gutter-lg">
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;">Accueil</span>
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;">Explorer</span>
          <span class="nav-link cursor-pointer" style="color: rgba(255,255,255,0.85); font-size: 0.9rem; font-weight: 500;">Comment ça marche</span>
        </div>

        <q-space class="gt-sm" />

        <!-- Auth buttons -->
        <div class="row items-center q-gutter-sm">
          <template v-if="!authStore.isAuthenticated.value">
            <q-btn
              flat no-caps
              label="Se connecter"
              class="gt-xs"
              style="color: rgba(255,255,255,0.85); font-weight: 500;"
              to="/login"
            />
            <!-- Bouton S'inscrire = CTA jaune Akkodis -->
            <q-btn
              no-caps
              label="S'inscrire"
              style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 8px 20px;"
              to="/register"
            />
          </template>
          <template v-else>
            <q-btn flat no-caps label="Tableau de bord" style="color: rgba(255,255,255,0.85);" to="/admin" />
            <q-btn flat round dense icon="logout" style="color: rgba(255,255,255,0.75);" @click="onLogout">
              <q-tooltip>Se déconnecter</q-tooltip>
            </q-btn>
          </template>
        </div>

      </q-toolbar>
    </q-header>

    <!-- CONTENU -->
    <q-page-container>
      <router-view />

      <!-- FOOTER – dark navy Akkodis avec top-border jaune -->
      <div style="background: #0D1B2E; border-top: 3px solid #FFB300;">
        <div class="q-px-xl q-py-xl" style="max-width: 1200px; margin: 0 auto;">
          <div class="row q-col-gutter-xl">

            <!-- Brand -->
            <div class="col-12 col-md-4">
              <div class="row items-center q-mb-md">
                <div style="width: 4px; height: 24px; background: #FFB300; margin-right: 10px; border-radius: 2px;"></div>
                <span style="color: #FFFFFF; font-weight: 800; font-size: 1.2rem; letter-spacing: 1px;">POTIFY</span>
              </div>
              <p style="color: rgba(255,255,255,0.6); line-height: 1.7; font-size: 0.9rem;">
                Ensemble, donnons vie à ce qui compte. La plateforme de solidarité transparente et facile à utiliser.
              </p>
            </div>

            <!-- À propos -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">À propos</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Notre mission</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Contact</a>
              </div>
            </div>

            <!-- Légal -->
            <div class="col-12 col-md-2">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">Légal</div>
              <div class="column q-gutter-y-sm">
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Conditions</a>
                <a href="#" style="color: rgba(255,255,255,0.65); text-decoration: none; font-size: 0.9rem;">Confidentialité</a>
              </div>
            </div>

            <!-- Réseaux sociaux -->
            <div class="col-12 col-md-4 text-right">
              <div class="text-weight-bold q-mb-md" style="color: #FFB300; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px;">Suivez-nous</div>
              <div class="row justify-end q-gutter-sm">
                <q-btn round flat icon="fab fa-linkedin" style="color: rgba(255,255,255,0.7);" />
                <q-btn round flat icon="fab fa-twitter" style="color: rgba(255,255,255,0.7);" />
                <q-btn round flat icon="fab fa-instagram" style="color: rgba(255,255,255,0.7);" />
              </div>
            </div>
          </div>

          <!-- Séparateur + copyright -->
          <q-separator style="background: rgba(255,255,255,0.1); margin: 24px 0;" />
          <div class="text-center" style="color: rgba(255,255,255,0.4); font-size: 0.85rem;">
            © 2026 Potify. Tous droits réservés.
          </div>
        </div>
      </div>
    </q-page-container>

  </q-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import authStore from 'src/shared/stores/auth'

const router = useRouter()

function onLogout () {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* Hover sur liens navbar */
.nav-link:hover {
  color: #FFB300 !important;
  transition: color 0.2s ease;
}
</style>
