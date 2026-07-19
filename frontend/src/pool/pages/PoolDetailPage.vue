<template>
  <q-page class="pool-detail-page" style="background: #F8F9FA; padding-bottom: 60px;">
    <!-- État de chargement -->
    <div v-if="loading" class="flex flex-center" style="height: 60vh;">
      <q-spinner-dots color="primary" size="40px" />
    </div>

    <!-- État d'erreur -->
    <div v-else-if="error" class="flex flex-center" style="height: 60vh;">
      <div class="text-center">
        <q-icon name="error_outline" color="negative" size="64px" />
        <div class="text-h6 q-mt-md">{{ error }}</div>
        <q-btn flat color="primary" :label="$t('poolDetail.backHome')" class="q-mt-md" @click="$router.push('/')" />
      </div>
    </div>

    <!-- Contenu -->
    <div v-else-if="pool" class="container q-mx-auto" style="max-width: 1100px; padding: 20px 20px 0 20px;">
      
      <!-- MAIN GRID (Two Columns: Left for media, stats, actions, donors. Right for Title, Story, Phases, Comments) -->
      <div class="row q-col-gutter-xl items-start">
        
        <!-- LEFT COLUMN (Media, profile, balance details, stats, main buttons, donors) -->
        <div class="col-12 col-md-5 sticky-sidebar-desktop">
          
          <!-- Image/Video Carousel Card -->
          <q-card class="media-card q-mb-lg no-shadow" style="border-radius: 20px; overflow: hidden; border: 1px solid #E2E8F0;">
            <q-carousel
              v-if="pool.imageUrl || youtubeId || isDirectVideo"
              v-model="mediaSlide"
              swipeable
              animated
              :arrows="(pool.imageUrl && (youtubeId || isDirectVideo))"
              :navigation="(pool.imageUrl && (youtubeId || isDirectVideo))"
              control-color="primary"
              height="200px"
              class="bg-black"
            >
              <q-carousel-slide v-if="pool.imageUrl" name="image" class="q-pa-none relative-position">
                <q-img :src="pool.imageUrl" height="100%" fit="cover" />
                <div class="category-badge">
                  {{ pool.category || 'Cagnotte' }}
                </div>
              </q-carousel-slide>

              <q-carousel-slide v-if="youtubeId || isDirectVideo" name="video" class="q-pa-none flex flex-center relative-position">
                <q-video
                  v-if="youtubeId"
                  :ratio="16/9"
                  :src="`https://www.youtube.com/embed/${youtubeId}`"
                  style="height: 100%; width: 100%;"
                />
                <video
                  v-else-if="isDirectVideo"
                  controls
                  style="height: 100%; width: 100%; object-fit: contain; background: #000;"
                  :src="pool.videoUrl"
                >
                  Votre navigateur ne supporte pas la lecture de vidéos.
                </video>
                <div class="category-badge">
                  {{ pool.category || 'Cagnotte' }}
                </div>
              </q-carousel-slide>
            </q-carousel>

            <div v-else class="bg-grey-2 relative-position flex flex-center" style="height: 280px;">
              <div class="category-badge">
                {{ pool.category || 'Cagnotte' }}
              </div>
              <q-icon name="image_not_supported" size="64px" color="grey-4" />
            </div>
          </q-card>

          <!-- Creator Profile Card -->
          <div class="profile-row flex items-center justify-between q-py-md q-px-sm q-mb-md">
            <div class="flex items-center">
              <q-avatar size="44px" class="bg-primary text-secondary text-weight-bold shadow-sm q-mr-md">
                {{ pool.ownerName?.charAt(0).toUpperCase() || 'U' }}
              </q-avatar>
              <div>
                <div class="flex items-center text-weight-bold text-dark text-subtitle1" style="line-height: 1.2;">
                  {{ pool.ownerName || 'Anonyme' }}
                  <q-icon name="verified" color="blue" size="18px" class="q-ml-xs" />
                </div>
                <div class="text-caption text-grey-6 q-mt-xs">Créé le {{ formatDate(pool.createdAt) }}</div>
              </div>
            </div>
            <q-btn label="Suivre" color="dark" outline size="sm" class="text-weight-bold px-4" style="border-radius: 20px; border-color: #E2E8F0;" no-caps />
          </div>

          <!-- Balance & Progress Card -->
          <div class="balance-card q-mb-lg q-px-sm">
            
            <!-- Deadline Alert -->
            <div v-if="pool.hasDeadline && pool.deadlineDate" class="q-mb-md q-pa-sm bg-amber-1 text-amber-9 rounded-borders text-caption text-weight-bold flex items-center">
              <q-icon name="alarm" class="q-mr-xs" size="sm" />
              {{ $t('poolDetail.deadlineLabel') }} : {{ formatDateWithTime(pool.deadlineDate) }}
            </div>

            <!-- Amount Level -->
            <div class="row items-baseline q-mb-sm">
              <div class="text-h3 text-weight-bolder text-positive" style="color: #00e676; letter-spacing: -0.5px;">
                {{ pool.currentAmount || 0 }} €
              </div>
              <div class="text-subtitle1 text-grey-6 q-ml-sm text-weight-medium">
                {{ $t('poolDetail.ofGoal') }} {{ pool.goalAmount }} €
              </div>
            </div>

            <!-- Progress Bar -->
            <q-linear-progress 
              :value="(pool.currentAmount || 0) / pool.goalAmount" 
              color="primary" 
              size="10px" 
              rounded 
              class="q-mb-md"
              style="background-color: #F1F5F9;"
            />

            <!-- Statistics Row -->
            <div class="row items-center justify-between text-caption text-grey-7 text-weight-bold q-py-sm q-mb-md stats-row">
              <div class="flex items-center">
                <q-icon name="flag" size="18px" class="q-mr-xs text-grey-5" />
                <span>{{ Math.round(((pool.currentAmount || 0) / pool.goalAmount) * 100) }}% {{ $t('poolDetail.completedPercentSuffix') }}</span>
              </div>
              <div class="flex items-center">
                <q-icon name="people" size="18px" class="q-mr-xs text-grey-5" />
                <span>{{ contributions.length }} {{ $t('poolDetail.participants') }}</span>
              </div>
              <div class="flex items-center">
                <q-icon name="comment" size="18px" class="q-mr-xs text-grey-5" />
                <span>{{ messages.length }}</span>
              </div>
              <div class="flex items-center">
                <q-icon name="visibility" size="18px" class="q-mr-xs text-grey-5" />
                <span>{{ Math.round((pool.currentAmount || 0) * 0.15 + 18) }} vues</span>
              </div>
            </div>
          </div>

          <!-- Action Buttons Row -->
          <div class="row q-col-gutter-sm q-mb-md items-center">
            <div class="col">
              <q-btn 
                outline 
                icon="share" 
                :label="$t('poolDetail.share')" 
                class="full-width q-py-md text-weight-bold shadow-sm share-btn" 
                no-caps 
                style="border-radius: 24px;" 
                @click="openShareDialog" 
              />
            </div>
            <div class="col">
              <q-btn
                :label="$t('poolDetail.contributeNow') || 'Participer'"
                unelevated
                color="primary"
                text-color="secondary"
                class="full-width q-py-md text-weight-bolder shadow-2"
                style="border-radius: 24px; font-size: 1rem;"
                no-caps
                @click="contribute"
              />
            </div>
            <div class="col-auto">
              <q-btn 
                outline 
                icon="favorite_border" 
                class="q-py-md share-btn" 
                style="border-radius: 24px; min-width: 50px;" 
              />
            </div>
          </div>

          <!-- Report / Secondary Actions -->
          <div v-if="authStore.isAuthenticated.value && pool && String(pool.ownerId) !== String(authStore.user.value?.id)" class="row justify-center q-mt-sm q-mb-lg">
            <q-btn 
              flat 
              dense 
              color="negative" 
              icon="flag" 
              :label="$t('poolDetail.report') || 'Signaler cette cagnotte'" 
              no-caps 
              class="text-weight-bold text-caption" 
              @click="reportPool"
            />
          </div>

        </div>

        <!-- RIGHT COLUMN (Title, Story/Description, Timeline/Phases, Financial dashboard, Comments) -->
        <div class="col-12 col-md-7">
          
          <!-- Pool Title -->
          <h1 class="text-weight-bolder text-dark q-mt-none q-mb-xs pool-title" style="font-size: 2.3rem; line-height: 1.25; letter-spacing: -0.8px;">
            {{ pool.title }}
          </h1>

          <!-- Meta Info Bar (Creation Date, Category, Visibility) -->
          <div class="row items-center q-gutter-x-md text-caption text-grey-6 q-mb-lg">
            <div class="row items-center">
              <q-icon name="today" size="16px" class="q-mr-xs text-primary" />
              <span>Créée le {{ formatDate(pool.createdAt) }}</span>
            </div>
            <div v-if="pool.category" class="row items-center">
              <q-icon name="folder" size="16px" class="q-mr-xs text-primary" />
              <span>{{ pool.category }}</span>
            </div>
            <div class="row items-center">
              <q-icon name="public" size="16px" class="q-mr-xs text-primary" />
              <span>{{ $t('poolDetail.visibilityPublic') || 'Publique' }}</span>
            </div>
          </div>

          <!-- About Pool/Story Card -->
          <q-card class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center justify-between q-mb-md">
              <div class="row items-center">
                <q-icon name="info" class="q-mr-sm" color="primary" size="24px" />
                <div class="text-subtitle1 text-weight-bold text-dark">
                  {{ $t('poolDetail.aboutPool') }}
                </div>
              </div>
              <div class="row q-gutter-sm">
                <q-btn 
                  v-if="isOwner || isAdmin" 
                  :label="$t('poolDetail.edit')" 
                  icon="edit" 
                  color="warning" 
                  flat 
                  no-caps 
                  style="border-radius: 8px;"
                  @click="router.push(`/pools/${pool.id}/edit`)"
                />
                <q-btn 
                  v-if="isOwner" 
                  :label="$t('poolDetail.addPhase')" 
                  icon="add_circle" 
                  color="primary" 
                  flat 
                  no-caps 
                  style="border-radius: 8px;"
                  @click="router.push({ path: '/pools/create', query: { parentId: pool.id } })"
                />
              </div>
            </div>
            <div class="text-body2 text-grey-8 q-mb-none" style="white-space: pre-line; line-height: 1.6; font-size: 0.95rem;">
              {{ pool.description }}
            </div>
          </q-card>

          <!-- SECTION : ACTUALITES & TIMELINE DE CONFIANCE -->
          <q-card class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center justify-between q-mb-md">
              <div class="row items-center">
                <q-icon name="campaign" class="q-mr-sm" color="primary" size="24px" />
                <div class="text-subtitle1 text-weight-bold text-dark">
                  Actualités du projet ({{ updates.length }})
                </div>
              </div>
              <q-btn 
                v-if="isOwner" 
                label="Publier une actualité" 
                icon="add_alert" 
                color="positive" 
                unelevated 
                no-caps 
                style="border-radius: 12px;"
                @click="openPublishUpdateDialog"
              />
            </div>

            <!-- Liste des actualités -->
            <div v-if="updates.length > 0" class="updates-timeline q-pl-sm">
              <div v-for="update in updates" :key="update.id" class="update-timeline-item q-mb-xl" style="position: relative;">
                <div class="row items-start">
                  <!-- L'icône de l'actualité -->
                  <div class="update-icon-circle q-mr-md shadow-sm bg-positive text-white flex flex-center" style="width: 40px; height: 40px; border-radius: 50%; z-index: 1;">
                    <q-icon name="announcement" size="20px" />
                  </div>
                  
                  <div class="col">
                    <div class="text-weight-bold text-dark text-subtitle1 q-mb-xs">
                      {{ update.title }}
                    </div>
                    <div class="text-caption text-grey-5 q-mb-md">
                      Publié le {{ formatDateWithTime(update.createdAt) }}
                    </div>
                    <div class="text-body2 text-grey-8 q-mb-md" style="white-space: pre-line; line-height: 1.6;">
                      {{ update.content }}
                    </div>

                    <!-- Images / Vidéos de l'actualité -->
                    <div v-if="update.imageUrl" class="q-mb-md" style="max-width: 100%; border-radius: 12px; overflow: hidden; border: 1px solid #E2E8F0;">
                      <q-img :src="update.imageUrl" max-height="350px" fit="contain" class="bg-grey-1" />
                    </div>

                    <div v-if="update.videoUrl" class="q-mb-md" style="max-width: 100%; border-radius: 12px; overflow: hidden; border: 1px solid #E2E8F0;">
                      <q-video :src="update.videoUrl" :ratio="16/9" style="max-height: 350px;" />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="text-center q-py-xl text-grey-5">
              <q-icon name="notifications_none" size="48px" class="q-mb-sm text-grey-4" />
              <div>Aucune actualité publiée pour le moment.</div>
              <div class="text-caption text-grey-5 q-mt-xs">Suivez cette cagnotte pour rester informé des derniers développements.</div>
            </div>
          </q-card>

          <!-- Last Donors List Card (CotizUp Style) -->
          <q-card class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center justify-between q-mb-md">
              <div class="row items-center">
                <q-icon name="favorite" class="q-mr-sm" color="primary" size="24px" />
                <div class="text-subtitle1 text-weight-bold text-dark">
                  {{ $t('poolDetail.lastDonors') }} ({{ contributions.length }})
                </div>
              </div>
              <q-btn flat dense no-caps color="grey-6" label="Les plus récents" icon-right="expand_more" size="sm" class="text-weight-bold" />
            </div>

            <q-list v-if="contributions.length > 0" class="q-gutter-y-xs">
              <q-item v-for="contrib in contributions.slice(0, 8)" :key="contrib.id" class="q-px-none q-py-md" style="border-bottom: 1px solid #F8FAFC;">
                <q-item-section avatar>
                  <q-avatar size="38px" class="bg-grey-2 text-grey-7 text-weight-bold">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ (contrib.contributorName || 'A').charAt(0).toUpperCase() }}</span>
                      <span v-else>-</span>
                    </template>
                    <span v-else>{{ (contrib.contributorName || 'A').charAt(0).toUpperCase() }}</span>
                  </q-avatar>
                </q-item-section>
                <q-item-section>
                  <div class="row items-baseline justify-between">
                    <div class="text-weight-bold text-dark" style="font-size: 0.95rem;">
                      <template v-if="contrib.anonymous">
                        <span v-if="isOwner">{{ contrib.contributorName }} (Anonyme)</span>
                        <span v-else>{{ $t('poolDetail.anonymousDonor') }}</span>
                      </template>
                      <span v-else>{{ contrib.contributorName }}</span>
                    </div>
                    <div class="text-weight-bolder text-dark" style="font-size: 0.95rem;">
                      +{{ contrib.amount }} €
                    </div>
                  </div>
                  <q-item-label caption class="text-grey-5 q-mt-xs">Il y a quelques jours</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>

            <div v-else class="text-center q-py-xl text-grey-5 text-italic">
              {{ $t('poolDetail.firstContributorPrompt') }}
            </div>
          </q-card>

          <!-- SECTION : PHASES DE PROGRESSION (Cagnotte Simple) -->
          <q-card v-if="(!pool.subPools || pool.subPools.length === 0) && pool.phases && pool.phases.length > 0" class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center q-mb-lg">
              <q-icon name="flag" class="q-mr-sm" color="primary" size="24px" />
              <div class="text-subtitle1 text-weight-bold text-dark">
                {{ $t('poolDetail.phasesHeader') }} ({{ pool.phases.length }})
              </div>
            </div>
            
            <div class="phases-timeline q-pl-sm">
              <div v-for="(phase, phIndex) in pool.phases" :key="phase.id" class="phase-item q-mb-md">
                <div class="row items-center q-mb-xs">
                  <div class="phase-number-circle q-mr-md shadow-sm" :class="getPhaseStatusClass(phase)">
                    {{ phIndex + 1 }}
                  </div>
                  <div class="col">
                    <div class="row items-center justify-between">
                      <div class="text-body2 text-weight-bold text-dark">
                        {{ phase.title }}
                        <q-badge v-if="phase.status === 'ACTIVE'" color="secondary" label="ACTIVE" class="q-ml-xs text-weight-bold text-xxs" />
                      </div>
                      <div class="text-caption text-weight-medium text-grey-6">
                        {{ phase.currentAmount || 0 }} € / {{ phase.goalAmount }} €
                      </div>
                    </div>
                  </div>
                </div>
                <div class="q-pl-xl">
                  <q-linear-progress 
                    :value="(phase.currentAmount || 0) / phase.goalAmount" 
                    :color="phase.status === 'COMPLETED' ? 'green' : (phase.status === 'ACTIVE' ? 'secondary' : 'grey-4')" 
                    size="6px" 
                    rounded 
                  />
                </div>
              </div>
            </div>
          </q-card>

          <!-- SECTION : SOUS-CAGNOTTES / ÉTAPES DU PROJET -->
          <q-card v-if="pool.subPools && pool.subPools.length > 0" class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center q-mb-lg">
              <q-icon name="account_tree" class="q-mr-sm" color="primary" size="24px" />
              <div class="text-subtitle1 text-weight-bold text-dark">
                {{ $t('poolDetail.subPoolsHeader') }} ({{ pool.subPools.length }})
              </div>
            </div>
            
            <div class="q-gutter-y-lg">
              <div v-for="(subPool, spIndex) in pool.subPools" :key="subPool.id" :class="{'q-pt-lg border-top-divider': spIndex > 0}">
                <!-- Title and Contribute button -->
                <div class="row items-center justify-between q-mb-md">
                  <div class="col">
                    <div class="text-subtitle1 text-weight-bold text-dark">{{ subPool.title }}</div>
                    <div v-if="subPool.description" class="text-body2 text-grey-6 q-mt-xs">{{ subPool.description }}</div>
                    <!-- Subpool deadline -->
                    <div v-if="subPool.hasDeadline && subPool.deadlineDate" class="text-caption text-amber-9 text-weight-bold q-mt-xs">
                      <q-icon name="alarm" class="q-mr-xs" /> Fin le {{ formatDateWithTime(subPool.deadlineDate) }}
                    </div>
                  </div>
                  
                  <div class="col-auto">
                    <q-btn 
                      label="Contribuer" 
                      color="secondary" 
                      unelevated 
                      no-caps 
                      style="border-radius: 12px;"
                      @click="contributeToSubPool(subPool.id)"
                      :disable="subPool.status === 'COMPLETED'"
                    />
                  </div>
                </div>

                <!-- Progress bar -->
                <div class="q-mb-md">
                  <div class="row justify-between text-caption text-grey-6 q-mb-xs">
                    <span>{{ subPool.currentAmount || 0 }} € collectés sur {{ subPool.goalAmount }} €</span>
                    <span class="text-weight-bold">{{ Math.round(((subPool.currentAmount || 0) / subPool.goalAmount) * 100) }}%</span>
                  </div>
                  <q-linear-progress 
                    :value="(subPool.currentAmount || 0) / subPool.goalAmount" 
                    color="primary" 
                    size="8px" 
                    rounded 
                  />
                </div>

                <!-- Phases within this sub-pool -->
                <div v-if="subPool.phases && subPool.phases.length > 0" class="q-mt-md">
                  <div class="text-subtitle2 text-weight-bold text-grey-7 q-mb-sm">{{ $t('poolDetail.phasesHeader') }} :</div>
                  <div class="phases-timeline q-pl-sm">
                    <div v-for="(phase, phIndex) in subPool.phases" :key="phase.id" class="phase-item q-mb-md">
                      <div class="row items-center q-mb-xs">
                        <div class="phase-number-circle q-mr-md shadow-sm" :class="getPhaseStatusClass(phase)">
                          {{ phIndex + 1 }}
                        </div>
                        <div class="col">
                          <div class="row items-center justify-between">
                            <div class="text-caption text-weight-bold text-dark">
                              {{ phase.title }}
                              <q-badge v-if="phase.status === 'ACTIVE'" color="secondary" label="ACTIVE" class="q-ml-xs text-weight-bold text-xxs" />
                            </div>
                            <div class="text-caption text-weight-medium text-grey-6">
                              {{ phase.currentAmount }} € / {{ phase.goalAmount }} €
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="q-pl-xl">
                        <q-linear-progress 
                          :value="phase.currentAmount / phase.goalAmount" 
                          :color="phase.status === 'COMPLETED' ? 'green' : (phase.status === 'ACTIVE' ? 'secondary' : 'grey-4')" 
                          size="6px" 
                          rounded 
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </q-card>

          <!-- SECTION FINANCIERE (Visible uniquement par le proprietaire) -->
          <q-card v-if="isOwner" class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center q-mb-lg">
              <q-icon name="account_balance_wallet" class="q-mr-sm" color="primary" size="24px" />
              <div class="text-subtitle1 text-weight-bold text-dark">
                {{ $t('poolDetail.financialDashboard') }}
              </div>
            </div>
            
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <div class="financial-box q-pa-md column justify-between" style="min-height: 150px; border-radius: 16px;">
                  <div class="text-overline text-grey-6">{{ $t('poolDetail.availableBalance') }}</div>
                  <div class="text-h4 text-weight-bolder text-dark">{{ pool.availableBalance || 0 }} €</div>
                  <div class="text-caption text-grey-5">{{ $t('poolDetail.readyToWithdraw') }}</div>
                  <div class="q-mt-md" v-if="pool.availableBalance > 0">
                    <q-btn 
                      label="Demander un retrait" 
                      color="positive" 
                      unelevated 
                      no-caps 
                      icon="payment"
                      class="full-width text-weight-bold"
                      style="border-radius: 12px;"
                      @click="openWithdrawDialog"
                    />
                  </div>
                </div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="financial-box q-pa-md column justify-between" style="min-height: 150px; border-radius: 16px;">
                  <div class="text-overline text-grey-6">{{ $t('poolDetail.pending') }}</div>
                  <div class="text-h4 text-weight-bolder text-grey-6">{{ pool.pendingBalance || 0 }} €</div>
                  <div class="text-caption text-grey-5">{{ $t('poolDetail.pendingDesc') }}</div>
                </div>
              </div>
            </div>
          </q-card>

          <!-- SECTION : COMMENTAIRES -->
          <q-card class="custom-card q-pa-lg q-mb-lg no-shadow">
            <div class="row items-center q-mb-lg">
              <q-icon name="forum" class="q-mr-sm" color="primary" size="24px" />
              <div class="text-subtitle1 text-weight-bold text-dark">
                {{ $t('poolDetail.commentsHeader') }} ({{ messages.length }})
              </div>
            </div>

            <!-- Champ de saisie pour nouveau commentaire -->
            <div v-if="authStore.isAuthenticated.value" class="q-mb-lg">
              <q-input
                outlined
                v-model="newComment"
                type="textarea"
                :placeholder="$t('poolDetail.commentPlaceholder')"
                rows="3"
                color="secondary"
                bg-color="white"
                style="border-radius: 12px; overflow: hidden;"
              >
                <template v-slot:after>
                  <div class="column justify-end full-height q-pb-xs">
                    <q-btn round color="primary" icon="send" :loading="sendingComment" @click="postComment" />
                  </div>
                </template>
              </q-input>
            </div>
            <div v-else class="q-pa-md bg-blue-1 text-blue-9 text-weight-bold q-mb-lg" style="border-radius: 12px; border: 1px dashed rgba(33, 150, 243, 0.3);">
              <q-icon name="info" class="q-mr-sm" />
              {{ $t('poolDetail.loginToComment') }}
            </div>

            <!-- Liste des commentaires -->
            <div v-if="messages.length > 0" class="q-gutter-y-md">
              <div v-for="msg in messages" :key="msg.id" class="comment-item q-pa-md">
                <div class="row justify-between items-center q-mb-sm">
                  <div class="row items-center">
                    <q-avatar size="32px" class="bg-primary text-secondary text-weight-bold q-mr-sm">
                      {{ msg.userName?.charAt(0).toUpperCase() || '?' }}
                    </q-avatar>
                    <div>
                      <div class="text-weight-bold text-dark">
                        {{ msg.userName || 'Utilisateur anonyme' }}
                      </div>
                      <div class="text-caption text-grey-5">{{ formatDate(msg.createdAt) }}</div>
                    </div>
                  </div>
                  
                  <!-- Reactions and flags -->
                  <div class="row items-center q-gutter-x-xs">
                    <div v-if="authStore.isAuthenticated.value" class="reaction-trigger">
                      <q-btn flat round dense icon="add_reaction" color="grey-6" size="sm">
                        <q-menu anchor="top middle" self="bottom middle" class="reaction-menu no-shadow">
                          <div class="row no-wrap q-pa-xs q-gutter-x-sm bg-white shadow-2" style="border-radius: 30px; border: 1px solid #EEE;">
                            <q-btn v-for="emoji in availableEmojis" :key="emoji.type" 
                                   flat round dense :label="emoji.icon" class="emoji-btn"
                                   @click="toggleReaction(msg.id, emoji.type)" v-close-popup />
                          </div>
                        </q-menu>
                      </q-btn>
                    </div>
                    
                    <q-btn v-if="authStore.isModerator.value" flat round dense icon="delete" color="negative" size="sm" @click="deleteComment(msg.id)">
                      <q-tooltip>Supprimer</q-tooltip>
                    </q-btn>

                    <q-btn v-if="authStore.isAuthenticated.value && msg.userId !== authStore.user.value?.id" flat round dense icon="flag" color="warning" size="sm" @click="reportComment(msg)">
                      <q-tooltip>Signaler</q-tooltip>
                    </q-btn>
                  </div>
                </div>
                
                <div class="text-body2 text-grey-8 q-pl-md q-mb-sm" style="border-left: 2px solid #CBD5E1; line-height: 1.6;">
                  {{ msg.content }}
                </div>

                <!-- Reaction Badges -->
                <div v-if="msg.reactions && msg.reactions.length > 0" class="row q-gutter-xs q-pl-md">
                  <div v-for="(group, type) in groupReactions(msg.reactions)" :key="type" 
                       class="reaction-badge row items-center q-px-sm q-py-xs"
                       @click="toggleReaction(msg.id, type)"
                       :class="{'user-reacted': hasUserReacted(msg.reactions, type)}">
                    <span class="q-mr-xs">{{ getEmojiIcon(type) }}</span>
                    <span class="text-caption text-weight-bold">{{ group.length }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center q-py-xl text-grey-5">
              <q-icon name="chat_bubble_outline" size="48px" class="q-mr-sm" />
              <div>{{ $t('poolDetail.noComments') }}</div>
            </div>
          </q-card>
        </div>
      </div>
    </div>

    <!-- Dialogue de Contribution -->
    <q-dialog v-model="contributionDialog" persistent>
      <q-card style="min-width: 400px; border-radius: 16px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold">{{ $t('poolDetail.donateDialogTitle') }}</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <div class="text-subtitle2 q-mb-md text-grey-7">
            {{ $t('poolDetail.donateDialogSubtitlePrefix') }} "{{ pool?.title }}" {{ $t('poolDetail.donateDialogSubtitleSuffix') }}
          </div>
          
          <div v-if="pool.subPools && pool.subPools.length > 0" class="q-mb-md">
            <q-select
              outlined
              v-model="selectedSubPoolId"
              :options="subPoolOptions"
              option-value="value"
              option-label="label"
              emit-value
              map-options
              :label="$t('poolDetail.selectSubPoolLabel')"
              color="secondary"
              :rules="[val => !!val || $t('poolDetail.selectSubPoolRequired')]"
            />
          </div>

          <div class="q-gutter-md">
            <q-input
              outlined
              v-model.number="contributionForm.amount"
              type="number"
              :label="$t('poolDetail.donationAmountLabel')"
              suffix="€"
              :rules="[val => val > 0 || 'Le montant doit être supérieur à 0']"
              color="secondary"
              autofocus
            />

            <!-- Résumé des frais de contribution -->
            <div class="q-pa-md rounded-borders q-mt-sm" :style="{ background: $q.dark.isActive ? 'rgba(255,255,255,0.05)' : '#F5F5F5', borderRadius: '8px' }">
              <div class="row justify-between text-caption text-grey-7 q-mb-xs">
                <span>Montant de votre don :</span>
                <span>{{ contributionForm.amount || 0 }} €</span>
              </div>
              <div class="row justify-between text-caption text-negative q-mb-xs">
                <span>Frais de service ({{ poolFees }}% déduits) :</span>
                <span>- {{ (contributionForm.amount * (poolFees / 100) || 0).toFixed(2) }} €</span>
              </div>
              <q-separator class="q-my-sm" />
              <div class="row justify-between text-subtitle2 text-weight-bold text-primary">
                <span>Montant net collecté pour la cagnotte :</span>
                <span>{{ (contributionForm.amount * (1 - poolFees / 100) || 0).toFixed(2) }} €</span>
              </div>
            </div>

            <div v-if="!authStore.isAuthenticated.value" class="q-pa-md bg-amber-1 text-amber-9 text-caption rounded-borders q-mb-sm flex items-center no-wrap">
              <q-icon name="info" class="q-mr-xs" size="sm" />
              <span>Vous n'êtes pas connecté. Saisissez votre nom et adresse email. Si vous créez un compte plus tard avec cette même adresse email, toutes vos contributions y seront associées après confirmation de votre compte.</span>
            </div>

            <q-input
              v-if="!authStore.isAuthenticated.value"
              outlined
              v-model="contributionForm.contributorName"
              :label="$t('poolDetail.contributorNameLabel')"
              placeholder="Ex: Jean Dupont"
              color="secondary"
            />

            <q-input
              v-if="!authStore.isAuthenticated.value"
              outlined
              v-model="contributionForm.contributorEmail"
              :label="$t('poolDetail.contributorEmailLabel')"
              type="email"
              placeholder="Ex: jean.dupont@example.com"
              color="secondary"
              :rules="[val => !!val || $t('poolDetail.emailRequiredForReceipt'), val => val.includes('@') || $t('poolDetail.emailInvalid')]"
            />

            <q-input
              outlined
              v-model="contributionForm.message"
              type="textarea"
              :label="$t('poolDetail.messageLabel')"
              :placeholder="$t('poolDetail.messagePlaceholder')"
              color="secondary"
              rows="3"
            />

            <q-checkbox
              v-model="contributionForm.anonymous"
              :label="$t('poolDetail.anonymousToggle')"
              color="secondary"
            />

            <div class="q-mt-lg">
              <div class="text-subtitle2 q-mb-sm text-grey-8 font-weight-bold">
                {{ $t('poolDetail.selectPaymentMethod') }} :
              </div>
              <div class="row q-col-gutter-sm">
                <div class="col-6">
                  <q-btn
                    unelevated
                    color="indigo-7"
                    class="full-width q-py-sm text-weight-bold"
                    no-caps
                    style="border-radius: 8px;"
                    @click="submitPayment('stripe')"
                    :loading="submitting && selectedMethod === 'stripe'"
                    :disable="submitting"
                  >
                    <q-icon name="credit_card" class="q-mr-xs" />
                    Stripe
                  </q-btn>
                </div>
                <div class="col-6">
                  <q-btn
                    unelevated
                    color="blue-8"
                    class="full-width q-py-sm text-weight-bold text-white"
                    no-caps
                    style="border-radius: 8px;"
                    @click="submitPayment('paypal')"
                    :loading="submitting && selectedMethod === 'paypal'"
                    :disable="submitting"
                  >
                    <q-icon name="payment" class="q-mr-xs" />
                    PayPal
                  </q-btn>
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
    <!-- Dialogue de Retrait (Withdrawal) -->
    <q-dialog v-model="withdrawDialog" persistent>
      <q-card style="min-width: 400px; border-radius: 16px;" :dark="$q.dark.isActive">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold">Demander un retrait</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <div class="q-pa-md bg-green-1 text-green-9 text-caption rounded-borders q-mb-md flex items-start no-wrap" :style="$q.dark.isActive ? { background: 'rgba(76, 175, 80, 0.15)', color: '#81C784' } : {}">
            <q-icon name="info" class="q-mr-xs q-mt-xs" size="sm" />
            <div>
              Les frais de service de {{ poolFees }}% ont déjà été déduits lors des contributions. Aucun frais supplémentaire ne sera prélevé sur ce retrait.
            </div>
          </div>

          <div class="q-gutter-md">
            <q-input
              outlined
              v-model.number="withdrawForm.amount"
              type="number"
              label="Montant à retirer"
              suffix="€"
              :rules="[
                val => val > 0 || 'Le montant doit être supérieur à 0',
                val => val <= pool.availableBalance || 'Le montant dépasse le solde disponible (' + pool.availableBalance + ' €)'
              ]"
              color="secondary"
              autofocus
            />

            <q-input
              outlined
              v-model="withdrawForm.accountHolderName"
              label="Titulaire du compte"
              placeholder="Ex: Jean Dupont"
              color="secondary"
              :rules="[val => !!val || 'Le titulaire du compte est obligatoire']"
            />

            <q-input
              outlined
              v-model="withdrawForm.iban"
              label="IBAN"
              placeholder="FR76 3000..."
              color="secondary"
              :rules="[
                val => !!val || 'L\'IBAN est obligatoire',
                val => val.length >= 14 || 'Format IBAN invalide'
              ]"
            />

            <q-input
              outlined
              v-model="withdrawForm.bankName"
              label="Nom de la banque"
              placeholder="Ex: Société Générale"
              color="secondary"
            />

            <!-- Résumé des frais -->
            <div class="q-pa-md rounded-borders" :style="{ background: $q.dark.isActive ? 'rgba(255,255,255,0.05)' : '#F5F5F5' }">
              <div class="row justify-between q-mb-xs">
                <span>Montant demandé :</span>
                <span class="text-weight-bold">{{ withdrawForm.amount || 0 }} €</span>
              </div>
              <div class="row justify-between text-grey-7 q-mb-xs">
                <span>Frais de service (déduits au dépôt) :</span>
                <span>0.00 €</span>
              </div>
              <q-separator class="q-my-sm" />
              <div class="row justify-between text-h6 text-weight-bolder text-primary">
                <span>Montant transféré net :</span>
                <span>{{ netAmount }} €</span>
              </div>
            </div>

            <div class="row justify-end q-mt-lg">
              <q-btn label="Annuler" flat v-close-popup class="q-mr-sm" />
              <q-btn 
                label="Confirmer le retrait" 
                color="positive" 
                unelevated 
                :loading="withdrawing"
                @click="submitWithdrawal"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Dialogue de Partage et d'Invitation -->
    <q-dialog v-model="shareDialog" persistent>
      <q-card style="min-width: 450px; max-width: 90vw; border-radius: 16px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6 text-weight-bold">
            <q-icon name="share" color="primary" class="q-mr-sm" size="sm" />
            {{ isOwner ? 'Partager & Inviter' : 'Partager la cagnotte' }}
          </div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-md">
          <!-- Lien de Partage -->
          <div class="text-subtitle2 q-mb-sm text-grey-7">Lien de la cagnotte :</div>
          <div class="row items-center q-col-gutter-sm q-mb-lg">
            <div class="col">
              <q-input
                outlined
                dense
                readonly
                v-model="shareUrl"
                bg-color="grey-1"
                style="border-radius: 8px;"
              />
            </div>
            <div class="col-auto">
              <q-btn
                color="primary"
                icon="content_copy"
                unelevated
                style="border-radius: 8px; height: 40px;"
                @click="copyShareUrl"
              >
                <q-tooltip>Copier le lien</q-tooltip>
              </q-btn>
            </div>
          </div>

          <!-- Boutons Réseaux Sociaux -->
          <div class="text-subtitle2 q-mb-sm text-grey-7">Partager sur :</div>
          <div class="row q-col-gutter-md justify-center q-mb-lg">
            <div class="col-4 text-center">
              <q-btn
                flat
                round
                dense
                color="indigo-7"
                icon="M22,12c0-5.52-4.48-10-10-10S2,6.48,2,12c0,4.84,3.44,8.87,8,9.8V15H8v-3h2V9.5C10,7.57,11.57,6,13.5,6H16v3h-2c-0.55,0-1,0.45-1,1v2h3v3h-3v6.8C18.56,20.87,22,16.84,22,12z"
                size="lg"
                @click="shareOnFacebook"
              />
              <div class="text-caption text-grey-7 q-mt-xs">Facebook</div>
            </div>
            <div class="col-4 text-center">
              <q-btn
                flat
                round
                dense
                color="black"
                icon="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" 
                size="lg"
                @click="shareOnTwitter"
              >
                <q-tooltip>Twitter / X</q-tooltip>
              </q-btn>
              <div class="text-caption text-grey-7 q-mt-xs">Twitter / X</div>
            </div>
            <div class="col-4 text-center">
              <q-btn
                flat
                round
                dense
                color="green-6"
                icon="M12.004 2C6.48 2 2.014 6.47 2.014 12c0 1.91.53 3.69 1.46 5.25L2 22l5.05-1.33c1.5.83 3.2 1.33 4.95 1.33 5.52 0 10-4.48 10-10S17.524 2 12.004 2zm5.72 13.04c-.25.7-1.45 1.37-2 1.41-.55.05-1.25.07-3.75-1-3.21-1.37-5.25-4.63-5.41-4.85-.16-.22-1.3-1.72-1.3-3.28 0-1.56.81-2.33 1.1-2.63.3-.3.65-.37.85-.37.2 0 .4 0 .58.01.2 0 .47-.08.73.55.26.63.88 2.16.96 2.3.08.15.13.33.03.53-.1.2-.15.33-.3.5-.15.17-.32.39-.46.52-.16.15-.33.32-.14.65.18.33.82 1.37 1.77 2.22.95.85 1.75 1.11 2 1.24.25.13.4.1.55-.07.15-.17.65-.75.82-1 .17-.25.33-.2.56-.11.23.09 1.45.68 1.7.81.25.13.42.2.48.3.06.1.06.58-.19 1.28z"
                size="lg"
                @click="shareOnWhatsApp"
              />
              <div class="text-caption text-grey-7 q-mt-xs">WhatsApp</div>
            </div>
          </div>

          <!-- Section Invitation (Seulement pour le propriétaire) -->
          <div v-if="isOwner">
            <q-separator class="q-my-md" />
            
            <div class="text-subtitle2 q-mb-sm text-weight-bold text-dark row items-center">
              <q-icon name="person_add" color="secondary" class="q-mr-xs" size="xs" />
              Inviter des participants par email :
            </div>
            
            <div class="row q-col-gutter-sm items-center q-mb-md">
              <div class="col">
                <q-input
                  v-model="inviteEmail"
                  label="Email de l'invité"
                  dense
                  outlined
                  color="primary"
                  bg-color="grey-1"
                  style="border-radius: 8px;"
                  @keyup.enter="sendInvitation"
                />
              </div>
              <div class="col-auto">
                <q-btn
                  label="Inviter"
                  color="primary"
                  unelevated
                  style="border-radius: 8px; height: 40px;"
                  @click="sendInvitation"
                  :loading="sendingInvite"
                />
              </div>
            </div>

            <!-- Liste des invités existants -->
            <div v-if="invitations.length > 0" class="q-mt-sm">
              <div class="text-caption text-grey-7 q-mb-sm">Membres déjà invités ({{ invitations.length }}) :</div>
              <div class="row q-gutter-xs" style="max-height: 120px; overflow-y: auto;">
                <q-chip
                  v-for="inv in invitations"
                  :key="inv.id"
                  outline
                  dense
                  color="primary"
                  size="sm"
                  removable
                  @remove="removeInvitation(inv.id)"
                >
                  {{ inv.email }}
                  <q-tooltip>Supprimer l'invitation</q-tooltip>
                </q-chip>
              </div>
            </div>
          </div>

        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Dialogue de Publication d'Actualité -->
    <q-dialog v-model="publishUpdateDialog" persistent transition-show="scale" transition-hide="scale">
      <q-card style="min-width: 550px; max-width: 90vw; border-radius: 24px; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);" :dark="$q.dark.isActive">
        <q-card-section class="row items-center q-pb-none q-pa-lg">
          <div class="row items-center">
            <q-avatar color="primary-light" text-color="primary" size="40px" class="q-mr-md" style="background: rgba(0, 230, 118, 0.1); color: #00e676;">
              <q-icon name="campaign" size="24px" />
            </q-avatar>
            <div>
              <div class="text-h6 text-weight-bolder text-dark" style="font-family: 'Outfit', sans-serif;">Publier une actualité</div>
              <div class="text-caption text-grey-5">Timeline de confiance & notification</div>
            </div>
          </div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup color="grey-6" />
        </q-card-section>

        <q-card-section class="q-px-lg q-pb-lg">
          <q-separator class="q-mb-md" style="opacity: 0.5;" />
          
          <div class="text-caption text-grey-6 q-mb-lg">
            Partagez avec vos contributeurs les dernières nouvelles (achat de matériel, photos d'avancement, etc.). Ils recevront immédiatement une notification par email/SSE.
          </div>
          
          <div class="q-gutter-y-md">
            <!-- Champ Titre -->
            <div>
              <div class="text-subtitle2 text-weight-bold text-dark q-mb-xs">Titre de l'actualité *</div>
              <q-input
                outlined
                v-model="updateForm.title"
                placeholder="Ex: Le fauteuil roulant a été commandé !"
                color="secondary"
                dense
                style="border-radius: 12px;"
                :rules="[val => !!val || 'Le titre est obligatoire']"
                hide-bottom-space
              >
                <template v-slot:prepend>
                  <q-icon name="title" color="grey-5" />
                </template>
              </q-input>
            </div>

            <!-- Champ Contenu -->
            <div>
              <div class="text-subtitle2 text-weight-bold text-dark q-mb-xs">Contenu de la mise à jour *</div>
              <q-input
                outlined
                v-model="updateForm.content"
                type="textarea"
                placeholder="Décrivez en détail ce qui s'est passé, les étapes franchies..."
                color="secondary"
                rows="4"
                style="border-radius: 12px;"
                :rules="[val => !!val || 'Le contenu est obligatoire']"
                hide-bottom-space
              >
                <template v-slot:prepend>
                  <q-icon name="edit_note" color="grey-5" class="q-mt-sm" />
                </template>
              </q-input>
            </div>

            <!-- Uploader d'Image Cloudinary -->
            <div>
              <div class="text-subtitle2 text-weight-bold text-dark q-mb-xs">Image d'illustration (optionnel)</div>
              <div 
                class="upload-box flex flex-center cursor-pointer relative-position text-center q-pa-md" 
                style="border: 2px dashed #CBD5E1; border-radius: 16px; height: 180px; overflow: hidden; background: #F8FAFC; transition: all 0.3s;"
                @click="triggerImageUpload"
                v-ripple
              >
                <template v-if="uploadingImage">
                  <div class="column items-center">
                    <q-spinner-oval color="primary" size="40px" />
                    <div class="text-caption text-grey-6 q-mt-sm">Téléchargement vers Cloudinary...</div>
                  </div>
                </template>
                <template v-else-if="updateForm.imageUrl">
                  <q-img :src="updateForm.imageUrl" height="100%" width="100%" fit="cover" />
                  <div class="absolute-top-right q-pa-sm">
                    <q-btn round color="negative" icon="delete" size="sm" @click.stop="removeUploadedImage" />
                  </div>
                </template>
                <template v-else>
                  <div class="column items-center">
                    <q-icon name="add_a_photo" size="36px" color="primary" class="q-mb-sm" />
                    <div class="text-subtitle2 text-weight-bold text-dark">Cliquez pour importer une photo</div>
                    <div class="text-caption text-grey-5">Elle sera directement sauvegardée sur Cloudinary</div>
                  </div>
                </template>
              </div>
              <input type="file" ref="imageFileInput" class="hidden" accept="image/*" @change="onImageSelected" />
            </div>

            <!-- Champ Vidéo -->
            <div>
              <div class="text-subtitle2 text-weight-bold text-dark q-mb-xs">URL d'une vidéo YouTube/Vimeo (optionnel)</div>
              <q-input
                outlined
                v-model="updateForm.videoUrl"
                placeholder="https://www.youtube.com/watch?v=..."
                color="secondary"
                dense
                style="border-radius: 12px;"
              >
                <template v-slot:prepend>
                  <q-icon name="play_circle_outline" color="grey-5" />
                </template>
              </q-input>
            </div>

            <!-- Boutons d'action -->
            <div class="row justify-end q-mt-xl q-col-gutter-sm">
              <div class="col-auto">
                <q-btn 
                  label="Annuler" 
                  flat 
                  v-close-popup 
                  color="grey-7" 
                  no-caps
                  class="q-px-lg text-weight-bold"
                  style="border-radius: 12px;" 
                />
              </div>
              <div class="col-auto">
                <q-btn 
                  label="Publier l'actualité" 
                  color="positive" 
                  unelevated 
                  :loading="publishingUpdate"
                  @click="submitPublishUpdate"
                  no-caps
                  class="q-px-xl text-weight-bold"
                  style="border-radius: 12px;"
                />
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'
import { poolApi } from 'boot/axios'
import { useQuasar } from 'quasar'
import authStore from 'src/shared/stores/auth'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const pool = ref({})
const loading = ref(true)
const error = ref(null)
const contributions = ref([])
const messages = ref([])
const invitations = ref([])
const inviteEmail = ref('')
const sendingInvite = ref(false)
const mediaSlide = ref('image')

const updates = ref([])
const publishUpdateDialog = ref(false)
const publishingUpdate = ref(false)
const imageFileInput = ref(null)
const uploadingImage = ref(false)
const updateForm = reactive({
  title: '',
  content: '',
  imageUrl: '',
  videoUrl: ''
})

const withdrawDialog = ref(false)
const shareDialog = ref(false)
const shareUrl = computed(() => window.location.href)

const openShareDialog = () => {
  shareDialog.value = true
}

const copyShareUrl = () => {
  navigator.clipboard.writeText(shareUrl.value)
  $q.notify({
    type: 'positive',
    message: 'Lien copié dans le presse-papiers !',
    position: 'top',
    timeout: 2000
  })
}

const shareOnFacebook = () => {
  const url = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(shareUrl.value)}`
  window.open(url, '_blank', 'width=600,height=400')
}

const shareOnTwitter = () => {
  const text = `Soutenez cette cagnotte sur Potify : ${pool.value.title}`
  const url = `https://twitter.com/intent/tweet?url=${encodeURIComponent(shareUrl.value)}&text=${encodeURIComponent(text)}`
  window.open(url, '_blank', 'width=600,height=400')
}

