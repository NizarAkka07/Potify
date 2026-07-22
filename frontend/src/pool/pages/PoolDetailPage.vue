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
                <q-icon :name="poolLiked ? 'favorite' : 'favorite_border'" :color="poolLiked ? 'red-6' : 'grey-5'" size="18px" class="q-mr-xs" />
                <span>{{ poolLikesCount }} likes</span>
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
                :outline="!poolLiked" 
                :unelevated="poolLiked"
                :icon="poolLiked ? 'favorite' : 'favorite_border'" 
                :color="poolLiked ? 'red-6' : 'grey-8'" 
                :text-color="poolLiked ? 'white' : 'grey-9'"
                class="q-py-md share-btn" 
                :class="{ 'heart-pop': poolLiked }"
                style="border-radius: 24px; min-width: 50px;" 
                @click="togglePoolLike"
              >
                <q-tooltip>
                  {{ poolLiked ? 'Vous aimez cette cagnotte' : 'J\'aime cette cagnotte' }}
                </q-tooltip>
              </q-btn>
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

        <!-- RIGHT COLUMN (Seamless Open Flow - No Heavy Box Cards) -->
        <div class="col-12 col-md-7 right-content-column q-pl-md-lg">
          
          <!-- Pool Title & Meta Info -->
          <div class="q-mb-xl">
            <h1 class="text-weight-bolder text-dark q-mt-none q-mb-sm pool-main-title">
              {{ pool.title }}
            </h1>

            <div class="row items-center q-gutter-sm text-caption q-mt-sm">
              <div class="meta-pill-chip">
                <q-icon name="today" size="14px" class="q-mr-xs text-primary" />
                <span>Créée le {{ formatDate(pool.createdAt) }}</span>
              </div>
              <div v-if="pool.category" class="meta-pill-chip">
                <q-icon name="folder" size="14px" class="q-mr-xs text-primary" />
                <span>{{ pool.category }}</span>
              </div>
              <div class="meta-pill-chip">
                <q-icon name="public" size="14px" class="q-mr-xs text-primary" />
                <span>{{ $t('poolDetail.visibilityPublic') || 'Publique' }}</span>
              </div>
            </div>
          </div>

          <!-- SECTION 1: ABOUT / STORY (Seamless Flow) -->
          <div class="story-section q-mb-2xl">
            <div class="row items-center justify-between q-mb-md">
              <div class="row items-center">
                <div class="section-icon-dot bg-primary q-mr-sm"></div>
                <h2 class="section-heading-title text-dark q-ma-none">
                  {{ $t('poolDetail.aboutPool') }}
                </h2>
              </div>
              <div class="row q-gutter-xs">
                <q-btn 
                  v-if="isOwner || isAdmin" 
                  :label="$t('poolDetail.edit')" 
                  icon="edit" 
                  color="warning" 
                  flat 
                  no-caps 
                  class="action-link-btn"
                  @click="router.push(`/pools/${pool.id}/edit`)"
                />
                <q-btn 
                  v-if="isOwner" 
                  :label="$t('poolDetail.addPhase')" 
                  icon="add_circle" 
                  color="primary" 
                  unelevated 
                  no-caps 
                  class="action-pill-btn"
                  @click="router.push({ path: '/pools/create', query: { parentId: pool.id } })"
                />
              </div>
            </div>
            
            <div class="open-story-text text-body1 text-grey-9 q-mb-lg">
              {{ pool.description }}
            </div>
          </div>

          <q-separator class="section-divider q-my-xl" />

          <!-- SECTION 2: ACTUALITES (Horizontal Green Cards like CotizUp) -->
          <div class="updates-section q-mb-2xl">
            <div class="row items-center justify-between q-mb-lg">
              <div class="row items-center">
                <h2 class="section-heading-title text-dark q-ma-none">
                  Actualités ({{ updates.length }})
                </h2>
              </div>
              <q-btn 
                v-if="isOwner" 
                label="Publier une actualité" 
                icon="add_alert" 
                color="positive" 
                unelevated 
                no-caps 
                class="action-pill-btn"
                @click="openPublishUpdateDialog"
              />
            </div>

            <!-- Horizontal Scrollable Row of Green Cards -->
            <div v-if="updates.length > 0" class="row q-gutter-md no-wrap overflow-auto q-pb-md updates-scroll-container">
              <div 
                v-for="update in updates" 
                :key="update.id" 
                class="update-green-card column justify-between cursor-pointer q-pa-lg shadow-4"
                style="background: linear-gradient(145deg, #10B981 0%, #059669 100%) !important; color: #FFFFFF !important;"
                @click="openUpdateDetail(update)"
              >
                <div>
                  <div class="row items-center justify-between q-mb-sm">
                    <div class="text-subtitle2 text-weight-bolder text-white card-date-header">
                      {{ formatRelativeTime(update.createdAt) }}
                    </div>
                    <!-- Bouton Like Coeur -->
                    <div 
                      class="row items-center cursor-pointer like-badge"
                      @click.stop="toggleLikeUpdate(update)"
                    >
                      <q-icon 
                        :name="update.userLiked ? 'favorite' : 'favorite_border'" 
                        :color="update.userLiked ? 'red-5' : 'white'" 
                        size="15px" 
                        class="q-mr-xs heart-icon"
                        :class="{ 'heart-pop': update.userLiked }"
                      />
                      <span class="text-caption text-weight-bolder text-white">{{ update.likesCount || 0 }}</span>
                    </div>
                  </div>
                  <div class="text-body1 text-weight-bold text-white card-snippet-text">
                    {{ update.title || update.content }}
                  </div>
                </div>

                <div class="row items-center justify-between text-white text-caption q-mt-md">
                  <span class="text-weight-bold" style="opacity: 0.95;">Voir la suite</span>
                  <q-icon name="arrow_forward" size="16px" />
                </div>
              </div>
            </div>

            <div v-else class="empty-state-open text-center q-py-lg text-grey-6">
              <q-icon name="notifications_none" size="32px" color="grey-4" class="q-mb-xs" />
              <div class="text-body2 text-grey-6">Aucune actualité publiée pour le moment.</div>
            </div>
          </div>

          <q-separator class="section-divider q-my-xl" />

          <!-- SECTION 3: LAST DONORS -->
          <div class="donors-section q-mb-2xl">
            <div class="row items-center justify-between q-mb-lg">
              <div class="row items-center">
                <div class="section-icon-dot bg-rose q-mr-sm"></div>
                <h2 class="section-heading-title text-dark q-ma-none">
                  {{ $t('poolDetail.lastDonors') }} <span class="text-caption text-grey-6 text-weight-medium">({{ contributions.length }})</span>
                </h2>
              </div>
            </div>

            <div v-if="contributions.length > 0" class="donors-open-list">
              <div v-for="contrib in contributions.slice(0, 8)" :key="contrib.id" class="donor-seamless-row flex items-center justify-between q-py-sm">
                <div class="flex items-center">
                  <q-avatar size="40px" class="donor-avatar text-weight-bold q-mr-md">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ (contrib.contributorName || 'A').charAt(0).toUpperCase() }}</span>
                      <span v-else>-</span>
                    </template>
                    <span v-else>{{ (contrib.contributorName || 'A').charAt(0).toUpperCase() }}</span>
                  </q-avatar>
                  <div>
                    <div class="text-weight-bold text-dark text-body2">
                      <template v-if="contrib.anonymous">
                        <span v-if="isOwner">{{ contrib.contributorName }} (Anonyme)</span>
                        <span v-else>{{ $t('poolDetail.anonymousDonor') }}</span>
                      </template>
                      <span v-else>{{ contrib.contributorName }}</span>
                    </div>
                    <div class="text-caption text-grey-5 flex items-center">
                      <q-icon name="schedule" size="12px" class="q-mr-xs" />
                      Récemment
                    </div>
                  </div>
                </div>
                <div class="donor-clean-amount text-weight-bolder text-positive">
                  +{{ contrib.amount }} €
                </div>
              </div>
            </div>

            <div v-else class="empty-state-open text-center q-py-lg text-grey-6 text-italic">
              {{ $t('poolDetail.firstContributorPrompt') }}
            </div>
          </div>

          <q-separator class="section-divider q-my-xl" />

          <!-- SECTION 4: PHASES & SUBPOOLS -->
          <div v-if="(pool.subPools && pool.subPools.length > 0) || (pool.phases && pool.phases.length > 0)" class="phases-section q-mb-2xl">
            <div class="row items-center q-mb-lg">
              <div class="section-icon-dot bg-indigo q-mr-sm"></div>
              <h2 class="section-heading-title text-dark q-ma-none">
                Étapes & Progression
              </h2>
            </div>

            <div v-if="(!pool.subPools || pool.subPools.length === 0) && pool.phases && pool.phases.length > 0" class="phases-timeline">
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

            <div v-if="pool.subPools && pool.subPools.length > 0" class="q-gutter-y-lg">
              <div v-for="(subPool, spIndex) in pool.subPools" :key="subPool.id" :class="{'q-pt-lg border-top-divider': spIndex > 0}">
                <div class="row items-center justify-between q-mb-md">
                  <div class="col">
                    <div class="text-subtitle1 text-weight-bold text-dark">{{ subPool.title }}</div>
                    <div v-if="subPool.description" class="text-body2 text-grey-6 q-mt-xs">{{ subPool.description }}</div>
                  </div>
                  <div class="col-auto">
                    <q-btn 
                      label="Contribuer" 
                      color="secondary" 
                      unelevated 
                      no-caps 
                      class="action-pill-btn"
                      @click="contributeToSubPool(subPool.id)"
                      :disable="subPool.status === 'COMPLETED'"
                    />
                  </div>
                </div>

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
              </div>
            </div>

            <q-separator class="section-divider q-my-xl" />
          </div>

          <!-- SECTION 5: FINANCIAL DASHBOARD (OWNER ONLY) -->
          <div v-if="isOwner" class="finance-section q-mb-2xl">
            <div class="row items-center q-mb-lg">
              <div class="section-icon-dot bg-teal q-mr-sm"></div>
              <h2 class="section-heading-title text-dark q-ma-none">
                {{ $t('poolDetail.financialDashboard') }}
              </h2>
            </div>
            
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <div class="financial-box q-pa-lg column justify-between" style="border-radius: 20px;">
                  <div>
                    <div class="text-overline text-grey-6 text-weight-bold">{{ $t('poolDetail.availableBalance') }}</div>
                    <div class="text-h4 text-weight-bolder text-positive q-mt-xs">{{ pool.availableBalance || 0 }} €</div>
                  </div>
                  <div class="q-mt-md" v-if="pool.availableBalance > 0">
                    <q-btn 
                      label="Demander un retrait" 
                      color="positive" 
                      unelevated 
                      no-caps 
                      icon="payment"
                      class="full-width text-weight-bold action-pill-btn"
                      @click="openWithdrawDialog"
                    />
                  </div>
                </div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="financial-box q-pa-lg column justify-between" style="border-radius: 20px;">
                  <div>
                    <div class="text-overline text-grey-6 text-weight-bold">{{ $t('poolDetail.pending') }}</div>
                    <div class="text-h4 text-weight-bolder text-grey-7 q-mt-xs">{{ pool.pendingBalance || 0 }} €</div>
                  </div>
                  <div class="text-caption text-grey-6 q-mt-sm">{{ $t('poolDetail.pendingDesc') }}</div>
                </div>
              </div>
            </div>

            <q-separator class="section-divider q-my-xl" />
          </div>

          <!-- SECTION 6: COMMENTS -->
          <div class="comments-section q-mb-2xl">
            <div class="row items-center q-mb-lg">
              <div class="section-icon-dot bg-primary q-mr-sm"></div>
              <h2 class="section-heading-title text-dark q-ma-none">
                {{ $t('poolDetail.commentsHeader') }} <span class="text-caption text-grey-6 text-weight-medium">({{ messages.length }})</span>
              </h2>
            </div>

            <div v-if="authStore.isAuthenticated.value" class="q-mb-lg">
              <q-input
                outlined
                v-model="newComment"
                type="textarea"
                :placeholder="$t('poolDetail.commentPlaceholder')"
                rows="3"
                color="secondary"
                bg-color="white"
                class="comment-input-area"
              >
                <template v-slot:after>
                  <div class="column justify-end full-height q-pb-xs">
                    <q-btn round color="primary" icon="send" class="shadow-1" :loading="sendingComment" @click="postComment" />
                  </div>
                </template>
              </q-input>
            </div>
            <div v-else class="q-pa-md bg-blue-1 text-blue-9 text-weight-bold q-mb-lg rounded-borders flex items-center" style="border: 1px dashed rgba(33, 150, 243, 0.3);">
              <q-icon name="info" class="q-mr-sm" size="20px" />
              {{ $t('poolDetail.loginToComment') }}
            </div>

            <div v-if="messages.length > 0" class="q-gutter-y-md">
              <div v-for="msg in messages" :key="msg.id" class="comment-item q-pa-md">
                <div class="row justify-between items-center q-mb-sm">
                  <div class="row items-center">
                    <q-avatar size="34px" class="bg-primary text-secondary text-weight-bold q-mr-sm shadow-xs">
                      {{ msg.userName?.charAt(0).toUpperCase() || '?' }}
                    </q-avatar>
                    <div>
                      <div class="text-weight-bold text-dark text-body2">
                        {{ msg.userName || 'Utilisateur anonyme' }}
                      </div>
                      <div class="text-caption text-grey-5 flex items-center">
                        <q-icon name="schedule" size="12px" class="q-mr-xs" />
                        {{ formatDate(msg.createdAt) }}
                      </div>
                    </div>
                  </div>
                  
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
                
                <div class="text-body2 text-grey-8 q-pl-md q-mb-sm comment-text-body">
                  {{ msg.content }}
                </div>

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
            <div v-else class="empty-state-open text-center q-py-lg text-grey-6">
              <div class="text-body2 text-grey-6">{{ $t('poolDetail.noComments') }}</div>
            </div>
          </div>
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

    <!-- Dialogue de Détail d'Actualité -->
    <q-dialog v-model="updateDetailDialog" transition-show="scale" transition-hide="scale">
      <q-card v-if="selectedUpdate" style="min-width: 500px; max-width: 650px; border-radius: 24px; overflow: hidden;" :dark="$q.dark.isActive">
        <q-card-section class="row items-center justify-between text-white q-pa-lg" style="background: linear-gradient(145deg, #10B981 0%, #059669 100%);">
          <div>
            <div class="text-caption text-weight-bold" style="opacity: 0.95;">
              <q-icon name="schedule" size="14px" class="q-mr-xs" />
              Publié le {{ formatDateWithTime(selectedUpdate.createdAt) }} ({{ formatRelativeTime(selectedUpdate.createdAt) }})
            </div>
            <div class="text-h6 text-weight-bolder q-mt-xs text-white" style="font-family: 'Outfit', sans-serif;">
              {{ selectedUpdate.title || 'Actualité du projet' }}
            </div>
          </div>
          <q-btn icon="close" flat round dense v-close-popup class="text-white" />
        </q-card-section>

        <q-card-section class="q-pa-lg">
          <div 
            class="text-body1 q-mb-lg" 
            :class="$q.dark.isActive ? 'text-grey-3' : 'text-grey-9'"
            style="white-space: pre-line; line-height: 1.7; font-size: 1.05rem;"
          >
            {{ selectedUpdate.content }}
          </div>

          <div v-if="selectedUpdate.imageUrl" class="q-mb-md media-preview-wrapper">
            <q-img :src="selectedUpdate.imageUrl" max-height="400px" fit="contain" class="bg-grey-1" />
          </div>

          <div v-if="selectedUpdate.videoUrl" class="q-mb-md media-preview-wrapper">
            <q-video :src="selectedUpdate.videoUrl" :ratio="16/9" style="max-height: 400px;" />
          </div>
        </q-card-section>

        <q-card-actions align="between" class="q-pa-md" :class="$q.dark.isActive ? 'bg-dark' : 'bg-grey-1'">
          <q-btn 
            :icon="selectedUpdate.userLiked ? 'favorite' : 'favorite_border'" 
            :label="selectedUpdate.userLiked ? 'Aimé (' + (selectedUpdate.likesCount || 0) + ')' : 'J\'aime (' + (selectedUpdate.likesCount || 0) + ')'" 
            :color="selectedUpdate.userLiked ? 'red-6' : 'grey-8'" 
            flat 
            no-caps 
            class="text-weight-bold"
            @click="toggleLikeUpdate(selectedUpdate)"
          />
          <q-btn label="Fermer" flat color="positive" v-close-popup no-caps class="text-weight-bold" />
        </q-card-actions>
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
const selectedUpdate = ref(null)
const updateDetailDialog = ref(false)

