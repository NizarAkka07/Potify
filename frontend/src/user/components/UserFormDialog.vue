<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin" style="width: 700px; max-width: 90vw;">
      <q-form @submit="onSubmit" class="q-gutter-md">
        <q-card-section>
          <div class="text-h6">{{ isEdit ? 'Modifier l\'Utilisateur' : 'Ajouter un Utilisateur' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <div class="row q-col-gutter-sm">
            <div class="col-12">
              <q-input
                filled
                v-model="formData.fullName"
                label="Nom complet *"
                lazy-rules
                :rules="[ val => val && val.length > 0 || 'Veuillez saisir le nom complet']"
              />
            </div>
            <div class="col-12">
              <q-input
                filled
                v-model="formData.email"
                label="Email *"
                type="email"
                lazy-rules
                :rules="[ val => val && val.length > 0 || 'Veuillez saisir un email']"
              />
            </div>
            <div class="col-12">
              <q-input
                filled
                v-model="formData.password"
                :label="isEdit ? 'Nouveau mot de passe (laisser vide pour ne pas changer)' : 'Mot de passe *'"
                type="password"
                lazy-rules
                :rules="isEdit ? [] : [ val => val && val.length > 0 || 'Veuillez saisir un mot de passe']"
              />
            </div>
            <div class="col-12" v-if="isEdit">
              <q-select
                filled
                v-model="formData.status"
                :options="['ACTIVE', 'SUSPENDED', 'DELETED']"
                label="Statut *"
                lazy-rules
                :rules="[ val => val && val.length > 0 || 'Veuillez sélectionner un statut']"
              />
            </div>
          </div>

          <q-separator class="q-my-md" v-if="isEdit" />

          <div v-if="isEdit" class="q-mt-md">
            <div class="text-subtitle1 q-mb-sm">Rôles assignés</div>
            <q-select
              filled
              v-model="selectedRole"
              :options="availableRoles"
              option-value="id"
              option-label="name"
              label="Ajouter un rôle"
              class="q-mb-md"
              @update:model-value="addRole"
            >
              <template v-slot:after>
                <q-btn round dense flat icon="add" @click="addRole" :disable="!selectedRole" />
              </template>
            </q-select>

            <q-list bordered separator v-if="userRoles.length > 0">
              <q-item v-for="role in userRoles" :key="role.id">
                <q-item-section>
                  <q-item-label>{{ role.name }}</q-item-label>
                  <q-item-label caption>{{ role.description }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-btn flat round dense color="negative" icon="delete" @click="removeRole(role)" />
                </q-item-section>
              </q-item>
            </q-list>
            <div v-else class="text-caption text-grey">Aucun rôle assigné</div>
          </div>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Annuler" color="primary" @click="onCancelClick" />
          <q-btn flat label="Enregistrer" type="submit" color="primary" :loading="loading" />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { useDialogPluginComponent, useQuasar } from 'quasar'
import { ref, onMounted } from 'vue'
import { createUser, updateUser, getRoles, assignRoleToUser, getUserRoles, removeRoleFromUser } from 'src/shared/services/api'

const props = defineProps({
  user: {
    type: Object,
    default: null
  }
})

defineEmits([
  ...useDialogPluginComponent.emits
])

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent()
const $q = useQuasar()

const isEdit = ref(false)
const loading = ref(false)
const formData = ref({
  fullName: '',
  email: '',
  password: '',
  status: 'ACTIVE'
})

const userRoles = ref([])
const availableRoles = ref([])
const selectedRole = ref(null)

onMounted(async () => {
  if (props.user) {
    isEdit.value = true
    formData.value = {
      id: props.user.id,
      fullName: props.user.fullName,
      email: props.user.email,
      status: props.user.status || 'ACTIVE',
      password: '' // empty so the user sets a new one
    }
    await loadUserRoles()
    await loadAvailableRoles()
  }
})

async function loadUserRoles() {
  try {
    const response = await getUserRoles(props.user.id)
    userRoles.value = response.data
  } catch (error) {
    console.error('Erreur de chargement des rôles de l\'utilisateur', error)
  }
}

async function loadAvailableRoles() {
  try {
    const response = await getRoles()
    // Filter out already assigned roles
    availableRoles.value = response.data.filter(r => !userRoles.value.find(ur => ur.id === r.id))
  } catch (error) {
    console.error('Erreur de chargement des rôles', error)
  }
}

async function addRole() {
  if (!selectedRole.value) return
  
  try {
    await assignRoleToUser(formData.value.id, selectedRole.value.id)
    userRoles.value.push(selectedRole.value)
    $q.notify({ type: 'positive', message: 'Rôle ajouté à l\'utilisateur' })
    await loadAvailableRoles() // Refresh options
    selectedRole.value = null
  } catch (error) {
    console.error('Erreur lors de l\'ajout du rôle', error)
    $q.notify({ type: 'negative', message: 'Erreur lors de l\'ajout du rôle' })
  }
}

async function removeRole(role) {
  try {
    await removeRoleFromUser(formData.value.id, role.id)
    userRoles.value = userRoles.value.filter(r => r.id !== role.id)
    $q.notify({ type: 'positive', message: 'Rôle retiré de l\'utilisateur' })
    await loadAvailableRoles() // Refresh options
  } catch (error) {
    console.error('Erreur lors de la suppression du rôle', error)
    $q.notify({ type: 'negative', message: 'Erreur lors de la suppression du rôle' })
  }
}

async function onSubmit() {
  loading.value = true
  try {
    if (isEdit.value) {
      const payload = {
        fullName: formData.value.fullName,
        email: formData.value.email,
        status: formData.value.status
      }
      if (formData.value.password) {
        payload.password = formData.value.password
      }
      await updateUser(formData.value.id, payload)
    } else {
      await createUser({
        fullName: formData.value.fullName,
        email: formData.value.email,
        password: formData.value.password
      })
    }
    onDialogOK()
  } catch (error) {
    console.error('Erreur lors de la sauvegarde:', error)
  } finally {
    loading.value = false
  }
}

function onCancelClick() {
  onDialogCancel()
}
</script>
