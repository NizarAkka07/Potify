<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin" style="width: 500px; max-width: 80vw;">
      <q-form @submit="onSubmit" class="q-gutter-md">
        <q-card-section>
          <div class="text-h6">{{ isEdit ? 'Modifier la Permission' : 'Ajouter une Permission' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            filled
            v-model="formData.code"
            label="Code (ex: USER_READ) *"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Veuillez saisir un code']"
            :disable="isEdit"
          />

          <q-input
            filled
            v-model="formData.description"
            label="Description"
            class="q-mt-md"
            type="textarea"
          />
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
import { useDialogPluginComponent } from 'quasar'
import { ref, onMounted } from 'vue'
import { createPermission, updatePermission } from 'src/shared/services/api'

const props = defineProps({
  permission: {
    type: Object,
    default: null
  }
})

defineEmits([
  ...useDialogPluginComponent.emits
])

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent()

const isEdit = ref(false)
const loading = ref(false)
const formData = ref({
  code: '',
  description: ''
})

onMounted(() => {
  if (props.permission) {
    isEdit.value = true
    formData.value = {
      id: props.permission.id,
      code: props.permission.code,
      description: props.permission.description
    }
  }
})

async function onSubmit() {
  loading.value = true
  try {
    if (isEdit.value) {
      await updatePermission(formData.value.id, {
        id: formData.value.id,
        code: formData.value.code,
        description: formData.value.description
      })
    } else {
      await createPermission({
        code: formData.value.code,
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