const shareOnWhatsApp = () => {
  const text = `Soutenez cette cagnotte sur Potify : ${pool.value.title} - ${shareUrl.value}`
  const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`
  window.open(url, '_blank')
}
const withdrawing = ref(false)
const withdrawForm = reactive({
  amount: 0,
  iban: '',
  accountHolderName: '',
  bankName: ''
})
let messageEventSource = null

const isOwner = computed(() => {
  return authStore.isAuthenticated.value && pool.value.ownerId && authStore.user.value?.id && String(pool.value.ownerId) === String(authStore.user.value?.id)
})

const isAdmin = computed(() => {
  return authStore.isAuthenticated.value && (authStore.user.value?.roles?.includes('ADMIN') || authStore.user.value?.role === 'ADMIN')
})

const poolFees = computed(() => {
  return pool.value && pool.value.fees !== undefined && pool.value.fees !== null
    ? pool.value.fees
    : 2
})

const youtubeId = computed(() => {
  if (!pool.value.videoUrl) return null
  const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/
  const match = pool.value.videoUrl.match(regExp)
  return (match && match[2].length === 11) ? match[2] : null
})

const isDirectVideo = computed(() => {
  return pool.value.videoUrl && !youtubeId.value
})

// Logique pour les phases et sous-cagnottes
const selectedSubPoolId = ref(null)

const subPoolOptions = computed(() => {
  if (!pool.value.subPools) return []
  return pool.value.subPools.map(sp => ({
    label: `${sp.title} (${sp.currentAmount || 0} € / ${sp.goalAmount} €)`,
    value: sp.id
  }))
})

const contributeToSubPool = (subPoolId) => {
  selectedSubPoolId.value = subPoolId
  contributionDialog.value = true
}

const formatDateWithTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getPhaseStatusClass = (phase) => {
  if (phase.status === 'COMPLETED') return 'bg-green text-white'
  if (phase.status === 'ACTIVE') return 'bg-secondary text-white'
  return 'bg-grey-3 text-grey-7'
}

// Gestion des commentaires (Pool Messages)
const newComment = ref('')
const sendingComment = ref(false)

const availableEmojis = [
  { type: 'LIKE', icon: '👍' },
  { type: 'HEART', icon: '❤️' },
  { type: 'LAUGH', icon: '😂' },
  { type: 'SURPRISE', icon: '😮' },
  { type: 'SAD', icon: '😢' },
  { type: 'PRAY', icon: '🙏' }
]

const getEmojiIcon = (type) => {
  const emoji = availableEmojis.find(e => e.type === type)
  return emoji ? emoji.icon : '👍'
}

const groupReactions = (reactions) => {
  if (!reactions) return {}
  return reactions.reduce((groups, r) => {
    const type = r.reactionType
    if (!groups[type]) groups[type] = []
    groups[type].push(r)
    return groups
  }, {})
}

const hasUserReacted = (reactions, type) => {
  if (!reactions || !authStore.isAuthenticated.value) return false
  return reactions.some(r => r.userId === authStore.user.value?.id && r.reactionType === type)
}

const toggleReaction = async (messageId, type) => {
  if (!authStore.isAuthenticated.value) return
  
  try {
    await poolApi.post(`/messages/${messageId}/react`, null, {
      params: {
        userId: authStore.user.value.id,
        type: type
      }
    })
    await fetchMessages() // Rafraîchir pour voir la réaction
  } catch (err) {
    console.error('Erreur reaction:', err)
  }
}

const postComment = async () => {
  if (!newComment.value.trim()) return
  
  sendingComment.value = true
  try {
    await poolApi.post('/messages', {
      poolId: pool.value.id,
      userId: authStore.user.value?.id,
      content: newComment.value,
      isPublic: true // Tous les messages sont désormais publics
    })
    
    newComment.value = ''
    $q.notify({
      type: 'positive',
      message: 'Commentaire publié !',
      position: 'bottom'
    })
    await fetchMessages()
  } catch (err) {
    console.error('Erreur publication commentaire:', err)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors de la publication.'
    })
  } finally {
    sendingComment.value = false
  }
}

const deleteComment = async (messageId) => {
  $q.dialog({
    title: 'Confirmer la suppression',
    message: 'Voulez-vous vraiment supprimer ce commentaire ? Cette action est irréversible.',
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
      await poolApi.delete(`/messages/${messageId}`)
      $q.notify({
        type: 'positive',
        message: 'Le commentaire a été supprimé par modération.'
      })
      await fetchMessages()
    } catch (err) {
      console.error('Erreur suppression commentaire:', err)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors de la suppression du commentaire.'
      })
    }
  })
}

const reportComment = (message) => {
  $q.dialog({
    title: 'Signaler ce commentaire',
    message: 'Veuillez saisir le motif du signalement (ex: propos injurieux, spam, etc.) :',
    prompt: {
      model: '',
      type: 'text',
      required: true,
      isValid: val => val.trim().length > 0
    },
    cancel: {
      label: 'Annuler',
      flat: true
    },
    ok: {
      label: 'Signaler',
      color: 'warning',
      unelevated: true
    },
    persistent: true
  }).onOk(async (reason) => {
    try {
      await poolApi.post(`/messages/${message.id}/report`, null, {
        params: { 
          userId: authStore.user.value?.id,
          reason 
        }
      })
      $q.notify({
        type: 'positive',
        message: 'Le commentaire a été signalé avec succès.'
      })
    } catch (err) {
      console.error('Erreur lors du signalement:', err)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors du signalement.'
      })
    }
  })
}

const reportPool = () => {
  if (!authStore.isAuthenticated.value) {
    $q.notify({
      type: 'warning',
      message: 'Vous devez être connecté pour signaler une cagnotte.'
    })
    return
  }
  $q.dialog({
    title: 'Signaler cette cagnotte',
    message: 'Veuillez saisir le motif du signalement (ex: contenu frauduleux, usurpation d\'identité, etc.) :',
    prompt: {
      model: '',
      type: 'text',
      required: true,
      isValid: val => val.trim().length > 0
    },
    cancel: {
      label: 'Annuler',
      flat: true
    },
    ok: {
      label: 'Signaler',
      color: 'negative',
      unelevated: true
    },
    persistent: true
  }).onOk(async (reason) => {
    try {
      await poolApi.post(`/pools/${pool.value.id}/report`, null, {
        params: { 
          userId: authStore.user.value?.id,
          reason 
        }
      })
      $q.notify({
        type: 'positive',
        message: 'La cagnotte a été signalée avec succès.'
      })
    } catch (err) {
      console.error('Erreur lors du signalement de la cagnotte:', err)
      $q.notify({
        type: 'negative',
        message: 'Erreur lors du signalement.'
      })
    }
  })
}

// Gestion des contributions
const contributionDialog = ref(false)
const submitting = ref(false)
const selectedMethod = ref(null)
const contributionForm = reactive({
  amount: 20,
  contributorName: '',
  contributorEmail: '',
  message: '',
  anonymous: false
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

const fetchContributions = async () => {
  try {
    const id = route.params.id
    const response = await poolService.getPoolContributions(id)
    contributions.value = response.data
  } catch (err) {
    console.error('Erreur chargement contributions:', err)
  }
}

const fetchMessages = async () => {
  try {
    const id = route.params.id
    const response = await poolApi.get(`/messages/pool/${id}`)
    messages.value = response.data
  } catch (err) {
    console.error('Erreur chargement messages:', err)
  }
}

const setupMessageSSE = () => {
  if (messageEventSource) {
    messageEventSource.close()
  }

  const url = `http://localhost:8082/api/messages/pool/${route.params.id}/stream`
  messageEventSource = new EventSource(url)

  messageEventSource.addEventListener('message', (event) => {
    try {
      const newMessage = JSON.parse(event.data)
      const index = messages.value.findIndex(m => m.id === newMessage.id)
      if (index !== -1) {
        // Mise a jour (ex: nouvelle reaction)
        messages.value[index] = newMessage
      } else {
        // Nouveau message : Ajouter au debut de la liste
        messages.value.unshift(newMessage)
      }
    } catch (e) {
      console.error('Error parsing SSE message:', e)
    }
  })

  messageEventSource.onerror = (err) => {
    console.error('SSE Connection Error:', err)
    // Tentative de reconnexion automatique par le navigateur par defaut, 
    // mais on log l'erreur.
  }
}

