<template>
  <q-page class="admin-dashboard-page q-pa-xl font-inter">
    
    <!-- Tabler Header Title -->
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="text-overline text-grey-5 font-inter text-weight-bold" style="letter-spacing: 0.8px; line-height: 1.2;">
          Aperçu analytique
        </div>
        <h1 class="dashboard-title q-my-none text-weight-bold text-dark font-inter" style="font-size: 1.8rem; letter-spacing: -0.5px;">
          Tableau de bord
        </h1>
      </div>
      <div class="row items-center q-gutter-md">
        <q-btn
          outline
          no-caps
          color="primary"
          icon="open_in_new"
          label="Visiter le site"
          to="/"
          class="action-btn text-weight-bold bg-white text-primary"
        />
        <div class="role-badge">
          <q-avatar size="24px" class="bg-blue-1 text-primary q-mr-sm">
            <q-icon name="admin_panel_settings" size="16px" />
          </q-avatar>
          <div class="column">
            <span class="text-caption text-grey-5 leading-none" style="font-size: 0.65rem; font-weight: 700; letter-spacing: 0.5px;">RÔLE ACTUEL</span>
            <span class="text-weight-bold text-dark font-inter leading-none q-mt-xs" style="font-size: 0.75rem;">{{ userRoleName }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="flex flex-center q-py-xl" style="min-height: 60vh;">
      <q-spinner-dots size="50px" color="primary" />
    </div>

    <div v-else class="q-gutter-y-lg">
      
      <!-- 1. Six KPI Cards Row -->
      <div class="row q-col-gutter-md">
        <!-- Metric 1: Nouveaux utilisateurs -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-success text-weight-bold">
                  {{ stats.totalUsers > 0 ? 'Actif' : '0%' }}
                </span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.totalUsers }}</div>
              <div class="text-caption text-grey-5 font-inter">Inscrits</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 2: Cagnottes créées -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-grey-5 text-weight-bold">Total</span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.totalPools }}</div>
              <div class="text-caption text-grey-5 font-inter">Cagnottes</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 3: Cagnottes Actives -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-success text-weight-bold">En cours</span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.activePools }}</div>
              <div class="text-caption text-grey-5 font-inter">Actives</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 4: Cagnottes Terminées -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-success text-weight-bold">Finies</span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.completedPools }}</div>
              <div class="text-caption text-grey-5 font-inter">Terminées</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 5: Fonds collectés -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-success text-weight-bold">Cumulé</span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.totalFunds }} €</div>
              <div class="text-caption text-grey-5 font-inter">Collectés</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Metric 6: Taux de réussite -->
        <div class="col-6 col-sm-4 col-md-2">
          <q-card class="tabler-kpi-card no-shadow">
            <q-card-section class="q-pa-md">
              <div class="row justify-between items-center q-mb-xs">
                <span class="trend-indicator text-primary text-weight-bold">Ratio</span>
              </div>
              <div class="kpi-value text-dark q-mb-xs">{{ stats.successRate }}%</div>
              <div class="text-caption text-grey-5 font-inter">Réussite</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 2. Chart and Side Column Grid -->
      <div class="row q-col-gutter-lg">
        
        <!-- LEFT PANEL: Development line chart + table below it -->
        <div class="col-12 col-md-8">
          <q-card class="tabler-card no-shadow">
            <q-card-section class="card-header q-py-md q-px-lg">
              <div class="text-subtitle1 text-weight-bold text-dark font-inter">
                Activité de développement (Fonds cumulés)
              </div>
            </q-card-section>
            <q-separator class="separator-light" />
            
            <q-card-section class="q-pa-lg">
              <VueApexCharts
                type="area"
                height="240"
                :options="chartOptions"
                :series="chartSeries"
              />
            </q-card-section>
            
            <q-separator class="separator-light" />

            <!-- Table of recent activities embedded in card (just like the template commits table) -->
            <div class="tabler-table-header row items-center text-overline text-grey-5 font-inter text-weight-bold q-px-lg q-py-sm">
              <div class="col-4">Utilisateur</div>
              <div class="col-5">Action</div>
              <div class="col-2 text-right">Date</div>
              <div class="col-1 text-center"></div>
            </div>
            <q-separator class="separator-light" />
            
            <div v-for="act in recentActivities" :key="act.id" class="row items-center q-px-lg q-py-md list-row-hover border-bottom-light">
              <div class="col-4 row items-center q-gutter-x-sm">
                <q-avatar size="28px" class="bg-grey-2 border-gray">
                  <q-img v-if="act.avatar" :src="act.avatar" />
                  <q-icon v-else name="person" color="grey-5" />
                </q-avatar>
                <span class="text-weight-bold text-dark font-inter">{{ act.userName }}</span>
              </div>
              <div class="col-5 text-dark font-inter text-body2 text-truncate">
                {{ act.description }}
              </div>
              <div class="col-2 text-right text-grey-5 font-inter text-caption">
                {{ act.date }}
              </div>
              <div class="col-1 text-center">
                <q-btn flat round color="grey-3" text-color="grey-5" icon="delete" size="sm" @click="deletePool(act.id)" />
              </div>
            </div>

            <div v-if="recentActivities.length === 0" class="text-center q-py-xl text-grey-5 text-italic">
              Aucune activité récente enregistrée.
            </div>
          </q-card>
        </div>

        <!-- RIGHT PANEL: Alert + Charts + Three vertical comment indicators -->
        <div class="col-12 col-md-4">
          <div class="q-gutter-y-lg">
            <!-- Blue Info Banner -->
            <div class="tabler-alert-banner q-pa-md">
              <span class="text-body2 text-blue-9 font-inter">
                Vous rencontrez des difficultés ? Consultez notre <strong>documentation</strong> contenant des exemples de code.
              </span>
            </div>

            <!-- Single Donut Chart Card -->
            <q-card class="tabler-card no-shadow">
              <q-card-section class="card-header q-py-md q-px-lg">
                <div class="text-subtitle2 text-weight-bold text-dark font-inter">
                  Répartition des Cagnottes par Catégorie
                </div>
              </q-card-section>
              <q-separator class="separator-light" />
              <q-card-section class="q-pa-md">
                <div v-if="recentPools.length > 0" class="flex flex-center">
                  <VueApexCharts
                    type="donut"
                    width="100%"
                    :options="categoryChartOptions"
                    :series="categoryChartSeries"
                  />
                </div>
                <div v-else class="text-center q-py-xl text-grey-5 text-italic">
                  Aucun graphique disponible (cagnottes insuffisantes).
                </div>
              </q-card-section>
            </q-card>

            <!-- Three vertical KPI widgets with lines -->
            <div class="row q-col-gutter-md">
              <!-- Widget 1 -->
              <div class="col-12 col-sm-4 col-md-12">
                <q-card class="tabler-kpi-card no-shadow relative-position overflow-hidden q-pa-md">
                  <div class="text-caption text-grey-5 font-inter text-uppercase text-weight-bold text-center">Nouveaux retraits</div>
                  <div class="kpi-value text-dark q-my-xs text-center">{{ pendingWithdrawals.length }}</div>
                  <div class="q-mt-sm" style="height: 4px; background: #f1f3f9; border-radius: 2px;">
                    <div style="height: 100%; width: 45%; background: #467fcf; border-radius: 2px;"></div>
                  </div>
                </q-card>
              </div>

              <!-- Widget 2 -->
              <div class="col-12 col-sm-4 col-md-12">
                <q-card class="tabler-kpi-card no-shadow relative-position overflow-hidden q-pa-md">
                  <div class="text-caption text-grey-5 font-inter text-uppercase text-weight-bold text-center">Objectif moyen</div>
                  <div class="kpi-value text-dark q-my-xs text-center">{{ stats.avgGoal }} €</div>
                  <div class="q-mt-sm" style="height: 4px; background: #f1f3f9; border-radius: 2px;">
                    <div style="height: 100%; width: 68%; background: #5eba00; border-radius: 2px;"></div>
                  </div>
                </q-card>
              </div>

              <!-- Widget 3 -->
              <div class="col-12 col-sm-4 col-md-12">
                <q-card class="tabler-kpi-card no-shadow relative-position overflow-hidden q-pa-md">
                  <div class="text-caption text-grey-5 font-inter text-uppercase text-weight-bold text-center">Membres inscrits</div>
                  <div class="kpi-value text-dark q-my-xs text-center">{{ stats.totalUsers }}</div>
                  <div class="q-mt-sm" style="height: 4px; background: #f1f3f9; border-radius: 2px;">
                    <div style="height: 100%; width: 35%; background: #ffc107; border-radius: 2px;"></div>
                  </div>
                </q-card>
              </div>
            </div>
          </div>
        </div>

      </div>

      <!-- 3. Four Mini Icon Cards Row -->
      <div class="row q-col-gutter-md">
        <!-- Card 1: blue -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="tabler-kpi-card no-shadow flex items-center q-pa-sm">
            <div class="icon-block bg-blue text-white flex flex-center q-mr-md">
              <q-icon name="euro" size="sm" />
            </div>
            <div>
              <div class="text-subtitle1 text-weight-bold text-dark leading-none">{{ stats.totalFunds }} €</div>
              <div class="text-caption text-grey-5">Fonds collectés</div>
            </div>
          </q-card>
        </div>

        <!-- Card 2: green -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="tabler-kpi-card no-shadow flex items-center q-pa-sm">
            <div class="icon-block bg-green text-white flex flex-center q-mr-md">
              <q-icon name="check_circle" size="sm" />
            </div>
            <div>
              <div class="text-subtitle1 text-weight-bold text-dark leading-none">{{ stats.activePools }} cagnottes</div>
              <div class="text-caption text-grey-5">Actives en ce moment</div>
            </div>
          </q-card>
        </div>

        <!-- Card 3: red -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="tabler-kpi-card no-shadow flex items-center q-pa-sm">
            <div class="icon-block bg-red text-white flex flex-center q-mr-md">
              <q-icon name="people" size="sm" />
            </div>
            <div>
              <div class="text-subtitle1 text-weight-bold text-dark leading-none">{{ stats.totalUsers }} membres</div>
              <div class="text-caption text-grey-5">Inscrits sur Potify</div>
            </div>
          </q-card>
        </div>

        <!-- Card 4: yellow -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="tabler-kpi-card no-shadow flex items-center q-pa-sm">
            <div class="icon-block bg-warning text-white flex flex-center q-mr-md">
              <q-icon name="history" size="sm" />
            </div>
            <div>
              <div class="text-subtitle1 text-weight-bold text-dark leading-none">{{ auditLogs.length }} actions</div>
              <div class="text-caption text-grey-5">Journal d'audit actif</div>
            </div>
          </q-card>
        </div>
      </div>

      <!-- 4. Post cards with images (Puffin Bird / Nature style) -->
      <div v-if="featuredPools.length > 0" class="row q-col-gutter-lg">
        <div v-for="pool in featuredPools" :key="pool.id" class="col-12 col-md-6">
          <q-card class="tabler-card no-shadow overflow-hidden flex no-wrap items-center">
            <q-img :src="pool.imageUrl" style="width: 150px; height: 150px; object-fit: cover;" />
            <q-card-section class="q-pa-md flex-1">
              <div class="text-subtitle1 text-weight-bold text-dark font-inter q-mb-xs">{{ pool.title }}</div>
              <div class="text-caption text-grey-6 q-mb-md text-truncate" style="max-height: 48px; white-space: normal;">
                {{ pool.description }}
              </div>
              <div class="row justify-between items-center">
                <div class="row items-center q-gutter-x-sm">
                  <q-avatar size="24px" class="bg-primary text-white text-caption">{{ pool.initials }}</q-avatar>
                  <span class="text-caption text-grey-8 text-weight-bold">{{ pool.ownerName }}</span>
                </div>
                <q-btn flat round dense color="red-4" icon="favorite" size="sm" />
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 5. Large Detailed Engagement & User Table -->
      <q-card class="tabler-card no-shadow">
        <div class="card-header q-py-md q-px-lg row items-center justify-between">
          <div class="text-subtitle1 text-weight-bold text-dark font-inter">
            Membres actifs & Engagement des projets
          </div>
        </div>
        <q-separator class="separator-light" />
        
        <q-table
          flat
          :rows="recentPoolsComputed"
          :columns="detailedTableColumns"
          row-key="id"
          hide-pagination
          no-data-label="Aucun projet ou utilisateur actif."
          class="tabler-table font-inter text-dark"
        >
          <template v-slot:header="props">
            <q-tr :props="props" class="tabler-table-header-row">
              <q-th v-for="col in props.cols" :key="col.name" :props="props" class="text-weight-bold text-grey-6 text-uppercase">
                {{ col.label }}
              </q-th>
              <q-th class="text-center">Action</q-th>
            </q-tr>
          </template>

          <template v-slot:body="props">
            <q-tr :props="props">
              <!-- Column 1: UTILISATEUR -->
              <q-td>
                <div class="row items-center q-gutter-x-sm">
                  <q-avatar size="32px" class="bg-grey-2 border-gray">
                    <q-img v-if="props.row.imageUrl" :src="props.row.imageUrl" />
                    <q-icon v-else name="person" color="grey-5" />
                  </q-avatar>
                  <div>
                    <div class="text-weight-bold text-dark">{{ props.row.ownerName }}</div>
                    <div class="text-caption text-grey-5" style="font-size: 0.75rem;">
                      Inscrit le : {{ props.row.registrationDate }}
                    </div>
                  </div>
                </div>
              </q-td>

              <!-- Column 2: USAGE (Progression) -->
              <q-td>
                <div class="q-gutter-y-xs" style="min-width: 140px;">
                  <div class="row justify-between text-caption font-inter text-dark">
                    <span class="text-weight-bold">{{ props.row.progressPercent }}%</span>
                    <span class="text-grey-5">{{ props.row.currentAmount }} / {{ props.row.goalAmount }} €</span>
                  </div>
                  <q-linear-progress 
                    :value="(props.row.currentAmount || 0) / props.row.goalAmount" 
                    color="primary" 
                    track-color="grey-2"
                    size="4px"
                    style="border-radius: 2px;"
                  />
                </div>
              </q-td>

              <!-- Column 3: PAIEMENT -->
              <q-td class="text-center">
                <q-chip dense color="grey-2" text-color="grey-8" class="text-weight-bold text-caption font-inter">
                  {{ props.row.paymentType }}
                </q-chip>
              </q-td>

              <!-- Column 4: ACTIVITÉ -->
              <q-td class="text-grey-6 text-caption">
                {{ props.row.lastUpdate }}
              </q-td>

              <!-- Column 5: SATISFACTION (Circular completion rate) -->
              <q-td class="text-center">
                <q-circular-progress
                  show-value
                  font-size="9px"
                  :value="props.row.progressPercent"
                  size="28px"
                  :thickness="0.25"
                  color="green"
                  track-color="grey-2"
                  class="text-weight-bold text-green"
                >
                  {{ props.row.progressPercent }}%
                </q-circular-progress>
              </q-td>

              <!-- Column 6: ACTIONS DROPDOWN -->
              <q-td class="text-center">
                <q-btn flat round dense color="grey-6" icon="more_vert" size="sm">
                  <q-menu auto-close>
                    <q-list style="min-width: 120px;">
                      <q-item clickable v-ripple @click="$router.push(`/pools/${props.row.id}`)">
                        <q-item-section>Voir le projet</q-item-section>
                      </q-item>
                      <q-item clickable v-ripple @click="deletePool(props.row.id)" class="text-negative">
                        <q-item-section>Supprimer</q-item-section>
                      </q-item>
                    </q-list>
                  </q-menu>
                </q-btn>
              </q-td>
            </q-tr>
          </template>
        </q-table>
      </q-card>

      <!-- 6. Detailed Withdrawal requests table -->
      <div v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPaymentAdmin.value" class="row q-col-gutter-lg">
        <div class="col-12">
          <q-card class="tabler-card no-shadow">
            <div class="card-header q-py-md q-px-lg row items-center justify-between">
              <div class="text-subtitle1 text-weight-bold text-dark font-inter">
                Demandes de retrait en attente
              </div>
              <q-chip color="orange-1" text-color="orange-8" size="sm" class="text-weight-bold rounded-chip">
                Rôle Paiements
              </q-chip>
            </div>
            <q-separator class="separator-light" />

            <q-table
              flat
              :rows="pendingWithdrawals"
              :columns="withdrawalColumns"
              row-key="id"
              :loading="loadingWithdrawals"
              no-data-label="Aucune demande de retrait en attente."
              class="tabler-table font-inter text-dark"
            >
              <template v-slot:header="props">
                <q-tr :props="props" class="tabler-table-header-row">
                  <q-th v-for="col in props.cols" :key="col.name" :props="props" class="text-weight-bold text-grey-6 text-uppercase">
                    {{ col.label }}
                  </q-th>
                </q-tr>
              </template>

              <template v-slot:body-cell-amount="props">
                <q-td :props="props" class="text-weight-bold text-dark text-subtitle2">
                  {{ props.row.amount }} €
                </q-td>
              </template>

              <template v-slot:body-cell-fees="props">
                <q-td :props="props" class="text-weight-bold text-negative text-subtitle2">
                  - {{ props.row.fees }} €
                </q-td>
              </template>

              <template v-slot:body-cell-net="props">
                <q-td :props="props" class="text-weight-bolder text-success text-subtitle2">
                  {{ (props.row.amount - props.row.fees).toFixed(2) }} €
                </q-td>
              </template>

              <template v-slot:body-cell-actions="props">
                <q-td :props="props" class="text-center">
                  <q-btn
                    label="Confirmer le Retrait"
                    color="primary"
                    flat
                    dense
                    size="sm"
                    no-caps
                    icon="check"
                    class="text-weight-bolder no-shadow"
                    :loading="confirmingWithdrawalId === props.row.id"
                    @click="confirmWithdrawalRequest(props.row.id)"
                  />
                </q-td>
              </template>
            </q-table>
          </q-card>
        </div>
      </div>

      <!-- 7. Audit logs table -->
      <div v-if="(authStore.isSuperAdmin.value || authStore.isAdmin.value) && auditLogs.length > 0" class="row q-col-gutter-lg">
        <div class="col-12">
          <q-card class="tabler-card no-shadow">
            <div class="card-header q-py-md q-px-lg row items-center justify-between">
              <div class="text-subtitle1 text-weight-bold text-dark font-inter">
                Journal d'Audit - Actions Critiques de la Plateforme
              </div>
              <q-chip color="purple-1" text-color="purple-8" size="sm" class="text-weight-bold rounded-chip">
                Rôle Super Admin
              </q-chip>
            </div>
            <q-separator class="separator-light" />

            <q-table
              flat
              :rows="auditLogs"
              :columns="auditColumns"
              row-key="id"
              :rows-per-page-options="[5, 10, 20]"
              class="tabler-table font-inter text-dark"
            >
              <template v-slot:header="props">
                <q-tr :props="props" class="tabler-table-header-row">
                  <q-th v-for="col in props.cols" :key="col.name" :props="props" class="text-weight-bold text-grey-6 text-uppercase">
                    {{ col.label }}
                  </q-th>
                </q-tr>
              </template>

              <template v-slot:body-cell-status="props">
                <q-td :props="props" class="text-center">
                  <q-chip 
                    :color="props.row.status === 'SUCCESS' ? 'green-1' : 'red-1'" 
                    :text-color="props.row.status === 'SUCCESS' ? 'green-7' : 'red-7'" 
                    size="sm" 
                    class="text-weight-bolder text-uppercase rounded-chip"
                  >
                    {{ props.row.status }}
                  </q-chip>
                </q-td>
              </template>
              
              <template v-slot:body-cell-role="props">
                <q-td :props="props" class="text-center">
                  <q-chip dense outline color="primary" class="text-weight-bolder" style="font-size: 0.75rem;">
                    {{ props.row.role }}
                  </q-chip>
                </q-td>
              </template>
            </q-table>
          </q-card>
        </div>
      </div>

      <!-- 8. Bottom Widgets Row -->
      <div class="row q-col-gutter-lg">
        
        <!-- Left: IP Stats -->
        <div class="col-12 col-md-4">
          <q-card class="tabler-card no-shadow">
            <q-card-section class="card-header q-py-md q-px-lg">
              <div class="text-subtitle2 text-weight-bold text-dark font-inter">
                Adresses IP Actives (Audit)
              </div>
            </q-card-section>
            <q-separator class="separator-light" />
            <q-list v-if="ipDistribution.length > 0" class="q-pa-sm">
              <q-item v-for="ipObj in ipDistribution" :key="ipObj.ip">
                <q-item-section>
                  <q-item-label class="text-weight-bold text-dark">{{ ipObj.ip }}</q-item-label>
                  <q-item-label caption>Audit trace</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-chip size="sm" color="grey-2" text-color="grey-8">{{ ipObj.count }} actions</q-chip>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-else class="text-center q-py-lg text-grey-5 text-italic">
              Aucune adresse IP enregistrée.
            </div>
          </q-card>
        </div>

        <!-- Middle: Progression goals -->
        <div class="col-12 col-md-4">
          <q-card class="tabler-card no-shadow">
            <q-card-section class="card-header q-py-md q-px-lg">
              <div class="text-subtitle2 text-weight-bold text-dark font-inter">
                Progression des Cagnottes
              </div>
            </q-card-section>
            <q-separator class="separator-light" />
            <q-card-section v-if="recentPoolsComputed.length > 0" class="q-pa-md q-gutter-y-md">
              <div v-for="p in recentPoolsComputed.slice(0, 3)" :key="p.id">
                <div class="row justify-between text-caption font-inter text-dark q-mb-xs">
                  <span class="text-weight-bold text-truncate" style="max-width: 180px;">{{ p.title }}</span>
                  <span>{{ p.progressPercent }}%</span>
                </div>
                <q-linear-progress 
                  :value="(p.currentAmount || 0) / p.goalAmount" 
                  color="primary" 
                  track-color="grey-2"
                  size="6px"
                  style="border-radius: 4px;"
                />
              </div>
            </q-card-section>
            <div v-else class="text-center q-py-lg text-grey-5 text-italic">
              Aucune progression disponible.
            </div>
          </q-card>
        </div>

        <!-- Right: Administrators -->
        <div class="col-12 col-md-4">
          <q-card class="tabler-card no-shadow">
            <q-card-section class="card-header q-py-md q-px-lg">
              <div class="text-subtitle2 text-weight-bold text-dark font-inter">
                Membres Administrateurs
              </div>
            </q-card-section>
            <q-separator class="separator-light" />
            <q-list v-if="adminsList.length > 0" class="q-pa-sm">
              <q-item v-for="admin in adminsList" :key="admin.email">
                <q-item-section avatar>
                  <q-avatar size="28px" color="primary" text-color="white">
                    {{ admin.email[0].toUpperCase() }}
                  </q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold text-dark font-inter">{{ admin.email }}</q-item-label>
                  <q-item-label caption>{{ admin.role }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-else class="text-center q-py-lg text-grey-5 text-italic">
              Aucun administrateur trouvé.
            </div>
          </q-card>
        </div>

      </div>

    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import { getUsers } from 'src/shared/services/api'
import poolService from 'src/shared/services/poolService'
import authStore from 'src/shared/stores/auth'
import VueApexCharts from 'vue3-apexcharts'

const $q = useQuasar()
const loading = ref(true)

const userRoleName = computed(() => {
  if (authStore.isSuperAdmin.value) return 'Super Admin'
  if (authStore.isAdmin.value) return 'Admin Général'
  if (authStore.isPoolAdmin.value) return 'Admin Cagnottes'
  if (authStore.isPaymentAdmin.value) return 'Admin Paiements'
  if (authStore.isModerator.value) return 'Modérateur'
  return 'Utilisateur Connecté'
})

const stats = reactive({
  totalUsers: 0,
  totalPools: 0,
  activePools: 0,
  completedPools: 0,
  totalFunds: '0',
  avgGoal: 0,
  successRate: 0
})

const recentPools = ref([])
const pendingWithdrawals = ref([])
const loadingWithdrawals = ref(false)
const confirmingWithdrawalId = ref(null)

const usersListRef = ref([])
const paymentMethodsMap = ref({})

const withdrawalColumns = [
  { name: 'walletId', label: 'ID Cagnotte', field: 'walletId', align: 'left', sortable: true },
  { name: 'accountHolderName', label: 'Titulaire', field: 'accountHolderName', align: 'left' },
  { name: 'iban', label: 'IBAN', field: 'iban', align: 'left' },
  { name: 'bankName', label: 'Banque', field: 'bankName', align: 'left' },
  { name: 'amount', label: 'Montant demandé', field: 'amount', align: 'right' },
  { name: 'fees', label: 'Frais', field: 'fees', align: 'right' },
  { name: 'net', label: 'Net à transférer', align: 'right' },
  { name: 'actions', label: 'Actions', align: 'center' }
]

// Real session audit logs (starts empty, grows with user actions)
const auditLogs = ref([])

const auditColumns = [
  { name: 'timestamp', label: 'Date/Heure', field: 'timestamp', align: 'left', sortable: true },
  { name: 'actor', label: 'Acteur', field: 'actor', align: 'left', sortable: true },
  { name: 'role', label: 'Rôle', field: 'role', align: 'center', sortable: true },
  { name: 'action', label: 'Action effectuée', field: 'action', align: 'left' },
  { name: 'status', label: 'Statut', field: 'status', align: 'center', sortable: true },
  { name: 'ip', label: 'Adresse IP', field: 'ip', align: 'center' }
]

const detailedTableColumns = [
  { name: 'owner', label: 'Utilisateur', align: 'left' },
  { name: 'usage', label: 'Progression', align: 'left' },
  { name: 'payment', label: 'Paiement', align: 'center' },
  { name: 'activity', label: 'Activité', align: 'left' },
  { name: 'satisfaction', label: 'Satisfaction', align: 'center' }
]

const formatElapsedTime = (dateString) => {
  if (!dateString) return 'Pas d\'activité'
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  if (diffMins < 1) return 'À l\'instant'
  if (diffMins < 60) return `Il y a ${diffMins} min`
  const diffHours = Math.floor(diffMins / 60)
  if (diffHours < 24) return `Il y a ${diffHours} h`
  const diffDays = Math.floor(diffHours / 24)
  return `Il y a ${diffDays} j`
}

// Real database data for tables & lists (empty array fallback handles lack of entries)
const recentPoolsComputed = computed(() => {
  if (!recentPools.value || recentPools.value.length === 0) {
    return []
  }
  return recentPools.value.map((pool) => {
    const progressPercent = pool.goalAmount ? Math.min(Math.round(((pool.currentAmount || 0) / pool.goalAmount) * 100), 100) : 0
    
    // Lookup matching user registration date and name
    const creator = usersListRef.value.find(u => u.id === pool.ownerId)
    const registrationDate = creator && creator.createdAt 
      ? new Date(creator.createdAt).toLocaleDateString('fr-FR')
      : new Date(pool.createdAt).toLocaleDateString('fr-FR')

    const paymentType = paymentMethodsMap.value[pool.id] || 'Stripe'
    const lastUpdate = `Dernière modification ${formatElapsedTime(pool.updatedAt || pool.createdAt)}`

    return {
      ...pool,
      ownerName: creator?.fullName || pool.ownerName || 'Anonyme',
      registrationDate,
      paymentType,
      lastUpdate,
      progressPercent
    }
  })
})

const recentActivities = computed(() => {
  if (!recentPools.value || recentPools.value.length === 0) {
    return []
  }
  return recentPools.value.map(pool => {
    const creator = usersListRef.value.find(u => u.id === pool.ownerId)
    return {
      id: pool.id,
      avatar: pool.imageUrl || '',
      userName: creator?.fullName || pool.ownerName || 'Anonyme',
      description: `Création de la cagnotte "${pool.title}"`,
      date: new Date(pool.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
    }
  })
})

const featuredPools = computed(() => {
  if (!recentPools.value || recentPools.value.length === 0) {
    return []
  }
  return recentPools.value.slice(0, 2).map((pool, idx) => {
    const images = [
      'https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=300&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=300&auto=format&fit=crop&q=80'
    ]
    const creator = usersListRef.value.find(u => u.id === pool.ownerId)
    const name = creator?.fullName || pool.ownerName || 'Anonyme'
    const initials = name.split(' ').map(n => n[0]).join('').toUpperCase()
    return {
      id: pool.id,
      title: pool.title,
      description: pool.description || 'Aucune description disponible pour ce projet.',
      imageUrl: pool.imageUrl || images[idx % 2],
      ownerName: name,
      initials
    }
  })
})

// IP Active distribution computed for bottom widgets
const ipDistribution = computed(() => {
  const counts = {}
  auditLogs.value.forEach(l => {
    counts[l.ip] = (counts[l.ip] || 0) + 1
  })
  return Object.keys(counts).map(ip => ({ ip, count: counts[ip] })).slice(0, 3)
})

// Real administrators list fetched from server
const adminsList = ref([])

// Delete/moderation functionality for cagnottes
const deletePool = async (poolId) => {
  $q.dialog({
    title: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer cette cagnotte ? Cette action est irréversible.',
    cancel: { flat: true, color: 'grey-6', label: 'Annuler' },
    ok: { flat: true, color: 'negative', label: 'Supprimer' },
    persistent: true
  }).onOk(async () => {
    try {
      await poolService.deletePool(poolId)
      $q.notify({
        type: 'positive',
        message: 'La cagnotte a été supprimée avec succès.'
      })
      
      // Enregistrer l'action réelle dans le journal d'audit de la session
      auditLogs.value.unshift({
        id: Date.now(),
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        actor: authStore.user.value?.email || 'Admin',
        role: userRoleName.value.toUpperCase().replace(' ', '_'),
        action: `Suppression de la cagnotte #${poolId}`,
        status: 'SUCCESS',
        ip: '127.0.0.1'
      })
      await loadStats()
    } catch (error) {
      console.error('Erreur suppression cagnotte', error)
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || 'Erreur lors de la suppression de la cagnotte.'
      })
    }
  })
}