const poolLiked = ref(false)
const poolLikesCount = ref(0)

const togglePoolLike = async () => {
  if (!pool.value || !pool.value.id) return

  if (poolLiked.value) {
    poolLiked.value = false
    poolLikesCount.value = Math.max(0, poolLikesCount.value - 1)
  } else {
    poolLiked.value = true
    poolLikesCount.value = poolLikesCount.value + 1
  }

  try {
    const params = {}
    if (authStore.user.value?.id) {
      params.userId = authStore.user.value.id
    }
    await poolApi.post(`/pools/${pool.value.id}/like`, null, { params })
  } catch (err) {
    console.debug('Optimistic pool like toggle:', err)
  }
}

const openUpdateDetail = (update) => {
  selectedUpdate.value = update
  updateDetailDialog.value = true
}

const toggleLikeUpdate = async (update) => {
  if (!update) return
  if (update.likesCount === undefined) update.likesCount = 0

  if (update.userLiked) {
    update.userLiked = false
    update.likesCount = Math.max(0, update.likesCount - 1)
  } else {
    update.userLiked = true
    update.likesCount = update.likesCount + 1
  }

  try {
    const params = {}
    if (authStore.user.value?.id) {
      params.userId = authStore.user.value.id
    }
    await poolApi.post(`/updates/${update.id}/like`, null, { params })
  } catch (err) {
    console.debug('Optimistic update like:', err)
  }
}

