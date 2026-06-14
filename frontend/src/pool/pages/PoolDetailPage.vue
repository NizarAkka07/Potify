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
    <div v-else-if="pool" class="container q-mx-auto" style="max-width: 1000px; padding: 0 20px;">
      <!-- Section Hero avec Image -->
      <div class="row q-col-gutter-lg q-pt-lg">
        <div class="col-12 col-md-7">
          <q-card class="image-card no-shadow" style="border-radius: 16px; overflow: hidden;">
            <q-carousel
              v-if="pool.imageUrl || youtubeId || isDirectVideo"
              v-model="mediaSlide"
              swipeable
              animated
              :arrows="(pool.imageUrl && (youtubeId || isDirectVideo))"
              :navigation="(pool.imageUrl && (youtubeId || isDirectVideo))"
              control-color="primary"
              height="450px"
              class="bg-black"
            >
              <q-carousel-slide v-if="pool.imageUrl" name="image" class="q-pa-none">
                <q-img
                  :src="pool.imageUrl"
                  height="100%"
                  fit="cover"
                >
                  <div class="absolute-top-left q-ma-md" style="background: transparent;">
                    <q-chip color="secondary" text-white class="text-weight-bold shadow-2">
                      {{ pool.category }}
                    </q-chip>
                  </div>
                </q-img>
              </q-carousel-slide>

              <q-carousel-slide v-if="youtubeId || isDirectVideo" name="video" class="q-pa-none flex flex-center">
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
                <div class="absolute-top-left q-ma-md" style="z-index: 10;">
                  <q-chip color="secondary" text-white class="text-weight-bold shadow-2">
                    {{ pool.category }}
                  </q-chip>
                </div>
              </q-carousel-slide>
            </q-carousel>

            <div v-else class="bg-grey-3 relative-position flex flex-center" style="height: 450px;">
              <div class="absolute-top-left q-ma-md">
                <q-chip color="secondary" text-white class="text-weight-bold shadow-2">
                  {{ pool.category }}
                </q-chip>
              </div>
              <q-icon name="image_not_supported" size="64px" color="grey-5" />
            </div>
          </q-card>
        </div>

        <!-- Sidebar / Actions -->
        <div class="col-12 col-md-5">
          <q-card class="action-card q-pa-lg no-shadow shadow-1" style="border-radius: 16px; border: 1px solid #EEE; background: white;">
            <div class="text-h4 text-weight-bold q-mb-md" style="color: #0D1B2E; line-height: 1.2;">
              {{ pool.title }}
            </div>

            <div class="flex items-center q-mb-lg">
              <span class="text-grey-7">Créé par <span class="text-weight-bold text-primary">{{ pool.ownerName || 'Anonyme' }}</span></span>
            </div>

            <!-- Progression -->
            <div class="q-mb-xl">
              <!-- Deadline Alert / Display -->
              <div v-if="pool.hasDeadline && pool.deadlineDate" class="q-mb-sm q-pa-sm bg-amber-1 text-amber-9 rounded-borders text-subtitle2 text-weight-bold flex items-center">
                <q-icon name="alarm" class="q-mr-xs" size="sm" />
                {{ $t('poolDetail.deadlineLabel') }} : {{ formatDateWithTime(pool.deadlineDate) }}
              </div>
              <div class="row items-end justify-between q-mb-sm">
                <div class="text-h4 text-weight-bolder text-primary">
                  {{ pool.currentAmount || 0 }} €
                </div>
                <div class="text-grey-7">
                  {{ $t('poolDetail.ofGoal') }} {{ pool.goalAmount }} €
                </div>
              </div>
              
              <q-linear-progress 
                :value="(pool.currentAmount || 0) / pool.goalAmount" 
                color="secondary" 
                size="12px" 
                rounded 
                class="q-mb-sm"
              />
              
              <div class="row justify-between text-caption text-grey-6 text-weight-medium">
                <span>{{ Math.round(((pool.currentAmount || 0) / pool.goalAmount) * 100) }}% {{ $t('poolDetail.completedPercentSuffix') }}</span>
                <span>{{ contributions.length }} {{ $t('poolDetail.participants') }}</span>
              </div>
            </div>

            <!-- Bouton principal -->
            <q-btn
              :label="$t('poolDetail.contributeNow')"
              unelevated
              class="full-width q-py-md text-weight-bold"
              style="background: #FFB300; color: #1A1A2A; border-radius: 12px; font-size: 1.1rem;"
              no-caps
              @click="contribute"
            />
            
            <div class="row q-gutter-sm q-mt-md">
              <q-btn outline color="primary" icon="share" :label="$t('poolDetail.share')" class="col" no-caps style="border-radius: 10px;" />
              <q-btn outline color="grey-7" icon="favorite_border" class="col-auto" style="border-radius: 10px;" />
            </div>
          </q-card>

          <!-- Liste des derniers contributeurs (Sidebar) -->
          <q-card class="q-mt-lg q-pa-lg no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="text-subtitle1 text-weight-bold q-mb-md">{{ $t('poolDetail.lastDonors') }} ({{ contributions.length }})</div>
            
            <q-list v-if="contributions.length > 0">
              <q-item v-for="contrib in contributions.slice(0, 5)" :key="contrib.id" class="q-px-none q-py-sm">
                <q-item-section avatar>
                  <q-avatar size="32px" color="blue-1" text-color="primary">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ contrib.contributorName.charAt(0) }}</span>
                      <span v-else>?</span>
                    </template>
                    <span v-else>{{ contrib.contributorName.charAt(0) }}</span>
                  </q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold" style="font-size: 0.9rem;">
                    <template v-if="contrib.anonymous">
                      <span v-if="isOwner">{{ contrib.contributorName }} (Anonyme)</span>
                      <span v-else>{{ $t('poolDetail.anonymousDonor') }}</span>
                    </template>
                    <span v-else>{{ contrib.contributorName }}</span>
                  </q-item-label>
                  <q-item-label caption>{{ contrib.amount }} € • {{ formatDate(contrib.createdAt) }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
            
            <div v-else class="text-center q-py-md text-grey-6 text-italic">
              {{ $t('poolDetail.firstContributorPrompt') }}
            </div>
          </q-card>
        </div>
      </div>

      <!-- Section Détails -->
      <div class="row q-col-gutter-lg q-mt-lg">
        <div class="col-12 col-md-7">
          <q-card class="details-card q-pa-xl no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="row items-center justify-between q-mb-lg">
              <div class="text-h5 text-weight-bold" style="color: #0D1B2E;">
                {{ $t('poolDetail.aboutPool') }}
              </div>
              <div class="row q-gutter-sm">
                <q-btn 
                  v-if="isOwner || isAdmin" 
                  :label="$t('poolDetail.edit')" 
                  icon="edit" 
                  color="warning" 
                  flat 
                  no-caps 
                  @click="router.push(`/pools/${pool.id}/edit`)"
                />
                <q-btn 
                  v-if="isOwner" 
                  :label="$t('poolDetail.addPhase')" 
                  icon="add_circle" 
                  color="primary" 
                  flat 
                  no-caps 
                  @click="router.push({ path: '/pools/create', query: { parentId: pool.id } })"
                />
              </div>
            </div>
            <div class="text-body1 text-grey-8 q-mb-xl" style="white-space: pre-line; line-height: 1.6;">
              {{ pool.description }}
            </div>

            <!-- SECTION : PHASES DE PROGRESSION (Cagnotte Simple) -->
            <div v-if="(!pool.subPools || pool.subPools.length === 0) && pool.phases && pool.phases.length > 0" class="q-mb-xl">
              <div class="text-h5 text-weight-bold q-mb-lg row items-center" style="color: #0D1B2E;">
                <q-icon name="flag" class="q-mr-sm" color="primary" />
                {{ $t('poolDetail.phasesHeader') }} ({{ pool.phases.length }})
              </div>
              
              <q-card flat bordered style="border-radius: 16px; border: 1px solid #E0E0E0; background: white;" class="q-pa-lg">
                <div class="phases-timeline q-pl-sm">
                  <div v-for="(phase, phIndex) in pool.phases" :key="phase.id" class="phase-item q-mb-md">
                    <div class="row items-center q-mb-xs">
                      <div class="phase-number-circle q-mr-md" :class="getPhaseStatusClass(phase)">
                        {{ phIndex + 1 }}
                      </div>
                      <div class="col">
                        <div class="row items-center justify-between">
                          <div class="text-caption text-weight-bold" :class="phase.status === 'COMPLETED' ? 'text-grey-6' : 'text-dark'">
                            {{ phase.title }}
                            <q-badge v-if="phase.status === 'ACTIVE'" color="secondary" label="ACTIVE" class="q-ml-xs" />
                          </div>
                          <div class="text-caption text-weight-medium text-grey-7">
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
            </div>

             <!-- SECTION : SOUS-CAGNOTTES / ÉTAPES DU PROJET -->
            <div v-if="pool.subPools && pool.subPools.length > 0" class="q-mb-xl">
              <div class="text-h5 text-weight-bold q-mb-lg row items-center" style="color: #0D1B2E;">
                <q-icon name="account_tree" class="q-mr-sm" color="primary" />
                {{ $t('poolDetail.subPoolsHeader') }} ({{ pool.subPools.length }})
              </div>
              
              <div v-for="subPool in pool.subPools" :key="subPool.id" class="q-mb-lg">
                <q-card flat bordered style="border-radius: 16px; border: 1px solid #E0E0E0; background: white;">
                  <q-card-section class="q-pa-lg">
                    <!-- Title and Contribute button -->
                    <div class="row items-center justify-between q-mb-md">
                      <div class="col">
                        <div class="text-h6 text-weight-bold text-dark">{{ subPool.title }}</div>
                        <div v-if="subPool.description" class="text-body2 text-grey-7 q-mt-xs">{{ subPool.description }}</div>
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
                          style="border-radius: 8px;"
                          @click="contributeToSubPool(subPool.id)"
                          :disable="subPool.status === 'COMPLETED'"
                        />
                      </div>
                    </div>

                    <!-- Progress bar -->
                    <div class="q-mb-md">
                      <div class="row justify-between text-caption text-grey-7 q-mb-xs">
                        <span>{{ subPool.currentAmount || 0 }} € collectés sur {{ subPool.goalAmount }} €</span>
                        <span class="text-weight-bold">{{ Math.round(((subPool.currentAmount || 0) / subPool.goalAmount) * 100) }}%</span>
                      </div>
                      <q-linear-progress 
                        :value="(subPool.currentAmount || 0) / subPool.goalAmount" 
                        color="primary" 
                        size="10px" 
                        rounded 
                      />
                    </div>

                    <!-- Phases within this sub-pool -->
                    <div v-if="subPool.phases && subPool.phases.length > 0" class="q-mt-md">
                      <div class="text-subtitle2 text-weight-bold text-grey-8 q-mb-sm">{{ $t('poolDetail.phasesHeader') }} :</div>
                      <div class="phases-timeline q-pl-sm">
                        <div v-for="(phase, phIndex) in subPool.phases" :key="phase.id" class="phase-item q-mb-md">
                          <div class="row items-center q-mb-xs">
                            <div class="phase-number-circle q-mr-md" :class="getPhaseStatusClass(phase)">
                              {{ phIndex + 1 }}
                            </div>
                            <div class="col">
                              <div class="row items-center justify-between">
                                <div class="text-caption text-weight-bold" :class="phase.status === 'COMPLETED' ? 'text-grey-6' : 'text-dark'">
                                  {{ phase.title }}
                                  <q-badge v-if="phase.status === 'ACTIVE'" color="secondary" label="ACTIVE" class="q-ml-xs" />
                                </div>
                                <div class="text-caption text-weight-medium text-grey-7">
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
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <!-- SECTION FINANCIERE (Visible uniquement par le proprietaire) -->
            <div v-if="isOwner" class="q-mb-xl">
              <div class="text-h6 text-weight-bold q-mb-md" style="color: #0D1B2E;">
                <q-icon name="account_balance_wallet" color="primary" class="q-mr-sm" />
                {{ $t('poolDetail.financialDashboard') }}
              </div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-sm-6">
                  <q-card flat bordered class="bg-green-1 text-green-9" style="border-radius: 12px;">
                    <q-card-section>
                      <div class="text-overline">{{ $t('poolDetail.availableBalance') }}</div>
                      <div class="text-h4 text-weight-bolder">{{ pool.availableBalance || 0 }} €</div>
                      <div class="text-caption">{{ $t('poolDetail.readyToWithdraw') }}</div>
                    </q-card-section>
                  </q-card>
                </div>
                <div class="col-12 col-sm-6">
                  <q-card flat bordered class="bg-blue-1 text-blue-9" style="border-radius: 12px;">
                    <q-card-section>
                      <div class="text-overline">{{ $t('poolDetail.pending') }}</div>
                      <div class="text-h4 text-weight-bolder">{{ pool.pendingBalance || 0 }} €</div>
                      <div class="text-caption">{{ $t('poolDetail.pendingDesc') }}</div>
                    </q-card-section>
                  </q-card>
                </div>
              </div>
            </div>

            <!-- SECTION INVITATIONS (Visible uniquement par le proprietaire pour les cagnottes non publiques) -->
            <div v-if="isOwner && pool.type !== 'PUBLIC'" class="q-mb-xl">
              <div class="text-h6 text-weight-bold q-mb-md" style="color: #0D1B2E;">
                <q-icon name="person_add" color="primary" class="q-mr-sm" />
                {{ $t('poolDetail.inviteParticipants') }}
              </div>
              <q-card flat bordered style="border-radius: 12px;">
                <q-card-section>
                  <div class="row q-col-gutter-md items-center">
                    <div class="col">
                      <q-input v-model="inviteEmail" label="Email de l'invité" dense filled color="primary" @keyup.enter="sendInvitation" />
                    </div>
                    <div class="col-auto">
                      <q-btn :label="$t('poolDetail.inviteButton')" color="primary" unelevated @click="sendInvitation" :loading="sendingInvite" />
                    </div>
                  </div>
                  
                  <div v-if="invitations.length > 0" class="q-mt-md">
                    <div class="text-caption text-grey-7 q-mb-sm">{{ $t('poolDetail.invitedMembers') }} ({{ invitations.length }}) :</div>
                    <div class="row q-gutter-xs">
                      <q-chip v-for="inv in invitations" :key="inv.id" outline dense color="primary" size="sm" removable @remove="removeInvitation(inv.id)">
                        {{ inv.email }}
                        <q-tooltip>Supprimer l'invitation</q-tooltip>
                      </q-chip>
                    </div>
                  </div>
                </q-card-section>
              </q-card>
            </div>

            <!-- SECTION : COMMENTAIRES (Messages du Pool) -->
            <div class="text-h5 text-weight-bold q-mb-lg row items-center" style="color: #0D1B2E;">
              <q-icon name="forum" class="q-mr-sm" color="primary" />
              {{ $t('poolDetail.commentsHeader') }} ({{ messages.length }})
            </div>

            <!-- Champ de saisie pour nouveau commentaire -->
            <div v-if="authStore.isAuthenticated.value" class="q-mb-xl">
              <q-input
                filled
                v-model="newComment"
                type="textarea"
                :placeholder="$t('poolDetail.commentPlaceholder')"
                rows="3"
                color="secondary"
                bg-color="white"
                class="shadow-1"
                style="border-radius: 8px;"
              >
                <template v-slot:after>
                  <div class="column justify-end full-height q-pb-xs">
                    <q-btn round color="primary" icon="send" :loading="sendingComment" @click="postComment" />
                  </div>
                </template>
              </q-input>
            </div>
            <div v-else class="q-pa-md bg-blue-1 text-blue-9 text-weight-bold q-mb-xl" style="border-radius: 8px; border: 1px dashed #2196F3;">
              <q-icon name="info" class="q-mr-sm" />
              {{ $t('poolDetail.loginToComment') }}
            </div>

            <!-- Liste des commentaires -->
            <div v-if="messages.length > 0" class="q-gutter-y-md">
              <div v-for="msg in messages" :key="msg.id" class="comment-item q-pa-md" style="background: white; border-radius: 12px; border: 1px solid #EEE;">
                <div class="row justify-between items-center q-mb-sm">
                  <div class="row items-center">
                    <q-avatar size="32px" color="primary" text-white class="q-mr-sm">
                      {{ msg.userName?.charAt(0) || '?' }}
                    </q-avatar>
                    <div>
                      <div class="text-weight-bold" style="color: #0D1B2E;">
                        {{ msg.userName || 'Utilisateur anonyme' }}
                      </div>
                      <div class="text-caption text-grey-6">{{ formatDate(msg.createdAt) }}</div>
                    </div>
                  </div>
                  
                  <!-- Bouton Réaction (Style WhatsApp) -->
                  <div v-if="authStore.isAuthenticated.value" class="reaction-trigger">
                    <q-btn flat round dense icon="add_reaction" color="grey-7" size="sm">
                      <q-menu anchor="top middle" self="bottom middle" class="reaction-menu no-shadow">
                        <div class="row no-wrap q-pa-xs q-gutter-x-sm bg-white shadow-2" style="border-radius: 30px; border: 1px solid #EEE;">
                          <q-btn v-for="emoji in availableEmojis" :key="emoji.type" 
                                 flat round dense :label="emoji.icon" class="emoji-btn"
                                 @click="toggleReaction(msg.id, emoji.type)" v-close-popup />
                        </div>
                      </q-menu>
                    </q-btn>
                  </div>
                </div>
                
                <div class="text-body2 text-grey-9 q-pl-md border-left q-mb-sm" style="border-left: 2px solid #EEE;">
                  {{ msg.content }}
                </div>

                <!-- Affichage des réactions -->
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
              <q-icon name="chat_bubble_outline" size="48px" class="q-mb-sm" />
              <div>{{ $t('poolDetail.noComments') }}</div>
            </div>

            <q-separator class="q-my-xl" />

            <div class="text-h6 text-weight-bold q-mb-md">{{ $t('poolDetail.howItWorksTitle') }}</div>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="security" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">{{ $t('poolDetail.howSecure') }}</div>
                <div class="text-caption text-grey-7">{{ $t('poolDetail.howSecureDesc') }}</div>
              </div>
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="history" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">{{ $t('poolDetail.howTransparent') }}</div>
                <div class="text-caption text-grey-7">{{ $t('poolDetail.howTransparentDesc') }}</div>
              </div>
              <div class="col-12 col-sm-4 text-center">
                <q-icon name="group" color="secondary" size="40px" />
                <div class="text-weight-bold q-mt-sm">{{ $t('poolDetail.howCollective') }}</div>
                <div class="text-caption text-grey-7">{{ $t('poolDetail.howCollectiveDesc') }}</div>
              </div>
            </div>
          </q-card>
        </div>

        <!-- Sidebar Secondaire -->
        <div class="col-12 col-md-5">
          <q-card class="info-card q-pa-lg no-shadow" style="border-radius: 16px; background: white; border: 1px solid #EEE;">
            <div class="text-subtitle1 text-weight-bold q-mb-md">{{ $t('poolDetail.infoTitle') }}</div>
            <q-list dense>
              <q-item>
                <q-item-section avatar>
                  <q-icon name="calendar_today" color="grey-6" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-grey-7">{{ $t('poolDetail.createdAtLabel') }}</q-item-label>
                  <q-item-label class="text-weight-bold">{{ formatDate(pool.createdAt) }}</q-item-label>
                </q-item-section>
              </q-item>
              <q-item class="q-mt-sm">
                <q-item-section avatar>
                  <q-icon name="public" color="grey-6" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-grey-7">{{ $t('poolDetail.visibilityLabel') }}</q-item-label>
                  <q-item-label class="text-weight-bold">{{ $t('poolDetail.visibilityPublic') }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
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
let messageEventSource = null

const isOwner = computed(() => {
  return authStore.isAuthenticated.value && pool.value.ownerId && authStore.user.value?.id && String(pool.value.ownerId) === String(authStore.user.value?.id)
})

const isAdmin = computed(() => {
  return authStore.isAuthenticated.value && (authStore.user.value?.roles?.includes('ADMIN') || authStore.user.value?.role === 'ADMIN')
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

onMounted(async () => {
  await fetchPool()
  setupMessageSSE()
})

onUnmounted(() => {
  if (messageEventSource) {
    messageEventSource.close()
  }
})
</script>

<style scoped>
.image-card {
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.action-card {
  position: sticky;
  top: 100px;
}
.reaction-badge {
  background: #F1F4F9;
  border-radius: 12px;
  border: 1px solid #E0E0E0;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}
.reaction-badge:hover {
  background: #E8EAF6;
  border-color: #3F51B5;
}
.user-reacted {
  background: #E3F2FD !important;
  border-color: #2196F3 !important;
  color: #1976D2;
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
  background: #E0E0E0;
  z-index: 0;
}
@media (max-width: 991px) {
  .action-card {
    position: static;
  }
}
</style>
