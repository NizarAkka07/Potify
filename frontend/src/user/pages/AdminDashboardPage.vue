<template>
  <q-page class="admin-dashboard-page q-pa-xl font-inter">
    
    <!-- Tabler Header Title -->
    <div class="row items-center justify-between q-mb-xl">
      <div>
        <div class="text-overline text-grey-5 font-inter text-weight-bold" style="letter-spacing: 0.8px; line-height: 1.2;">
          Aperçu analytique
        </div>
        <h1 class="dashboard-title q-my-none text-weight-bold font-inter" style="font-size: 1.8rem; letter-spacing: -0.5px;">
          Tableau de bord
        </h1>
      </div>
      <div class="row items-center q-gutter-md">
        <q-btn
          flat
          no-caps
          icon="open_in_new"
          label="Visiter le site"
          to="/"
          class="visit-site-btn"
        />
        <div class="role-badge-container">
          <div class="role-badge-icon">
            <q-icon name="admin_panel_settings" size="18px" />
          </div>
          <div class="column">
            <span class="role-badge-title">RÔLE ACTUEL</span>
            <span class="role-badge-name">{{ userRoleName }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="flex flex-center q-py-xl" style="min-height: 60vh;">
      <q-spinner-dots size="50px" color="primary" />
    </div>

    <div v-else class="q-gutter-y-lg">
      
      <!-- 1. Five KPI Cards Grid -->
      <div class="kpi-grid q-mb-xl">
        <!-- Metric 1: Nouveaux utilisateurs -->
        <q-card class="kpi-card no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row justify-between items-start">
              <div>
                <div class="kpi-label font-inter">Inscrits</div>
                <div class="kpi-value q-mt-sm">{{ stats.totalUsers }}</div>
              </div>
              <div class="kpi-icon-wrapper bg-green-glow">
                <q-icon name="group" size="24px" color="positive" />
              </div>
            </div>
            <div class="kpi-footer q-mt-md">
              <q-icon name="trending_up" size="16px" color="positive" class="q-mr-xs" />
              <span class="text-weight-medium text-positive">Actif</span>
            </div>
          </q-card-section>
        </q-card>

        <!-- Metric 2: Cagnottes créées -->
        <q-card class="kpi-card no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row justify-between items-start">
              <div>
                <div class="kpi-label font-inter">Cagnottes</div>
                <div class="kpi-value q-mt-sm">{{ stats.totalPools }}</div>
              </div>
              <div class="kpi-icon-wrapper bg-primary-glow">
                <q-icon name="folder" size="24px" color="primary" />
              </div>
            </div>
            <div class="kpi-footer q-mt-md">
              <span class="text-weight-medium text-grey-6">Total créées</span>
            </div>
          </q-card-section>
        </q-card>

        <!-- Metric 3: Cagnottes Terminées -->
        <q-card class="kpi-card no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row justify-between items-start">
              <div>
                <div class="kpi-label font-inter">Terminées</div>
                <div class="kpi-value q-mt-sm">{{ stats.completedPools }}</div>
              </div>
              <div class="kpi-icon-wrapper bg-success-glow">
                <q-icon name="check_circle" size="24px" color="positive" />
              </div>
            </div>
            <div class="kpi-footer q-mt-md">
              <q-icon name="done_all" size="16px" color="positive" class="q-mr-xs" />
              <span class="text-weight-medium text-positive">Finies</span>
            </div>
          </q-card-section>
        </q-card>

        <!-- Metric 4: Fonds collectés -->
        <q-card class="kpi-card no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row justify-between items-start">
              <div>
                <div class="kpi-label font-inter">Collectés</div>
                <div class="kpi-value q-mt-sm">{{ stats.totalFunds }} €</div>
              </div>
              <div class="kpi-icon-wrapper bg-info-glow">
                <q-icon name="payments" size="24px" color="info" />
              </div>
            </div>
            <div class="kpi-footer q-mt-md">
              <q-icon name="arrow_upward" size="16px" color="info" class="q-mr-xs" />
              <span class="text-weight-medium text-info">Cumulé</span>
            </div>
          </q-card-section>
        </q-card>

        <!-- Metric 5: Taux de réussite -->
        <q-card class="kpi-card no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row justify-between items-start">
              <div>
                <div class="kpi-label font-inter">Réussite</div>
                <div class="kpi-value q-mt-sm">{{ stats.successRate }}%</div>
              </div>
              <div class="kpi-icon-wrapper bg-warning-glow">
                <q-icon name="insights" size="24px" color="warning" />
              </div>
            </div>
            <div class="kpi-footer q-mt-md">
              <q-icon name="star" size="16px" color="warning" class="q-mr-xs" />
              <span class="text-weight-medium text-warning">Ratio</span>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- 2. Chart and Side Column Grid -->
      <div class="row q-col-gutter-xl q-mb-xl">
        
        <!-- LEFT PANEL: Development line chart + table below it -->
        <div class="col-12 col-md-8">
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium">
              <div class="card-header-title">
                Activité de développement (Fonds cumulés)
              </div>
            </div>
            
            <q-card-section class="q-pa-lg">
              <VueApexCharts
                type="area"
                height="240"
                :options="chartOptions"
                :series="chartSeries"
              />
            </q-card-section>

            <!-- Table of recent activities -->
            <div class="recent-act-header row items-center q-px-lg q-py-sm">
              <div class="col-4">Utilisateur</div>
              <div class="col-5">Action</div>
              <div class="col-2 text-right">Date</div>
              <div class="col-1 text-center"></div>
            </div>
            
            <div v-for="act in recentActivities" :key="act.id" class="row items-center q-px-lg q-py-md recent-act-row">
              <div class="col-4 row items-center q-gutter-x-sm">
                <q-avatar size="32px" class="avatar-border">
                  <q-img v-if="act.avatar" :src="act.avatar" />
                  <q-icon v-else name="person" color="grey-5" />
                </q-avatar>
                <span class="recent-act-username">{{ act.userName }}</span>
              </div>
              <div class="col-5 recent-act-desc text-truncate">
                {{ act.description }}
              </div>
              <div class="col-2 text-right recent-act-date">
                {{ act.date }}
              </div>
              <div class="col-1 text-center">
                <q-btn flat round class="delete-act-btn" icon="delete" size="sm" @click="deletePool(act.id)" />
              </div>
            </div>

            <div v-if="recentActivities.length === 0" class="text-center q-py-xl text-grey-5 text-italic">
              Aucune activité récente enregistrée.
            </div>
          </q-card>
        </div>

        <!-- RIGHT PANEL: Alert + Charts + Three vertical comment indicators -->
        <div class="col-12 col-md-4">
          <div class="column q-gutter-y-lg">
            <!-- Info Banner -->
            <div class="premium-alert-banner q-pa-lg row no-wrap items-center">
              <q-icon name="help_outline" size="28px" class="q-mr-md banner-icon" />
              <div>
                <div class="banner-title text-weight-bold">Besoin d'aide ?</div>
                <div class="banner-text">
                  Consultez notre <a href="#" class="banner-link">documentation</a> contenant des exemples de code et guides d'intégration.
                </div>
              </div>
            </div>

            <!-- Single Donut Chart Card -->
            <q-card class="premium-card no-shadow">
              <div class="card-header-premium">
                <div class="card-header-title">
                  Répartition des Cagnottes par Catégorie
                </div>
              </div>
              <q-card-section class="q-pa-lg">
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
            <div class="column q-gutter-y-md">
              <!-- Widget 1 -->
              <q-card class="widget-progress-card no-shadow q-pa-lg">
                <div class="row justify-between items-center q-mb-xs">
                  <span class="widget-label">Nouveaux retraits</span>
                  <span class="widget-value text-primary">{{ pendingWithdrawals.length }}</span>
                </div>
                <q-linear-progress 
                  :value="0.45" 
                  color="primary" 
                  track-color="transparent" 
                  size="6px" 
                  class="widget-progress-bar"
                />
              </q-card>

              <!-- Widget 2 -->
              <q-card class="widget-progress-card no-shadow q-pa-lg">
                <div class="row justify-between items-center q-mb-xs">
                  <span class="widget-label">Objectif moyen</span>
                  <span class="widget-value text-warning">{{ stats.avgGoal }} €</span>
                </div>
                <q-linear-progress 
                  :value="0.68" 
                  color="warning" 
                  track-color="transparent" 
                  size="6px" 
                  class="widget-progress-bar"
                />
              </q-card>

              <!-- Widget 3 -->
              <q-card class="widget-progress-card no-shadow q-pa-lg">
                <div class="row justify-between items-center q-mb-xs">
                  <span class="widget-label">Membres inscrits</span>
                  <span class="widget-value text-info">{{ stats.totalUsers }}</span>
                </div>
                <q-linear-progress 
                  :value="0.35" 
                  color="info" 
                  track-color="transparent" 
                  size="6px" 
                  class="widget-progress-bar"
                />
              </q-card>
            </div>
          </div>
        </div>

      </div>

      <!-- 3. Four Mini Icon Cards Row -->
      <div class="row q-col-gutter-lg q-my-md q-mb-xl">
        <!-- Card 1: Fonds collectés -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="mini-metric-card no-shadow flex items-center q-pa-md">
            <div class="mini-icon-block bg-info-glow">
              <q-icon name="euro" size="20px" color="info" />
            </div>
            <div>
              <div class="mini-value font-inter">{{ stats.totalFunds }} €</div>
              <div class="mini-label">Fonds collectés</div>
            </div>
          </q-card>
        </div>

        <!-- Card 2: Actives -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="mini-metric-card no-shadow flex items-center q-pa-md">
            <div class="mini-icon-block bg-primary-glow">
              <q-icon name="check_circle" size="20px" color="primary" />
            </div>
            <div>
              <div class="mini-value font-inter">{{ stats.activePools }} cagnottes</div>
              <div class="mini-label">Actives en ce moment</div>
            </div>
          </q-card>
        </div>

        <!-- Card 3: Membres -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="mini-metric-card no-shadow flex items-center q-pa-md">
            <div class="mini-icon-block bg-success-glow">
              <q-icon name="people" size="20px" color="positive" />
            </div>
            <div>
              <div class="mini-value font-inter">{{ stats.totalUsers }} membres</div>
              <div class="mini-label">Inscrits sur Potify</div>
            </div>
          </q-card>
        </div>

        <!-- Card 4: Journal d'audit -->
        <div class="col-12 col-sm-6 col-md-3">
          <q-card class="mini-metric-card no-shadow flex items-center q-pa-md">
            <div class="mini-icon-block bg-warning-glow">
              <q-icon name="history" size="20px" color="warning" />
            </div>
            <div>
              <div class="mini-value font-inter">{{ auditLogs.length }} actions</div>
              <div class="mini-label">Journal d'audit actif</div>
            </div>
          </q-card>
        </div>
      </div>

      <!-- 4. Featured Project Cards -->
      <div v-if="featuredPools.length > 0" class="row q-col-gutter-lg q-my-md q-mb-xl">
        <div v-for="pool in featuredPools" :key="pool.id" class="col-12 col-md-6">
          <q-card class="project-showcase-card no-shadow overflow-hidden flex no-wrap items-stretch">
            <div class="project-image-wrapper">
              <q-img :src="pool.imageUrl" class="project-image" />
            </div>
            <q-card-section class="q-pa-lg flex-1 column justify-between">
              <div>
                <div class="project-title font-inter">{{ pool.title }}</div>
                <div class="project-desc">
                  {{ pool.description }}
                </div>
              </div>
              <div class="row justify-between items-center q-mt-md">
                <div class="row items-center q-gutter-x-sm">
                  <q-avatar size="28px" class="bg-primary text-white text-weight-bold text-caption">{{ pool.initials }}</q-avatar>
                  <span class="project-owner">{{ pool.ownerName }}</span>
                </div>
                <q-btn flat round dense color="negative" icon="favorite" size="sm" class="project-fav-btn" />
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 5. Large Detailed Engagement & User Table -->
      <q-card class="premium-card no-shadow q-mb-xl">
        <div class="card-header-premium row items-center justify-between">
          <div class="card-header-title">
            Membres actifs & Engagement des projets
          </div>
        </div>
        
        <q-table
          flat
          :rows="recentPoolsComputed"
          :columns="detailedTableColumns"
          row-key="id"
          hide-pagination
          no-data-label="Aucun projet ou utilisateur actif."
          class="font-inter"
        >
          <template v-slot:header="props">
            <q-tr :props="props">
              <q-th v-for="col in props.cols" :key="col.name" :props="props">
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
                  <q-avatar size="32px" class="avatar-border">
                    <q-img v-if="props.row.imageUrl" :src="props.row.imageUrl" />
                    <q-icon v-else name="person" color="grey-5" />
                  </q-avatar>
                  <div>
                    <div class="text-weight-bold" style="color: var(--text-main)">{{ props.row.ownerName }}</div>
                    <div class="text-caption" style="color: var(--text-muted); font-size: 0.75rem;">
                      Inscrit le : {{ props.row.registrationDate }}
                    </div>
                  </div>
                </div>
              </q-td>

              <!-- Column 2: USAGE (Progression) -->
              <q-td>
                <div class="q-gutter-y-xs" style="min-width: 140px;">
                  <div class="row justify-between text-caption font-inter" style="color: var(--text-main)">
                    <span class="text-weight-bold">{{ props.row.progressPercent }}%</span>
                    <span style="color: var(--text-muted)">{{ props.row.currentAmount }} / {{ props.row.goalAmount }} €</span>
                  </div>
                  <q-linear-progress 
                    :value="(props.row.currentAmount || 0) / props.row.goalAmount" 
                    color="primary" 
                    track-color="transparent"
                    size="6px"
                    style="border-radius: 3px; background: var(--border-light) !important;"
                  />
                </div>
              </q-td>

              <!-- Column 3: PAIEMENT -->
              <q-td class="text-center">
                <q-chip dense color="transparent" style="border: 1px solid var(--card-border); color: var(--text-main)" class="text-weight-bold text-caption font-inter">
                  {{ props.row.paymentType }}
                </q-chip>
              </q-td>

              <!-- Column 4: ACTIVITÉ -->
              <q-td style="color: var(--text-muted)" class="text-caption">
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
                  color="positive"
                  track-color="transparent"
                  style="border-radius: 50%; background: var(--border-light); font-weight: 700; color: var(--positive)"
                >
                  {{ props.row.progressPercent }}%
                </q-circular-progress>
              </q-td>

              <!-- Column 6: ACTIONS DROPDOWN -->
              <q-td class="text-center">
                <q-btn flat round dense color="grey-6" icon="more_vert" size="sm">
                  <q-menu auto-close>
                    <q-list style="min-width: 120px; background: var(--card-bg); border: 1px solid var(--card-border);">
                      <q-item clickable v-ripple @click="$router.push(`/pools/${props.row.id}`)">
                        <q-item-section style="color: var(--text-main)">Voir le projet</q-item-section>
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
      <div v-if="authStore.isSuperAdmin.value || authStore.isAdmin.value || authStore.isPaymentAdmin.value" class="row q-col-gutter-lg q-mb-xl">
        <div class="col-12">
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium row items-center justify-between">
              <div class="card-header-title">
                Demandes de retrait en attente
              </div>
              <q-chip color="transparent" style="border: 1px solid var(--card-border); color: var(--text-main)" size="sm" class="text-weight-bold">
                Rôle Paiements
              </q-chip>
            </div>

            <q-table
              flat
              :rows="pendingWithdrawals"
              :columns="withdrawalColumns"
              row-key="id"
              :loading="loadingWithdrawals"
              no-data-label="Aucune demande de retrait en attente."
              class="font-inter"
            >
              <template v-slot:header="props">
                <q-tr :props="props">
                  <q-th v-for="col in props.cols" :key="col.name" :props="props">
                    {{ col.label }}
                  </q-th>
                </q-tr>
              </template>

              <template v-slot:body-cell-amount="props">
                <q-td :props="props" class="text-weight-bold text-subtitle2" style="color: var(--text-main)">
                  {{ props.row.amount }} €
                </q-td>
              </template>

              <template v-slot:body-cell-fees="props">
                <q-td :props="props" class="text-weight-bold text-negative text-subtitle2">
                  - {{ props.row.fees }} €
                </q-td>
              </template>

              <template v-slot:body-cell-net="props">
                <q-td :props="props" class="text-weight-bolder text-positive text-subtitle2">
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
                    class="text-weight-bolder"
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
      <div v-if="(authStore.isSuperAdmin.value || authStore.isAdmin.value) && auditLogs.length > 0" class="row q-col-gutter-lg q-mb-xl">
        <div class="col-12">
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium row items-center justify-between">
              <div class="card-header-title">
                Journal d'Audit - Actions Critiques de la Plateforme
              </div>
              <q-chip color="transparent" style="border: 1px solid var(--card-border); color: var(--text-main)" size="sm" class="text-weight-bold">
                Rôle Super Admin
              </q-chip>
            </div>

            <q-table
              flat
              :rows="auditLogs"
              :columns="auditColumns"
              row-key="id"
              :rows-per-page-options="[5, 10, 20]"
              class="font-inter"
            >
              <template v-slot:header="props">
                <q-tr :props="props">
                  <q-th v-for="col in props.cols" :key="col.name" :props="props">
                    {{ col.label }}
                  </q-th>
                </q-tr>
              </template>

              <template v-slot:body-cell-status="props">
                <q-td :props="props" class="text-center">
                  <q-chip 
                    color="transparent" 
                    :style="props.row.status === 'SUCCESS' ? 'border: 1px solid var(--positive); color: var(--positive)' : 'border: 1px solid var(--negative); color: var(--negative)'"
                    size="sm" 
                    class="text-weight-bolder text-uppercase"
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
      <div class="row q-col-gutter-xl">
        
        <!-- Left: IP Stats -->
        <div class="col-12 col-md-4">
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium">
              <div class="card-header-title">
                Adresses IP Actives (Audit)
              </div>
            </div>
            <q-list v-if="ipDistribution.length > 0" class="q-pa-sm">
              <q-item v-for="ipObj in ipDistribution" :key="ipObj.ip">
                <q-item-section>
                  <q-item-label class="text-weight-bold" style="color: var(--text-main)">{{ ipObj.ip }}</q-item-label>
                  <q-item-label caption style="color: var(--text-muted)">Audit trace</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-chip size="sm" color="transparent" style="border: 1px solid var(--card-border); color: var(--text-main)">{{ ipObj.count }} actions</q-chip>
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
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium">
              <div class="card-header-title">
                Progression des Cagnottes
              </div>
            </div>
            <q-card-section v-if="recentPoolsComputed.length > 0" class="q-pa-md q-gutter-y-md">
              <div v-for="p in recentPoolsComputed.slice(0, 3)" :key="p.id">
                <div class="row justify-between text-caption font-inter q-mb-xs" style="color: var(--text-main)">
                  <span class="text-weight-bold text-truncate" style="max-width: 180px;">{{ p.title }}</span>
                  <span>{{ p.progressPercent }}%</span>
                </div>
                <q-linear-progress 
                  :value="(p.currentAmount || 0) / p.goalAmount" 
                  color="primary" 
                  track-color="transparent"
                  size="6px"
                  style="border-radius: 4px; background: var(--border-light) !important;"
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
          <q-card class="premium-card no-shadow">
            <div class="card-header-premium">
              <div class="card-header-title">
                Membres Administrateurs
              </div>
            </div>
            <q-list v-if="adminsList.length > 0" class="q-pa-sm">
              <q-item v-for="admin in adminsList" :key="admin.email">
                <q-item-section avatar>
                  <q-avatar size="32px" color="primary" text-color="white" class="text-weight-bold">
                    {{ admin.email[0].toUpperCase() }}
                  </q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold font-inter" style="color: var(--text-main)">{{ admin.email }}</q-item-label>
                  <q-item-label caption style="color: var(--text-muted)">{{ admin.role }}</q-item-label>
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

const chartOptions = computed(() => {
  const isDark = $q.dark.isActive
  const labelColor = isDark ? '#8ea39f' : '#627571'
  const gridColor = isDark ? 'rgba(255, 255, 255, 0.08)' : 'rgba(9, 31, 26, 0.06)'
  
  return {
    chart: {
      type: 'area',
      height: 240,
      sparkline: { enabled: false },
      toolbar: { show: false },
      fontFamily: 'Inter, sans-serif'
    },
    colors: ['#00e676'], // Brand Neon Green
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.45,
        opacityTo: 0.02,
        stops: [0, 100]
      }
    },
    stroke: {
      curve: 'smooth',
      width: 3
    },
    xaxis: {
      categories: chartData.value.categories,
      labels: { style: { colors: labelColor, fontSize: '11px', fontWeight: 500 } },
      axisBorder: { show: false },
      axisTicks: { show: false }
    },
    yaxis: {
      labels: { style: { colors: labelColor, fontSize: '11px', fontWeight: 500 } }
    },
    grid: {
      borderColor: gridColor,
      strokeDashArray: 5
    },
    dataLabels: { enabled: false },
    tooltip: {
      theme: isDark ? 'dark' : 'light'
    }
  }
})

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