const fetchPool = async () => {
  loading.value = true
  try {
    const response = await poolApi.get(`/pools/${route.params.id}`, {
      params: {
        userId: authStore.user.value?.id,
        email: authStore.user.value?.email
      }
    })
    pool.value = response.data
    if (!pool.value.imageUrl && pool.value.videoUrl) {
      mediaSlide.value = 'video'
    }
    await fetchMessages()
    await fetchContributions()
    if (isOwner.value) {
      await fetchInvitations()
    }
  } catch (err) {
    console.error('Erreur chargement cagnotte:', err)
    if (err.response?.status === 403) {
      $q.notify({
        type: 'negative',
        message: 'Accès refusé : Cette cagnotte est privée.'
      })
      router.push('/pools')
    }
    error.value = 'Cagnotte introuvable.'
  } finally {
    loading.value = false
  }
}

const fetchInvitations = async () => {
  try {
    const response = await poolApi.get(`/invitations/pool/${route.params.id}`)
    invitations.value = response.data
  } catch (err) {
    console.error('Erreur invitations:', err)
  }
}

const removeInvitation = async (invitationId) => {
  $q.dialog({
    title: 'Confirmer',
    message: 'Voulez-vous vraiment supprimer cette invitation ?',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await poolApi.delete(`/invitations/${invitationId}`)
      $q.notify({ type: 'positive', message: 'Invitation supprimée' })
      await fetchInvitations()
    } catch (err) {
      console.error('Erreur suppression invitation:', err)
      $q.notify({ type: 'negative', message: 'Erreur lors de la suppression' })
    }
  })
}

