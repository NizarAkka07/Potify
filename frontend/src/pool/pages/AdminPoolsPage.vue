<template>
  <q-page padding style="background: #F8F9FA;">


    <!-- Tab system for Moderator -->
    <div v-if="authStore.isModerator.value" class="q-mb-md">
      <q-tabs
        v-model="activeTab"
        dense
        class="text-grey-7 bg-white q-pa-xs shadow-1"
        active-color="primary"
        indicator-color="primary"
        align="left"
        narrow-indicator
        style="border-radius: 8px;"
      >
        <q-tab name="pools" label="Cagnottes" icon="account_balance_wallet" no-caps />
        <q-tab name="reports" label="Messages Signalés" icon="report_problem" no-caps />
        <q-tab name="poolReports" label="Cagnottes Signalées" icon="warning" no-caps />
      </q-tabs>
    </div>

    <div v-if="activeTab === 'pools'">
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
            <q-btn v-if="canManagePools" flat dense no-caps color="primary" icon-right="edit" class="text-weight-bold" @click="editPoolFees(props.row)">
              {{ props.row.fees !== undefined && props.row.fees !== null ? props.row.fees : '2.00' }} %
              <q-tooltip>Modifier le taux de frais</q-tooltip>
            </q-btn>
            <span v-else class="text-weight-bold text-grey-8">
              {{ props.row.fees !== undefined && props.row.fees !== null ? props.row.fees : '2.00' }} %
            </span>
          </q-td>
        </template>

        <!-- Custom Actions cell -->
        <template v-slot:body-cell-actions="props">
          <q-td :props="props" class="text-center q-gutter-xs">
            <q-btn flat round color="primary" icon="visibility" size="sm" :to="`/pools/${props.row.id}`">
              <q-tooltip>{{ $t('adminPools.viewPageTooltip') }}</q-tooltip>
            </q-btn>
            <q-btn v-if="canManagePools" flat round color="warning" icon="edit" size="sm" :to="`/pools/${props.row.id}/edit`">
              <q-tooltip>{{ $t('adminPools.editTooltip') }}</q-tooltip>
            </q-btn>
            <q-btn 
              flat 
              round 
              color="secondary" 
              icon="check_circle" 
              size="sm" 
              v-if="canManagePools && props.row.status === 'ACTIVE'"
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
              v-if="canManagePools && props.row.status === 'ACTIVE'"
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
              v-if="canManagePools && props.row.status === 'SUSPENDED'"
              @click="changeStatus(props.row, 'ACTIVE')"
            >
              <q-tooltip>{{ $t('adminPools.reactivateTooltip') }}</q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </div>

    <!-- Reported messages view -->
    <div v-if="authStore.isModerator.value && activeTab === 'reports'">
      <q-card class="q-pa-md shadow-2" style="border-radius: 12px; background: white;">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold text-dark">Commentaires signalés par les utilisateurs</div>
          <q-btn flat dense round icon="refresh" color="primary" @click="loadReportedMessages">
            <q-tooltip>Actualiser</q-tooltip>
          </q-btn>
        </div>

        <q-table
          :rows="reportedMessages"
          :columns="reportColumns"
          row-key="id"
          :loading="loadingReports"
          flat
          bordered
          style="border-radius: 12px;"
          :pagination="{ rowsPerPage: 10 }"
          no-data-label="Aucun message signalé pour le moment"
        >
          <!-- Custom content body cell -->
          <template v-slot:body-cell-content="props">
            <q-td :props="props">
              <div class="text-weight-medium text-dark" style="white-space: normal; max-width: 400px; word-break: break-all;">
                {{ props.row.content }}
              </div>
            </q-td>
          </template>

          <!-- Custom count cell -->
          <template v-slot:body-cell-count="props">
            <q-td :props="props" class="text-center">
              <q-chip color="red-1" text-color="red-9" class="text-weight-bold" size="sm">
                {{ props.row.reportCount }}
              </q-chip>
            </q-td>
          </template>

          <!-- Custom actions cell -->
          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="text-center q-gutter-x-xs">
              <q-btn flat round color="info" icon="info" size="sm" @click="showReportDetails(props.row)">
                <q-tooltip>Voir les détails des signalements</q-tooltip>
              </q-btn>
              <q-btn flat round color="positive" icon="check" size="sm" @click="dismissReport(props.row.id)">
                <q-tooltip>Rejeter le signalement (Conserver le message)</q-tooltip>
              </q-btn>
              <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteReportedMessage(props.row.id)">
                <q-tooltip>Supprimer le commentaire</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>

    <!-- Reported pools view -->
    <div v-if="authStore.isModerator.value && activeTab === 'poolReports'">
      <q-card class="q-pa-md shadow-2" style="border-radius: 12px; background: white;">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold text-dark">Cagnottes signalées par les utilisateurs</div>
          <q-btn flat dense round icon="refresh" color="primary" @click="loadReportedPools">
            <q-tooltip>Actualiser</q-tooltip>
          </q-btn>
        </div>

        <q-table
          :rows="reportedPools"
          :columns="poolReportColumns"
          row-key="id"
          :loading="loadingPoolReports"
          flat
          bordered
          style="border-radius: 12px;"
          :pagination="{ rowsPerPage: 10 }"
          no-data-label="Aucune cagnotte signalée pour le moment"
        >
          <!-- Custom title cell linking to details page -->
          <template v-slot:body-cell-title="props">
            <q-td :props="props">
              <router-link :to="`/pools/${props.row.id}`" class="text-primary text-weight-bold text-subtitle2" style="text-decoration: none;">
                {{ props.row.title }}
              </router-link>
            </q-td>
          </template>

          <!-- Custom count cell -->
          <template v-slot:body-cell-count="props">
            <q-td :props="props" class="text-center">
              <q-chip color="red-1" text-color="red-9" class="text-weight-bold" size="sm">
                {{ props.row.reportCount }}
              </q-chip>
            </q-td>
          </template>

          <!-- Custom actions cell -->
          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="text-center q-gutter-x-xs">
              <q-btn flat round color="info" icon="info" size="sm" @click="showPoolReportDetails(props.row)">
                <q-tooltip>Voir les détails des signalements</q-tooltip>
              </q-btn>
              <q-btn flat round color="positive" icon="check" size="sm" @click="dismissPoolReport(props.row.id)">
                <q-tooltip>Rejeter le signalement (Conserver la cagnotte)</q-tooltip>
              </q-btn>
              <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteReportedPool(props.row)">
                <q-tooltip>Supprimer la cagnotte</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>

    <!-- Dialog Détails des Signalements -->
    <q-dialog v-model="detailsDialog">
      <q-card style="border-radius: 16px; min-width: 450px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold text-dark">Détails des Signalements</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pa-md">
          <div class="q-mb-md bg-grey-1 q-pa-sm rounded-borders" style="border-left: 4px solid var(--q-warning);">
            <div class="text-weight-bold text-grey-8">Message signalé :</div>
            <div class="text-italic text-grey-9 q-mt-xs">"{{ selectedMessageForDetails?.content }}"</div>
            <div class="text-caption text-grey-6 q-mt-xs">Auteur : {{ selectedMessageForDetails?.userName }}</div>
          </div>

          <div class="text-subtitle2 q-mb-xs text-weight-medium">Historique des signalements ({{ selectedMessageForDetails?.reports?.length || 0 }}) :</div>
          <q-list bordered separator style="border-radius: 8px;">
            <q-item v-for="rep in selectedMessageForDetails?.reports" :key="rep.id">
              <q-item-section>
                <q-item-label class="text-weight-medium text-primary">
                  {{ rep.userName || 'Utilisateur inconnu' }}
                </q-item-label>
                <q-item-label caption class="text-grey-8">
                  Motif : {{ rep.reason }}
                </q-item-label>
              </q-item-section>
              <q-item-section side top>
                <q-item-label caption>
                  {{ rep.createdAt ? new Date(rep.createdAt).toLocaleString('fr-FR') : '-' }}
                </q-item-label>
              </q-item-section>
            </q-item>
            <q-item v-if="!selectedMessageForDetails?.reports || selectedMessageForDetails.reports.length === 0">
              <q-item-section class="text-center text-grey-5 q-py-md">
                Aucun rapport individuel enregistré.
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn label="Fermer" color="grey" flat v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Dialog Détails des Signalements de Cagnotte -->
    <q-dialog v-model="poolDetailsDialog">
      <q-card style="border-radius: 16px; min-width: 450px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold text-dark">Détails des Signalements de Cagnotte</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pa-md">
          <div class="q-mb-md bg-grey-1 q-pa-sm rounded-borders" style="border-left: 4px solid var(--q-warning);">
            <div class="text-weight-bold text-grey-8">Cagnotte signalée :</div>
            <div class="text-weight-medium text-grey-9 q-mt-xs">{{ selectedPoolForDetails?.title }}</div>
            <div class="text-caption text-grey-6 q-mt-xs">Créateur : {{ selectedPoolForDetails?.ownerName }}</div>
          </div>

          <div class="text-subtitle2 q-mb-xs text-weight-medium">Historique des signalements ({{ selectedPoolForDetails?.reports?.length || 0 }}) :</div>
          <q-list bordered separator style="border-radius: 8px;">
            <q-item v-for="rep in selectedPoolForDetails?.reports" :key="rep.id">
              <q-item-section>
                <q-item-label class="text-weight-medium text-primary">
                  {{ rep.userName || 'Utilisateur inconnu' }}
                </q-item-label>
                <q-item-label caption class="text-grey-8">
                  Motif : {{ rep.reason }}
                </q-item-label>
              </q-item-section>
              <q-item-section side top>
                <q-item-label caption>
                  {{ rep.createdAt ? new Date(rep.createdAt).toLocaleString('fr-FR') : '-' }}
                </q-item-label>
              </q-item-section>
            </q-item>
            <q-item v-if="!selectedPoolForDetails?.reports || selectedPoolForDetails.reports.length === 0">
              <q-item-section class="text-center text-grey-5 q-py-md">
                Aucun rapport individuel enregistré.
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn label="Fermer" color="grey" flat v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, reactive, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import poolService from 'src/shared/services/poolService'
import authStore from 'src/shared/stores/auth'

