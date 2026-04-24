<template>
  <q-layout view="hHh lpR fFf" class="bg-grey-1">
    
    <!-- NAVBAR -->
    <q-header elevated class="bg-white text-grey-9 q-py-xs">
      <q-toolbar class="q-px-lg">
        <!-- Logo -->
        <q-toolbar-title class="row items-center cursor-pointer no-wrap" @click="$router.push('/')">
          <q-icon name="favorite" color="primary" size="md" class="q-mr-sm" />
          <span class="text-h5 text-weight-bolder text-primary">Potify</span>
        </q-toolbar-title>

        <q-space />

        <!-- Desktop Links -->
        <div class="gt-sm row items-center q-gutter-md">
          <q-btn flat no-caps label="Accueil" class="text-weight-bold" />
          <q-btn flat no-caps label="Explorer" class="text-weight-bold" />
          <q-btn flat no-caps label="Comment ça marche" class="text-weight-bold" />
          <q-btn flat no-caps label="Créer une cagnotte" color="primary" class="text-weight-bold" />
        </div>

        <q-space class="gt-sm" />

        <!-- Buttons -->
        <div class="row items-center q-gutter-sm">
          <q-btn flat no-caps label="Se connecter" class="gt-xs text-weight-bold" to="/users" />
          <q-btn outline no-caps color="primary" label="S'inscrire" class="text-weight-bold" />
        </div>
      </q-toolbar>
    </q-header>

    <!-- PAGE CONTENT -->
    <q-page-container>
      <router-view />

      <!-- STATIC FOOTER (Not fixed) -->
      <div class="bg-dark text-white q-py-xl q-mt-xl">
        <div class="q-px-lg" style="max-width: 1200px; margin: 0 auto;">
          <div class="row q-col-gutter-lg">
            <div class="col-12 col-md-4">
              <div class="text-h5 text-weight-bolder q-mb-md flex items-center">
                <q-icon name="favorite" color="primary" class="q-mr-sm" />
                Potify
              </div>
              <p class="text-grey-4">Ensemble, donnons vie à ce qui compte. La plateforme de solidarité transparente et facile à utiliser.</p>
            </div>
            <div class="col-12 col-md-2">
              <div class="text-h6 q-mb-md">À propos</div>
              <div class="column q-gutter-y-sm">
                <a href="#" class="text-grey-4" style="text-decoration: none;">Notre mission</a>
                <a href="#" class="text-grey-4" style="text-decoration: none;">Contact</a>
              </div>
            </div>
            <div class="col-12 col-md-2">
              <div class="text-h6 q-mb-md">Légal</div>
              <div class="column q-gutter-y-sm">
                <a href="#" class="text-grey-4" style="text-decoration: none;">Conditions</a>
                <a href="#" class="text-grey-4" style="text-decoration: none;">Confidentialité</a>
              </div>
            </div>
            <div class="col-12 col-md-4 text-right">
              <q-btn round flat icon="fab fa-facebook" color="white" />
              <q-btn round flat icon="fab fa-twitter" color="white" />
              <q-btn round flat icon="fab fa-instagram" color="white" />
            </div>
          </div>
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
</script>

<style scoped>
</style>