const fetchUpdates = async () => {
  try {
    const id = route.params.id
    const params = {}
    if (authStore.user.value?.id) {
      params.userId = authStore.user.value.id
    }
    const response = await poolApi.get(`/pools/${id}/updates`, { params })
    updates.value = response.data
  } catch (err) {
    console.error('Erreur chargement actualités:', err)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

const formatDateWithTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatRelativeTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return "À l'instant"
  const diffInMinutes = Math.floor(diffInSeconds / 60)
  if (diffInMinutes < 60) return `Il y a ${diffInMinutes} min`
  const diffInHours = Math.floor(diffInMinutes / 60)
  if (diffInHours < 24) return `Il y a ${diffInHours} h`
  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays < 30) return `Il y a ${diffInDays} jour${diffInDays > 1 ? 's' : ''}`
  const diffInMonths = Math.floor(diffInDays / 30)
  if (diffInMonths < 12) return `Il y a ${diffInMonths} mois`
  const diffInYears = Math.floor(diffInMonths / 12)
  return `Il y a ${diffInYears} an${diffInYears > 1 ? 's' : ''}`
}
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
    try {
      const likesRes = await poolApi.get('/reactions/status', {
        params: {
          targetId: route.params.id,
          targetType: 'POOL',
          userId: authStore.user.value?.id
        }
      })
      if (likesRes.data) {
        poolLikesCount.value = likesRes.data.count || 0
        poolLiked.value = likesRes.data.liked || false
      }
    } catch (err) {
      console.debug('Could not fetch initial pool likes status:', err)
    }

    if (!pool.value.imageUrl && pool.value.videoUrl) {
      mediaSlide.value = 'video'
    }
    await fetchUpdates()
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
  if (authStore.user.value && authStore.user.value.adminVerification === false) {
    $q.notify({
      type: 'warning',
      message: 'Votre compte est en attente d\'approbation par un administrateur. Vous ne pouvez pas effectuer de retrait tant que votre compte n\'est pas vérifié.',
      icon: 'hourglass_top',
      timeout: 6000
    })
    return
  }

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

