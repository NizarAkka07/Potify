<template>
  <q-page padding class="admin-pools-page font-inter">

    <!-- Tab system for Moderator/PoolAdmin -->
    <div v-if="authStore.isModerator.value || authStore.isPoolAdmin.value" class="q-mb-md">
      <q-tabs
        v-model="activeTab"
        dense
        class="premium-tabs q-pa-xs no-shadow"
        active-color="primary"
        indicator-color="primary"
        align="left"
        narrow-indicator
      >
        <q-tab name="pools" label="Cagnottes" icon="account_balance_wallet" no-caps />
        <q-tab name="reports" label="Messages Signalés" icon="report_problem" no-caps v-if="authStore.isModerator.value" />
        <q-tab name="poolReports" label="Cagnottes Signalées" icon="warning" no-caps />
        <q-tab name="suspendedPools" label="Cagnottes Suspendues" icon="pause_circle" no-caps />
      </q-tabs>
    </div>

    <div v-if="activeTab === 'pools'">
      <!-- Filters & Search -->
      <q-card class="premium-card q-pa-md q-mb-lg no-shadow">
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
      <q-card class="premium-card no-shadow">
        <q-table
          :rows="filteredPools"
          :columns="columns"
          row-key="id"
          :loading="loading"
          flat
          :pagination="{ rowsPerPage: 10 }"
          class="transparent-table"
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
                v-if="canManagePools && (props.row.status === 'ACTIVE' || props.row.status === 'PUBLIEE')"
                @click="changeStatus(props.row, 'CLOTUREE')"
              >
                <q-tooltip>{{ $t('adminPools.markCompletedTooltip') }}</q-tooltip>
              </q-btn>
              <q-btn 
                flat 
                round 
                color="negative" 
                icon="pause" 
                size="sm" 
                v-if="canManagePools && (props.row.status === 'ACTIVE' || props.row.status === 'PUBLIEE')"
                @click="changeStatus(props.row, 'SUSPENDUE')"
              >
                <q-tooltip>{{ $t('adminPools.suspendTooltip') }}</q-tooltip>
              </q-btn>
              <q-btn 
                flat 
                round 
                color="green" 
                icon="play_arrow" 
                size="sm" 
                v-if="canManagePools && (props.row.status === 'SUSPENDED' || props.row.status === 'SUSPENDUE')"
                @click="changeStatus(props.row, 'PUBLIEE')"
              >
                <q-tooltip>{{ $t('adminPools.reactivateTooltip') }}</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>

    <!-- Reported messages view -->
    <div v-if="authStore.isModerator.value && activeTab === 'reports'">
      <q-card class="premium-card no-shadow q-pa-md">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold card-header-title">Commentaires signalés par les utilisateurs</div>
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
          class="transparent-table"
          :pagination="{ rowsPerPage: 10 }"
          no-data-label="Aucun message signalé pour le moment"
        >
          <!-- Custom content body cell -->
          <template v-slot:body-cell-content="props">
            <q-td :props="props">
              <div class="text-weight-medium table-text" style="white-space: normal; max-width: 400px; word-break: break-all;">
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
    <div v-if="(authStore.isModerator.value || authStore.isPoolAdmin.value) && activeTab === 'poolReports'">
      <q-card class="premium-card no-shadow q-pa-md">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold card-header-title">Cagnottes signalées par les utilisateurs</div>
          <q-btn flat dense round icon="refresh" color="primary" @click="loadReportedPools">
            <q-tooltip>Actualiser</q-tooltip>
          </q-btn>
        </div>

        <q-table
          :rows="activeReportedPools"
          :columns="poolReportColumns"
          row-key="id"
          :loading="loadingPoolReports"
          flat
          class="transparent-table"
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
              <!-- Suspend action shown for moderators/admins if not already suspended -->
              <q-btn 
                flat 
                round 
                color="warning" 
                icon="pause" 
                size="sm" 
                v-if="props.row.status !== 'SUSPENDUE' && props.row.status !== 'SUSPENDED'"
                @click="suspendReportedPool(props.row)"
              >
                <q-tooltip>Suspendre la cagnotte</q-tooltip>
              </q-btn>
              <!-- Delete action ONLY shown for Super Admin -->
              <q-btn 
                flat 
                round 
                color="negative" 
                icon="delete" 
                size="sm" 
                v-if="authStore.isSuperAdmin.value"
                @click="deleteReportedPool(props.row)"
              >
                <q-tooltip>Supprimer la cagnotte</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>

    <!-- Suspended pools view -->
    <div v-if="(authStore.isModerator.value || authStore.isPoolAdmin.value) && activeTab === 'suspendedPools'">
      <q-card class="premium-card no-shadow q-pa-md">
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6 text-weight-bold card-header-title">Cagnottes suspendues</div>
          <q-btn flat dense round icon="refresh" color="primary" @click="loadPools">
            <q-tooltip>Actualiser</q-tooltip>
          </q-btn>
        </div>

        <q-table
          :rows="suspendedPools"
          :columns="suspendedPoolColumns"
          row-key="id"
          :loading="loading"
          flat
          class="transparent-table"
          :pagination="{ rowsPerPage: 10 }"
          no-data-label="Aucune cagnotte suspendue pour le moment"
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

          <!-- Custom title cell linking to details page -->
          <template v-slot:body-cell-title="props">
            <q-td :props="props">
              <router-link :to="`/pools/${props.row.id}`" class="text-primary text-weight-bold text-subtitle2" style="text-decoration: none;">
                {{ props.row.title }}
              </router-link>
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

          <!-- Custom actions cell -->
          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="text-center q-gutter-x-xs">
              <q-btn flat round color="primary" icon="visibility" size="sm" :to="`/pools/${props.row.id}`">
                <q-tooltip>Voir la cagnotte</q-tooltip>
              </q-btn>
              <!-- Reactivate / publish again -->
              <q-btn 
                flat 
                round 
                color="positive" 
                icon="play_arrow" 
                size="sm" 
                @click="reactivatePool(props.row)"
              >
                <q-tooltip>Rétablir et publier la cagnotte</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>
    </div>

    <!-- Dialog Détails des Signalements -->
    <q-dialog v-model="detailsDialog">
      <q-card class="premium-dialog-card" style="min-width: 450px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold dialog-title">Détails des Signalements</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pa-md">
          <div class="q-mb-md reported-summary-box q-pa-sm rounded-borders">
            <div class="text-weight-bold text-muted-title">Message signalé :</div>
            <div class="text-italic text-content q-mt-xs">"{{ selectedMessageForDetails?.content }}"</div>
            <div class="text-caption text-sub q-mt-xs">Auteur : {{ selectedMessageForDetails?.userName }}</div>
          </div>

          <div class="text-subtitle2 q-mb-xs text-weight-medium">Historique des signalements ({{ selectedMessageForDetails?.reports?.length || 0 }}) :</div>
          <q-list bordered separator style="border-radius: 8px;">
            <q-item v-for="rep in selectedMessageForDetails?.reports" :key="rep.id">
              <q-item-section>
                <q-item-label class="text-weight-medium text-primary">
                  {{ rep.userName || 'Utilisateur inconnu' }}
                </q-item-label>
                <q-item-label caption class="text-content">
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
      <q-card class="premium-dialog-card" style="min-width: 450px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold dialog-title">Détails des Signalements de Cagnotte</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pa-md">
          <div class="q-mb-md reported-summary-box q-pa-sm rounded-borders">
            <div class="text-weight-bold text-muted-title">Cagnotte signalée :</div>
            <div class="text-weight-medium text-content q-mt-xs">{{ selectedPoolForDetails?.title }}</div>
            <div class="text-caption text-sub q-mt-xs">Créateur : {{ selectedPoolForDetails?.ownerName }}</div>
          </div>

          <div class="text-subtitle2 q-mb-xs text-weight-medium">Historique des signalements ({{ selectedPoolForDetails?.reports?.length || 0 }}) :</div>
          <q-list bordered separator style="border-radius: 8px;">
            <q-item v-for="rep in selectedPoolForDetails?.reports" :key="rep.id">
              <q-item-section>
                <q-item-label class="text-weight-medium text-primary">
                  {{ rep.userName || 'Utilisateur inconnu' }}
                </q-item-label>
                <q-item-label caption class="text-content">
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

    <!-- Modale de personnalisation de notification Admin (Suspension / Réactivation) -->
    <q-dialog v-model="workflowDialog.show" persistent>
      <q-card style="min-width: 500px; border-radius: 16px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold" :class="workflowDialog.action === 'suspend' ? 'text-negative' : 'text-positive'">
            <q-icon :name="workflowDialog.action === 'suspend' ? 'pause_circle' : 'check_circle'" size="28px" class="q-mr-sm" />
            {{ workflowDialog.action === 'suspend' ? 'Suspendre la cagnotte' : 'Réactiver / Approuver la cagnotte' }}
          </div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pa-md">
          <div class="text-subtitle2 text-grey-8 q-mb-md">
            Cagnotte : <strong>{{ workflowDialog.pool?.title }}</strong> (Créateur : {{ workflowDialog.pool?.ownerName || 'Inconnu' }})
          </div>

          <q-input
            v-model="workflowDialog.title"
            label="Titre de la notification"
            outlined
            dense
            class="q-mb-md"
            hint="Titre de la notification et de l'email envoyés au propriétaire"
          />

          <q-input
            v-model="workflowDialog.message"
            type="textarea"
            label="Message d'explication pour le propriétaire"
            outlined
            rows="4"
            hint="Message personnalisé expliquant les raisons à l'utilisateur."
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn label="Annuler" flat color="grey" v-close-popup />
          <q-btn 
            :label="workflowDialog.action === 'suspend' ? 'Suspendre et Notifier' : 'Réactiver et Notifier'" 
            :color="workflowDialog.action === 'suspend' ? 'negative' : 'positive'"
            unelevated
            :loading="workflowDialog.loading"
            @click="confirmWorkflowAction"
          />
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
  return authStore.isSuperAdmin.value || authStore.isPoolAdmin.value
})