const categoryChartOptions = computed(() => {
  const isDark = $q.dark.isActive
  const labelColor = isDark ? '#f1f5f4' : '#091f1a'
  const legendColor = isDark ? '#8ea39f' : '#627571'
  
  return {
    chart: {
      type: 'donut',
      fontFamily: 'Inter, sans-serif'
    },
    labels: Object.keys(categoryChartData.value),
    colors: ['#00e676', '#10b981', '#facc15', '#06b6d4', '#062f25'], // Brand colors
    legend: {
      show: true,
      position: 'bottom',
      horizontalAlign: 'center',
      fontSize: '11px',
      labels: {
        colors: legendColor
      },
      markers: {
        radius: 12
      }
    },
    plotOptions: {
      pie: {
        donut: {
          size: '72%',
          labels: {
            show: true,
            name: {
              show: true,
              fontSize: '13px',
              fontFamily: 'Inter, sans-serif',
              color: legendColor
            },
            value: {
              show: true,
              fontSize: '18px',
              fontFamily: 'Inter, sans-serif',
              fontWeight: '700',
              color: labelColor,
              formatter: (val) => val
            },
            total: {
              show: true,
              label: 'Total',
              color: legendColor,
              formatter: (w) => {
                return w.globals.seriesTotals.reduce((a, b) => a + b, 0)
              }
            }
          }
        }
      }
    },
    stroke: {
      show: true,
      width: 2,
      colors: [isDark ? '#091f1a' : '#ffffff']
    },
    dataLabels: { enabled: false },
    tooltip: {
      theme: isDark ? 'dark' : 'light'
    }
  }
})

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
      if (p.status === 'PUBLIEE') active++
      if (p.status === 'CLOTUREE') completed++
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
  --bg-dashboard: #f4f7f6;
  --card-bg: #ffffff;
  --card-border: rgba(0, 230, 118, 0.08);
  --card-shadow: 0 8px 24px -4px rgba(9, 31, 26, 0.04), 0 1px 2px rgba(9, 31, 26, 0.02);
  --card-shadow-hover: 0 20px 32px -8px rgba(9, 31, 26, 0.08), 0 1px 4px rgba(9, 31, 26, 0.03);
  --text-main: #091f1a;
  --text-muted: #627571;
  --border-light: rgba(9, 31, 26, 0.06);
  --table-header-bg: #edf2f0;
  --glow-green: rgba(16, 185, 129, 0.15);
  --glow-primary: rgba(0, 230, 118, 0.15);
  --glow-info: rgba(6, 182, 212, 0.15);
  --glow-warning: rgba(250, 204, 21, 0.15);
  
  background: var(--bg-dashboard);
  color: var(--text-main);
  min-height: 100vh;
  transition: background 0.3s ease, color 0.3s ease;
}