const sendInvitation = async () => {
  if (!inviteEmail.value || !inviteEmail.value.includes('@')) {
    $q.notify({ type: 'warning', message: 'Veuillez saisir un email valide.' })
    return
  }

  if (inviteEmail.value.toLowerCase() === authStore.user.value?.email.toLowerCase()) {
    $q.notify({ type: 'warning', message: 'Vous ne pouvez pas vous inviter vous-même.' })
    return
  }
  
  sendingInvite.value = true
  try {
    await poolApi.post(`/invitations/pool/${pool.value.id}`, null, {
      params: { email: inviteEmail.value }
    })
    $q.notify({ type: 'positive', message: `Invitation envoyée à ${inviteEmail.value}` })
    inviteEmail.value = ''
    await fetchInvitations()
  } catch (err) {
    console.error('Erreur envoi invitation:', err)
    const errorMsg = err.response?.data?.message || 'Une erreur est survenue lors de l\'envoi de l\'invitation.'
    $q.notify({ type: 'negative', message: errorMsg })
  } finally {
    sendingInvite.value = false
  }
}

const contribute = () => {
  if (pool.value.subPools && pool.value.subPools.length > 0) {
    if (!selectedSubPoolId.value) {
      const firstActive = pool.value.subPools.find(sp => sp.status !== 'COMPLETED')
      selectedSubPoolId.value = firstActive ? firstActive.id : pool.value.subPools[0].id
    }
  }
  contributionDialog.value = true
}

