<template>
  <q-page class="flex flex-center bg-grey-2">
    <q-card class="auth-card shadow-24">
      <q-card-section class="bg-primary text-white q-pa-lg">
        <div class="text-h5 text-weight-bold">Inscription</div>
        <div class="text-subtitle2">Rejoignez notre communauté solidaire</div>
      </q-card-section>

      <q-card-section class="q-pa-xl">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            v-model="registerForm.fullName"
            label="Nom complet"
            filled
            lazy-rules
            :rules="[val => !!val || 'Le nom est requis']"
          >
            <template v-slot:prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="registerForm.email"
            label="Email"
            type="email"
            filled
            lazy-rules
            :rules="[val => !!val || 'L\'email est requis']"
          >
            <template v-slot:prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="registerForm.password"
            label="Mot de passe"
            type="password"
            filled
            lazy-rules
            :rules="[
              val => !!val || 'Le mot de passe est requis',
              val => val.length >= 8 || 'Minimum 8 caractères'
            ]"
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
          </q-input>

          <div class="row justify-end q-mt-md">
            <q-btn
              label="Créer mon compte"
              type="submit"
              color="primary"
              padding="sm xl"
              :loading="loading"
            />
          </div>
        </q-form>
      </q-card-section>

      <q-card-section class="text-center q-pa-md bg-grey-1">
        <span>Vous avez déjà un compte ? </span>
        <q-btn flat color="primary" label="Se connecter" to="/login" />
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
      message: 'Inscription réussie ! Vous pouvez maintenant vous connecter.',
      icon: 'check_circle'
    })

    router.push('/login')
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || 'Erreur lors de l\'inscription',
      icon: 'report_problem'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-card {
  width: 100%;
  max-width: 450px;
  border-radius: 12px;
}
</style>
