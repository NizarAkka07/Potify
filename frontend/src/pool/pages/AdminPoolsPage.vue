<template>
  <q-page padding style="background: #F8F9FA;">
    <div class="row items-center justify-between q-mb-xl q-pa-md bg-white shadow-2" style="border-radius: 12px; border-left: 6px solid #FF5E62;">
      <div>
        <div class="text-h4 text-weight-bold text-dark">{{ $t('adminPools.title') }}</div>
        <div class="text-subtitle2 text-grey-7">{{ $t('adminPools.subtitle') }}</div>
      </div>
      <q-btn color="orange-9" icon="add" :label="$t('home.createButton')" to="/pools/create" no-caps />
    </div>

    <!-- Filters & Search -->
    <q-card class="q-pa-md q-mb-lg shadow-2" style="border-radius: 12px;">
      <div class="row q-col-gutter-md items-center">
        <div class="col-12 col-md-6">
          <q-input outlined dense v-model="filter.search" :label="$t('adminPools.searchPlaceholder')" color="secondary">
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </div>
        <div class="col-12 col-md-3">
          <q-select
            outlined
            dense
            v-model="filter.status"
            :options="statusFilterOptions"
            :label="$t('adminPools.statusFilterPlaceholder')"
            color="secondary"
          />
        </div>
        <div class="col-12 col-md-3 text-right">
          <q-btn :label="$t('adminPools.resetButton')" flat color="grey-7" icon="refresh" @click="resetFilters" no-caps />
        </div>
      </div>
    </q-card>

    <!-- Table or Grid list -->
    <q-table
      :rows="filteredPools"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat
      bordered
      style="border-radius: 12px; background: white;"
      :pagination="{ rowsPerPage: 10 }"
    >
      <!-- Custom image cell -->
      <template v-slot:body-cell-image="props">
        <q-td :props="props">
          <q-avatar rounded size="40px">
            <q-img v-if="props.row.imageUrl" :src="props.row.imageUrl" />
            <q-icon v-else name="account_balance" color="grey-5" />
          </q-avatar>
        </q-td>
      </template>

      <!-- Custom progress cell -->
      <template v-slot:body-cell-progress="props">
        <q-td :props="props">
          <div class="row items-center q-col-gutter-xs">
            <div class="col">
              <q-linear-progress :value="(props.row.currentAmount || 0) / props.row.goalAmount" color="primary" size="8px" rounded />
            </div>
            <div class="col-auto text-caption text-weight-bold">
              {{ Math.round(((props.row.currentAmount || 0) / props.row.goalAmount) * 100) }}%
            </div>
          </div>
          <div class="text-caption text-grey-6">{{ props.row.currentAmount || 0 }} € / {{ props.row.goalAmount }} €</div>
        </q-td>
      </template>

      <!-- Custom status cell -->
      <template v-slot:body-cell-status="props">
        <q-td :props="props" class="text-center">
          <q-chip 
            :color="getStatusColor(props.row.status)" 
            :text-color="getStatusTextColor(props.row.status)" 
            size="sm" 
            class="text-weight-bold"
          >
            {{ props.row.status }}
          </q-chip>
        </q-td>
      </template>

      <!-- Custom fees cell -->
      <template v-slot:body-cell-fees="props">
        <q-td :props="props" class="text-center">
          <q-btn flat dense no-caps color="primary" icon-right="edit" class="text-weight-bold" @click="editPoolFees(props.row)">
            {{ props.row.fees !== undefined && props.row.fees !== null ? props.row.fees : '2.00' }} %
            <q-tooltip>Modifier le taux de frais</q-tooltip>
          </q-btn>
        </q-td>
      </template>

      <!-- Custom Actions cell -->
      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="text-center q-gutter-xs">
          <q-btn flat round color="primary" icon="visibility" size="sm" :to="`/pools/${props.row.id}`">
            <q-tooltip>{{ $t('adminPools.viewPageTooltip') }}</q-tooltip>
          </q-btn>
          <q-btn flat round color="warning" icon="edit" size="sm" :to="`/pools/${props.row.id}/edit`">
            <q-tooltip>{{ $t('adminPools.editTooltip') }}</q-tooltip>
          </q-btn>
          <q-btn 
            flat 
            round 
            color="secondary" 
            icon="check_circle" 
            size="sm" 
            v-if="props.row.status === 'ACTIVE'"
            @click="changeStatus(props.row, 'COMPLETED')"
          >
            <q-tooltip>{{ $t('adminPools.markCompletedTooltip') }}</q-tooltip>
          </q-btn>
          <q-btn 
            flat 
            round 
            color="negative" 
            icon="pause" 
            size="sm" 
            v-if="props.row.status === 'ACTIVE'"
            @click="changeStatus(props.row, 'SUSPENDED')"
          >
            <q-tooltip>{{ $t('adminPools.suspendTooltip') }}</q-tooltip>
          </q-btn>
          <q-btn 
            flat 
            round 
            color="green" 
            icon="play_arrow" 
            size="sm" 
            v-if="props.row.status === 'SUSPENDED'"
            @click="changeStatus(props.row, 'ACTIVE')"
          >
            <q-tooltip>{{ $t('adminPools.reactivateTooltip') }}</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, reactive, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import poolService from 'src/shared/services/poolService'