const submitPayment = async (method) => {
  if (!contributionForm.amount || contributionForm.amount <= 0) {
    $q.notify({
      type: 'warning',
      message: 'Le montant doit être supérieur à 0 €'
    })
    return
  }

  if (!authStore.isAuthenticated.value && (!contributionForm.contributorEmail || !contributionForm.contributorEmail.includes('@'))) {
    $q.notify({
      type: 'warning',
      message: 'Veuillez saisir une adresse email valide'
    })
    return
  }

  selectedMethod.value = method
  submitting.value = true
  try {
    const isAuth = authStore.isAuthenticated.value
    const user = authStore.user.value

    let name = contributionForm.contributorName || t('poolDetail.anonymousDonor')
    if (contributionForm.anonymous) {
      name = 'Anonyme'
    } else if (isAuth && user) {
      name = user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Utilisateur'
    }

    const payload = {
      poolId: selectedSubPoolId.value || pool.value.id,
      userId: user?.id || null,
      contributorEmail: user?.email || contributionForm.contributorEmail || 'anonyme@potify.com',
      amount: contributionForm.amount,
      contributorName: name,
      message: contributionForm.message,
      anonymous: contributionForm.anonymous
    }

    let response
    if (method === 'stripe') {
      response = await poolService.initiateStripeCheckout(payload)
    } else {
      response = await poolService.initiatePayPalCheckout(payload)
    }

    if (response.data && response.data.checkoutUrl) {
      window.location.href = response.data.checkoutUrl
    } else {
      throw new Error("L'URL de redirection de paiement n'a pas pu être générée.")
    }
  } catch (err) {
    console.error('Erreur initiation paiement:', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || 'Erreur lors de l\'initiation du paiement.'
    })
  } finally {
    submitting.value = false
    selectedMethod.value = null
  }
}

