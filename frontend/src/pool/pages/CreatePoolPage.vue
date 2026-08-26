<template>
  <q-page class="flex flex-center" style="background: #F8F9FA; padding: 10px 15px; min-height: auto;">
    <!-- Main Card Container -->
    <q-card class="shadow-4" style="width: 100%; max-width: 1000px; border-radius: 16px; overflow: hidden; background: white; border: 1px solid rgba(0,0,0,0.05);">
      <!-- Header Modern Style -->
      <div class="q-py-md q-px-xl" style="background: #FFFFFF; border-bottom: 1px solid #F1F5F9; position: relative;">
        <div class="row items-center justify-between">
          <div>
            <div class="text-h6 text-weight-bolder" style="font-size: 1.25rem; letter-spacing: -0.5px; color: #0F172A;">
              {{ isTontineMode ? 'Créer une Tontine Rotative' : $t('createPool.title') }}
            </div>
            <div class="text-caption text-grey-6 q-mt-xs">
              {{ isTontineMode ? 'Organisez une épargne collective et solidaire entre membres de confiance.' : $t('createPool.subtitle') }}
            </div>
          </div>
          <!-- Type badge -->
          <q-badge v-if="isTontineMode" color="orange-8" class="q-px-sm q-py-xs text-weight-bold text-caption" style="border-radius: 8px;">
            <q-icon name="sync" size="14px" class="q-mr-xs" /> Tontine Rotative
          </q-badge>
          <q-badge v-else-if="currentStep > 1" :color="mode === 'simple' ? 'primary' : 'secondary'" class="q-px-sm q-py-xs text-weight-bold text-caption" style="border-radius: 8px;">
            {{ mode === 'simple' ? 'Cagnotte Simple' : 'Cagnotte avec Phases' }}
          </q-badge>
        </div>
      </div>

      <!-- Premium Progress Stepper (Visible for Step 1 to 5) -->
      <div v-if="currentStep <= 5" class="stepper-progress-wrapper q-px-lg q-pt-md q-pb-xs" style="background: white; border-bottom: 1px solid rgba(0,0,0,0.05);">
        <div class="row justify-between items-center no-wrap relative-position" style="max-width: 600px; margin: 0 auto;">
          <!-- Stepper Background & Fill Line -->
          <div class="stepper-line-bg"></div>
          <div class="stepper-line-fill" :style="{ width: stepperLineWidth + '%' }"></div>
          
          <!-- Step 1: Type -->
          <div class="step-indicator column items-center" :class="{ 'step-active': currentStep >= 1, 'step-completed': currentStep > 1 }" @click="goToStep(1)">
            <q-avatar size="28px" :color="currentStep > 1 ? 'secondary' : (currentStep === 1 ? 'primary' : 'grey-3')" :text-color="currentStep >= 1 ? 'white' : 'grey-7'">
              <q-icon v-if="currentStep > 1" name="check" size="16px" />
              <span v-else class="text-weight-bold text-caption">1</span>
            </q-avatar>
            <div class="step-label q-mt-xs text-caption text-weight-medium" style="font-size: 0.7rem;">Structure</div>
          </div>
          
          <!-- Step 2: Assistant IA -->
          <div class="step-indicator column items-center" :class="{ 'step-active': currentStep >= 2, 'step-completed': currentStep > 2 }" @click="goToStep(2)">
            <q-avatar size="28px" :color="currentStep > 2 ? 'secondary' : (currentStep === 2 ? 'primary' : 'grey-3')" :text-color="currentStep >= 2 ? 'white' : 'grey-7'">
              <q-icon v-if="currentStep > 2" name="check" size="16px" />
              <q-icon v-else name="auto_awesome" size="14px" />
            </q-avatar>
            <div class="step-label q-mt-xs text-caption text-weight-medium" style="font-size: 0.7rem;">Assistant IA</div>
          </div>
          
          <!-- Step 3: Général -->
          <div class="step-indicator column items-center" :class="{ 'step-active': currentStep >= 3, 'step-completed': currentStep > 3 }" @click="goToStep(3)">
            <q-avatar size="28px" :color="currentStep > 3 ? 'secondary' : (currentStep === 3 ? 'primary' : 'grey-3')" :text-color="currentStep >= 3 ? 'white' : 'grey-7'">
              <q-icon v-if="currentStep > 3" name="check" size="16px" />
              <span v-else class="text-weight-bold text-caption">3</span>
            </q-avatar>
            <div class="step-label q-mt-xs text-caption text-weight-medium" style="font-size: 0.7rem;">Général</div>
          </div>
          
          <!-- Step 4: Configuration -->
          <div class="step-indicator column items-center" :class="{ 'step-active': currentStep >= 4, 'step-completed': currentStep > 4 }" @click="goToStep(4)">
            <q-avatar size="28px" :color="currentStep > 4 ? 'secondary' : (currentStep === 4 ? 'primary' : 'grey-3')" :text-color="currentStep >= 4 ? 'white' : 'grey-7'">
              <q-icon v-if="currentStep > 4" name="check" size="16px" />
              <span v-else class="text-weight-bold text-caption">4</span>
            </q-avatar>
            <div class="step-label q-mt-xs text-caption text-weight-medium" style="font-size: 0.7rem;">Configuration</div>
          </div>
          
          <!-- Step 5: Vérification -->
          <div class="step-indicator column items-center" :class="{ 'step-active': currentStep >= 5, 'step-completed': currentStep > 5 }" @click="goToStep(5)">
            <q-avatar size="28px" :color="currentStep > 5 ? 'secondary' : (currentStep === 5 ? 'primary' : 'grey-3')" :text-color="currentStep >= 5 ? 'white' : 'grey-7'">
              <q-icon v-if="currentStep > 5" name="check" size="16px" />
              <span v-else class="text-weight-bold text-caption">5</span>
            </q-avatar>
            <div class="step-label q-mt-xs text-caption text-weight-medium" style="font-size: 0.7rem;">Vérification</div>
          </div>
        </div>
      </div>

      <!-- Main Form Wrapper -->
      <q-form @submit="onSubmit">
        
        <!-- STEP 1 — Choix du type de cagnotte -->
        <div v-if="currentStep === 1" class="q-px-xl q-py-lg animate-fade">
          <div class="text-h6 text-weight-bold text-center q-mb-xs" style="color: #0D1B2E;">
            {{ isTontineMode ? 'Structure de la Tontine Rotative' : 'Sélectionnez la structure de la cagnotte' }}
          </div>
          <p class="text-caption text-grey-7 text-center q-mb-lg q-mx-auto" style="max-width: 500px;">
            {{ isTontineMode ? 'Une tontine rotative permet à un groupe de membres de cotiser périodiquement une somme fixe, chaque tour reversant l\'intégralité du pot à un bénéficiaire.' : 'Choisissez la structure la plus adaptée pour collecter et gérer vos fonds de manière transparente.' }}
          </p>

          <!-- TONTINE CARD IF IN TONTINE MODE -->
          <div v-if="isTontineMode" class="row justify-center">
            <div class="col-12 col-sm-8 col-md-6">
              <q-card 
                flat 
                bordered 
                class="pool-type-card cursor-pointer q-pa-lg text-center column justify-between card-active"
                style="border-color: #F59E0B !important;"
                @click="selectModeAndNext('simple')"
              >
                <div>
                  <q-avatar size="56px" color="amber-1" text-color="orange-8" class="q-mb-md">
                    <q-icon name="sync" size="30px" />
                  </q-avatar>
                  <div class="text-subtitle1 text-weight-bold q-mb-xs" style="color: #0D1B2E;">Tontine Rotative Solidaire</div>
                  <p class="text-caption text-grey-7 q-mb-sm" style="min-height: 48px; line-height: 1.4;">
                    Collecte cyclique fermée accessible par invitation. À chaque période définie (semaine, mois...), un bénéficiaire différent perçoit l'intégralité du pot.
                  </p>
                  <div class="q-gutter-xs q-mb-md row justify-center">
                    <q-chip dense outline color="orange-8" style="font-size: 0.65rem;">Cycle régulier</q-chip>
                    <q-chip dense outline color="orange-8" style="font-size: 0.65rem;">Participants fixes</q-chip>
                    <q-chip dense outline color="orange-8" style="font-size: 0.65rem;">Ordre de passage</q-chip>
                    <q-chip dense outline color="orange-8" style="font-size: 0.65rem;">Sécurité garantie</q-chip>
                  </div>
                </div>
                <q-btn 
                  label="Configurer ma Tontine" 
                  unelevated 
                  color="orange-8" 
                  no-caps 
                  class="full-width q-py-sm text-weight-bold"
                  style="border-radius: 8px;"
                />
              </q-card>
            </div>
          </div>

          <!-- NORMAL POOL CARDS IF NORMAL MODE -->
          <div v-else class="row q-col-gutter-lg justify-center">
            <!-- Simple Card -->
            <div class="col-12 col-sm-6 col-md-4">
              <q-card 
                flat 
                bordered 
                class="pool-type-card cursor-pointer q-pa-lg text-center column justify-between"
                :class="{ 'card-active': mode === 'simple' }"
                @click="selectModeAndNext('simple')"
              >
                <div>
                  <q-avatar size="56px" color="teal-1" text-color="teal" class="q-mb-md">
                    <q-icon name="savings" size="30px" />
                  </q-avatar>
                  <div class="text-subtitle1 text-weight-bold q-mb-xs" style="color: #0D1B2E;">Cagnotte Simple</div>
                  <p class="text-caption text-grey-7 q-mb-sm" style="min-height: 48px; line-height: 1.4;">
                    Créer rapidement une cagnotte classique destinée à collecter des contributions pour un objectif unique.
                  </p>
                  <div class="q-gutter-xs q-mb-md row justify-center">
                    <q-chip dense outline color="primary" style="font-size: 0.65rem;">Cadeau</q-chip>
                    <q-chip dense outline color="primary" style="font-size: 0.65rem;">Mariage</q-chip>
                    <q-chip dense outline color="primary" style="font-size: 0.65rem;">Voyage</q-chip>
                    <q-chip dense outline color="primary" style="font-size: 0.65rem;">Solidarité</q-chip>
                  </div>
                </div>
                <q-btn 
                  label="Créer une cagnotte simple" 
                  unelevated 
                  color="primary" 
                  no-caps 
                  class="full-width q-py-sm text-weight-bold"
                  style="border-radius: 8px;"
                />
              </q-card>
            </div>

            <!-- Complex Card -->
            <div class="col-12 col-sm-6 col-md-4">
              <q-card 
                flat 
                bordered 
                class="pool-type-card cursor-pointer q-pa-lg text-center column justify-between"
                :class="{ 'card-active': mode === 'multi' }"
                @click="selectModeAndNext('multi')"
              >
                <div>
                  <q-avatar size="56px" color="blue-1" text-color="blue" class="q-mb-md">
                    <q-icon name="account_tree" size="30px" />
                  </q-avatar>
                  <div class="text-subtitle1 text-weight-bold q-mb-xs" style="color: #0D1B2E;">Cagnotte avec Phases</div>
                  <p class="text-caption text-grey-7 q-mb-sm" style="min-height: 48px; line-height: 1.4;">
                    Créer une cagnotte avancée composée de plusieurs étapes (jalons) avec un suivi transparent des fonds.
                  </p>
                  <div class="q-gutter-xs q-mb-md row justify-center">
                    <q-chip dense outline color="secondary" style="font-size: 0.65rem;">Construction</q-chip>
                    <q-chip dense outline color="secondary" style="font-size: 0.65rem;">Startup</q-chip>
                    <q-chip dense outline color="secondary" style="font-size: 0.65rem;">Humanitaire</q-chip>
                    <q-chip dense outline color="secondary" style="font-size: 0.65rem;">Projet long terme</q-chip>
                  </div>
                </div>
                <q-btn 
                  label="Créer une cagnotte complexe" 
                  unelevated 
                  color="secondary" 
                  no-caps 
                  class="full-width q-py-sm text-weight-bold"
                  style="border-radius: 8px;"
                />
              </q-card>
            </div>
          </div>
        </div>

        <!-- STEP 2 — Souhaitez-vous utiliser l'IA ? -->
        <div v-if="currentStep === 2" class="q-px-xl q-py-lg animate-fade">
          <div class="text-center q-mb-md">
            <q-avatar size="60px" class="q-mb-xs shadow-3 breathing-avatar">
              <img src="~assets/poti.jpeg" style="object-fit: cover;" alt="PotiBuddy" />
            </q-avatar>
            <div class="text-h6 text-weight-bold text-dark q-mb-xs">
              Créer votre cagnotte avec l'aide de PotiBuddy
            </div>
            <p class="text-caption text-grey-7 q-mx-auto q-mb-sm" style="max-width: 500px;">
              Notre assistant IA peut remplir automatiquement votre cagnotte en quelques secondes. Vous pourrez toujours modifier tous les champs manuellement avant la publication.
            </p>
          </div>

          <!-- Buttons selection -->
          <div class="row q-col-gutter-md justify-center q-mb-md" style="max-width: 600px; margin: 0 auto;">
            <div class="col-12 col-sm-6">
              <q-card 
                flat 
                bordered 
                class="ai-choice-card cursor-pointer q-pa-md text-center"
                @click="chooseAi(true)"
              >
                <q-icon name="auto_awesome" size="md" color="amber-9" class="q-mb-xs animate-pulse" />
                <div class="text-weight-bold text-subtitle2">✨ Oui, utiliser l'IA</div>
                <div class="text-caption text-grey-7" style="font-size: 0.72rem; line-height: 1.2;">
                  Décrivez votre projet et laissez l'IA remplir le formulaire.
                </div>
              </q-card>
            </div>
            <div class="col-12 col-sm-6">
              <q-card 
                flat 
                bordered 
                class="ai-choice-card cursor-pointer q-pa-md text-center"
                @click="chooseAi(false)"
              >
                <q-icon name="edit" size="md" color="grey-7" class="q-mb-xs" />
                <div class="text-weight-bold text-subtitle2">✏️ Non, remplir moi-même</div>
                <div class="text-caption text-grey-7" style="font-size: 0.72rem; line-height: 1.2;">
                  Remplissez manuellement le formulaire étape par étape.
                </div>
              </q-card>
            </div>
          </div>

          <!-- In-page Chat UI when IA selected -->
          <q-slide-transition>
            <div v-if="inPageChatActive" class="q-mt-md q-pa-sm bg-grey-1 rounded-borders border-accent shadow-1" style="max-width: 700px; margin: 0 auto;">
              <div class="row items-center justify-between q-mb-xs">
                <div class="row items-center">
                  <q-avatar size="24px" class="q-mr-sm">
                    <img src="~assets/poti.jpeg" style="object-fit: cover;" />
                  </q-avatar>
                  <div class="text-subtitle2 text-weight-bold" style="color: #0D1B2E;">Discuter avec PotiBuddy</div>
                </div>
                <q-btn flat round dense icon="refresh" color="primary" size="xs" @click="resetChat">
                  <q-tooltip>Réinitialiser la discussion</q-tooltip>
                </q-btn>
              </div>

              <!-- Conversational Chat Area -->
              <div ref="chatContainer" class="q-mb-sm q-pa-sm rounded-borders chat-container-box" style="max-height: 180px;">
                <div v-for="(msg, idx) in chatHistory" :key="idx" class="q-mb-sm">
                  <!-- Bot Message -->
                  <div v-if="msg.role === 'assistant'" class="row items-start justify-start q-col-gutter-xs">
                    <div class="col-auto">
                      <q-avatar size="22px" class="shadow-1">
                        <img src="~assets/poti.jpeg" style="object-fit: cover;" />
                      </q-avatar>
                    </div>
                    <div class="col" style="max-width: 85%;">
                      <div class="bg-white q-pa-sm text-grey-9 text-caption shadow-sm border-light" style="border-radius: 4px 12px 12px 12px; line-height: 1.4; white-space: pre-line;">
                        {{ msg.content }}
                      </div>
                    </div>
                  </div>
                  <!-- User Message -->
                  <div v-else class="row items-start justify-end q-col-gutter-xs">
                    <div class="col text-right" style="max-width: 85%;">
                      <div class="bg-primary text-white q-pa-sm text-caption text-left inline-block shadow-sm" style="border-radius: 12px 12px 4px 12px; line-height: 1.4; white-space: pre-line;">
                        {{ msg.content }}
                      </div>
                    </div>
                    <div class="col-auto">
                      <q-avatar size="22px" color="secondary" text-color="white" icon="person" />
                    </div>
                  </div>
                </div>

                <!-- Typing Indicator -->
                <div v-if="aiLoading" class="row items-start justify-start q-col-gutter-xs q-mb-sm">
                  <div class="col-auto">
                    <q-avatar size="22px">
                      <img src="~assets/poti.jpeg" style="object-fit: cover;" />
                    </q-avatar>
                  </div>
                  <div class="col" style="max-width: 85%;">
                    <div class="bg-white q-pa-xs text-grey-9 text-caption inline-block shadow-sm" style="border-radius: 4px 12px 12px 12px;">
                      <q-spinner-dots color="primary" size="xs" />
                    </div>
                  </div>
                </div>
              </div>

              <!-- Message Input Area -->
              <div class="row q-col-gutter-xs items-center">
                <div class="col">
                  <q-input
                    v-model="aiPrompt"
                    type="text"
                    outlined
                    dense
                    bg-color="white"
                    placeholder="Décrivez votre projet..."
                    class="q-mb-none"
                    style="font-size: 0.8rem;"
                    @keyup.enter="sendChatMessage"
                  />
                </div>
                <div class="col-auto">
                  <q-btn
                    round
                    dense
                    color="primary"
                    icon="send"
                    size="sm"
                    :loading="aiLoading"
                    @click="sendChatMessage"
                  />
                </div>
              </div>

              <!-- Suggestion Preview Panel (shows up when a suggestion is generated) -->
              <q-slide-transition>
                <div v-if="aiSuggestion" class="q-mt-sm q-pa-sm bg-white text-dark rounded-borders shadow-2 border-amber">
                  <div class="row items-center justify-between q-mb-xs border-bottom q-pb-xs">
                    <div class="text-caption text-weight-bold text-amber-9">
                      <q-icon name="auto_awesome" color="amber-9" class="q-mr-xs animate-pulse" /> Proposition de PotiBuddy
                    </div>
                    <q-badge :label="aiSuggestion.category" color="amber-9" style="font-size: 0.65rem;" />
                  </div>

                  <div class="text-caption q-mb-xs"><strong>Titre suggéré :</strong> {{ aiSuggestion.title }}</div>
                  <div class="text-caption q-mb-xs"><strong>Budget total :</strong> {{ aiSuggestion.goalAmount }} €</div>
                  <div class="text-caption text-grey-8 q-mb-xs" style="line-height: 1.3;">
                    <strong>Description :</strong> {{ aiSuggestion.description }}
                  </div>

                  <!-- Suggested Structure / Phases preview -->
                  <div class="q-mb-sm q-pa-xs bg-grey-1 rounded-borders border-light">
                    <div class="text-caption text-weight-bold text-grey-7" style="font-size: 0.7rem;">Structure proposée :</div>
                    <div v-if="aiSuggestion.mode === 'simple'">
                      <div class="text-caption text-weight-bold text-secondary" style="font-size: 0.7rem;">Phases de financement :</div>
                      <div v-for="(p, i) in aiSuggestion.simplePhases" :key="i" class="text-caption q-pl-xs" style="font-size: 0.7rem;">
                        • {{ p.title }} : <strong>{{ p.goalAmount }} €</strong>
                      </div>
                    </div>
                    <div v-else>
                      <div class="text-caption text-weight-bold text-secondary" style="font-size: 0.7rem;">Sous-cagnottes et phases :</div>
                      <div v-for="(sp, i) in aiSuggestion.subPools" :key="i" class="text-caption q-pl-xs q-mb-xs" style="font-size: 0.7rem;">
                        <strong>{{ sp.title }}</strong> :
                        <div v-for="(p, j) in sp.phases" :key="j" class="text-caption q-pl-sm" style="font-size: 0.68rem; line-height: 1.1;">
                          - {{ p.title }} : {{ p.goalAmount }} €
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="row justify-between items-center">
                    <q-btn label="Passer et remplir manuellement" flat color="grey-6" size="xs" @click="currentStep = 3" no-caps />
                    <div class="row q-gutter-xs">
                      <q-btn label="Ignorer" flat color="grey-7" size="sm" @click="aiSuggestion = null" no-caps />
                      <q-btn label="Appliquer et continuer" color="amber-9" class="text-weight-bold text-dark" size="sm" @click="applyAiSuggestionAndGo" no-caps />
                    </div>
                  </div>
                </div>
              </q-slide-transition>
            </div>
          </q-slide-transition>

          <!-- Back button -->
          <div class="row justify-between q-mt-md" style="max-width: 600px; margin: 10px auto 0 auto;">
            <q-btn label="Précédent" flat color="grey-6" icon="arrow_back" @click="currentStep = 1" no-caps size="sm" />
            <q-btn v-if="!inPageChatActive" label="Remplir manuellement" color="primary" unelevated @click="currentStep = 3" no-caps size="sm" />
          </div>
        </div>

        <!-- STEP 3 — Informations générales -->
        <div v-show="currentStep === 3" class="q-px-xl q-py-lg animate-fade">
          <div class="text-h6 text-weight-bold text-dark q-mb-md">
            Informations générales de votre cagnotte
          </div>
          
          <div class="q-gutter-y-md">
            <div>
              <div class="text-caption text-grey-8 q-mb-xs">Titre de la cagnotte</div>
              <q-input
                outlined
                dense
                v-model="form.title"
                :label="$t('createPool.mainTitleLabel')"
                placeholder="Ex: Projet Éco-Solidaire Village"
                lazy-rules
                :rules="[ val => val && val.length > 0 || $t('createPool.titleRequired')]"
                color="secondary"
              />
            </div>

            <div>
              <div class="text-caption text-grey-8 q-mb-xs">Description</div>
              <q-input
                outlined
                dense
                v-model="form.description"
                type="textarea"
                :label="$t('createPool.descriptionLabel')"
                placeholder="Expliquez votre projet en détails..."
                lazy-rules
                :rules="[ val => val && val.length > 0 || $t('createPool.descriptionRequired')]"
                color="secondary"
                rows="3"
              />
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-8 q-mb-xs">Catégorie</div>
                <q-select
                  outlined
                  dense
                  v-model="form.category"
                  :options="categoryOptions"
                  emit-value
                  map-options
                  :label="$t('createPool.categoryLabel')"
                  color="secondary"
                />
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-8 q-mb-xs">Visibilité</div>
                <template v-if="isTontineMode">
                  <q-input
                    outlined
                    dense
                    readonly
                    model-value="Tontine Privée (Membres invités)"
                    label="Type de visibilité"
                    color="orange-8"
                  >
                    <template v-slot:prepend>
                      <q-icon name="lock" color="orange-8" size="xs" />
                    </template>
                  </q-input>
                  <div class="text-caption text-grey-6 q-mt-xs" style="font-size: 0.72rem; line-height: 1.2;">
                    Tontine: Collecte cyclique et fermée accessible uniquement aux membres invités.
                  </div>
                </template>
                <template v-else>
                  <q-select
                    outlined
                    dense
                    v-model="form.type"
                    :options="[
                      { value: 'PUBLIC', label: $t('createPool.typePublicRadio') },
                      { value: 'PRIVATE', label: $t('createPool.typePrivateRadio') }
                    ]"
                    emit-value
                    map-options
                    label="Type de visibilité"
                    color="secondary"
                  />
                  <div class="text-caption text-grey-6 q-mt-xs" style="font-size: 0.72rem; line-height: 1.2;">
                    {{ form.type === 'PUBLIC' ? 'Public: Visible de tous sur la plateforme.' : 'Privé: Accessible uniquement par lien direct.' }}
                  </div>
                </template>
              </div>
            </div>
          </div>

          <!-- Bottom navigation buttons -->
          <div class="row justify-between q-mt-lg">
            <q-btn label="Précédent" flat color="grey-6" icon="arrow_back" @click="currentStep = 2" no-caps size="sm" />
            <q-btn label="Continuer" color="primary" unelevated @click="validateStep3AndNext" no-caps size="sm" />
          </div>
        </div>

        <!-- STEP 4 — Configuration financière & étapes -->
        <div v-show="currentStep === 4" class="q-px-xl q-py-lg animate-fade">
          <div class="text-h6 text-weight-bold text-dark q-mb-md">
            {{ isTontineMode ? 'Paramètres & Fréquence de la Tontine' : 'Configuration financière & étapes' }}
          </div>

          <div class="q-gutter-y-md">
            <!-- Goal Amount & Deadline Row -->
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-8 q-mb-xs">
                  {{ isTontineMode ? 'Objectif du pot par tour' : 'Objectif financier total' }}
                </div>
                <q-input
                  outlined
                  dense
                  v-model.number="form.goalAmount"
                  type="number"
                  :label="isTontineMode ? 'Montant distribué au bénéficiaire à chaque tour' : $t('createPool.goalAmountLabel')"
                  suffix="€"
                  lazy-rules
                  :rules="[ val => val > 0 || $t('createPool.amountPositive')]"
                  color="secondary"
                  :readonly="!isTontineMode && (mode === 'multi' || (mode === 'simple' && form.simplePhases.length > 1))"
                  :hint="!isTontineMode && (mode === 'multi' || (mode === 'simple' && form.simplePhases.length > 1)) ? $t('createPool.calculatedFromPhasesHint') : ''"
                  @update:model-value="onTotalGoalUpdate"
                />
              </div>

              <div class="col-12 col-sm-6">
                <div class="row items-center justify-between q-mb-xs">
                  <div class="text-caption text-grey-8">Date limite (Optionnel)</div>
                  <q-toggle v-model="form.hasDeadline" color="secondary" size="sm" />
                </div>
                
                <q-input 
                  v-if="form.hasDeadline" 
                  outlined 
                  dense
                  v-model="form.deadlineDate" 
                  :label="$t('createPool.deadlineLabel')" 
                  placeholder="Sélectionnez une date limite"
                  :rules="[val => !form.hasDeadline || !!val || $t('createPool.deadlineRequired')]"
                >
                  <template v-slot:prepend>
                    <q-icon name="event" class="cursor-pointer">
                      <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                        <q-date v-model="form.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss">
                          <div class="row items-center justify-end">
                            <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                          </div>
                        </q-date>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer">
                      <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                        <q-time v-model="form.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss" format24h>
                          <div class="row items-center justify-end">
                            <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                          </div>
                        </q-time>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>

            <!-- TONTINE CONFIGURATION CARD (If isTontineMode) -->
            <div v-if="isTontineMode || form.type === 'PRIVATE_TONTINE'" class="q-pa-md rounded-borders bg-amber-1 q-mb-md" style="border: 1px solid #FCD34D;">
              <div class="text-subtitle2 text-weight-bold text-amber-10 q-mb-xs flex items-center">
                <q-icon name="tune" class="q-mr-xs" /> Paramètres de la Tontine Rotative
              </div>
              <div class="text-caption text-grey-8 q-mb-md" style="font-size: 0.75rem;">
                Le montant du pot sera équitablement divisé entre tous les participants pour chaque période de tour.
              </div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-sm-6">
                  <div class="text-caption text-grey-8 q-mb-xs">Durée / Fréquence d'un tour</div>
                  <q-select
                    outlined
                    dense
                    v-model="form.tontineFrequency"
                    :options="[
                      { value: 'WEEKLY', label: 'Hebdomadaire (1 semaine)' },
                      { value: 'MONTHLY', label: 'Mensuelle (1 mois)' },
                      { value: 'YEARLY', label: 'Annuelle (1 an)' }
                    ]"
                    emit-value
                    map-options
                    color="secondary"
                  />
                </div>
                <div class="col-12 col-sm-6">
                  <div class="text-caption text-grey-8 q-mb-xs">Pénalité de retard (% / jour de retard)</div>
                  <q-input outlined dense v-model.number="form.latePenaltyRate" type="number" suffix="%" color="secondary" />
                </div>
              </div>
            </div>

            <!-- PHASES SECTION (Only if mode === 'simple' and !isTontineMode) -->
            <div v-if="mode === 'simple' && !isTontineMode" class="q-mt-md">
              <div class="row items-center justify-between q-mb-sm">
                <div class="text-subtitle2 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#0D1B2E', borderBottom: '2px solid #FFB300' }" style="display: inline-block;">
                  {{ $t('createPool.phasesHeaderSimple') }}
                </div>
                <q-btn 
                  :label="$t('createPool.addPhaseButton')" 
                  icon="add" 
                  color="primary" 
                  outline 
                  dense
                  size="sm"
                  no-caps 
                  @click="addSimplePhase" 
                />
              </div>

              <div class="q-gutter-y-sm">
                <div v-for="(phase, phIndex) in form.simplePhases" :key="phIndex" class="row q-col-gutter-sm items-center q-mb-sm">
                  <div class="col-6">
                    <q-input 
                      outlined 
                      v-model="phase.title" 
                      dense 
                      :label="$t('createPool.phaseTitleLabel')" 
                      placeholder="Ex: Achat de matériel" 
                      bg-white
                      :rules="[val => !!val || $t('createPool.titleRequired')]"
                    />
                  </div>
                  <div class="col-4">
                    <q-input 
                      outlined 
                      v-model.number="phase.goalAmount" 
                      dense 
                      type="number"
                      :label="$t('createPool.phaseGoalLabel')" 
                      suffix="€" 
                      bg-white
                      :rules="[val => val > 0 || $t('createPool.amountPositive')]"
                      @update:model-value="calculateTotalGoal"
                    />
                  </div>
                  <div class="col-2 text-right">
                    <q-btn 
                      v-if="form.simplePhases.length > 1"
                      icon="delete" 
                      flat 
                      round 
                      dense 
                      color="negative" 
                      @click="removeSimplePhase(phIndex)" 
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- SOUS-CAGNOTTES SECTION (Only if mode === 'multi') -->
            <div v-if="mode === 'multi'" class="q-mt-md">
              <div class="row items-center justify-between q-mb-sm">
                <div class="text-subtitle2 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#0D1B2E', borderBottom: '2px solid #FFB300' }" style="display: inline-block;">
                  {{ $t('createPool.step3ComplexHeader') }}
                </div>
                <q-btn 
                  :label="$t('createPool.addSubPoolButton')" 
                  icon="add" 
                  color="primary" 
                  outline 
                  dense
                  size="sm"
                  no-caps 
                  @click="addSubPool" 
                />
              </div>

              <div v-for="(subPool, spIndex) in form.subPools" :key="spIndex" class="q-pa-sm q-mb-sm bg-grey-2 rounded-borders border-accent relative-position" :style="{ borderInlineStart: $q.dark.isActive ? '6px solid #FFA726' : '6px solid #0D1B2E' }">
                <q-btn 
                  v-if="form.subPools.length > 1"
                  icon="close" 
                  flat 
                  round 
                  dense 
                  color="negative" 
                  class="absolute-top-right q-ma-xs"
                  size="sm"
                  @click="removeSubPool(spIndex)" 
                />
                
                <div class="text-caption text-weight-bold q-mb-xs">{{ $t('createPool.subPoolTitleDefault') }} {{ spIndex + 1 }}</div>

                <div class="q-gutter-y-sm">
                  <q-input 
                    outlined 
                    v-model="subPool.title" 
                    dense 
                    :label="$t('createPool.subPoolTitleLabel')" 
                    placeholder="Ex: Phase 1: Fondation" 
                    bg-white
                    :rules="[val => !!val || $t('createPool.titleRequired')]"
                  />

                  <q-input 
                    outlined 
                    v-model="subPool.description" 
                    dense 
                    type="textarea"
                    rows="2"
                    :label="$t('createPool.subPoolDescLabel')" 
                    placeholder="Détails de cette sous-cagnotte..." 
                    bg-white
                  />

                  <div class="row items-center justify-between">
                    <span class="text-caption text-grey-8">{{ $t('createPool.subPoolDeadlineToggle') }}</span>
                    <q-toggle v-model="subPool.hasDeadline" dense color="secondary" />
                  </div>

                  <q-input 
                    v-if="subPool.hasDeadline" 
                    outlined 
                    dense 
                    v-model="subPool.deadlineDate" 
                    :label="$t('createPool.deadlineLabel')" 
                    bg-white
                    :rules="[val => !subPool.hasDeadline || !!val || $t('createPool.deadlineRequired')]"
                  >
                    <template v-slot:prepend>
                      <q-icon name="event" class="cursor-pointer">
                        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                          <q-date v-model="subPool.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss">
                            <div class="row items-center justify-end">
                              <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                            </div>
                          </q-date>
                        </q-popup-proxy>
                      </q-icon>
                    </template>
                    <template v-slot:append>
                      <q-icon name="access_time" class="cursor-pointer">
                        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                          <q-time v-model="subPool.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss" format24h>
                            <div class="row items-center justify-end">
                              <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                            </div>
                          </q-time>
                        </q-popup-proxy>
                      </q-icon>
                    </template>
                  </q-input>

                  <!-- PHASES for this subPool -->
                  <div class="q-mt-md">
                    <div class="row items-center justify-between q-mb-sm">
                      <span class="text-weight-bold text-caption">{{ $t('createPool.subPoolPhasesHeader') }}</span>
                      <q-btn 
                        :label="$t('createPool.addSubPoolPhaseButton')" 
                        icon="add" 
                        dense 
                        flat 
                        color="secondary" 
                        no-caps 
                        @click="addPhaseToSubPool(spIndex)" 
                      />
                    </div>

                    <div v-for="(phase, phIndex) in subPool.phases" :key="phIndex" class="row q-col-gutter-sm items-center q-mb-xs">
                      <div class="col-6">
                        <q-input 
                          outlined 
                          v-model="phase.title" 
                          dense 
                          :label="$t('createPool.phaseTitleLabel')" 
                          placeholder="Ex: Étape 1" 
                          bg-white
                          :rules="[val => !!val || $t('createPool.titleRequired')]"
                        />
                      </div>
                      <div class="col-4">
                        <q-input 
                          outlined 
                          v-model.number="phase.goalAmount" 
                          dense 
                          type="number"
                          :label="$t('createPool.phaseGoalLabel')" 
                          suffix="€" 
                          bg-white
                          :rules="[val => val > 0 || $t('createPool.amountPositive')]"
                          @update:model-value="calculateTotalGoal"
                        />
                      </div>
                      <div class="col-2 text-right">
                        <q-btn 
                          v-if="subPool.phases.length > 1"
                          icon="delete" 
                          flat 
                          round 
                          dense 
                          color="negative" 
                          @click="removePhaseFromSubPool(spIndex, phIndex)" 
                        />
                      </div>
                    </div>
                  </div>

                  <div class="text-right text-caption text-weight-bold text-primary q-mt-xs">
                    {{ $t('createPool.subPoolGoalLabel') }} {{ getSubPoolGoal(subPool) }} €
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Bottom navigation buttons -->
          <div class="row justify-between q-mt-lg">
            <q-btn label="Précédent" flat color="grey-6" icon="arrow_back" @click="currentStep = 3" no-caps size="sm" />
            <q-btn label="Continuer" color="primary" unelevated @click="validateStep4AndNext" no-caps size="sm" />
          </div>
        </div>

        <!-- STEP 5 — Vérification & Médias -->
        <div v-show="currentStep === 5" class="q-px-xl q-py-lg animate-fade">
          <div class="text-h6 text-weight-bold text-dark q-mb-md">
            Médias & Vérification finale
          </div>

          <div class="row q-col-gutter-md">
            <!-- Media Upload Column -->
            <div class="col-12 col-md-6 q-gutter-y-md">
              <!-- Image Upload -->
              <div class="q-pa-sm bg-grey-1 rounded-borders border-light">
                <div class="text-caption text-grey-8 q-mb-xs">Image de couverture</div>
                <q-file
                  outlined
                  dense
                  v-model="imageFile"
                  :label="$t('createPool.imageLabel')"
                  accept=".jpg, .jpeg, .png, .webp"
                  @update:model-value="onFileSelected"
                  color="secondary"
                  bg-color="white"
                >
                  <template v-slot:prepend>
                    <q-icon name="cloud_upload" />
                  </template>
                </q-file>
                
                <div v-if="imagePreview" class="q-mt-sm row justify-center">
                  <q-img
                    :src="imagePreview"
                    style="max-width: 100%; height: 110px; border-radius: 8px; border: 1px solid rgba(0,0,0,0.1);"
                    fit="cover"
                  />
                </div>
              </div>

              <!-- Video Section -->
              <div class="q-pa-sm bg-grey-1 rounded-borders border-light">
                <div class="text-caption text-grey-8 q-mb-xs">Vidéo du projet (Optionnel)</div>
                
                <q-tabs
                  v-model="videoSource"
                  dense
                  class="text-grey"
                  active-color="primary"
                  indicator-color="primary"
                  align="left"
                  narrow-indicator
                  no-caps
                  style="background: transparent;"
                >
                  <q-tab name="url" :label="$t('createPool.videoTabUrl')" style="font-size: 0.72rem;" />
                  <q-tab name="upload" :label="$t('createPool.videoTabUpload')" style="font-size: 0.72rem;" />
                </q-tabs>

                <q-tab-panels v-model="videoSource" animated style="background: transparent;">
                  <q-tab-panel name="url" class="q-pa-none q-pt-xs">
                    <q-input
                      outlined
                      dense
                      v-model="form.videoUrl"
                      :label="$t('createPool.videoUrlLabel')"
                      placeholder="https://www.youtube.com/watch?v=..."
                      color="secondary"
                      bg-color="white"
                      style="font-size: 0.8rem;"
                    >
                      <template v-slot:prepend>
                        <q-icon name="link" size="xs" />
                      </template>
                    </q-input>
                  </q-tab-panel>

                  <q-tab-panel name="upload" class="q-pa-none q-pt-xs">
                    <q-file
                      outlined
                      dense
                      v-model="videoFile"
                      :label="$t('createPool.videoTabUpload')"
                      accept="video/*"
                      color="secondary"
                      bg-color="white"
                      style="font-size: 0.8rem;"
                      @update:model-value="onVideoSelected"
                    >
                      <template v-slot:prepend>
                        <q-icon name="movie_filter" size="xs" />
                      </template>
                    </q-file>
                  </q-tab-panel>
                </q-tab-panels>
                
                <div v-if="videoFilePreview && videoSource === 'upload'" class="q-mt-sm">
                  <video controls style="width: 100%; max-height: 110px; border-radius: 8px;">
                    <source :src="videoFilePreview" type="video/mp4">
                  </video>
                </div>

                <div v-if="videoPreviewId && videoSource === 'url'" class="q-mt-sm">
                  <q-video
                    :ratio="16/9"
                    :src="`https://www.youtube.com/embed/${videoPreviewId}`"
                    style="border-radius: 8px; max-height: 110px;"
                  />
                </div>
              </div>
            </div>

            <!-- Summary Column -->
            <div class="col-12 col-md-6">
              <q-card class="bg-grey-1 no-shadow rounded-borders q-pa-sm border-light" style="height: 100%;">
                <div class="text-caption text-weight-bold text-dark q-mb-sm">
                  {{ isTontineMode ? 'Récapitulatif de la tontine' : 'Récapitulatif de la cagnotte' }}
                </div>
                
                <div class="q-gutter-y-xs text-caption">
                  <div class="row"><span class="col-4 text-grey-6 text-weight-medium">Titre :</span><strong class="col-8 text-dark">{{ form.title }}</strong></div>
                  <div class="row"><span class="col-4 text-grey-6 text-weight-medium">Catégorie :</span><span class="col-8 text-dark">{{ form.category }}</span></div>
                  <div class="row"><span class="col-4 text-grey-6 text-weight-medium">Type :</span><span class="col-8 text-dark">{{ isTontineMode ? 'Tontine Rotative' : (mode === 'simple' ? 'Simple' : 'Avec Phases') }}</span></div>
                  <div class="row"><span class="col-4 text-grey-6 text-weight-medium">Visibilité :</span><q-badge :label="isTontineMode ? 'Tontine Privée' : form.type" :color="isTontineMode ? 'orange-8' : 'secondary'" style="font-size: 0.65rem;" /></div>
                  <div v-if="isTontineMode" class="row"><span class="col-4 text-grey-6 text-weight-medium">Fréquence :</span><span class="col-8 text-dark text-weight-bold">{{ form.tontineFrequency === 'WEEKLY' ? 'Hebdomadaire' : (form.tontineFrequency === 'YEARLY' ? 'Annuelle' : 'Mensuelle') }}</span></div>
                  <div v-if="isTontineMode && form.latePenaltyRate" class="row"><span class="col-4 text-grey-6 text-weight-medium">Pénalité :</span><span class="col-8 text-dark">{{ form.latePenaltyRate }} % / jour</span></div>
                  <div class="row"><span class="col-4 text-grey-6 text-weight-medium">{{ isTontineMode ? 'Pot par tour :' : 'Objectif :' }}</span><strong class="col-8 text-primary">{{ form.goalAmount }} €</strong></div>
                  <div v-if="form.hasDeadline" class="row">
                    <span class="col-4 text-grey-6 text-weight-medium">Date limite :</span>
                    <span class="col-8 text-dark">{{ form.deadlineDate ? form.deadlineDate.replace('T', ' ') : '' }}</span>
                  </div>
                </div>

                <template v-if="!isTontineMode">
                  <q-separator class="q-my-sm" />

                  <div class="text-caption text-weight-bold text-grey-7 q-mb-xs">Structure des étapes :</div>
                  <div style="max-height: 120px; overflow-y: auto;" class="q-pr-xs">
                    <div v-if="mode === 'simple'">
                      <div v-for="(p, idx) in form.simplePhases" :key="idx" class="row justify-between items-center q-py-xs border-bottom">
                        <span class="text-caption text-grey-9" style="font-size: 0.72rem;">• {{ p.title }}</span>
                        <strong class="text-caption text-dark" style="font-size: 0.72rem;">{{ p.goalAmount }} €</strong>
                      </div>
                    </div>
                    <div v-else>
                      <div v-for="(sp, spIdx) in form.subPools" :key="spIdx" class="q-mb-xs">
                        <div class="text-caption text-weight-bold text-secondary" style="font-size: 0.72rem;">{{ sp.title }} ({{ getSubPoolGoal(sp) }} €)</div>
                        <div v-for="(p, pIdx) in sp.phases" :key="pIdx" class="row justify-between items-center q-pl-sm q-py-xs border-bottom">
                          <span class="text-caption text-grey-7" style="font-size: 0.68rem;">- {{ p.title }}</span>
                          <span class="text-caption text-grey-9 text-dark" style="font-size: 0.68rem;">{{ p.goalAmount }} €</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </q-card>
            </div>
          </div>

          <!-- Bottom navigation and submit buttons -->
          <div class="row justify-between items-center q-mt-lg">
            <q-btn label="Précédent" flat color="grey-6" icon="arrow_back" @click="currentStep = 4" no-caps size="sm" />
            <div class="row q-gutter-x-sm">
              <q-btn
                :label="isTontineMode ? 'Lancer ma tontine' : $t('createPool.submitBtn')"
                type="submit"
                unelevated
                class="q-px-lg q-py-xs text-weight-bold animate-pulse"
                :style="{
                  background: isTontineMode ? '#F59E0B' : '#FFB300',
                  color: isTontineMode ? '#FFFFFF' : '#1A1A2A',
                  borderRadius: '8px'
                }"
                :loading="loading"
                no-caps
                size="sm"
              />
            </div>
          </div>
        </div>

      </q-form>
    </q-card>

    <!-- Floating PotiBuddy Assistant -->
    <div v-if="showFloatingBuddy" class="potibuddy-floating-container" :class="{ 'buddy-minimized': isBuddyMinimized }">
      <!-- Chat Window -->
      <q-slide-transition>
        <div v-if="!isBuddyMinimized" class="buddy-chat-window shadow-15">
          <!-- Header -->
          <div class="buddy-header row items-center justify-between q-px-md q-py-sm">
            <div class="row items-center q-col-gutter-x-sm">
              <div class="col-auto relative-position">
                <q-avatar size="32px" class="buddy-header-avatar shadow-1">
                  <img src="~assets/poti.jpeg" style="object-fit: cover;" />
                </q-avatar>
                <span class="buddy-online-indicator"></span>
              </div>
              <div class="col">
                <div class="text-subtitle2 text-weight-bold text-white leading-tight">PotiBuddy</div>
                <div class="text-caption text-amber-3" style="font-size: 0.72rem; line-height: 1;">Assistant IA • En ligne</div>
              </div>
            </div>
            <div class="row items-center q-gutter-x-xs">
              <q-btn flat round dense size="xs" icon="refresh" color="white" @click="resetChat">
                <q-tooltip>Réinitialiser</q-tooltip>
              </q-btn>
              <q-btn flat round dense size="xs" icon="minimize" color="white" @click="isBuddyMinimized = true" />
              <q-btn flat round dense size="xs" icon="close" color="white" @click="showFloatingBuddy = false" />
            </div>
          </div>

          <!-- Chat Area -->
          <div ref="floatingChatContainer" class="buddy-messages q-pa-md">
            <div v-for="(msg, idx) in chatHistory" :key="idx" class="q-mb-md">
              <!-- Bot Message -->
              <div v-if="msg.role === 'assistant'" class="row items-start justify-start q-col-gutter-xs">
                <div class="col" style="max-width: 90%;">
                  <div class="buddy-msg-bubble assistant-bubble text-body2">
                    {{ msg.content }}
                  </div>
                </div>
              </div>
              <!-- User Message -->
              <div v-else class="row items-start justify-end q-col-gutter-xs">
                <div class="col text-right" style="max-width: 90%;">
                  <div class="buddy-msg-bubble user-bubble text-body2 text-left inline-block">
                    {{ msg.content }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Typing Indicator -->
            <div v-if="aiLoading" class="row items-start justify-start q-mb-md">
              <div class="buddy-msg-bubble assistant-bubble text-body2 inline-block">
                <q-spinner-dots color="primary" size="sm" />
              </div>
            </div>

            <!-- AI Suggestion Apply Card in Floating Chat -->
            <div v-if="aiSuggestion" class="buddy-suggestion-card q-pa-sm q-mt-sm rounded-borders">
              <div class="row items-center justify-between no-wrap">
                <div class="text-caption text-weight-bold text-amber-9 row items-center" style="font-size: 0.72rem;">
                  <q-icon name="auto_awesome" class="q-mr-xs" color="amber-9" />
                  <span>Suggestion !</span>
                </div>
                <div class="row no-wrap q-gutter-x-xs">
                  <q-btn label="Ignorer" flat dense color="grey-7" size="xs" class="text-weight-bold" style="font-size: 0.65rem;" @click="aiSuggestion = null" />
                  <q-btn label="Voir" flat dense color="primary" size="xs" class="text-weight-bold" style="font-size: 0.65rem;" @click="showSuggestionDialog = true" />
                  <q-btn label="Appliquer" dense unelevated color="amber-9" text-color="dark" size="xs" class="text-weight-bold q-px-xs" style="font-size: 0.65rem;" @click="applyAiSuggestionAndGo" />
                </div>
              </div>
            </div>
          </div>

          <!-- Input Area -->
          <div class="buddy-input-area q-px-sm q-py-xs row items-center q-gutter-x-xs">
            <q-input
              v-model="aiPrompt"
              dense
              outlined
              bg-color="white"
              placeholder="Décrivez votre projet..."
              class="col"
              style="font-size: 0.82rem;"
              @keyup.enter="sendChatMessage"
            />
            <q-btn
              round
              dense
              unelevated
              color="primary"
              icon="send"
              size="sm"
              :loading="aiLoading"
              @click="sendChatMessage"
            />
          </div>
        </div>
      </q-slide-transition>

      <!-- Avatar Button (Floating Avatar) -->
      <div class="buddy-avatar-btn shadow-10 cursor-pointer" @click="toggleBuddy">
        <div class="avatar-breath-ring"></div>
        <q-avatar size="56px" class="buddy-avatar-img">
          <img src="~assets/poti.jpeg" style="object-fit: cover;" />
          <div class="buddy-online-dot"></div>
        </q-avatar>
        <div v-if="isBuddyMinimized" class="buddy-tooltip shadow-2">
          Besoin d'aide ? <span>✨</span>
        </div>
      </div>

      <!-- Dialog for AI Suggestion Details -->
      <q-dialog v-model="showSuggestionDialog" backdrop-filter="blur(4px)">
        <q-card style="width: 100%; max-width: 500px; border-radius: 16px;">
          <q-card-section class="row items-center q-pb-none">
            <div class="row items-center">
              <q-icon name="auto_awesome" color="amber-9" size="24px" class="q-mr-sm" />
              <div class="text-subtitle1 text-weight-bold text-dark">Proposition de PotiBuddy</div>
            </div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="q-pa-md" v-if="aiSuggestion">
            <div class="q-mb-md">
              <div class="text-caption text-grey-6 text-weight-medium">Titre suggéré</div>
              <div class="text-subtitle2 text-weight-bold text-dark q-mt-xs">{{ aiSuggestion.title }}</div>
            </div>

            <div class="q-mb-md">
              <div class="text-caption text-grey-6 text-weight-medium">Description</div>
              <div class="text-body2 text-grey-8 q-mt-xs" style="line-height: 1.5; white-space: pre-line;">
                {{ aiSuggestion.description }}
              </div>
            </div>

            <div class="row q-col-gutter-sm q-mb-md">
              <div class="col-6">
                <div class="text-caption text-grey-6 text-weight-medium">Budget total</div>
                <div class="text-subtitle2 text-weight-bold text-dark q-mt-xs">{{ aiSuggestion.goalAmount }} €</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-6 text-weight-medium">Catégorie</div>
                <q-badge :label="aiSuggestion.category" color="primary" class="q-mt-xs text-weight-bold" />
              </div>
            </div>

            <div class="q-pa-md bg-grey-2 rounded-borders border-light" style="border-radius: 12px;">
              <div class="text-caption text-weight-bold text-dark q-mb-xs">Structure proposée</div>
              <div v-if="aiSuggestion.mode === 'simple'">
                <div class="text-caption text-weight-medium text-grey-7 q-mb-xs">Phases de financement :</div>
                <div v-for="(p, i) in aiSuggestion.simplePhases" :key="i" class="text-caption q-pl-sm q-py-xs border-top-divider row justify-between">
                  <span>{{ p.title }}</span>
                  <span class="text-weight-bold text-dark">{{ p.goalAmount }} €</span>
                </div>
              </div>
              <div v-else>
                <div class="text-caption text-weight-medium text-grey-7 q-mb-xs">Sous-cagnottes et étapes :</div>
                <div v-for="(sp, i) in aiSuggestion.subPools" :key="i" class="q-mb-sm">
                  <div class="text-caption text-weight-bold text-dark">{{ sp.title }}</div>
                  <div v-for="(p, j) in sp.phases" :key="j" class="text-caption q-pl-md row justify-between text-grey-8">
                    <span>- {{ p.title }}</span>
                    <span>{{ p.goalAmount }} €</span>
                  </div>
                </div>
              </div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-pa-md">
            <q-btn label="Ignorer" flat color="grey-7" v-close-popup no-caps class="text-weight-bold" />
            <q-btn label="Appliquer cette suggestion" color="amber-9" text-color="dark" unelevated no-caps class="text-weight-bold q-px-md" style="border-radius: 8px;" @click="applyAiSuggestionAndGoFromDialog" />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, watch } from 'vue'
