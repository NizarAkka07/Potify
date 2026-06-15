<template>
  <q-page padding style="background: #F8F9FA;">
    <!-- Welcome Header -->
    <div class="row items-center justify-between q-mb-xl q-pa-lg text-white" style="background: linear-gradient(135deg, #0D1B2E 0%, #1A2E40 100%); border-radius: 16px; border-left: 6px solid #FFB300;">
      <div>
        <div class="text-h4 text-weight-bold">Tableau de Bord Admin</div>
        <div class="text-subtitle1 text-grey-4 q-mt-xs">{{ $t('adminDashboard.subtitle') }}</div>
      </div>
      <q-icon name="dashboard" size="64px" color="amber-8" class="q-mr-md" />
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-center q-py-xl">
      <q-spinner-dots size="50px" color="primary" />
    </div>

    <div v-else class="q-gutter-y-lg">
      <!-- KPI Metrics Grid -->
      <div class="row q-col-gutter-lg">
        <!-- Metric 1: Total Users -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="kpi-card text-white" style="background: linear-gradient(135deg, #3A7BD5 0%, #3A6073 100%); border-radius: 12px; overflow: hidden; position: relative;">
            <q-card-section class="q-pa-lg">
              <q-icon name="people" class="absolute-top-right q-ma-md text-white-50" size="48px" style="opacity: 0.3;" />
              <div class="text-subtitle2 text-uppercase text-weight-bold text-grey-3">{{ $t('adminDashboard.registeredUsers') }}</div>
              <div class="text-h3 text-weight-bold q-my-sm">{{ stats.totalUsers }}</div>
              <div class="text-caption text-grey-3">
                <q-icon name="trending_up" color="green-4" /> +12% {{ $t('adminDashboard.thisWeek') }}
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 2: Total Pools -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="kpi-card text-white" style="background: linear-gradient(135deg, #FF9966 0%, #FF5E62 100%); border-radius: 12px; overflow: hidden; position: relative;">
            <q-card-section class="q-pa-lg">
              <q-icon name="account_balance_wallet" class="absolute-top-right q-ma-md text-white-50" size="48px" style="opacity: 0.3;" />
              <div class="text-subtitle2 text-uppercase text-weight-bold text-grey-3">{{ $t('adminDashboard.poolsCreated') }}</div>
              <div class="text-h3 text-weight-bold q-my-sm">{{ stats.totalPools }}</div>
              <div class="text-caption text-grey-3">
                <span class="text-weight-bold">{{ stats.activePools }}</span> {{ $t('adminDashboard.active') }} / <span class="text-weight-bold">{{ stats.completedPools }}</span> {{ $t('adminDashboard.completed') }}
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 3: Total Funds Collected -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="kpi-card text-white" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); border-radius: 12px; overflow: hidden; position: relative;">
            <q-card-section class="q-pa-lg">
              <q-icon name="euro" class="absolute-top-right q-ma-md text-white-50" size="48px" style="opacity: 0.3;" />
              <div class="text-subtitle2 text-uppercase text-weight-bold text-grey-3">{{ $t('adminDashboard.fundsCollected') }}</div>
              <div class="text-h3 text-weight-bold q-my-sm">{{ stats.totalFunds }} €</div>
              <div class="text-caption text-grey-3">
                {{ $t('adminDashboard.averageOf') }} <span class="text-weight-bold">{{ stats.avgGoal }} €</span> {{ $t('adminDashboard.perProject') }}
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 4: Success rate -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="kpi-card text-white" style="background: linear-gradient(135deg, #8E2DE2 0%, #4A00E0 100%); border-radius: 12px; overflow: hidden; position: relative;">
            <q-card-section class="q-pa-lg">
              <q-icon name="check_circle" class="absolute-top-right q-ma-md text-white-50" size="48px" style="opacity: 0.3;" />
              <div class="text-subtitle2 text-uppercase text-weight-bold text-grey-3">{{ $t('adminDashboard.successRate') }}</div>
              <div class="text-h3 text-weight-bold q-my-sm">{{ stats.successRate }}%</div>
              <div class="text-caption text-grey-3">
                {{ $t('adminDashboard.goalsReached') }}
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Quick Action Panels & Charts -->
      <div class="row q-col-gutter-lg q-mt-md">
        <!-- Shortcut Navigation Cards -->
        <div class="col-12 col-md-4">
          <q-card class="q-pa-md shadow-2" style="border-radius: 12px; background: white;">
            <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
              {{ $t('adminDashboard.shortcutsHeader') }}
            </div>
            
            <q-list class="q-gutter-y-sm">
              <q-item clickable v-ripple to="/admin/users" class="q-pa-md rounded-borders bg-blue-1 text-blue-9">
                <q-item-section avatar>
                  <q-icon name="people" size="md" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ $t('adminDashboard.manageAccounts') }}</q-item-label>
                  <q-item-label caption>{{ $t('adminDashboard.manageAccountsDesc') }}</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-ripple to="/admin/pools" class="q-pa-md rounded-borders bg-orange-1 text-orange-9">
                <q-item-section avatar>
                  <q-icon name="account_balance_wallet" size="md" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ $t('adminDashboard.managePools') }}</q-item-label>
                  <q-item-label caption>{{ $t('adminDashboard.managePoolsDesc') }}</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-ripple to="/admin/roles" class="q-pa-md rounded-borders bg-purple-1 text-purple-9">
                <q-item-section avatar>
                  <q-icon name="security" size="md" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ $t('adminDashboard.manageRoles') }}</q-item-label>
                  <q-item-label caption>{{ $t('adminDashboard.manageRolesDesc') }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card>
        </div>

        <!-- Recent Pools Created -->
        <div class="col-12 col-md-8">
          <q-card class="q-pa-md shadow-2" style="border-radius: 12px; background: white;">
            <div class="row items-center justify-between q-mb-md">
              <div class="text-subtitle1 text-weight-bold" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
                {{ $t('adminDashboard.lastProjectsHeader') }}
              </div>
              <q-btn flat color="primary" label="{{ $t('adminDashboard.viewAll') }}" to="/admin/pools" no-caps />
            </div>

            <q-list bordered class="rounded-borders separator">
              <q-item v-for="pool in recentPools" :key="pool.id" class="q-py-md">
                <q-item-section avatar>
                  <q-avatar rounded size="48px">
                    <q-img v-if="pool.imageUrl" :src="pool.imageUrl" />
                    <q-icon v-else name="account_balance" color="grey-6" />
                  </q-avatar>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="text-weight-bold" style="font-size: 1rem;">{{ pool.title }}</q-item-label>
                  <q-item-label caption>{{ $t('adminDashboard.creatorLabel') }} : {{ pool.ownerName }} • {{ pool.category }}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <div class="text-right">
                    <div class="text-weight-bold text-primary">{{ pool.currentAmount || 0 }} €</div>
                    <div class="text-caption text-grey-6">{{ $t('adminDashboard.ofGoal') }} {{ pool.goalAmount }} €</div>
                  </div>
                  <q-chip 
                    :color="pool.status === 'ACTIVE' ? 'orange-2' : 'green-2'" 
                    :text-color="pool.status === 'ACTIVE' ? 'orange-9' : 'green-9'" 
                    size="sm"
                    class="text-weight-bold q-mt-xs"
                  >
                    {{ pool.status }}
                  </q-chip>
                </q-item-section>
              </q-item>

              <div v-if="recentPools.length === 0" class="text-center q-py-xl text-grey-6 text-italic">
                {{ $t('adminDashboard.noPools') }}
              </div>
            </q-list>
          </q-card>
        </div>
      </div>

      <!-- Pending Withdrawal Requests Section -->
      <div class="row q-col-gutter-lg q-mt-md">
        <div class="col-12">
          <q-card class="q-pa-md shadow-2" style="border-radius: 12px; background: white;">
            <div class="text-subtitle1 text-weight-bold q-mb-md" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
              Demandes de Retrait en Attente
            </div>

            <q-table
              flat
              bordered
              :rows="pendingWithdrawals"
              :columns="withdrawalColumns"
              row-key="id"
              :loading="loadingWithdrawals"
              no-data-label="Aucune demande de retrait en attente."
            >
              <template v-slot:body-cell-amount="props">
                <q-td :props="props" class="text-weight-bold text-primary">
                  {{ props.row.amount }} €
                </q-td>
              </template>

              <template v-slot:body-cell-fees="props">
                <q-td :props="props" class="text-negative">
                  - {{ props.row.fees }} €
                </q-td>
              </template>

              <template v-slot:body-cell-net="props">
                <q-td :props="props" class="text-weight-bold text-success" style="color: #2e7d32;">
                  {{ (props.row.amount - props.row.fees).toFixed(2) }} €
                </q-td>
              </template>

              <template v-slot:body-cell-actions="props">
                <q-td :props="props" class="q-gutter-xs">
                  <q-btn
                    label="Confirmer le Retrait"
                    color="positive"
                    unelevated
                    size="sm"
                    no-caps
                    icon="check"
                    :loading="confirmingWithdrawalId === props.row.id"
                    @click="confirmWithdrawalRequest(props.row.id)"
                  />
                </q-td>
              </template>
            </q-table>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { getUsers } from 'src/shared/services/api'
import poolService from 'src/shared/services/poolService'

const $q = useQuasar()
const loading = ref(true)

const stats = reactive({
  totalUsers: 0,
  totalPools: 0,
  activePools: 0,
  completedPools: 0,
  totalFunds: 0,
  avgGoal: 0,
  successRate: 0
})

const recentPools = ref([])

const pendingWithdrawals = ref([])
const loadingWithdrawals = ref(false)
const confirmingWithdrawalId = ref(null)

const withdrawalColumns = [
  { name: 'walletId', label: 'ID Cagnotte', field: 'walletId', align: 'left', sortable: true },
  { name: 'accountHolderName', label: 'Titulaire', field: 'accountHolderName', align: 'left' },
  { name: 'iban', label: 'IBAN', field: 'iban', align: 'left' },
  { name: 'bankName', label: 'Banque', field: 'bankName', align: 'left' },
  { name: 'amount', label: 'Montant demandé', field: 'amount', align: 'right' },
  { name: 'fees', label: 'Frais (2%)', field: 'fees', align: 'right' },
  { name: 'net', label: 'Net à transférer', align: 'right' },
  { name: 'actions', label: 'Actions', align: 'center' }
]

const loadWithdrawals = async () => {
  loadingWithdrawals.value = true
  try {
    const response = await poolService.getTransactions()
    pendingWithdrawals.value = response.data.filter(
      tx => tx.type === 'WITHDRAWAL' && tx.status === 'PENDING'
    )
  } catch (error) {
    console.error('Erreur chargement retraits', error)
  } finally {
    loadingWithdrawals.value = false
  }
}

const confirmWithdrawalRequest = async (transactionId) => {
  confirmingWithdrawalId.value = transactionId
  try {
    await poolService.confirmWithdrawal(transactionId)
    $q.notify({
      type: 'positive',
      message: 'Le retrait a été confirmé et le virement réel a été exécuté avec succès !'
    })
    await Promise.all([loadWithdrawals(), loadStats()])
  } catch (error) {
    console.error('Erreur confirmation retrait', error)
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Erreur lors de la confirmation du retrait.'
    })
  } finally {
    confirmingWithdrawalId.value = null
  }
}