const $q = useQuasar()
const loading = ref(false)
const pools = ref([])

const filter = reactive({
  search: '',
  status: 'TOUS'
})

const statusFilterOptions = [
  { label: 'Tous les statuts', value: 'TOUS' },
  { label: 'Actives', value: 'ACTIVE' },
  { label: 'Terminées', value: 'COMPLETED' },
  { label: 'Suspendues', value: 'SUSPENDED' }
]

const columns = [
  { name: 'image', align: 'center', label: 'Aperçu', field: 'imageUrl', sortable: false },
  { name: 'title', required: true, label: 'Titre de la cagnotte', align: 'left', field: 'title', sortable: true },
  { name: 'owner', align: 'left', label: 'Créateur', field: 'ownerName', sortable: true },
  { name: 'category', align: 'center', label: 'Catégorie', field: 'category', sortable: true },
  { name: 'progress', align: 'left', label: 'Collecte', field: 'currentAmount', sortable: true },
  { name: 'status', align: 'center', label: 'Statut', field: 'status', sortable: true },
  { name: 'fees', align: 'center', label: 'Frais (%)', field: 'fees', sortable: true },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

const loadPools = async () => {
  loading.value = true
  try {
    const response = await poolService.getAllPools()
    pools.value = response.data
  } catch (error) {
    console.error('Erreur de chargement des cagnottes', error)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du chargement des cagnottes'
    })
  } finally {
    loading.value = false
  }
}

const filteredPools = computed(() => {
  return pools.value.filter(pool => {
    // Exclure les sous-cagnottes
    if (pool.parentId) return false

    // Search filter
    const matchesSearch = 
      pool.title.toLowerCase().includes(filter.search.toLowerCase()) || 
      (pool.ownerName && pool.ownerName.toLowerCase().includes(filter.search.toLowerCase()))
    
    // Status filter
    const matchesStatus = filter.status === 'TOUS' || pool.status === filter.status
    
    return matchesSearch && matchesStatus
  })
})

const getStatusColor = (status) => {
  if (status === 'ACTIVE') return 'orange-2'
  if (status === 'COMPLETED') return 'green-2'
  if (status === 'SUSPENDED') return 'red-2'
  return 'grey-2'
}

const getStatusTextColor = (status) => {
  if (status === 'ACTIVE') return 'orange-9'
  if (status === 'COMPLETED') return 'green-9'
  if (status === 'SUSPENDED') return 'red-9'
  return 'grey-9'
}

const changeStatus = (pool, newStatus) => {
  $q.dialog({
    title: t('adminPools.confirmDialogTitle'),
    message: `${t('adminPools.confirmDialogMessage')} "${pool.title}" vers "${newStatus}" ?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await poolService.updatePool(pool.id, {
        title: pool.title,
        description: pool.description,
        goalAmount: pool.goalAmount,
        status: newStatus,
        videoUrl: pool.videoUrl
      })
      $q.notify({
        type: 'positive',
        message: 'Le statut de la cagnotte a été mis à jour.'
      })
      loadPools()
    } catch (error) {
      console.error(error)
      $q.notify({
        type: 'negative',
        message: 'Erreur de mise à jour du statut'
      })
    }
  })
}

const editPoolFees = (pool) => {
  $q.dialog({
    title: 'Modifier les frais',
    message: `Définir le pourcentage de frais pour la cagnotte "${pool.title}" :`,
    prompt: {
      model: pool.fees !== undefined && pool.fees !== null ? pool.fees.toString() : '2.00',
      type: 'number',
      step: '0.01',
      min: '0',
      max: '100',
      isValid: val => val >= 0 && val <= 100
    },
    cancel: true,
    persistent: true
  }).onOk(async (newFees) => {
    try {
      await poolService.updatePool(pool.id, {
        title: pool.title,
        description: pool.description,
        goalAmount: pool.goalAmount,
        status: pool.status,
        videoUrl: pool.videoUrl,
        fees: parseFloat(newFees)
      })
      $q.notify({
        type: 'positive',
        message: 'Le taux de frais a été mis à jour avec succès.'
      })
      loadPools()
    } catch (error) {
      console.error(error)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors de la mise à jour des frais'
      })
    }
  })
}

const resetFilters = () => {
  filter.search = ''
  filter.status = 'TOUS'
}

onMounted(() => {
  loadPools()
})
</script>

<style scoped>
</style>