import { poolApi } from 'boot/axios'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import authStore from '../../shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const loading = ref(false)
const imageFile = ref(null)

const isTontineMode = computed(() => {
  return route.query.type === 'tontine' || route.query.mode === 'tontine'
})

const aiPrompt = ref('')
const aiLoading = ref(false)
const aiSuggestion = ref(null)
const showSuggestionDialog = ref(false)

// Stepper navigation states
const currentStep = ref(1)
const showFloatingBuddy = ref(false)
const isBuddyMinimized = ref(true)
const inPageChatActive = ref(false)

const stepperLineWidth = computed(() => {
  return ((currentStep.value - 1) / 4) * 80 // 80% total span between step 1 (0%) and step 5 (80%)
})

const chatHistory = ref([
  {
    role: 'assistant',
    content: t('createPool.aiDesc') || "Je suis PotiBuddy, votre assistant intelligent. Décrivez votre projet de cagnotte en quelques mots, et je vous aiderai à le concevoir, le structurer et remplir le formulaire automatiquement (titre, budget, description et phases de financement) !"
  }
])
const apiHistory = ref([])
const chatContainer = ref(null)
const floatingChatContainer = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
    if (floatingChatContainer.value) {
      floatingChatContainer.value.scrollTop = floatingChatContainer.value.scrollHeight
    }
  })
}