const loadStats = async () => {
  loading.value = true
  try {
    const [usersResponse, poolsResponse] = await Promise.all([
      getUsers(),
      poolService.getAllPools()
    ])

    const usersList = usersResponse.data
    // Filtrer pour ne garder que les cagnottes principales (pas les sous-cagnottes)
    const poolsList = poolsResponse.data.filter(p => !p.parentId)

    stats.totalUsers = usersList.length
    stats.totalPools = poolsList.length

    let totalAmount = 0
    let totalGoal = 0
    let active = 0
    let completed = 0

    poolsList.forEach(p => {
      totalAmount += p.currentAmount || 0
      totalGoal += p.goalAmount || 0
      if (p.status === 'ACTIVE') active++
      if (p.status === 'COMPLETED') completed++
    })

    stats.activePools = active
    stats.completedPools = completed
    stats.totalFunds = totalAmount.toLocaleString('fr-FR')
    stats.avgGoal = poolsList.length ? Math.round(totalGoal / poolsList.length) : 0
    stats.successRate = poolsList.length ? Math.round((completed / poolsList.length) * 100) : 0

    // Sort pools by date to show recent ones
    recentPools.value = [...poolsList]
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      .slice(0, 5)

  } catch (error) {
    console.error('Erreur chargement statistiques', error)
    $q.notify({
      type: 'negative',
      message: 'Erreur de chargement des statistiques du dashboard'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadWithdrawals()
})
</script>

<style scoped>
.kpi-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.kpi-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}
</style>