const canManagePools = computed(() => {
  return authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPoolAdmin.value
})

const $q = useQuasar()
const loading = ref(false)
const pools = ref([])

const activeTab = ref('pools')
const reportedMessages = ref([])
const loadingReports = ref(false)

const reportedPools = ref([])
const loadingPoolReports = ref(false)
const poolDetailsDialog = ref(false)
const selectedPoolForDetails = ref(null)

const reportColumns = [
  { name: 'author', align: 'left', label: 'Auteur', field: 'userName', sortable: true },
  { name: 'content', align: 'left', label: 'Message', field: 'content', sortable: true },
  { name: 'count', align: 'center', label: 'Signalements', field: 'reportCount', sortable: true },
  { name: 'reason', align: 'left', label: 'Motif(s)', field: 'reportReason', sortable: true },
  { name: 'date', align: 'left', label: 'Dernier signalement', field: 'createdAt', sortable: true, format: val => val ? new Date(val).toLocaleString('fr-FR') : '-' },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

const poolReportColumns = [
  { name: 'owner', align: 'left', label: 'Créateur', field: 'ownerName', sortable: true },
  { name: 'title', align: 'left', label: 'Titre de la cagnotte', field: 'title', sortable: true },
  { name: 'count', align: 'center', label: 'Signalements', field: 'reportCount', sortable: true },
  { name: 'reason', align: 'left', label: 'Motif(s)', field: 'reportReason', sortable: true },
  { name: 'date', align: 'left', label: 'Dernier signalement', field: 'createdAt', sortable: true, format: val => val ? new Date(val).toLocaleString('fr-FR') : '-' },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

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

const columns = computed(() => {
  const baseCols = [
    { name: 'image', align: 'center', label: 'Aperçu', field: 'imageUrl', sortable: false },
    { name: 'title', required: true, label: 'Titre de la cagnotte', align: 'left', field: 'title', sortable: true },
    { name: 'owner', align: 'left', label: 'Créateur', field: 'ownerName', sortable: true },
    { name: 'category', align: 'center', label: 'Catégorie', field: 'category', sortable: true }
  ]
  if (canManagePools.value) {
    baseCols.push(
      { name: 'progress', align: 'left', label: 'Collecte', field: 'currentAmount', sortable: true },
      { name: 'status', align: 'center', label: 'Statut', field: 'status', sortable: true },
      { name: 'fees', align: 'center', label: 'Frais (%)', field: 'fees', sortable: true }
    )
  }
  baseCols.push(
    { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
  )
  return baseCols
})

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

const loadReportedMessages = async () => {
  loadingReports.value = true
  try {
    const response = await poolService.getReportedMessages()
    reportedMessages.value = response.data
  } catch (err) {
    console.error('Erreur chargement signalements:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du chargement des messages signalés.'
    })
  } finally {
    loadingReports.value = false
  }
}

const dismissReport = async (messageId) => {
  try {
    await poolService.dismissReport(messageId)
    $q.notify({
      type: 'positive',
      message: 'Le signalement a été rejeté.'
    })
    await loadReportedMessages()
  } catch (err) {
    console.error('Erreur rejet signalement:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du rejet du signalement.'
    })
  }
}

const deleteReportedMessage = async (messageId) => {
  $q.dialog({
    title: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer définitivement ce commentaire ? Cette action est irréversible.',
    cancel: {
      label: 'Annuler',
      flat: true
    },
    ok: {
      label: 'Supprimer',
      color: 'negative',
      unelevated: true
    },
    persistent: true
  }).onOk(async () => {
    try {
      await poolService.deleteMessage(messageId)
      $q.notify({
        type: 'positive',
        message: 'Le commentaire a été supprimé.'
      })
      await loadReportedMessages()
    } catch (err) {
      console.error('Erreur suppression commentaire:', err)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors de la suppression.'
      })
    }
  })
}