body.body--dark .admin-dashboard-page {
  --bg-dashboard: #05100d;
  --card-bg: #091f1a;
  --card-border: rgba(0, 230, 118, 0.15);
  --card-shadow: 0 8px 24px -4px rgba(0, 0, 0, 0.35);
  --card-shadow-hover: 0 20px 32px -8px rgba(0, 0, 0, 0.5);
  --text-main: #f1f5f4;
  --text-muted: #8ea39f;
  --border-light: rgba(255, 255, 255, 0.08);
  --table-header-bg: #061713;
  --glow-green: rgba(16, 185, 129, 0.18);
  --glow-primary: rgba(0, 230, 118, 0.18);
  --glow-info: rgba(6, 182, 212, 0.18);
  --glow-warning: rgba(250, 204, 21, 0.18);
}

.font-inter {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dashboard-title {
  color: var(--text-main);
  font-size: 1.8rem;
  letter-spacing: -0.5px;
}

/* Visit Site Button & Role Badge */
.visit-site-btn {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  color: var(--text-main);
  font-weight: 700;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 8px 16px;
  transition: all 0.3s ease;
}
.visit-site-btn:hover {
  background: var(--glow-primary);
  border-color: var(--primary);
  color: var(--primary);
  transform: translateY(-2px);
}
.role-badge-container {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  box-shadow: var(--card-shadow);
  border-radius: 12px;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  transition: all 0.3s ease;
}
.role-badge-container:hover {
  transform: translateY(-2px);
  box-shadow: var(--card-shadow-hover);
}
.role-badge-icon {
  background: var(--glow-primary);
  color: var(--primary);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}
.role-badge-title {
  font-size: 0.65rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: var(--text-muted);
  line-height: 1;
}
.role-badge-name {
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--text-main);
  margin-top: 2px;
  line-height: 1;
}

