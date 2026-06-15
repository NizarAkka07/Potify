<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

onMounted(() => {
  const isDarkStored = localStorage.getItem('darkMode') === 'true'
  $q.dark.set(isDarkStored)
})
</script>