const sendChatMessage = async () => {
  const userText = aiPrompt.value.trim()
  if (!userText) {
    $q.notify({
      type: 'warning',
      message: t('createPool.aiPromptWarning') || 'Veuillez saisir un message.'
    })
    return
  }

  // Add to UI history
  chatHistory.value.push({
    role: 'user',
    content: userText
  })

  // Add to API history
  apiHistory.value.push({
    role: 'user',
    content: userText
  })

  aiPrompt.value = ''
  aiLoading.value = true
  scrollToBottom()

  try {
    const response = await poolApi.post('/pools/ai/chat', {
      messages: apiHistory.value,
      mode: mode.value
    })

    const suggestion = response.data
    aiSuggestion.value = suggestion

    // Add bot response to UI history
    chatHistory.value.push({
      role: 'assistant',
      content: suggestion.botReply || t('createPool.aiSuccessMsg') || 'Voici ma suggestion.'
    })

    // Add bot raw JSON to API history so it preserves full state
    apiHistory.value.push({
      role: 'assistant',
      content: JSON.stringify(suggestion)
    })

    $q.notify({
      type: 'positive',
      message: t('createPool.aiSuccessMsg') || 'Suggestion générée avec succès !',
      position: 'top-right'
    })
  } catch (error) {
    console.error(error)
    // Remove last user message from API history to let user retry
    apiHistory.value.pop()
    chatHistory.value.push({
      role: 'assistant',
      content: "Désolé, je n'ai pas pu générer de suggestion. Veuillez réessayer."
    })
    $q.notify({
      type: 'negative',
      message: (t('createPool.createError') || 'Erreur: ') + (error.response?.data?.message || error.response?.data?.error || 'Serveur injoignable'),
      position: 'top-right'
    })
  } finally {
    aiLoading.value = false
    scrollToBottom()
  }
}