// Dynamically calculated real line chart progression data based on DB pools
const chartData = computed(() => {
  const sorted = [...recentPools.value].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  let sum = 0
  const data = []
  const categories = []
  sorted.forEach(p => {
    sum += p.currentAmount || 0
    data.push(sum)
    categories.push(new Date(p.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' }))
  })
  if (data.length === 0) {
    return {
      series: [{ name: 'Fonds collectés (€)', data: [0] }],
      categories: ['Aucune donnée']
    }
  }
  return {
    series: [{ name: 'Fonds collectés (€)', data }],
    categories
  }
})

const chartSeries = computed(() => chartData.value.series)

const chartOptions = computed(() => ({
  chart: {
    type: 'area',
    height: 240,
    sparkline: { enabled: false },
    toolbar: { show: false },
    fontFamily: 'Inter, sans-serif'
  },
  colors: ['#467fcf'], // Tabler classic Blue
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.35,
      opacityTo: 0.02,
      stops: [0, 100]
    }
  },
  stroke: {
    curve: 'smooth',
    width: 2
  },
  xaxis: {
    categories: chartData.value.categories,
    labels: { style: { colors: '#9aa0ac', fontSize: '11px' } },
    axisBorder: { show: false },
    axisTicks: { show: false }
  },
  yaxis: {
    labels: { style: { colors: '#9aa0ac', fontSize: '11px' } }
  },
  grid: {
    borderColor: '#f1f3f9',
    strokeDashArray: 4
  },
  dataLabels: { enabled: false }
}))

