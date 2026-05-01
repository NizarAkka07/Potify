<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          Potify Admin
        </q-toolbar-title>

        <div>Microservice User</div>

        <q-btn
          flat
          round
          dense
          icon="logout"
          class="q-ml-md"
          @click="onLogout"
        >
          <q-tooltip>Se déconnecter</q-tooltip>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
    >
      <q-list>
        <q-item-label header>
          Menu
        </q-item-label>

        <q-item clickable v-ripple to="/users" exact>
          <q-item-section avatar>
            <q-icon name="people" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Utilisateurs</q-item-label>
            <q-item-label caption>Gestion des comptes</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/roles" exact>
          <q-item-section avatar>
            <q-icon name="security" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Rôles</q-item-label>
            <q-item-label caption>Gestion des rôles</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/permissions" exact>
          <q-item-section avatar>
            <q-icon name="vpn_key" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Permissions</q-item-label>
            <q-item-label caption>Gestion des droits</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authStore from 'src/shared/stores/auth'

const router = useRouter()
const leftDrawerOpen = ref(false)

function toggleLeftDrawer () {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function onLogout () {
  authStore.logout()
  router.push('/login')
}
</script>