const resetChat = () => {
  chatHistory.value = [
    {
      role: 'assistant',
      content: t('createPool.aiDesc') || "Je suis PotiBuddy, votre assistant intelligent. Décrivez votre projet de cagnotte en quelques mots, et je vous aiderai à le concevoir, le structurer et remplir le formulaire automatiquement (titre, budget, description et phases de financement) !"
    }
  ]
  apiHistory.value = []
  aiSuggestion.value = null
  $q.notify({
    type: 'info',
    message: 'Discussion réinitialisée.',
    position: 'top-right'
  })
}

const applyAiSuggestion = () => {
  if (!aiSuggestion.value) return
  
  const sugg = aiSuggestion.value
  
  form.title = sugg.title || ''
  form.description = sugg.description || ''
  
  if (categories.includes(sugg.category)) {
    form.category = sugg.category
  } else {
    form.category = 'Projets'
  }
  
  mode.value = sugg.mode || 'simple'
  
  if (mode.value === 'simple') {
    if (sugg.simplePhases && sugg.simplePhases.length > 0) {
      form.simplePhases = sugg.simplePhases.map(p => ({
        title: p.title,
        goalAmount: Number(p.goalAmount) || 0
      }))
    } else {
      form.simplePhases = [{ title: t('createPool.phaseUniqueTitle') || 'Phase Unique', goalAmount: Number(sugg.goalAmount) || 1000 }]
    }
  } else {
    if (sugg.subPools && sugg.subPools.length > 0) {
      form.subPools = sugg.subPools.map(sp => ({
        title: sp.title || t('createPool.subPoolTitleDefault') || 'Sous-cagnotte',
        description: sp.description || '',
        hasDeadline: !!sp.hasDeadline,
        deadlineDate: '',
        phases: sp.phases && sp.phases.length > 0 ? sp.phases.map(p => ({
          title: p.title,
          goalAmount: Number(p.goalAmount) || 0
        })) : [{ title: t('createPool.stepDefault1Title') || 'Étape 1', goalAmount: 500 }]
      }))
    } else {
      form.subPools = [{
        title: t('createPool.subPoolDefault1Title') || 'Sous-cagnotte principale',
        description: t('createPool.subPoolDefault1Desc') || 'Première sous-cagnotte',
        hasDeadline: false,
        deadlineDate: '',
        phases: [{ title: t('createPool.stepDefault1Title') || 'Étape 1', goalAmount: Number(sugg.goalAmount) || 1000 }]
      }]
    }
  }
  
  calculateTotalGoal()
  
  $q.notify({
    icon: 'auto_awesome',
    message: t('createPool.aiAppliedMsg') || 'Suggestion appliquée avec succès !',
    color: 'primary',
    position: 'top-right'
  })
}