/* KPI Responsive Grid */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1.25rem;
}
@media (max-width: 1200px) {
  .kpi-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (max-width: 768px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 480px) {
  .kpi-grid {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}

/* KPI Cards */
.kpi-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 20px;
  box-shadow: var(--card-shadow);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow-hover);
  border-color: rgba(0, 230, 118, 0.2);
}
.kpi-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.kpi-value {
  font-size: 1.8rem;
  font-weight: 800;
  color: var(--text-main);
  line-height: 1.2;
}
.kpi-icon-wrapper {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}
.kpi-card:hover .kpi-icon-wrapper {
  transform: scale(1.1) rotate(5deg);
}

/* Glow Backgrounds for Icon Wrappers */
.bg-green-glow { background: var(--glow-green); }
.bg-primary-glow { background: var(--glow-primary); }
.bg-success-glow { background: var(--glow-green); }
.bg-info-glow { background: var(--glow-info); }
.bg-warning-glow { background: var(--glow-warning); }

.kpi-footer {
  display: flex;
  align-items: center;
  font-size: 0.8rem;
}

/* Premium Card & Tables */
.premium-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 20px;
  box-shadow: var(--card-shadow);
  overflow: hidden;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}
.premium-card:hover {
  border-color: rgba(0, 230, 118, 0.25);
  box-shadow: var(--card-shadow-hover);
}
.card-header-premium {
  padding: 20px 24px;
}
.card-header-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-main);
  letter-spacing: -0.3px;
}

