<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Gestion des Permissions</div>
      <q-btn color="primary" icon="add" label="Ajouter" @click="openDialog()" />
    </div>

    <q-table
      :rows="permissions"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat
      bordered
    >
      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="q-gutter-sm">
          <q-btn flat round color="primary" icon="edit" size="sm" @click="openDialog(props.row)" />
          <q-btn flat round color="negative" icon="delete" size="sm" @click="confirmDelete(props.row)" />
        </q-td>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getPermissions, deletePermission } from 'src/services/api'
import PermissionFormDialog from 'src/components/permissions/PermissionFormDialog.vue'

const $q = useQuasar()

const permissions = ref([])
const loading = ref(false)

const columns = [
  { name: 'code', required: true, label: 'Code', align: 'left', field: 'code', sortable: true },
  { name: 'description', align: 'left', label: 'Description', field: 'description', sortable: true },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

async function loadData() {
  loading.value = true
  try {
    const response = await getPermissions()
    permissions.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des permissions', error)
    $q.notify({ type: 'negative', message: 'Erreur lors du chargement des permissions' })
  } finally {
    loading.value = false
  }
}

function openDialog(permission = null) {
  $q.dialog({
    component: PermissionFormDialog,
    componentProps: {
      permission
    }
  }).onOk(() => {
    loadData()
    $q.notify({ type: 'positive', message: permission ? 'Permission modifiée' : 'Permission ajoutée' })
  })
}

function confirmDelete(permission) {
  $q.dialog({
    title: 'Confirmation',
    message: `Voulez-vous vraiment supprimer la permission ${permission.code} ?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await deletePermission(permission.id)
      $q.notify({ type: 'positive', message: 'Permission supprimée' })
      loadData()
    } catch (error) {
      console.error('Erreur lors de la suppression', error)
      $q.notify({ type: 'negative', message: 'Erreur lors de la suppression' })
    }
  })
}

onMounted(() => {
  loadData()
})
</script>