const selectModeAndNext = (selectedMode) => {
  setMode(selectedMode)
  currentStep.value = 2
}

const chooseAi = (useAi) => {
  if (useAi) {
    inPageChatActive.value = true
    showFloatingBuddy.value = true
    isBuddyMinimized.value = false
    currentStep.value = 3
  } else {
    inPageChatActive.value = false
    currentStep.value = 3
  }
}

const goToStep = (step) => {
  if (step < currentStep.value) {
    currentStep.value = step
  } else if (step === 3 && currentStep.value >= 2) {
    currentStep.value = 3
  } else if (step === 4 && currentStep.value >= 3 && form.title && form.description) {
    currentStep.value = 4
  } else if (step === 5 && currentStep.value >= 4 && form.goalAmount > 0) {
    currentStep.value = 5
  }
}

const toggleBuddy = () => {
  isBuddyMinimized.value = !isBuddyMinimized.value
  if (!showFloatingBuddy.value) {
    showFloatingBuddy.value = true
  }
}

const applyAiSuggestionAndGo = () => {
  applyAiSuggestion()
  currentStep.value = 3
}

const applyAiSuggestionAndGoFromDialog = () => {
  showSuggestionDialog.value = false
  applyAiSuggestionAndGo()
}

const validateStep3AndNext = () => {
  if (!form.title || !form.title.trim()) {
    $q.notify({ type: 'warning', message: t('createPool.titleRequired') || 'Le titre est requis.' })
    return
  }
  if (!form.description || !form.description.trim()) {
    $q.notify({ type: 'warning', message: t('createPool.descriptionRequired') || 'La description est requise.' })
    return
  }
  currentStep.value = 4
}