const openWithdrawDialog = () => {
  withdrawForm.amount = pool.value.availableBalance || 0
  withdrawForm.iban = ''
  withdrawForm.accountHolderName = authStore.user.value?.firstName 
    ? `${authStore.user.value.firstName} ${authStore.user.value.lastName || ''}`.trim()
    : 'Titulaire'
  withdrawForm.bankName = ''
  withdrawDialog.value = true
}

const netAmount = computed(() => {
  if (!withdrawForm.amount || isNaN(withdrawForm.amount)) return '0.00'
  return withdrawForm.amount.toFixed(2)
})

const submitWithdrawal = async () => {
  if (withdrawForm.amount <= 0 || withdrawForm.amount > pool.value.availableBalance) {
    $q.notify({
      type: 'negative',
      message: 'Le montant du retrait est invalide.'
    })
    return
  }
  if (!withdrawForm.iban || !withdrawForm.accountHolderName) {
    $q.notify({
      type: 'negative',
      message: "Veuillez renseigner l'IBAN et le titulaire du compte."
    })
    return
  }
  
  withdrawing.value = true
  try {
    const payload = {
      poolId: pool.value.id,
      userId: authStore.user.value?.id,
      amount: withdrawForm.amount,
      iban: withdrawForm.iban,
      accountHolderName: withdrawForm.accountHolderName,
      bankName: withdrawForm.bankName
    }
    await poolService.withdrawFunds(payload)
    
    $q.notify({
      type: 'positive',
      message: 'Votre demande de retrait a été traitée avec succès !'
    })
    withdrawDialog.value = false
    
    // Refresh pool details to update balances
    await fetchPool()
  } catch (err) {
    console.error('Erreur lors du retrait:', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || 'Erreur lors du traitement du retrait.'
    })
  } finally {
    withdrawing.value = false
  }
}

