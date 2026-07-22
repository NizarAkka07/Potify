<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <div>
        <div class="text-h4 text-weight-bold">Gestion des Utilisateurs</div>
        <div class="text-subtitle2 text-grey-6">Administration des comptes et validations</div>
      </div>
      <q-btn color="primary" icon="person_add" :label="$t('userList.addButton')" @click="openDialog()" />
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

      <template v-slot:body-cell-adminVerification="props">
        <q-td :props="props">
          <q-chip
            :color="props.row.adminVerification ? 'positive' : 'warning'"
            text-color="white"
            size="sm"
            :icon="props.row.adminVerification ? 'verified' : 'hourglass_empty'"
          >
            {{ props.row.adminVerification ? 'Approuvé' : 'En attente' }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="q-gutter-xs">
          <q-btn flat round color="primary" icon="edit" size="sm" @click="openDialog(props.row)">
            <q-tooltip>Modifier l'utilisateur</q-tooltip>
          </q-btn>
          
          <template v-if="authStore.isSuperAdmin.value">
            <q-btn
              v-if="!props.row.adminVerification"
              flat
              round
              color="positive"
              icon="check_circle"
              size="sm"
              @click="toggleVerification(props.row, true)"
            >
              <q-tooltip>Approuver la vérification</q-tooltip>
            </q-btn>
            <q-btn
              v-else
              flat
              round
              color="negative"
              icon="cancel"
              size="sm"
              @click="toggleVerification(props.row, false)"
            >
              <q-tooltip>Révoquer l'approbation</q-tooltip>
            </q-btn>
          </template>
        </q-td>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getUsers, updateAdminVerification } from 'src/shared/services/api'
import authStore from 'src/shared/stores/auth'
import UserFormDialog from 'src/user/components/UserFormDialog.vue'

const $q = useQuasar()

const users = ref([])
const loading = ref(false)

const columns = [
  { name: 'name', required: true, label: 'Nom Complet', align: 'left', field: 'fullName', sortable: true },
  { name: 'email', align: 'left', label: 'Email', field: 'email', sortable: true },
  { name: 'status', align: 'center', label: 'Statut Email', field: 'status', sortable: true },
  { name: 'adminVerification', align: 'center', label: 'Vérification Admin', field: 'adminVerification', sortable: true },
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

async function toggleVerification(user, approved) {
  try {
    await updateAdminVerification(user.id, approved)
    $q.notify({
      type: 'positive',
      message: approved ? `L'utilisateur ${user.fullName} a été approuvé avec succès !` : `L'approbation de ${user.fullName} a été révoquée.`
    })
    await loadData()
  } catch (error) {
    console.error('Erreur lors du changement de statut de vérification', error)
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Erreur lors de la mise à jour de la vérification'
    })
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