const validateStep4AndNext = () => {
  calculateTotalGoal()
  if (form.goalAmount <= 0) {
    $q.notify({ type: 'warning', message: t('createPool.amountPositive') || 'Le montant de l\'objectif doit être positif.' })
    return
  }
  if (form.hasDeadline && !form.deadlineDate) {
    $q.notify({ type: 'warning', message: t('createPool.deadlineRequired') || 'La date limite est requise.' })
    return
  }
  // Check subpools/phases
  if (mode.value === 'multi') {
    for (const sp of form.subPools) {
      if (!sp.title || !sp.title.trim()) {
        $q.notify({ type: 'warning', message: 'Titre de la sous-cagnotte requis.' })
        return
      }
      for (const ph of sp.phases) {
        if (!ph.title || !ph.title.trim()) {
          $q.notify({ type: 'warning', message: 'Titre de l\'étape requis.' })
          return
        }
        if (ph.goalAmount <= 0) {
          $q.notify({ type: 'warning', message: 'Le montant des étapes doit être supérieur à 0.' })
          return
        }
      }
    }
  } else {
    for (const ph of form.simplePhases) {
      if (!ph.title || !ph.title.trim()) {
        $q.notify({ type: 'warning', message: 'Titre de l\'étape requis.' })
        return
      }
      if (ph.goalAmount <= 0) {
        $q.notify({ type: 'warning', message: 'Le montant des étapes doit être supérieur à 0.' })
        return
      }
    }
  }
  currentStep.value = 5
}