const triggerImageUpload = () => {
  if (imageFileInput.value) {
    imageFileInput.value.click()
  }
}

const onImageSelected = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  uploadingImage.value = true
  try {
    const response = await poolService.uploadImage(file)
    updateForm.imageUrl = response.data.url
    $q.notify({
      type: 'positive',
      message: 'Photo importée avec succès !',
      position: 'bottom',
      timeout: 2000
    })
  } catch (error) {
    console.error('Erreur upload actualité:', error)
    $q.notify({
      type: 'negative',
      message: 'Erreur lors du téléchargement de la photo.',
      position: 'bottom'
    })
  } finally {
    uploadingImage.value = false
  }
}

const removeUploadedImage = () => {
  updateForm.imageUrl = ''
  if (imageFileInput.value) {
    imageFileInput.value.value = ''
  }
}

const openPublishUpdateDialog = () => {
  updateForm.title = ''
  updateForm.content = ''
  updateForm.imageUrl = ''
  updateForm.videoUrl = ''
  publishUpdateDialog.value = true
}

const fetchUpdates = async () => {
  try {
    const id = route.params.id
    const response = await poolService.getPoolUpdates(id)
    updates.value = response.data
  } catch (err) {
    console.error('Erreur chargement updates:', err)
  }
}

const submitPublishUpdate = async () => {
  if (!updateForm.title.trim() || !updateForm.content.trim()) {
    $q.notify({
      type: 'warning',
      message: 'Le titre et le contenu sont obligatoires.'
    })
    return
  }

  publishingUpdate.value = true
  try {
    const id = route.params.id
    await poolService.createPoolUpdate(id, {
      title: updateForm.title,
      content: updateForm.content,
      imageUrl: updateForm.imageUrl || null,
      videoUrl: updateForm.videoUrl || null
    })
    
    $q.notify({
      type: 'positive',
      message: 'Actualité publiée avec succès et envoyée aux contributeurs !'
    })
    
    publishUpdateDialog.value = false
    await fetchUpdates()
  } catch (err) {
    console.error('Erreur publication actualité:', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || 'Erreur lors de la publication de l\'actualité.'
    })
  } finally {
    publishingUpdate.value = false
  }
}