/* Quasar Table Overrides */
.premium-card :deep(.q-table__container) {
  background: transparent !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}
.premium-card :deep(.q-table) {
  background: transparent !important;
  color: var(--text-main) !important;
}
.premium-card :deep(.q-table th) {
  background: var(--table-header-bg) !important;
  color: var(--text-muted) !important;
  font-weight: 700 !important;
  font-size: 0.75rem !important;
  letter-spacing: 0.5px !important;
  padding: 14px 20px !important;
  border-bottom: 1px solid var(--border-light) !important;
}
.premium-card :deep(.q-table td) {
  padding: 16px 20px !important;
  border-bottom: 1px solid var(--border-light) !important;
  font-size: 0.85rem !important;
  color: var(--text-main) !important;
}
.premium-card :deep(.q-table tr:hover) {
  background: rgba(0, 230, 118, 0.03) !important;
}
body.body--dark .premium-card :deep(.q-table tr:hover) {
  background: rgba(0, 230, 118, 0.05) !important;
}
.premium-card :deep(.q-table__middle) {
  border-radius: 0 !important;
}

/* Recent Activities Custom List */
.recent-act-header {
  background: var(--table-header-bg);
  color: var(--text-muted);
  font-weight: 700;
  font-size: 0.75rem;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  border-bottom: 1px solid var(--border-light);
}
.recent-act-row {
  border-bottom: 1px solid var(--border-light);
  transition: all 0.2s ease;
}
.recent-act-row:hover {
  background: rgba(0, 230, 118, 0.03);
}
body.body--dark .recent-act-row:hover {
  background: rgba(0, 230, 118, 0.05);
}
.recent-act-username {
  font-weight: 700;
  color: var(--text-main);
  font-size: 0.85rem;
}
.recent-act-desc {
  font-size: 0.85rem;
  color: var(--text-main);
}
.recent-act-date {
  font-size: 0.75rem;
  color: var(--text-muted);
  font-weight: 500;
}
.delete-act-btn {
  color: var(--text-muted);
  transition: all 0.2s ease;
}
.delete-act-btn:hover {
  color: var(--negative);
  background: var(--glow-warning);
}
.avatar-border {
  border: 1px solid var(--card-border);
}