const detailsDialog = ref(false)
const selectedMessageForDetails = ref(null)

const showReportDetails = (msg) => {
  selectedMessageForDetails.value = msg
  detailsDialog.value = true
}

const loadReportedPools = async () => {
  loadingPoolReports.value = true
  try {
    const response = await poolService.getReportedPools()
    reportedPools.value = response.data
  } catch (err) {
    console.error('Erreur chargement signalements cagnottes:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du chargement des cagnottes signalées.'
    })
  } finally {
    loadingPoolReports.value = false
  }
}

const dismissPoolReport = async (poolId) => {
  try {
    await poolService.dismissPoolReport(poolId)
    $q.notify({
      type: 'positive',
      message: 'Les signalements de la cagnotte ont été rejetés.'
    })
    await loadReportedPools()
  } catch (err) {
    console.error('Erreur rejet signalements cagnotte:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du rejet des signalements.'
    })
  }
}

const deleteReportedPool = async (pool) => {
  $q.dialog({
    title: 'Confirmer la suppression',
    message: `Voulez-vous vraiment supprimer définitivement la cagnotte "${pool.title}" ? Cette action est irréversible et supprimera toutes les contributions associées.`,
    cancel: {
      label: 'Annuler',
      flat: true
    },
    ok: {
      label: 'Supprimer',
      color: 'negative',
      unelevated: true
    },
    persistent: true
  }).onOk(async () => {
    try {
      await poolService.deletePool(pool.id)
      $q.notify({
        type: 'positive',
        message: 'La cagnotte a été supprimée avec succès.'
      })
      await loadReportedPools()
      await loadPools()
    } catch (err) {
      console.error('Erreur suppression cagnotte:', err)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors de la suppression de la cagnotte.'
      })
    }
  })
}

const showPoolReportDetails = (pool) => {
  selectedPoolForDetails.value = pool
  poolDetailsDialog.value = true
}

const resetFilters = () => {
  filter.search = ''
  filter.status = 'TOUS'
}

onMounted(() => {
  loadPools()
  if (authStore.isModerator.value) {
    loadReportedMessages()
    loadReportedPools()
  }
})
</script>

<style scoped>
</style>