onMounted(async () => {
  await fetchPool()
  await fetchUpdates()
  setupMessageSSE()
})

onUnmounted(() => {
  if (messageEventSource) {
    messageEventSource.close()
  }
})
</script>

<style scoped>
.category-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  background: #062f25;
  color: #00e676;
  padding: 8px 18px;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.7px;
  z-index: 10;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  border: 1.5px solid rgba(0, 230, 118, 0.2);
}

.pool-title {
  color: #0F172A;
  font-family: 'Outfit', 'Inter', sans-serif;
}
body.body--dark .pool-title {
  color: #FFFFFF;
}

.custom-card {
  border-radius: 20px;
  background: #ffffff;
  border: 1px solid #E2E8F0;
  transition: all 0.3s ease;
}
body.body--dark .custom-card {
  background: #121F32;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.profile-row {
  border-bottom: 1px solid #F1F5F9;
}
body.body--dark .profile-row {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.stats-row {
  border-bottom: 1px solid #F1F5F9;
  border-top: 1px solid #F1F5F9;
}
body.body--dark .stats-row {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.share-btn {
  border: 1.5px solid #E2E8F0;
  background: #ffffff;
  color: #475569;
  transition: all 0.2s ease;
}
.share-btn:hover {
  background: #F8FAFC;
  border-color: #CBD5E1;
}
body.body--dark .share-btn {
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  background: #121F32;
  color: #E2E8F0;
}
body.body--dark .share-btn:hover {
  background: #1A2E4C;
  border-color: rgba(255, 255, 255, 0.25);
}

.comment-item {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  transition: all 0.3s ease;
}
body.body--dark .comment-item {
  background: #0D1B2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.reaction-badge {
  background: #F1F4F9;
  border-radius: 12px;
  border: 1px solid #E0E0E0;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
  color: #475569;
}
.reaction-badge:hover {
  background: #E8EAF6;
  border-color: #3F51B5;
}
body.body--dark .reaction-badge {
  background: #162540;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #94A3B8;
}
body.body--dark .reaction-badge:hover {
  background: #1F3354;
  border-color: #00e676;
}

.user-reacted {
  background: #E3F2FD !important;
  border-color: #2196F3 !important;
  color: #1976D2 !important;
}
body.body--dark .user-reacted {
  background: rgba(33, 150, 243, 0.15) !important;
  border-color: #2196F3 !important;
  color: #64B5F6 !important;
}

.emoji-btn {
  font-size: 1.2rem;
  transition: transform 0.2s;
}
.emoji-btn:hover {
  transform: scale(1.3);
}
.reaction-menu {
  border-radius: 30px;
  overflow: hidden;
}

.phase-number-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}
.phase-item {
  position: relative;
}
.phase-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 16px;
  top: 32px;
  bottom: -20px;
  width: 2px;
  background: #CBD5E1;
  z-index: 0;
}
body.body--dark .phase-item:not(:last-child)::after {
  background: rgba(255, 255, 255, 0.08);
}

@media (min-width: 1024px) {
  .sticky-sidebar-desktop {
    position: sticky;
    top: 90px;
    height: fit-content;
    z-index: 10;
  }
}

.financial-box {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  transition: all 0.3s ease;
}
body.body--dark .financial-box {
  background: #1A2536;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.border-top-divider {
  border-top: 1px solid #E2E8F0;
}
body.body--dark .border-top-divider {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.update-timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 20px;
  top: 40px;
  bottom: -32px;
  width: 2px;
  background: #E2E8F0;
  z-index: 0;
}
body.body--dark .update-timeline-item:not(:last-child)::after {
  background: rgba(255, 255, 255, 0.08);
}

.upload-box:hover {
  border-color: #00e676 !important;
  background: #F1FDF5 !important;
}
body.body--dark .upload-box {
  background: #0D1B2E !important;
  border-color: rgba(255, 255, 255, 0.15) !important;
}
body.body--dark .upload-box:hover {
  border-color: #00e676 !important;
  background: rgba(0, 230, 118, 0.05) !important;
}
</style>
