<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Gestion des Utilisateurs</div>
      <q-btn color="primary" icon="person_add" label="Ajouter" @click="openDialog()" />
    </div>

    <q-table
      :rows="users"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat
      bordered
    >
      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <q-chip :color="props.row.status === 'ACTIVE' ? 'positive' : 'negative'" text-color="white" size="sm">
            {{ props.row.status }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="q-gutter-sm">
          <q-btn flat round color="primary" icon="edit" size="sm" @click="openDialog(props.row)" />
        </q-td>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getUsers } from 'src/services/api'
import UserFormDialog from 'src/components/users/UserFormDialog.vue'

const $q = useQuasar()

const users = ref([])
const loading = ref(false)

const columns = [
  { name: 'name', required: true, label: 'Nom Complet', align: 'left', field: 'fullName', sortable: true },
  { name: 'email', align: 'left', label: 'Email', field: 'email', sortable: true },
  { name: 'status', align: 'center', label: 'Statut', field: 'status', sortable: true },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

async function loadData() {
  loading.value = true
  try {
    const response = await getUsers()
    users.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des utilisateurs', error)
    $q.notify({ type: 'negative', message: 'Erreur lors du chargement des utilisateurs' })
  } finally {
    loading.value = false
  }
}

function openDialog(user = null) {
  $q.dialog({
    component: UserFormDialog,
    componentProps: {
      user
    }
  }).onOk(() => {
    loadData()
    $q.notify({ type: 'positive', message: user ? 'Utilisateur modifié' : 'Utilisateur ajouté' })
  })
}

onMounted(() => {
  loadData()
})
</script>
