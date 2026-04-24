<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin" style="width: 600px; max-width: 80vw;">
      <q-form @submit="onSubmit" class="q-gutter-md">
        <q-card-section>
          <div class="text-h6">{{ isEdit ? 'Modifier le Rôle' : 'Ajouter un Rôle' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            filled
            v-model="formData.name"
            label="Nom (ex: ROLE_ADMIN) *"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Veuillez saisir un nom']"
            :disable="isEdit"
          />

          <q-input
            filled
            v-model="formData.description"
            label="Description *"
            class="q-mt-md"
            type="textarea"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Veuillez saisir une description']"
          />

          <div v-if="isEdit" class="q-mt-lg">
            <div class="text-subtitle1 q-mb-sm">Permissions assignées</div>
            <q-select
              filled
              v-model="selectedPermission"
              :options="availablePermissions"
              option-value="id"
              option-label="code"
              label="Ajouter une permission"
              class="q-mb-md"
              @update:model-value="addPermission"
            >
              <template v-slot:after>
                <q-btn round dense flat icon="add" @click="addPermission" :disable="!selectedPermission" />
              </template>
            </q-select>

            <q-list bordered separator v-if="rolePermissions.length > 0">
              <q-item v-for="perm in rolePermissions" :key="perm.id">
                <q-item-section>
                  <q-item-label>{{ perm.code }}</q-item-label>
                  <q-item-label caption>{{ perm.description }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-btn flat round dense color="negative" icon="delete" @click="removePermission(perm)" />
                </q-item-section>
              </q-item>
            </q-list>
            <div v-else class="text-caption text-grey">Aucune permission assignée</div>
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
import { createRole, updateRole, getPermissions, assignPermissionToRole, removePermissionFromRole } from 'src/services/api'

const props = defineProps({
  role: {
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
  name: '',
  description: ''
})

const rolePermissions = ref([])
const availablePermissions = ref([])
const selectedPermission = ref(null)

onMounted(async () => {
  if (props.role) {
    isEdit.value = true
    formData.value = {
      id: props.role.id,
      name: props.role.name,
      description: props.role.description
    }
    rolePermissions.value = props.role.permissions || []
    await loadPermissions()
  }
})

async function loadPermissions() {
  try {
    const response = await getPermissions()
    // Filter out already assigned permissions
    availablePermissions.value = response.data.filter(p => !rolePermissions.value.find(rp => rp.id === p.id))
  } catch (error) {
    console.error('Erreur de chargement des permissions', error)
  }
}

async function addPermission() {
  if (!selectedPermission.value) return
  
  try {
    await assignPermissionToRole(formData.value.id, selectedPermission.value.id)
    rolePermissions.value.push(selectedPermission.value)
    $q.notify({ type: 'positive', message: 'Permission ajoutée au rôle' })
    await loadPermissions() // Refresh available options
    selectedPermission.value = null
  } catch (error) {
    console.error('Erreur lors de l\'ajout de la permission', error)
    $q.notify({ type: 'negative', message: 'Erreur lors de l\'ajout de la permission' })
  }
}

async function removePermission(perm) {
  try {
    await removePermissionFromRole(formData.value.id, perm.id)
    rolePermissions.value = rolePermissions.value.filter(p => p.id !== perm.id)
    $q.notify({ type: 'positive', message: 'Permission retirée du rôle' })
    await loadPermissions() // Refresh available options
  } catch (error) {
    console.error('Erreur lors de la suppression de la permission', error)
    $q.notify({ type: 'negative', message: 'Erreur lors de la suppression de la permission' })
  }
}

async function onSubmit() {
  loading.value = true
  try {
    if (isEdit.value) {
      await updateRole(formData.value.id, {
        id: formData.value.id,
        name: formData.value.name,
        description: formData.value.description
      })
    } else {
      await createRole({
        name: formData.value.name,
        description: formData.value.description
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
