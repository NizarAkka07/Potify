<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Gestion des Rôles</div>
      <q-btn color="primary" icon="add" label="Ajouter" @click="openDialog()" />
    </div>

    <q-table
      :rows="roles"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat
      bordered
    >
      <template v-slot:body-cell-permissions="props">
        <q-td :props="props">
          <q-chip v-for="perm in props.row.permissions" :key="perm.id" size="sm" color="secondary" text-color="white">
            {{ perm.code }}
          </q-chip>
          <span v-if="!props.row.permissions || props.row.permissions.length === 0" class="text-grey">Aucune</span>
        </q-td>
      </template>

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
import { getRoles, deleteRole } from 'src/services/api'
import RoleFormDialog from 'src/components/roles/RoleFormDialog.vue'

const $q = useQuasar()

const roles = ref([])
const loading = ref(false)

const columns = [
  { name: 'name', required: true, label: 'Nom', align: 'left', field: 'name', sortable: true },
  { name: 'description', align: 'left', label: 'Description', field: 'description', sortable: true },
  { name: 'permissions', align: 'left', label: 'Permissions', field: 'permissions' },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

async function loadData() {
  loading.value = true
  try {
    const response = await getRoles()
    roles.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des rôles', error)
    $q.notify({ type: 'negative', message: 'Erreur lors du chargement des rôles' })
  } finally {
    loading.value = false
  }
}

function openDialog(role = null) {
  $q.dialog({
    component: RoleFormDialog,
    componentProps: {
      role
    }
  }).onOk(() => {
    loadData()
    $q.notify({ type: 'positive', message: role ? 'Rôle modifié' : 'Rôle ajouté' })
  })
}

function confirmDelete(role) {
  $q.dialog({
    title: 'Confirmation',
    message: `Voulez-vous vraiment supprimer le rôle ${role.name} ?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await deleteRole(role.id)
      $q.notify({ type: 'positive', message: 'Rôle supprimé' })
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