const $q = useQuasar()
const loading = ref(false)
const pools = ref([])

const activeTab = ref('pools')
const reportedMessages = ref([])
const loadingReports = ref(false)

const reportedPools = ref([])
const activeReportedPools = computed(() => {
  return reportedPools.value.filter(p => p.status !== 'SUSPENDUE' && p.status !== 'SUSPENDED' && p.status !== 'ARCHIVEE')
})
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

const suspendedPoolColumns = [
  { name: 'image', align: 'center', label: 'Aperçu', field: 'imageUrl', sortable: false },
  { name: 'title', required: true, label: 'Titre de la cagnotte', align: 'left', field: 'title', sortable: true },
  { name: 'owner', align: 'left', label: 'Créateur', field: 'ownerName', sortable: true },
  { name: 'category', align: 'center', label: 'Catégorie', field: 'category', sortable: true },
  { name: 'progress', align: 'left', label: 'Collecte', field: 'currentAmount', sortable: true },
  { name: 'status', align: 'center', label: 'Statut', field: 'status', sortable: true },
  { name: 'actions', align: 'center', label: 'Actions', field: 'actions', sortable: false }
]

const suspendedPools = computed(() => {
  return pools.value.filter(p => p.status === 'SUSPENDUE' || p.status === 'SUSPENDED')
})

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
    let matchesStatus = false
    if (filter.status === 'TOUS') {
      matchesStatus = true
    } else if (filter.status === 'ACTIVE') {
      matchesStatus = pool.status === 'ACTIVE' || pool.status === 'PUBLIEE'
    } else if (filter.status === 'COMPLETED') {
      matchesStatus = pool.status === 'COMPLETED' || pool.status === 'CLOTUREE'
    } else if (filter.status === 'SUSPENDED') {
      matchesStatus = pool.status === 'SUSPENDED' || pool.status === 'SUSPENDUE'
    }
    
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
    message: 'Voulez-vous vraiment supprimer ce commentaire ? Un avertissement avec le motif sera envoyé à l\'auteur.',
    prompt: {
      model: 'Contenu non conforme aux règles de la communauté.',
      type: 'text',
      label: 'Motif de suppression / Avertissement'
    },
    cancel: {
      label: 'Annuler',
      flat: true
    },
    ok: {
      label: 'Supprimer et Notifier',
      color: 'negative',
      unelevated: true
    },
    persistent: true
  }).onOk(async (reason) => {
    try {
      await poolService.deleteMessage(messageId, reason)
      $q.notify({
        type: 'positive',
        message: 'Le commentaire a été supprimé et l\'auteur a été notifié.'
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

const workflowDialog = ref({
  show: false,
  action: 'suspend',
  pool: null,
  title: '',
  message: '',
  loading: false
})

const suspendReportedPool = (pool) => {
  workflowDialog.value = {
    show: true,
    action: 'suspend',
    pool,
    title: 'Cagnotte suspendue par l\'administration',
    message: `Votre cagnotte "${pool.title}" a été temporairement suspendue par un modérateur. Motif : Signalement ou non-conformité aux règles de la communauté.`,
    loading: false
  }
}

const reactivatePool = (pool) => {
  workflowDialog.value = {
    show: true,
    action: 'approve',
    pool,
    title: 'Cagnotte réactivée / approuvée 🎉',
    message: `Excellente nouvelle ! Votre cagnotte "${pool.title}" a été vérifiée, validée par la modération et est de nouveau active sur la plateforme.`,
    loading: false
  }
}

const confirmWorkflowAction = async () => {
  const { action, pool, title, message } = workflowDialog.value
  if (!pool) return

  workflowDialog.value.loading = true
  try {
    if (action === 'suspend') {
      await poolService.suspendPool(pool.id, { title, reason: message, message })
      $q.notify({
        type: 'positive',
        message: 'La cagnotte a été suspendue et le créateur notifié.'
      })
    } else {
      await poolService.approvePool(pool.id, { title, message })
      $q.notify({
        type: 'positive',
        message: 'La cagnotte a été réactivée et le créateur notifié.'
      })
    }
    workflowDialog.value.show = false
    await loadPools()
    await loadReportedPools()
  } catch (err) {
    console.error('Erreur lors de l action de modération:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de l exécution de l action.'
    })
  } finally {
    workflowDialog.value.loading = false
  }
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
  if (authStore.isModerator.value || authStore.isPoolAdmin.value) {
    loadReportedPools()
  }
  if (authStore.isModerator.value) {
    loadReportedMessages()
  }
})
</script>

<style scoped>
.admin-pools-page {
  --bg-dashboard: #f4f7f6;
  --card-bg: #ffffff;
  --card-border: rgba(0, 230, 118, 0.08);
  --card-shadow: 0 8px 24px -4px rgba(9, 31, 26, 0.04), 0 1px 2px rgba(9, 31, 26, 0.02);
  --card-shadow-hover: 0 20px 32px -8px rgba(9, 31, 26, 0.08), 0 1px 4px rgba(9, 31, 26, 0.03);
  --text-main: #091f1a;
  --text-muted: #627571;
  --border-light: rgba(9, 31, 26, 0.06);
  --table-header-bg: #edf2f0;

  background: var(--bg-dashboard);
  color: var(--text-main);
  min-height: 100vh;
  transition: background 0.3s ease, color 0.3s ease;
}

body.body--dark .admin-pools-page {
  --bg-dashboard: #05100d;
  --card-bg: #091f1a;
  --card-border: rgba(0, 230, 118, 0.15);
  --card-shadow: 0 8px 24px -4px rgba(0, 0, 0, 0.35);
  --card-shadow-hover: 0 20px 32px -8px rgba(0, 0, 0, 0.5);
  --text-main: #f1f5f4;
  --text-muted: #8ea39f;
  --border-light: rgba(255, 255, 255, 0.08);
  --table-header-bg: #061713;
}

.font-inter {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.premium-card {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
  border-radius: 16px !important;
  box-shadow: var(--card-shadow) !important;
  color: var(--text-main) !important;
  transition: all 0.3s ease;
}

.premium-card:hover {
  box-shadow: var(--card-shadow-hover) !important;
}

.premium-tabs {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
  border-radius: 12px !important;
  color: var(--text-muted) !important;
}

.card-header-title {
  color: var(--text-main) !important;
  font-weight: 700;
}

/* Table styling overrides */
.transparent-table {
  background: transparent !important;
}

.transparent-table :deep(.q-table__container) {
  background: transparent !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.transparent-table :deep(.q-table) {
  background: transparent !important;
  color: var(--text-main) !important;
}

.transparent-table :deep(thead tr) {
  background-color: var(--table-header-bg) !important;
}

.transparent-table :deep(thead th) {
  color: var(--text-muted) !important;
  font-weight: 700 !important;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.5px;
  border-bottom: 1px solid var(--border-light) !important;
}

.transparent-table :deep(tbody td) {
  border-bottom: 1px solid var(--border-light) !important;
  color: var(--text-main) !important;
}

.transparent-table :deep(tbody tr:hover) {
  background: rgba(0, 230, 118, 0.03) !important;
}

body.body--dark .transparent-table :deep(tbody tr:hover) {
  background: rgba(0, 230, 118, 0.05) !important;
}

/* Dialog Styles */
.premium-dialog-card {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
  border-radius: 20px !important;
  color: var(--text-main) !important;
}

.dialog-title {
  color: var(--text-main) !important;
}

.reported-summary-box {
  background: rgba(250, 204, 21, 0.06) !important;
  border: 1px solid rgba(250, 204, 21, 0.15) !important;
  border-left: 4px solid var(--q-warning) !important;
}

.text-muted-title {
  color: var(--text-muted) !important;
}

.text-content {
  color: var(--text-main) !important;
}

.text-sub {
  color: var(--text-muted) !important;
  font-size: 0.75rem;
}
</style>