/* Help / Alert Banner */
.premium-alert-banner {
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.1) 0%, rgba(0, 230, 118, 0.05) 100%);
  border: 1px solid rgba(6, 182, 212, 0.15);
  border-radius: 16px;
  color: var(--text-main);
  box-shadow: var(--card-shadow);
}
.banner-icon {
  color: var(--info);
}
.banner-title {
  font-size: 0.95rem;
  margin-bottom: 2px;
}
.banner-text {
  font-size: 0.8rem;
  color: var(--text-muted);
}
.banner-link {
  color: var(--info);
  text-decoration: none;
  font-weight: 700;
  transition: opacity 0.2s;
}
.banner-link:hover {
  text-decoration: underline;
  opacity: 0.8;
}

/* Sidebar Widgets with lines */
.widget-progress-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}
.widget-progress-card:hover {
  transform: translateX(4px);
  box-shadow: var(--card-shadow-hover);
}
.widget-label {
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--text-muted);
}
.widget-value {
  font-size: 1.25rem;
  font-weight: 700;
}
.widget-progress-bar {
  border-radius: 3px;
  background: var(--border-light) !important;
}

/* Mini metric icon cards */
.mini-metric-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 16px;
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}
.mini-metric-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--card-shadow-hover);
}
.mini-icon-block {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}
.mini-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1.2;
}
.mini-label {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
}

/* Featured projects */
.project-showcase-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 20px;
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}
.project-showcase-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow-hover);
}
.project-image-wrapper {
  width: 160px;
  position: relative;
  overflow: hidden;
}
.project-image {
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.project-showcase-card:hover .project-image {
  transform: scale(1.05);
}
.project-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 8px;
}
.project-desc {
  font-size: 0.8rem;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
.project-owner {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-main);
}
.project-fav-btn {
  background: var(--glow-info);
  border-radius: 50%;
  transition: all 0.2s ease;
}
.project-fav-btn:hover {
  transform: scale(1.1);
}

/* Bottom elements lists overrides */
.premium-card :deep(.q-list) {
  background: transparent !important;
}
.premium-card :deep(.q-item) {
  border-bottom: 1px solid var(--border-light);
  padding: 14px 20px;
  transition: all 0.2s ease;
}
.premium-card :deep(.q-item:last-child) {
  border-bottom: none;
}
.premium-card :deep(.q-item:hover) {
  background: rgba(0, 230, 118, 0.03);
}
body.body--dark .premium-card :deep(.q-item:hover) {
  background: rgba(0, 230, 118, 0.05);
}
</style>