const imagePreview = ref(null)
const videoFile = ref(null)
const videoFilePreview = ref(null)
const videoSource = ref('url')

const mode = ref('simple')

const setMode = (newMode) => {
  mode.value = newMode
  calculateTotalGoal()
}

const onVideoSelected = (file) => {
  if (file) {
    videoFilePreview.value = URL.createObjectURL(file)
  } else {
    videoFilePreview.value = null
  }
}

const categories = ['Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']
const getCategoryLabel = (catName) => {
  const keyMap = {
    'Santé': 'catHealth',
    'Éducation': 'catEducation',
    'Urgence': 'catEmergency',
    'Animaux': 'catAnimals',
    'Projets': 'catProjects',
    'Sport': 'catSport'
  }
  const key = keyMap[catName]
  return key ? t(`home.${key}`) : catName
}
const categoryOptions = computed(() => categories.map(cat => ({
  value: cat,
  label: getCategoryLabel(cat)
})))

const form = reactive({
  title: '',
  description: '',
  category: 'Santé',
  goalAmount: 1000,
  type: route.query.type === 'tontine' || route.query.mode === 'tontine' ? 'PRIVATE_TONTINE' : 'PUBLIC',
  contributionAmount: 100,
  tontineFrequency: 'MONTHLY',
  latePenaltyRate: 2.0,
  ownerId: '',
  videoUrl: '',
  parentId: route.query.parentId || null,
  hasDeadline: false,
  deadlineDate: '',
  simplePhases: [
    { title: t('createPool.phaseUniqueTitle') || 'Phase Unique', goalAmount: 1000 }
  ],
  subPools: [
    {
      title: t('createPool.subPoolDefault1Title') || 'Sous-cagnotte principale',
      description: t('createPool.subPoolDefault1Desc') || 'Première sous-cagnotte',
      hasDeadline: false,
      deadlineDate: '',
      phases: [
        { title: t('createPool.stepDefault1Title') || 'Étape 1', goalAmount: 500 }
      ]
    }
  ]
})

const syncTontineState = () => {
  if (isTontineMode.value) {
    form.type = 'PRIVATE_TONTINE'
    mode.value = 'simple'
  } else {
    if (form.type === 'PRIVATE_TONTINE') {
      form.type = 'PUBLIC'
    }
  }
}

onMounted(() => {
  syncTontineState()
})

watch(() => route.query, () => {
  syncTontineState()
}, { deep: true })

const addSubPool = () => {
  form.subPools.push({
    title: `${t('createPool.subPoolTitleDefault') || 'Sous-cagnotte'} ${form.subPools.length + 1}`,
    description: '',
    hasDeadline: false,
    deadlineDate: '',
    phases: [
      { title: t('createPool.stepDefault1Title') || 'Étape 1', goalAmount: 500 }
    ]
  })
  calculateTotalGoal()
}

const removeSubPool = (index) => {
  if (form.subPools.length > 1) {
    form.subPools.splice(index, 1)
    calculateTotalGoal()
  }
}

const addPhaseToSubPool = (spIndex) => {
  const stepWord = (t('createPool.stepDefault1Title') || 'Étape 1').replace('1', '').trim()
  form.subPools[spIndex].phases.push({
    title: `${stepWord} ${form.subPools[spIndex].phases.length + 1}`,
    goalAmount: 500
  })
  calculateTotalGoal()
}

const removePhaseFromSubPool = (spIndex, phIndex) => {
  if (form.subPools[spIndex].phases.length > 1) {
    form.subPools[spIndex].phases.splice(phIndex, 1)
    calculateTotalGoal()
  }
}

const addSimplePhase = () => {
  const stepWord = (t('createPool.stepDefault1Title') || 'Étape 1').replace('1', '').trim()
  if (form.simplePhases.length === 1 && form.simplePhases[0].title === (t('createPool.phaseUniqueTitle') || 'Phase Unique')) {
    form.simplePhases[0].title = `${stepWord} 1`
  }
  form.simplePhases.push({
    title: `${stepWord} ${form.simplePhases.length + 1}`,
    goalAmount: 500
  })
  calculateTotalGoal()
}