.pool-main-title {
  color: #0F172A;
  font-family: 'Outfit', 'Inter', sans-serif;
  font-size: 2.3rem;
  line-height: 1.25;
  letter-spacing: -0.8px;
}

.section-heading-title {
  font-family: 'Outfit', 'Inter', sans-serif;
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: -0.4px;
}

.section-icon-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.like-badge {
  background: rgba(255, 255, 255, 0.22);
  padding: 3px 8px;
  border-radius: 12px;
  backdrop-filter: blur(4px);
  transition: all 0.2s ease;
}

.like-badge:hover {
  background: rgba(255, 255, 255, 0.38);
  transform: scale(1.08);
}

@keyframes heartPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1); }
}

.heart-pop {
  animation: heartPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.updates-scroll-container {
  scroll-behavior: smooth;
  scrollbar-width: thin;
}

.update-green-card {
  width: 200px !important;
  min-width: 200px !important;
  height: 230px !important;
  border-radius: 20px !important;
  background: linear-gradient(145deg, #10B981 0%, #059669 100%) !important;
  color: #FFFFFF !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
  user-select: none;
  box-shadow: 0 10px 25px -5px rgba(16, 185, 129, 0.4) !important;
}

.update-green-card:hover {
  transform: translateY(-6px) scale(1.03) !important;
  box-shadow: 0 16px 32px -4px rgba(16, 185, 129, 0.5) !important;
}

.card-date-header {
  font-family: 'Outfit', sans-serif !important;
  font-size: 0.9rem !important;
  letter-spacing: -0.2px;
  color: #FFFFFF !important;
}

.card-snippet-text {
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  white-space: pre-line;
  font-family: 'Outfit', 'Inter', sans-serif !important;
  color: #FFFFFF !important;
}

.section-divider {
  background-color: #E2E8F0;
  opacity: 0.6;
}
body.body--dark .section-divider {
  background-color: rgba(255, 255, 255, 0.1);
}

.donor-seamless-row {
  border-bottom: 1px solid #F1F5F9;
  transition: background 0.2s ease;
}
.donor-seamless-row:last-child {
  border-bottom: none;
}
body.body--dark .donor-seamless-row {
  border-bottom-color: rgba(255, 255, 255, 0.05);
}

.media-preview-wrapper {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
}
body.body--dark .media-preview-wrapper {
  border-color: rgba(255, 255, 255, 0.1);
}

.update-timeline-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}
body.body--dark .pool-title {
  color: #FFFFFF;
}

.meta-pill-chip {
  display: inline-flex;
  align-items: center;
  background: #F1F5F9;
  color: #475569;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
  border: 1px solid #E2E8F0;
  transition: all 0.2s ease;
}
.meta-pill-chip:hover {
  background: #E2E8F0;
  color: #1E293B;
}
body.body--dark .meta-pill-chip {
  background: #1E293B;
  color: #94A3B8;
  border: 1px solid rgba(255, 255, 255, 0.08);
}
body.body--dark .meta-pill-chip:hover {
  background: #334155;
  color: #F8FAFC;
}

.custom-card {
  border-radius: 24px;
  background: #ffffff;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 25px -4px rgba(15, 23, 42, 0.03), 0 1px 3px rgba(15, 23, 42, 0.02);
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}
.custom-card:hover {
  border-color: rgba(203, 213, 225, 0.9);
  box-shadow: 0 12px 35px -6px rgba(15, 23, 42, 0.06);
}
body.body--dark .custom-card {
  background: #121F32;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: none;
}
body.body--dark .custom-card:hover {
  border-color: rgba(255, 255, 255, 0.15);
}

.icon-box-pill {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: rgba(0, 230, 118, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}
.custom-card:hover .icon-box-pill {
  transform: scale(1.05);
}
.bg-emerald-light {
  background: rgba(0, 230, 118, 0.12);
}
.bg-rose-light {
  background: rgba(244, 63, 94, 0.12);
}
.bg-indigo-light {
  background: rgba(99, 102, 241, 0.12);
}
.bg-teal-light {
  background: rgba(20, 184, 166, 0.12);
}

.section-title {
  font-family: 'Outfit', sans-serif;
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: -0.3px;
  line-height: 1.3;
}
body.body--dark .section-title {
  color: #FFFFFF !important;
}

.action-pill-btn {
  border-radius: 20px !important;
  font-weight: 600 !important;
  padding: 4px 16px !important;
  transition: transform 0.2s ease !important;
}
.action-pill-btn:hover {
  transform: translateY(-1px);
}

.story-description-content {
  white-space: pre-line;
  line-height: 1.75;
  font-size: 0.98rem;
  color: #334155;
  background: #F8FAFC;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #F1F5F9;
}
body.body--dark .story-description-content {
  background: #0D1B2E;
  color: #CBD5E1;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.update-card-box {
  background: #F8FAFC;
  border-radius: 18px;
  border: 1px solid #E2E8F0;
  transition: all 0.2s ease;
}
.update-card-box:hover {
  background: #FFFFFF;
  border-color: #CBD5E1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}
body.body--dark .update-card-box {
  background: #0D1B2E;
  border-color: rgba(255, 255, 255, 0.08);
}

.donor-item-row {
  background: #F8FAFC;
  border: 1px solid #F1F5F9;
  border-radius: 16px;
  transition: transform 0.2s ease, background 0.2s ease, border-color 0.2s ease;
}
.donor-item-row:hover {
  background: #FFFFFF;
  border-color: #E2E8F0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}
body.body--dark .donor-item-row {
  background: #0D1B2E;
  border-color: rgba(255, 255, 255, 0.06);
}
body.body--dark .donor-item-row:hover {
  background: #14243B;
  border-color: rgba(255, 255, 255, 0.12);
}

.donor-avatar {
  background: #E2E8F0;
  color: #334155;
}
body.body--dark .donor-avatar {
  background: #1E293B;
  color: #E2E8F0;
}

.donor-amount-badge {
  background: rgba(0, 230, 118, 0.12);
  color: #00A86B;
  font-weight: 800;
  font-size: 0.95rem;
  padding: 6px 14px;
  border-radius: 20px;
  border: 1px solid rgba(0, 230, 118, 0.25);
}
body.body--dark .donor-amount-badge {
  background: rgba(0, 230, 118, 0.15);
  color: #00E676;
}

.empty-state-box {
  background: #F8FAFC;
  border-radius: 18px;
  border: 1.5px dashed #E2E8F0;
  padding: 32px 20px;
}
body.body--dark .empty-state-box {
  background: #0D1B2E;
  border-color: rgba(255, 255, 255, 0.1);
}

.empty-icon-wrapper {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: #F1F5F9;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
body.body--dark .empty-icon-wrapper {
  background: #1E293B;
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

.comment-input-area {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.comment-input-area:focus-within {
  border-color: #00E676;
  box-shadow: 0 0 0 3px rgba(0, 230, 118, 0.15);
}

.comment-item {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 18px;
  transition: all 0.25s ease;
}
.comment-item:hover {
  background: #FFFFFF;
  border-color: #CBD5E1;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
}
body.body--dark .comment-item {
  background: #0D1B2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.comment-text-body {
  border-left: 3px solid #00E676;
  line-height: 1.65;
  color: #334155;
  font-size: 0.95rem;
}
body.body--dark .comment-text-body {
  color: #CBD5E1;
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
  width: 34px;
  height: 34px;
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
  left: 17px;
  top: 34px;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #EFF6FF 100%);
  border: 1px solid #E2E8F0;
  transition: all 0.3s ease;
}
body.body--dark .financial-box {
  background: linear-gradient(135deg, #1A2536 0%, #0F172A 100%);
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