// Tabler Donut Chart Category Configuration
const categoryChartData = computed(() => {
  const counts = {}
  recentPools.value.forEach(p => {
    const cat = p.category || 'Autre'
    counts[cat] = (counts[cat] || 0) + 1
  })
  return counts
})

const categoryChartSeries = computed(() => Object.values(categoryChartData.value))

const categoryChartOptions = computed(() => ({
  chart: {
    type: 'donut',
    fontFamily: 'Inter, sans-serif'
  },
  labels: Object.keys(categoryChartData.value),
  colors: ['#5eba00', '#467fcf', '#fa5c7c', '#ffc107', '#39cbd0'], // Tabler palette
  legend: {
    show: true,
    position: 'bottom',
    horizontalAlign: 'center',
    fontSize: '11px',
    markers: {
      radius: 12
    }
  },
  plotOptions: {
    pie: {
      donut: {
        size: '70%',
        labels: {
          show: false
        }
      }
    }
  },
  dataLabels: { enabled: false }
}))


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
    
    // Enregistrer l'action dans le journal d'audit de la session
    auditLogs.value.unshift({
      id: Date.now(),
      timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
      actor: authStore.user.value?.email || 'Admin',
      role: userRoleName.value.toUpperCase().replace(' ', '_'),
      action: `Confirmation de retrait de la transaction #${transactionId}`,
      status: 'SUCCESS',
      ip: '127.0.0.1'
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
    let usersList = []
    if (authStore.isSuperAdmin.value || authStore.isAdmin.value) {
      try {
        const usersResponse = await getUsers()
        usersList = usersResponse.data
      } catch (err) {
        console.warn('Accès refusé pour la liste des utilisateurs', err)
      }
    }
    usersListRef.value = usersList

    const poolsResponse = await poolService.getAllPools()
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

    // Fetch contributions payment methods for recent pools in parallel
    const map = {}
    await Promise.all(recentPools.value.map(async (pool) => {
      try {
        const contribRes = await poolService.getPoolContributions(pool.id)
        const contribs = contribRes.data || []
        const stripeOrPaypal = contribs.find(c => c.paymentMethod)?.paymentMethod
        map[pool.id] = stripeOrPaypal 
          ? (stripeOrPaypal.toUpperCase().includes('STRIPE') ? 'Stripe' : 'PayPal')
          : 'Stripe'
      } catch {
        map[pool.id] = 'Stripe'
      }
    }))
    paymentMethodsMap.value = map

    // Populate actual administrators list from real user data
    adminsList.value = usersList
      .filter(u => u.role && u.role !== 'USER' && u.role !== 'MEMBER')
      .map(u => ({
        email: u.email,
        role: u.role === 'SUPER_ADMIN' ? 'Super Administrateur' : u.role === 'ADMIN' ? 'Administrateur Général' : u.role
      }))

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
  if (authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPaymentAdmin.value) {
    loadWithdrawals()
  }
})
</script>