const removeSimplePhase = (index) => {
  if (form.simplePhases.length > 1) {
    form.simplePhases.splice(index, 1)
    if (form.simplePhases.length === 1) {
      form.simplePhases[0].title = t('createPool.phaseUniqueTitle') || 'Phase Unique'
    }
    calculateTotalGoal()
  }
}

const onTotalGoalUpdate = (val) => {
  if (mode.value === 'simple' && form.simplePhases.length === 1) {
    form.simplePhases[0].goalAmount = Number(val) || 0
  }
}

const getSubPoolGoal = (subPool) => {
  return subPool.phases.reduce((sum, phase) => sum + (Number(phase.goalAmount) || 0), 0)
}

const calculateTotalGoal = () => {
  if (mode.value === 'multi') {
    form.goalAmount = form.subPools.reduce((sum, subPool) => sum + getSubPoolGoal(subPool), 0)
  } else if (mode.value === 'simple') {
    form.goalAmount = form.simplePhases.reduce((sum, phase) => sum + (Number(phase.goalAmount) || 0), 0)
  }
}

const videoPreviewId = computed(() => {
  if (!form.videoUrl) return null
  const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/
  const match = form.videoUrl.match(regExp)
  return (match && match[2].length === 11) ? match[2] : null
})

const onFileSelected = (file) => {
  if (file) {
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imagePreview.value = null
  }
}


const onSubmit = async () => {
  loading.value = true
  try {
    const userId = authStore.user.value?.id
    
    if (!userId) {
      $q.notify({ type: 'negative', message: t('createPool.mustBeLoggedIn') || 'Vous devez être connecté.' })
      loading.value = false
      return
    }

    let imageData = null
    let imageContentType = null

    if (imageFile.value) {
      imageData = await new Promise((resolve) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.readAsDataURL(imageFile.value)
      })
      imageContentType = imageFile.value.type
    }

    let finalVideoUrl = form.videoUrl

    // Build the payload
    const mainPoolPayload = {
      title: form.title,
      description: form.description,
      category: form.category,
      goalAmount: form.goalAmount,
      type: form.type,
      ownerId: userId,
      imageData: imageData,
      imageContentType: imageContentType,
      videoUrl: finalVideoUrl,
      parentId: form.parentId,
      hasDeadline: form.hasDeadline,
      deadlineDate: form.hasDeadline ? form.deadlineDate : null,
      phases: [],
      subPools: []
    }

    if (mode.value === 'simple') {
      mainPoolPayload.phases = form.simplePhases.map(p => ({
        title: p.title,
        goalAmount: p.goalAmount
      }))
    } else {
      mainPoolPayload.subPools = form.subPools.map(sp => ({
        title: sp.title,
        description: sp.description,
        hasDeadline: sp.hasDeadline,
        deadlineDate: sp.hasDeadline ? sp.deadlineDate : null,
        phases: sp.phases.map(p => ({
          title: p.title,
          goalAmount: p.goalAmount
        }))
      }))
    }

    const response = await poolApi.post('/pools', mainPoolPayload)
    const mainPoolId = response.data.id

    // Si type Tontine, enregistrer la configuration tontine
    if (form.type === 'PRIVATE_TONTINE') {
      try {
        await poolApi.post(`/pools/${mainPoolId}/tontine/config`, {
          contributionAmount: form.contributionAmount || 100,
          frequency: form.tontineFrequency || 'MONTHLY',
          latePenaltyRate: form.latePenaltyRate || 0
        })
      } catch (err) {
        console.error('Erreur config tontine:', err)
      }
    }

    // Upload Video if selected
    if (videoSource.value === 'upload' && videoFile.value) {
      $q.notify({
        message: t('createPool.uploadingVideo') || 'Téléversement de la vidéo...',
        color: 'primary',
        icon: 'movie'
      })
      try {
        const formData = new FormData()
        formData.append('file', videoFile.value)
        
        await poolApi.post(`/pools/${mainPoolId}/video`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
      } catch (err) {
        console.error('Erreur upload vidéo:', err)
        $q.notify({
          type: 'negative',
          message: t('createPool.videoUploadError') || 'Erreur lors de l\'envoi de la vidéo.'
        })
      }
    }
    
    $q.notify({
      type: 'positive',
      message: t('createPool.success') || 'Cagnotte créée avec succès !',
      position: 'top'
    })
    
    router.push(`/pools/${mainPoolId}`)
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: (t('createPool.createError') || 'Erreur: ') + (error.response?.data?.message || 'Serveur injoignable'),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}
</script>

<script>
// For custom animations or imports if needed
</script>

<style scoped>
.q-card {
  transition: all 0.3s ease;
}
.phase-config {
  transition: all 0.3s ease;
}

/* Stepper progress */
.stepper-progress-wrapper {
  position: relative;
}
.stepper-line-bg {
  position: absolute;
  top: 18px;
  left: 24px;
  right: 24px;
  height: 3px;
  background: #EAEAEA;
  z-index: 1;
  border-radius: 2px;
}
.stepper-line-fill {
  position: absolute;
  top: 18px;
  left: 24px;
  height: 3px;
  background: var(--q-primary);
  z-index: 2;
  transition: width 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  border-radius: 2px;
}
.step-indicator {
  position: relative;
  z-index: 3;
  cursor: pointer;
}
.step-label {
  color: #888888;
  font-size: 0.78rem;
  transition: all 0.3s ease;
}
.step-active .step-label {
  color: var(--q-primary);
  font-weight: 700;
}
.step-completed .step-label {
  color: var(--q-secondary);
}

/* Choice Cards */
.pool-type-card {
  border-radius: 20px;
  border: 1px solid rgba(0,0,0,0.08);
  background: white;
  min-height: 340px;
  transition: all 0.35s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.pool-type-card:hover {
  transform: translateY(-6px);
  border-color: var(--q-primary);
  box-shadow: 0 12px 30px rgba(0,0,0,0.06);
}
.card-active {
  border-color: var(--q-primary) !important;
  background: #FFFBF2 !important;
}

/* AI Choice cards */
.ai-choice-card {
  border-radius: 16px;
  border: 1px solid rgba(0,0,0,0.08);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: white;
}
.ai-choice-card:hover {
  transform: translateY(-4px);
  border-color: var(--q-primary);
  box-shadow: 0 8px 24px rgba(0,0,0,0.05);
}

/* In-page Chat UI */
.chat-container-box {
  max-height: 320px;
  overflow-y: auto;
  background: #FFFFFF;
  border: 1px solid rgba(0,0,0,0.08);
  border-radius: 12px;
  padding: 16px;
}

/* Floating PotiBuddy container */
.potibuddy-floating-container {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-family: 'Inter', sans-serif;
}

/* Floating Chat Window */
.buddy-chat-window {
  width: 330px;
  height: 420px;
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 12px;
  animation: slideInUp 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.buddy-header {
  background: linear-gradient(135deg, #1e3a5f 0%, #0D1B2E 100%);
  color: white;
  position: relative;
}
.buddy-online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #4CAF50;
  border: 2px solid #0D1B2E;
  border-radius: 50%;
}
.buddy-messages {
  flex: 1;
  overflow-y: auto;
  background: #F8F9FA;
}
.buddy-msg-bubble {
  padding: 10px 14px;
  font-size: 0.85rem;
  line-height: 1.45;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.assistant-bubble {
  background: #FFFFFF;
  color: #333333;
  border-radius: 4px 16px 16px 16px;
  border: 1px solid #EAEAEA;
}
.user-bubble {
  background: var(--q-primary);
  color: #1A1A2A;
  font-weight: 500;
  border-radius: 16px 16px 4px 16px;
}
.buddy-input-area {
  background: #FFFFFF;
  border-top: 1px solid #EAEAEA;
}
.buddy-suggestion-card {
  background: #FFFDF0;
  border: 1px solid #FFE082;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

/* Avatar button and floating effect */
.buddy-avatar-btn {
  position: relative;
  border-radius: 50%;
  animation: floatBuddy 4s ease-in-out infinite;
}
.buddy-avatar-img {
  border: 3px solid white;
  background: white;
}
.buddy-online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #4CAF50;
  border: 2px solid white;
  border-radius: 50%;
  z-index: 5;
}
.avatar-breath-ring {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  border: 2px solid rgba(76, 175, 80, 0.4);
  animation: breath 2s infinite ease-in-out;
}

/* Tooltip */
.buddy-tooltip {
  position: absolute;
  right: 70px;
  top: 10px;
  background: #0D1B2E;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
  pointer-events: none;
  animation: fadeIn 0.5s ease;
}
.buddy-tooltip::after {
  content: '';
  position: absolute;
  right: -6px;
  top: 50%;
  transform: translateY(-50%);
  border-width: 6px 0 6px 6px;
  border-style: solid;
  border-color: transparent transparent transparent #0D1B2E;
}

/* Animations */
@keyframes floatBuddy {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
@keyframes breath {
  0% { transform: scale(0.95); opacity: 0.2; }
  50% { transform: scale(1.05); opacity: 0.8; }
  100% { transform: scale(0.95); opacity: 0.2; }
}
@keyframes slideInUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.animate-fade {
  animation: fadeIn 0.4s ease;
}
.border-light {
  border: 1px solid rgba(0,0,0,0.06);
}
.border-accent {
  border: 1px solid var(--q-primary);
}
.border-amber {
  border: 1px solid #FFE082;
}
</style>