<style scoped>
.admin-dashboard-page {
  background: #f4f6fa;
  min-height: 100vh;
}
body.body--dark .admin-dashboard-page {
  background: #0d1424;
}

.font-inter {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dashboard-title {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #354052;
  font-size: 1.75rem;
  font-weight: 400;
  letter-spacing: -0.5px;
}
body.body--dark .dashboard-title {
  color: #f1f3f9;
}

/* Tabler KPI Cards */
.tabler-kpi-card {
  background: #ffffff;
  border: 1px solid rgba(101, 109, 119, 0.16);
  border-radius: 3px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  transition: border-color 0.2s ease;
}
body.body--dark .tabler-kpi-card {
  background: #182235;
  border-color: rgba(255, 255, 255, 0.08);
}

.kpi-value {
  font-family: 'Inter', sans-serif;
  font-size: 1.75rem;
  font-weight: 500;
  color: #2c3e50;
  line-height: 1.2;
}
body.body--dark .kpi-value {
  color: #ffffff;
}

.trend-indicator {
  font-size: 0.8rem;
  display: flex;
  align-items: center;
}
.text-success {
  color: #2fb344 !important;
}
.text-danger {
  color: #d63939 !important;
}

/* Tabler Card Containers */
.tabler-card {
  background: #ffffff;
  border: 1px solid rgba(101, 109, 119, 0.16);
  border-radius: 3px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}
body.body--dark .tabler-card {
  background: #182235;
  border-color: rgba(255, 255, 255, 0.08);
}

.card-header {
  min-height: 48px;
}

.separator-light {
  background: rgba(101, 109, 119, 0.12);
}
body.body--dark .separator-light {
  background: rgba(255, 255, 255, 0.08);
}

/* Alert Banner */
.tabler-alert-banner {
  background-color: #f1f3f9;
  border: 1px solid rgba(101, 109, 119, 0.16);
  border-radius: 3px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}
body.body--dark .tabler-alert-banner {
  background-color: rgba(32, 107, 196, 0.05);
  border-color: rgba(255, 255, 255, 0.08);
}

.text-blue-9 {
  color: #495057;
}
body.body--dark .text-blue-9 {
  color: #a3d1ff;
}

.border-gray {
  border: 1px solid rgba(101, 109, 119, 0.16);
}

.rounded-chip {
  border-radius: 4px;
}

/* Mini metric icon blocks */
.icon-block {
  width: 42px;
  height: 42px;
  border-radius: 3px;
}

/* Custom list layout */
.tabler-table-header {
  background: #fcfcfc;
  border-bottom: 1px solid rgba(101, 109, 119, 0.16);
}
body.body--dark .tabler-table-header {
  background: #141c2c;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.list-row-hover {
  transition: background-color 0.15s ease;
}
.list-row-hover:hover {
  background-color: #f8f9fa;
}
body.body--dark .list-row-hover:hover {
  background-color: #1e293b;
}

.border-bottom-light {
  border-bottom: 1px solid rgba(101, 109, 119, 0.08);
}
body.body--dark .border-bottom-light {
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Table styles */
.tabler-table {
  background: transparent;
}
.tabler-table-header-row th {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(101, 109, 119, 0.16);
  background: #fcfcfc;
}
body.body--dark .tabler-table-header-row th {
  background: #141c2c;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.role-badge {
  background: #ffffff;
  border: 1px solid rgba(101, 109, 119, 0.16);
  border-radius: 4px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  padding: 6px 12px;
}
body.body--dark .role-badge {
  background: #182235;
  border-color: rgba(255, 255, 255, 0.12);
}
body.body--dark .role-badge .text-dark {
  color: #f1f3f9 !important;
}

.action-btn {
  border: 1px solid rgba(101, 109, 119, 0.16);
  border-radius: 4px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}
body.body--dark .action-btn {
  background: #182235 !important;
  border-color: rgba(255, 255, 255, 0.12);
}
</style>
